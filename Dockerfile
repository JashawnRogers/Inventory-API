# Build stage: uses the full JDK because Maven needs a compiler to build the app.
FROM eclipse-temurin:21-jdk AS build

# Sets the working directory inside the container for the build stage.
WORKDIR /app

# Copies Maven wrapper files first so Docker can cache dependency downloads.
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Downloads project dependencies before copying source code.
# This makes rebuilds faster when only source files change.
RUN ./mvnw dependency:go-offline

# Copies the application source code into the build container.
COPY src src

# Builds the Spring Boot jar.
RUN ./mvnw clean package -DskipTests

# Runtime stage: uses the smaller JRE image because the app only needs Java to run.
FROM eclipse-temurin:21-jre

# Sets the working directory inside the final runtime container.
WORKDIR /app

# Add environment variable
ENV SPRING_PROFILES_ACTIVE=prod

# Copies the built jar from the build stage into the runtime image.
COPY --from=build /app/target/inventory-api.jar inventory-api.jar

# Documents that the app listens on port 8080.
EXPOSE 8080

# Starts the Spring Boot application when the container runs.
ENTRYPOINT ["java", "-jar", "inventory-api.jar"]