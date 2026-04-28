# Build stage
FROM eclipse-temurin:25.0.2_10-jdk-jammy as builder
WORKDIR /app
# Copy project into container /app folder
COPY .. .
# Enter in module folder
WORKDIR /app/config-server-app
# Fix line endings and set execute permissions.
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
# package JAR (will be in /app/target/)
RUN ./mvnw package -DskipTests

# Run stage
FROM eclipse-temurin:25.0.2_10-jdk-jammy
WORKDIR /app
# Update packages and install curl
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
# Copy JAR from folder target/ that exists in builder container
COPY --from=builder /app/config-server-app/target/config-server-app-*.jar config-server-application.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar config-server-application.jar"]