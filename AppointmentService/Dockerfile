FROM eclipse-temurin:17-jdk-alpine
WORKDIR /DoctorQBackend/AppointmentService
COPY build/libs/AppointmentService-0.0.1-SNAPSHOT.jar /DoctorQBackend/AppointmentService/AppointmentService-0.0.1-SNAPSHOT.jar
EXPOSE 8040
ENTRYPOINT ["java", "-jar", "/DoctorQBackend/AppointmentService/AppointmentService-0.0.1-SNAPSHOT.jar"]