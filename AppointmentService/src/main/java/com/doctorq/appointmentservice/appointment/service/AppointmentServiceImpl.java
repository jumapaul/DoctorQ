package com.doctorq.appointmentservice.appointment.service;

import com.doctorq.appointmentservice.appointment.dtos.*;
import com.doctorq.appointmentservice.appointment.exception.BadRequestException;
import com.doctorq.appointmentservice.appointment.exception.ResourceNotFoundException;
import com.doctorq.appointmentservice.appointment.exception.ServiceUnavailableException;
import com.doctorq.appointmentservice.appointment.feign_client.DoctorClient;
import com.doctorq.appointmentservice.appointment.feign_client.UserClient;
import com.doctorq.appointmentservice.appointment.mappers.AppointmentMapper;
import com.doctorq.appointmentservice.appointment.entity.AppointmentEntity;
import com.doctorq.appointmentservice.appointment.mail.EmailService;
import com.doctorq.appointmentservice.appointment.repository.AppointmentRepository;
import com.doctorq.appointmentservice.history.HistoryEntity;
import com.doctorq.appointmentservice.history.HistoryRepository;
import com.doctorq.appointmentservice.kafka.AppointmentCompletionEvent;
import com.doctorq.appointmentservice.kafka.KafkaProducer;
import com.doctorq.appointmentservice.notification.Notification;
import com.doctorq.appointmentservice.notification.NotificationService;
import com.doctorq.appointmentservice.util.Constants;
import com.doctorq.appointmentservice.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.doctorq.appointmentservice.appointment.mail.EmailTemplate.DOCTOR_MAIL;
import static com.doctorq.appointmentservice.notification.NotificationType.*;
import static com.doctorq.appointmentservice.util.Constants.*;
import static com.doctorq.appointmentservice.util.RedisRetrieveMethods.readCacheValue;
import static com.doctorq.appointmentservice.util.RedisRetrieveMethods.setCacheValue;


