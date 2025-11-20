FROM eclipse-temurin:17-jdk-alpine
WORKDIR /DoctorQBackend/ConfigServer
COPY build/libs/ConfigServer-0.0.1-SNAPSHOT.jar /DoctorQBackend/ConfigServer/ConfigServer-0.0.1-SNAPSHOT.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/DoctorQBackend/ConfigServer/ConfigServer-0.0.1-SNAPSHOT.jar"]