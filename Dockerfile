FROM openjdk:19-jdk-alpine
WORKDIR /app
COPY ./target/ebanking-backend-0.0.1-SNAPSHOT.jar /app/ebanking-backend.jar
EXPOSE 8080
CMD ["java", "-jar", "ebanking-backend.jar"]
