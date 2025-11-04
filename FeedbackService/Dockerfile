FROM openjdk:17-jdk-alpine
WORKDIR /DoctorQBackend/FeedbackService
COPY build/libs/FeedbackService-0.0.1-SNAPSHOT.jar /DoctorQBackend/FeedbackService/FeedbackService-0.0.1-SNAPSHOT.jar
EXPOSE 8030
ENTRYPOINT ["java", "-jar", "/DoctorQBackend/FeedbackService/FeedbackService-0.0.1-SNAPSHOT.jar"]