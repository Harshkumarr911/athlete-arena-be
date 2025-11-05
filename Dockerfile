# Use OpenJDK 17 (LTS) as the base image
FROM eclipse-temurin:17-jdk-jammy AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY pom.xml mvnw ./
COPY .mvn .mvn

# ✅ Give execute permission to mvnw
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the project and build it
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port and run app
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
