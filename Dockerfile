## Multi-stage Dockerfile optimized for caching and smaller runtime image
## Build stage: cache dependencies separately to leverage Docker layer caching
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy only the files needed to download dependencies first (cache layer)
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x ./mvnw && sed -i 's/\r$//' ./mvnw

# Download dependencies into the image's local repo layer so they are cached
RUN ./mvnw -B -ntp -DskipTests dependency:go-offline

# Copy the source and build the application
COPY src ./src
RUN ./mvnw -B -ntp -DskipTests package

## Runtime stage: use a slim JRE and run as non-root
FROM eclipse-temurin:17-jre-jammy

# create a dedicated system user and group for running the app
RUN addgroup --system app && adduser --system --ingroup app app

# application directory and persistent data directory
WORKDIR /app
RUN mkdir -p /app/data

# Copy fat jar from build stage
COPY --from=build /workspace/target/*.jar app.jar

# ensure app user owns the application files and data directory
RUN chown -R app:app /app && chmod -R 0755 /app

# set default runtime profile to production (can be overridden)
ENV SPRING_PROFILES_ACTIVE=prod

# switch to non-root user for runtime
USER app
EXPOSE 8080

# JVM tuned for container environments: respect container memory and limit heap to a percentage
ENTRYPOINT ["sh", "-c", "exec java -XX:MaxRAMPercentage=75.0 -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app/app.jar"]
