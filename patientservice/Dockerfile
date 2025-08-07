# Use official JDK base image
FROM eclipse-temurin:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file
COPY target/patientservice-*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

