#Build stage
FROM gradle:8.5-jdk17 AS builder
WORKDIR /DoctorQBackend/GatewayService
COPY . .
RUN gradle build -x test --no-daemon

#Run stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /DoctorQBackend/GatewayService
COPY --from=builder /DoctorQBackend/GatewayService/build/libs/GatewayService-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]