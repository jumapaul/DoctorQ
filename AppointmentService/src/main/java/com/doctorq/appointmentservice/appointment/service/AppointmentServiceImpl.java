package com.doctorq.appointmentservice.appointment.service;

import com.doctorq.appointmentservice.appointment.dtos.*;
import com.doctorq.appointmentservice.appointment.feign_client.UserClient;
import com.doctorq.appointmentservice.exception.BadRequestException;
import com.doctorq.appointmentservice.exception.FirebaseMessaginException;
import com.doctorq.appointmentservice.exception.ResourceNotFoundException;
import com.doctorq.appointmentservice.exception.ServiceUnavailableException;
import com.doctorq.appointmentservice.appointment.feign_client.DoctorClient;
import com.doctorq.appointmentservice.appointment.mappers.AppointmentMapper;
import com.doctorq.appointmentservice.appointment.entity.AppointmentEntity;
import com.doctorq.appointmentservice.appointment.mail.EmailService;
import com.doctorq.appointmentservice.appointment.repository.AppointmentRepository;
import com.doctorq.appointmentservice.history.HistoryRepository;
import com.doctorq.appointmentservice.kafka.AppointmentCompletionEvent;
import com.doctorq.appointmentservice.kafka.KafkaProducer;
import com.doctorq.appointmentservice.notification.*;
import com.doctorq.appointmentservice.util.Constants;
import com.doctorq.appointmentservice.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.doctorq.appointmentservice.appointment.dtos.AppointmentStatus.*;
import static com.doctorq.appointmentservice.appointment.mail.EmailTemplate.DOCTOR_MAIL;
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
    private final NotificationService notificationService;
    private final RedisUtil redisUtil;
    private final KafkaProducer kafkaProducer;
    private final UserClient userClient;
    private final NotificationRepository notificationRepository;

    @Transactional
    @Override
    public AppointmentResponse addAppointment(AddAppointmentRequest request, String authToken) throws MessagingException {
        try {

            redisUtil.delete(userAppointmentByStatusCache + SCHEDULED + request.userId());
            redisUtil.delete(doctorAppointmentByStatusCache + SCHEDULED + request.doctorId());

            DoctorResponse doctorResponse = getDoctorById(request.doctorId(), authToken).getData();
            getUserById(request.userId(), authToken).getData();

            validateAppointment(request, doctorResponse);

            AppointmentEntity entity = appointmentMapper.toAppointmentEntity(request);

            appointmentRepository.save(entity);

            AppointmentResponse response = appointmentMapper.fromAppointmentEntity(entity);

            //Send mail to doctor when appointment is created
            sendMail(doctorResponse.getEmail(), doctorResponse.getFullName(), response,
                    "You have a new appointment scheduled.", DOCTOR_MAIL.getTemplate());

            historyRepository.save(appointmentMapper.toHistoryEntity(response.userId(), response.doctorId(), HistoryStatus.CREATED));

            return response;
        } catch (RuntimeException e) {
            log.error("--------------->{}", e.getMessage());
            throw new MessagingException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public AppointmentResponse approveAppointment(Long id, String token) {
        AppointmentResponse response = updateAppointmentStatus(id, AppointmentStatus.APPROVED, token);

        redisUtil.delete(userAppointmentByStatusCache + APPROVED + response.userId());
        redisUtil.delete(doctorAppointmentByStatusCache + APPROVED + response.doctorId());
        redisUtil.delete(userAppointmentByStatusCache + SCHEDULED + response.userId());
        redisUtil.delete(doctorAppointmentByStatusCache + SCHEDULED + response.doctorId());

        historyRepository.save(appointmentMapper.toHistoryEntity(response.userId(), response.doctorId(), HistoryStatus.APPROVED));

        NotificationEvent event = new NotificationEvent(
                response,
                "has been approved",
                "Appointment confirmed"
        );

        handleNotification(event);
        return response;
    }

    @Transactional
    @Override
    public AppointmentResponse cancelAppointment(Long id, String token) {
        redisUtil.deleteGroup(userAppointmentByStatusCache);
        redisUtil.deleteGroup(doctorAppointmentByStatusCache);
        AppointmentResponse response = updateAppointmentStatus(id, AppointmentStatus.CANCELLED, token);

        //Save to history
        historyRepository.save(appointmentMapper.toHistoryEntity(response.userId(), response.doctorId(), HistoryStatus.CANCEL));

//        Send mail to doctor

        NotificationEvent event = new NotificationEvent(
                response,
                "has been cancelled",
                "Appointment cancelled"
        );

        handleNotification(event);

        return response;
    }

    @Transactional
    @Override
    public AppointmentResponse completeAppointment(Long id, String token) {

        AppointmentResponse response = updateAppointmentStatus(id, COMPLETED, token);
        redisUtil.delete(userAppointmentByStatusCache + APPROVED + response.userId());
        redisUtil.delete(doctorAppointmentByStatusCache + APPROVED + response.doctorId());

        //Save to history
        historyRepository.save(appointmentMapper.toHistoryEntity(response.userId(), response.doctorId(), HistoryStatus.COMPLETE));

        AppointmentCompletionEvent event = new AppointmentCompletionEvent(
                response.id(),
                response.doctorId(),
                "Appointment complete"
        );

        kafkaProducer.publish(event);
        //Send notification to user.
        NotificationEvent notificationEvent = new NotificationEvent(
                response,
                "has been completed",
                "Appointment completed"
        );

        handleNotification(notificationEvent);

        return response;
    }

    private AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatus status, String token) {
        AppointmentEntity appointment = appointmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Appointment not found")
        );
//        if (appointment.getAppointmentStatus() == status)
//            throw new IllegalArgumentException("Appointment already " + status);

        DoctorResponse response = getDoctorById(appointment.getDoctorId(), token).getData();

        appointment.setAppointmentStatus(status);
        appointmentRepository.save(appointment);

        return appointmentMapper.fromAppointmentEntityWithDoctorOverview(appointment, response);
    }

    @Override
    public List<AppointmentResponse> getUserAppointmentByStatus(Long userId, AppointmentStatus status, String token)
            throws JsonProcessingException {
        Object appointmentByStatusCache = redisUtil.get(userAppointmentByStatusCache + userId + status);

        if (appointmentByStatusCache != null)
            return readCacheValue(appointmentByStatusCache.toString(), new TypeReference<>() {
            });

        List<AppointmentEntity> appointment = appointmentRepository.findAllByUserIdAndAppointmentStatus(userId, status);

        List<AppointmentResponse> appointmentResponses = appointment.stream().map(appointments -> {
            ApiResponse<DoctorResponse> response = doctorClient.getDoctorById(appointments.getDoctorId(), token);

            return appointmentMapper.fromAppointmentEntityWithDoctorOverview(appointments, response.getData());
        }).toList();
        setCacheValue(redisUtil, userAppointmentByStatusCache + status, appointmentResponses);
        return appointmentResponses;
    }

    @Override
    public List<AppointmentResponse> getUserAppointmentsByDateAndStatus(Long userId, LocalDate date, AppointmentStatus status, String token)
            throws JsonProcessingException {

        String cacheKey = Constants.appointmentByDateCacheAndStatus + status + date + userId;

        Object appointmentByDateCache = redisUtil.get(cacheKey);

        if (appointmentByDateCache != null)
            return readCacheValue(appointmentByDateCache.toString(), new TypeReference<>() {
            });

        List<AppointmentEntity> appointments =
                appointmentRepository.findAllByUserIdAndAndDateAndAppointmentStatus(userId, date, status);

        List<AppointmentResponse> appointmentResponses = appointments.stream().map(appointment -> {
            ApiResponse<DoctorResponse> response = doctorClient.getDoctorById(appointment.getDoctorId(), token);

            return appointmentMapper.fromAppointmentEntityWithDoctorOverview(appointment, response.getData());
        }).toList();

        setCacheValue(redisUtil, cacheKey, appointmentResponses);
        return appointmentResponses;

    }

    @Override
    public List<AppointmentResponse> getDoctorAppointmentByStatus(Long doctorId, AppointmentStatus status, String token) throws JsonProcessingException {
        String cacheKey = doctorAppointmentByStatusCache + status + doctorId;
        Object appointmentByStatusCache = redisUtil.get(cacheKey);

        if (appointmentByStatusCache != null)
            return readCacheValue(appointmentByStatusCache.toString(), new TypeReference<>() {
            });

        List<AppointmentEntity> appointments = appointmentRepository.findAllByDoctorIdAndAppointmentStatus(doctorId, status);

        List<AppointmentResponse> appointmentResponses = appointments.stream().map(appointment -> {
            ApiResponse<DoctorResponse> response = doctorClient.getDoctorById(appointment.getDoctorId(), token);

            return appointmentMapper.fromAppointmentEntityWithDoctorOverview(appointment, response.getData());
        }).toList();
        setCacheValue(redisUtil, cacheKey, appointmentResponses);

        return appointmentResponses;
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id, String token) throws JsonProcessingException {

        String cacheKey = appointmentById + id;
        Object appointmentByIdCache = redisUtil.get(cacheKey);

        if (appointmentByIdCache != null) return readCacheValue(appointmentByIdCache, new TypeReference<>() {
        });

        AppointmentEntity appointment = appointmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Appoint with id " + id + " not found")
        );

        ApiResponse<DoctorResponse> doctorResponse = doctorClient.getDoctorById(appointment.getDoctorId(), token);

        AppointmentResponse response = appointmentMapper.fromAppointmentEntityWithDoctorOverview(appointment, doctorResponse.getData());

        setCacheValue(redisUtil, cacheKey, response);
        return response;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotification(NotificationEvent event) {
        String cacheKey = notificationByUserId + event.response().userId() + 0 + 10;
        try {
            NotificationRequest request = new NotificationRequest(
                    event.response().userId(),
                    "Appointment with Dr." + event.response().doctorResponse().getFullName() + " " + event.desc(),
                    event.title()
            );
            NotificationEntity notification = NotificationEntity.builder()
                    .userId(request.getUserId())
                    .title(event.title())
                    .description(request.getMessage())
                    .date(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
            redisUtil.delete(cacheKey);
            notificationService.sendNotification(request);
        } catch (FirebaseMessaginException e) {
            log.error(e.getMessage());
        }
    }

    private void validateAppointment(AddAppointmentRequest request, DoctorResponse doctorResponse) {
        if (request.date().isBefore(LocalDate.now()))
            throw new BadRequestException("Cannot make appointment for past date");

        if (request.date().isEqual(LocalDate.now()) && request.startTime().isBefore(LocalTime.now()))
            throw new BadRequestException("Cannot make appointment for past time");

        if (request.startTime().isAfter(LocalTime.parse(doctorResponse.getSchedule().endTime())))
            throw new BadRequestException("Doctor will be out at " + request.startTime());

        Optional<AppointmentEntity> appointment = appointmentRepository.findOverlappingAppointment(request.date(),
                request.startTime(), request.endTime(), request.doctorId());

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

    @CircuitBreaker(name = "userCircuitBreaker", fallbackMethod = "userServiceFallback")
    public ApiResponse<UserResponse> getUserById(Long userId, String token) {
        try {
            return userClient.getUser(userId, token);
        } catch (Exception exception) {
            throw new ResourceNotFoundException(exception.getMessage());
        }
    }

    @CircuitBreaker(name = "doctorCircuitBreaker", fallbackMethod = "doctorFallback")
    public ApiResponse<DoctorResponse> getDoctorById(Long doctorId, String token) {
        try {
            return doctorClient.getDoctorById(doctorId, token);
        } catch (Exception exception) {
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
}
