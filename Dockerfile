# Multi-stage build for Java Spring Boot application
FROM openjdk:21-jdk-slim as build

# Set working directory
WORKDIR /app

# Copy Maven/Gradle files first (for better caching)
COPY pom.xml* ./
COPY mvnw* ./
COPY .mvn/ .mvn/

# Copy Gradle files if using Gradle
COPY build.gradle* ./
COPY gradlew* ./
COPY gradle/ gradle/

# Download dependencies
RUN if [ -f "mvnw" ]; then \
      ./mvnw dependency:go-offline; \
    elif [ -f "gradlew" ]; then \
      ./gradlew dependencies; \
    fi

# Copy source code
COPY src/ src/

# Build the application with layered JAR enabled
RUN if [ -f "mvnw" ]; then \
      ./mvnw clean package -DskipTests -Dspring-boot.build-image.builder=paketobuildpacks/builder:base; \
    elif [ -f "gradlew" ]; then \
      ./gradlew bootJar -x test; \
    else \
      mvn clean package -DskipTests; \
    fi

# Runtime stage
FROM openjdk:21-jre-slim

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Extract layers for better caching (Spring Boot 2.3+)
RUN java -Djarmode=layertools -jar app.jar extract

# Change ownership to non-root user
RUN chown -R appuser:appuser /app
USER appuser

# Expose port
EXPOSE 8080

# Health check for Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with Spring Boot optimizations
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