@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final HistoryRepository historyRepository;
    private final AppointmentMapper appointmentMapper;
    private final EmailService emailService;
    private final DoctorClient doctorClient;
    private final UserClient userClient;
    private final NotificationService notificationService;
    private final RedisUtil redisUtil;
    private final KafkaProducer kafkaProducer;

    @Transactional
    @Override
    public AppointmentResponse addAppointment(AddAppointmentRequest request, String authToken) throws MessagingException {

        redisUtil.deleteByPrefix(userAppointmentByStatusCache);
        redisUtil.deleteByPrefix(doctorAppointmentByStatusCache);
        DoctorResponse doctorResponse = getDoctorById(request.doctorId(), authToken).getData();

        validateAppointment(request, doctorResponse);

        AppointmentEntity entity = appointmentMapper.toAppointmentEntity(request);

        appointmentRepository.save(entity);

        AppointmentResponse response = appointmentMapper.fromAppointmentEntity(entity);

        //Send mail to doctor when appointment is created
        sendMail(doctorResponse.getEmail(), doctorResponse.getFullName(), response,
                "You have a new appointment scheduled.", DOCTOR_MAIL.getTemplate());

        //Send in app notification
        historyRepository.save(appointmentMapper.toHistoryEntity(response.userId(), response.doctorId(), HistoryStatus.CREATED));

        return response;
    }

    @Transactional
    @Override
    public AppointmentResponse approveAppointment(Long id, String token) {
        redisUtil.deleteByPrefix(userAppointmentByStatusCache);
        redisUtil.deleteByPrefix(doctorAppointmentByStatusCache);

        AppointmentResponse response = updateAppointmentStatus(id, AppointmentStatus.APPROVED);

        DoctorResponse doctorResponse = getDoctorById(response.doctorId(), token).getData();

        historyRepository.save(appointmentMapper.toHistoryEntity(response.userId(), response.doctorId(), HistoryStatus.APPROVED));

        //Send in app notification to user.
        Notification notification = Notification.builder()
                .message("Appointment with doctor " + doctorResponse.getFullName() + " at " + response.startTime())
                .title("Appointment confirmed")
                .type(APPROVED)
                .timestamp(LocalDateTime.now())
                .build();
        notificationService.sendNotification(response.userId(), notification);
        return response;
    }

    @Transactional
    @Override
    public AppointmentResponse cancelAppointment(Long id) {

        redisUtil.deleteByPrefix(userAppointmentByStatusCache);
        redisUtil.deleteByPrefix(doctorAppointmentByStatusCache);

        AppointmentResponse response = updateAppointmentStatus(id, AppointmentStatus.CANCELLED);

//        DoctorResponse doctorResponse = getDoctorById(response.doctorId(), token).getData();

        //Save to history
        historyRepository.save(appointmentMapper.toHistoryEntity(response.userId(), response.doctorId(), HistoryStatus.CANCEL));

        //Send mail to doctor
        //send in app notification
        Notification notification = Notification.builder()
                .message("Appointment successfully cancelled")
                .title("Appointment cancellation")
                .type(CANCELLED)
                .timestamp(LocalDateTime.now())
                .build();
        notificationService.sendNotification(response.userId(), notification);

        return response;
    }

    @Transactional
    @Override
    public AppointmentResponse completeAppointment(Long id) {

        redisUtil.deleteByPrefix(userAppointmentByStatusCache);
        redisUtil.deleteByPrefix(doctorAppointmentByStatusCache);

        AppointmentResponse response = updateAppointmentStatus(id, AppointmentStatus.COMPLETED);

        //Save to history
        historyRepository.save(appointmentMapper.toHistoryEntity(response.userId(), response.doctorId(), HistoryStatus.COMPLETE));

        AppointmentCompletionEvent event = new AppointmentCompletionEvent(
                response.id(),
                response.doctorId(),
                "Appointment complete"
        );

        kafkaProducer.publish(event);
        //Send notification to user.
        Notification notification = Notification.builder()
                .message("Appointment successfully completed")
                .title("Appointment completion")
                .type(COMPLETED)
                .timestamp(LocalDateTime.now())
                .build();
        notificationService.sendNotification(response.userId(), notification);

        return response;
    }

    private AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatus status) {
        AppointmentEntity appointment = appointmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Appointment not found")
        );

        appointment.setAppointmentStatus(status);
        appointmentRepository.save(appointment);

        return appointmentMapper.fromAppointmentEntity(appointment);
    }

    @Override
    public List<AppointmentEntity> getUserAppointmentByStatus(Long userId, AppointmentStatus status) throws JsonProcessingException {
        Object appointmentByStatusCache = redisUtil.get(userAppointmentByStatusCache + status);

        if (appointmentByStatusCache == null) {
            List<AppointmentEntity> appointment = appointmentRepository.findAllByUserIdAndAppointmentStatus(userId, status);
            setCacheValue(redisUtil, userAppointmentByStatusCache + status, appointment);
            return appointment;
        }

        return readCacheValue(appointmentByStatusCache.toString(), new TypeReference<>() {
        });
    }

    @Override
    public List<AppointmentEntity> getAppointmentsByDateAndStatus(LocalDate date, AppointmentStatus status) throws JsonProcessingException {
        Object appointmentByDateCache = redisUtil.get(Constants.appointmentByDateCacheAndStatus);

        if (appointmentByDateCache == null) {
            List<AppointmentEntity> appointments = appointmentRepository.findAllByDateAndAppointmentStatus(date, status);

            setCacheValue(redisUtil, Constants.appointmentByDateCacheAndStatus, appointments);
            return appointments;
        }

        return readCacheValue(appointmentByDateCache.toString(), new TypeReference<>() {
        });
    }

    @Override
    public List<AppointmentEntity> getDoctorAppointmentByStatus(Long doctorId, AppointmentStatus status) throws JsonProcessingException {
        Object appointmentByStatusCache = redisUtil.get(doctorAppointmentByStatusCache + status);

        if (appointmentByStatusCache == null) {
            List<AppointmentEntity> appointment = appointmentRepository.findAllByDoctorIdAndAppointmentStatus(doctorId, status);
            setCacheValue(redisUtil, doctorAppointmentByStatusCache + status, appointment);

            return appointment;
        }
        return readCacheValue(appointmentByStatusCache.toString(), new TypeReference<>() {
        });
    }

    private void validateAppointment(AddAppointmentRequest request, DoctorResponse doctorResponse) {
        if (request.date().isBefore(LocalDate.now()))
            throw new BadRequestException("Cannot make appointment for past date");

        if (request.date().isEqual(LocalDate.now()) && request.startTime().isBefore(LocalTime.now()))
            throw new BadRequestException("Cannot make appointment for past time");

        if (request.startTime().isAfter(LocalTime.parse(doctorResponse.getSchedule().endTime())))
            throw new BadRequestException("Doctor will be out at " + request.startTime());

        Optional<AppointmentEntity> appointment = appointmentRepository.findOverlappingAppointment(request.date(),
                request.startTime(), request.endTime());

        if (appointment.isPresent())
            throw new BadRequestException("Slot already taken");
    }

    private void sendMail(String mail, String fullName, Object response,
                          String title, String template) throws MessagingException {
        Map<String, Object> doctorVariables = new HashMap<>();
        doctorVariables.put("appointment", response);
        doctorVariables.put("doctorName", fullName);
        doctorVariables.put("title", title);
        doctorVariables.put("description", title);

        emailService.sendMail(mail, template,
                doctorVariables, title);
    }

    @CircuitBreaker(name = "doctorCircuitBreaker", fallbackMethod = "doctorFallback")
    private ApiResponse<DoctorResponse> getDoctorById(Long doctorId, String token) {
        try {
            return doctorClient.getDoctorById(doctorId, token);
        } catch (Exception exception) {
            log.error("------------>Error: {}", exception.getMessage());
            throw new ResourceNotFoundException(exception.getMessage());
        }
    }

    private DoctorResponse doctorFallback(AddAppointmentRequest request, Exception exception) {
        log.error("Circuit breaker activated: {}", exception.getMessage());
        throw new ServiceUnavailableException(
                "Doctor service temporarily unavailable please try again later"
        );
    }

    private UserResponse userServiceFallback(AddAppointmentRequest request, Exception exception) {
        log.error("Circuit breaker activated: {}", exception.getMessage());
        throw new ServiceUnavailableException(
                "User service temporarily unavailable please try again later"
        );
    }

    private <T> PaginatedResponse<T> paginate(List<T> data, Page<?> paginatedData) {
        return new PaginatedResponse<>(
                data,
                paginatedData.getNumber(),
                paginatedData.getTotalPages(),
                paginatedData.getSize(),
                paginatedData.getNumberOfElements(),
                paginatedData.getSort().isSorted(),
                paginatedData.isLast()
        );
    }
}
