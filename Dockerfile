# Use the official Spring Boot image as the base image
FROM openjdk:17-jdk-alpine

WORKDIR /My Database

# Copy the application JAR file into the container
COPY ./target/mydatabase-0.0.1-SNAPSHOT.jar mydatabase-0.0.1-SNAPSHOT.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "mydatabase-0.0.1-SNAPSHOT.jar"]
