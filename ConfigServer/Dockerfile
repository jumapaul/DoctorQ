#FROM eclipse-temurin:17-jdk-alpine
#WORKDIR /DoctorQBackend/ConfigServer
#COPY build/libs/ConfigServer-0.0.1-SNAPSHOT.jar /DoctorQBackend/ConfigServer/ConfigServer-0.0.1-SNAPSHOT.jar
#EXPOSE 8000
#ENTRYPOINT ["java", "-jar", "/DoctorQBackend/ConfigServer/ConfigServer-0.0.1-SNAPSHOT.jar"]

#Build stage
FROM gradle:8.5-jdk17-alpine AS builder
WORKDIR /DoctorQBackend/ConfigServer
COPY . .
RUN gradle build -x test

#Run stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /DoctorQBackend/ConfigServer
COPY --from=builder /DoctorQBackend/ConfigServer/build/libs/ConfigServer-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]