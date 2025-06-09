# Use an official Maven image to build the Spring Boot app
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn package -DskipTests

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/employee_management-0.0.1-SNAPSHOT.jar .

# Expose port 8181 (make sure your app runs on this port)
EXPOSE 8181

# Specify the command to run
ENTRYPOINT ["java", "-jar", "employee_management-0.0.1-SNAPSHOT.jar"]


