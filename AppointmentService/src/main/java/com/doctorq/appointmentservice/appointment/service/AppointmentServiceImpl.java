package com.doctorq.appointmentservice.appointment.service;

import com.doctorq.appointmentservice.appointment.dtos.*;
import com.doctorq.appointmentservice.appointment.entity.HistoryOuterBoxEntity;
import com.doctorq.appointmentservice.appointment.exception.BadRequestException;
import com.doctorq.appointmentservice.appointment.exception.ResourceNotFoundException;
import com.doctorq.appointmentservice.appointment.exception.ServiceUnavailableException;
import com.doctorq.appointmentservice.appointment.feign_client.DoctorClient;
import com.doctorq.appointmentservice.appointment.feign_client.UserClient;
import com.doctorq.appointmentservice.appointment.mappers.AppointmentMapper;
import com.doctorq.appointmentservice.appointment.entity.AppointmentEntity;
import com.doctorq.appointmentservice.appointment.mail.EmailService;
import com.doctorq.appointmentservice.appointment.repository.AppointmentRepository;
import com.doctorq.appointmentservice.appointment.repository.HistoryOuterBoxRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.doctorq.appointmentservice.appointment.mail.EmailTemplate.APPOINTMENT_APPROVAL_TEMPLATE;
import static com.doctorq.appointmentservice.appointment.mail.EmailTemplate.DOCTOR_MAIL;
import static com.doctorq.appointmentservice.notification.NotificationType.APPROVED;
import static com.doctorq.appointmentservice.notification.NotificationType.CREATED;
import static com.doctorq.appointmentservice.util.Constants.*;
import static com.doctorq.appointmentservice.util.RedisRetrieveMethods.readCacheValue;
import static com.doctorq.appointmentservice.util.RedisRetrieveMethods.setCacheValue;


