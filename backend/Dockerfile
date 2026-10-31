
# ==========================================
# STAGE 1: BUILD APPLICATION
# ==========================================

FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Maven Wrapper and dependency configuration first
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven Wrapper executable
RUN chmod +x mvnw

# Download dependencies before copying source code.
# This helps Docker reuse the dependency layer.
RUN ./mvnw -B dependency:go-offline

# Copy application source code
COPY src ./src

# Build executable Spring Boot JAR
RUN ./mvnw -B clean package -DskipTests


# ==========================================
# STAGE 2: RUNTIME APPLICATION
# ==========================================

FROM eclipse-temurin:25-jre-ubi10-minimal AS runtime

WORKDIR /app

RUN useradd --system --uid 10001 --no-create-home academicapp

# Run the application as a non-root user
COPY --from=builder --chown=academicapp:academicapp \
    /app/target/academic-survival-system.jar \
    app.jar

USER academicapp

# Spring Boot application port
EXPOSE 8081

# Start Spring Boot
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=65.0", "-jar", "app.jar"]
