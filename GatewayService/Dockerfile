FROM openjdk:17-jdk-alpine
WORKDIR /DoctorQBackend/GatewayService
COPY build/libs/GatewayService-0.0.1-SNAPSHOT.jar /DoctorQBackend/GatewayService/GatewayService-0.0.1-SNAPSHOT.jar
EXPOSE 8222
ENTRYPOINT ["java", "-jar", "/DoctorQBackend/GatewayService/GatewayService-0.0.1-SNAPSHOT.jar"]