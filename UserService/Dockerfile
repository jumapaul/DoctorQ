FROM openjdk:17-jdk-alpine
WORKDIR /DoctorQBackend/UserService
COPY build/libs/UserService-0.0.1-SNAPSHOT.jar /DoctorQBackend/UserService/UserService-0.0.1-SNAPSHOT.jar
EXPOSE 8010
ENTRYPOINT ["java", "-jar", "/DoctorQBackend/UserService/UserService-0.0.1-SNAPSHOT.jar"]