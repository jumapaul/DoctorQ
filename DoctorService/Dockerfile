FROM eclipse-temurin:17-jdk-alpine
WORKDIR /DoctorQBackend/DoctorService
COPY build/libs/DoctorService-0.0.1-SNAPSHOT.jar /DoctorQBackend/DoctorService/DoctorService-0.0.1-SNAPSHOT.jar
EXPOSE 8020
ENTRYPOINT ["java", "-jar", "/DoctorQBackend/DoctorService/DoctorService-0.0.1-SNAPSHOT.jar"]