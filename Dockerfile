# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-alpine AS build
LABEL authors="ibotirama"

# Set the working directory
WORKDIR /app

# Copy the Maven build files
COPY pom.xml .
COPY src ./src
COPY .mvn/ .mvn
COPY mvnw .

# Make the Maven wrapper executable
# Package the application
RUN chmod +x ./mvnw \
&& ./mvnw clean package -DskipTests

# Second stage: minimal runtime image
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the packaged jar from the build stage
COPY --from=build /app/target/se-challenge-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