@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final EmailService emailService;
    private final DoctorClient doctorClient;
    private final UserClient userClient;
    private final HistoryOuterBoxRepository historyOuterBoxRepository;
    private final NotificationService notificationService;
    private final RedisUtil redisUtil;

    @Transactional
    @Override
    public AppointmentResponse addAppointment(AddAppointmentRequest request, String authToken) throws MessagingException {

        redisUtil.deleteByPrefix(allAppointmentCache);
        redisUtil.delete(appointmentByStatusCache);
        redisUtil.delete(appointmentByDateCache);
        DoctorResponse doctorResponse = getDoctorById(request).getData();

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

        AppointmentEntity entity = appointmentMapper.toAppointmentEntity(request);

        AppointmentEntity savedAppointment = appointmentRepository.save(entity);

        saveToOuterBox(request.userId(), request.doctorId(), "Create appointment",
                HistoryStatus.SCHEDULED.name(), ProcessedStatus.UNPROCESSED);
        AppointmentResponse response = appointmentMapper.fromAppointmentEntity(savedAppointment);

        sendMail(doctorResponse.getEmail(), doctorResponse.getFullName(), response,
                "You have a new appointment scheduled.", DOCTOR_MAIL.getTemplate());

//        Notification notification = Notification.builder()
//                .message("Appointment successfully created")
//                .title("Appointment creation")
//                .type(CREATED)
//                .timestamp(LocalDateTime.now())
//                .build();
//        notificationService.sendNotification(request.userId(), notification);

        return response;
    }

    @Override
    public AppointmentResponse approveAppointment(Long id, UpdateAppointmentRequest request) throws MessagingException {
        redisUtil.delete(appointmentByStatusCache);

        AppointmentEntity appointment = appointmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Appointment not found")
        );

        appointment.setAppointmentStatus(AppointmentStatus.APPROVED);
        appointmentRepository.save(appointment);

        AppointmentResponse response = appointmentMapper.fromAppointmentEntity(appointment);

        saveToOuterBox(response.userId(), response.doctorId(), "Approve appointment",
                HistoryStatus.APPROVED.name(), ProcessedStatus.UNPROCESSED);

        sendMail(request.userMail(), request.doctorName(), response,
                "Your appointment has been successfully approved.", APPOINTMENT_APPROVAL_TEMPLATE.getTemplate());
        Notification notification = Notification.builder()
                .message("Appointment with doctor " + request.doctorName() + " at " + appointment.getStarTime())
                .title("Appointment confirmed")
                .type(APPROVED)
                .timestamp(LocalDateTime.now())
                .build();
        notificationService.sendNotification(response.userId(), notification);
        return response;
    }

    @Override
    public AppointmentResponse cancelAppointment(Long id, UpdateAppointmentRequest request) throws MessagingException {

        redisUtil.delete(appointmentByStatusCache);

        AppointmentEntity appointment = appointmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Appointment not found")
        );

        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        AppointmentResponse response = appointmentMapper.fromAppointmentEntity(appointment);

        saveToOuterBox(response.userId(), response.doctorId(), "Cancel appointment",
                HistoryStatus.CANCEL.name(), ProcessedStatus.UNPROCESSED
        );

        sendMail(request.userMail(), request.doctorName(), response,
                "Appointment cancelled", APPOINTMENT_APPROVAL_TEMPLATE.getTemplate());

        sendMail(request.doctorMail(), request.doctorName(), response,
                "Appointment cancelled", DOCTOR_MAIL.getTemplate());

        Notification notification = Notification.builder()
                .message("Appointment successfully cancelled")
                .title("Appointment cancellation")
                .type(CREATED)
                .timestamp(LocalDateTime.now())
                .build();
        notificationService.sendNotification(response.userId(), notification);
        return response;
    }

    @Override
    public PaginatedResponse<AppointmentEntity> getAllAppointment(int page, int size) throws JsonProcessingException {
        Object appointmentCache = redisUtil.get(allAppointmentCache + page + size);

        if (appointmentCache == null) {
            Pageable pageable = PageRequest.of(page, size);

            Page<AppointmentEntity> appointments = appointmentRepository.findAll(pageable);

            List<AppointmentEntity> appointmentEntityList = appointments.stream().toList();

            PaginatedResponse<AppointmentEntity> paginatedResponse = paginate(appointmentEntityList, appointments);

            setCacheValue(redisUtil, allAppointmentCache + page + size, paginatedResponse);
            return paginatedResponse;
        }

        return readCacheValue(appointmentCache.toString(), new TypeReference<>() {
        });
    }

    @Override
    public List<AppointmentEntity> getAppointmentByStatus(AppointmentStatus status) throws JsonProcessingException {
//        Pageable pageable = PageRequest.of(page, size);
        Object appointmentByStatusCache = redisUtil.get(Constants.appointmentByStatusCache);

        if (appointmentByStatusCache == null) {
            List<AppointmentEntity> appointment = appointmentRepository.findAllByAppointmentStatus(status);
            setCacheValue(redisUtil, Constants.appointmentByStatusCache, appointment);
            return appointment;
        }

        return readCacheValue(appointmentByStatusCache.toString(), new TypeReference<>() {
        });
    }

    @Override
    public List<AppointmentEntity> getAppointmentsByDate(LocalDate date) throws JsonProcessingException {
        Object appointmentByDateCache = redisUtil.get(Constants.appointmentByDateCache);

        if (appointmentByDateCache == null) {
            List<AppointmentEntity> appointments = appointmentRepository.findAllByDate(date);

            setCacheValue(redisUtil, Constants.appointmentByDateCache, appointments);
            return appointments;
        }

        return readCacheValue(appointmentByDateCache.toString(), new TypeReference<>() {
        });
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

    public void saveToOuterBox(Long userId, Long doctorId, String title, String historyStatus,
                               ProcessedStatus processedStatus) {
        HistoryOuterBoxEntity historyOuterBoxEntity = HistoryOuterBoxEntity.builder()
                .userId(userId)
                .doctorId(doctorId)
                .activityName(title)
                .historyStatus(historyStatus)
                .timestamp(LocalDateTime.now())
                .processedStatus(processedStatus)
                .build();

        historyOuterBoxRepository.save(historyOuterBoxEntity);
    }

    @CircuitBreaker(name = "doctorCircuitBreaker", fallbackMethod = "doctorFallback")
    private ApiResponse<DoctorResponse> getDoctorById(AddAppointmentRequest request) {
        try {
            return doctorClient.getDoctorById(request.doctorId());
        } catch (Exception exception) {
            log.error("------------>Error: {}", exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

//    @CircuitBreaker(name = "userCircuitBreaker", fallbackMethod = "userServiceFallback")
//    private ApiResponse<UserResponse> getUserById(AddAppointmentRequest request, String authToken) {
//        return userClient.getUser(request.doctorId(), authToken);
//    }

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
