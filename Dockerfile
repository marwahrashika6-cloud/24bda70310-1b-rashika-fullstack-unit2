# Multi-stage build for optimal image size and security
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw* ./
COPY src src

# Build application without running tests in container build
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Production runtime container
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as non-root user for enterprise container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
