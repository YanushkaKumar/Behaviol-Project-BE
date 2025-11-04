# Multi-stage build for Spring Boot application

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 5050

# Set JVM options (non-sensitive defaults)
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
# Environment variables will be passed at runtime
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]