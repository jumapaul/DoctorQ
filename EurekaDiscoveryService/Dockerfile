FROM openjdk:17-jdk-alpine
WORKDIR /DoctorQBackend/EurekaDiscoveryService
COPY build/libs/EurekaDiscoveryService-0.0.1-SNAPSHOT.jar /DoctorQBackend/EurekaDiscoveryService/EurekaDiscoveryService-0.0.1-SNAPSHOT.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/DoctorQBackend/EurekaDiscoveryService/EurekaDiscoveryService-0.0.1-SNAPSHOT.jar"]