FROM eclipse-temurin:17-jdk-alpine
WORKDIR /DoctorQBackend/FavoriteService
COPY build/libs/FavoriteService-0.0.1-SNAPSHOT.jar /DoctorQBackend/FavoriteService/FavoriteService-0.0.1-SNAPSHOT.jar
EXPOSE 8040
ENTRYPOINT ["java", "-jar", "/DoctorQBackend/FavoriteService/FavoriteService-0.0.1-SNAPSHOT.jar"]