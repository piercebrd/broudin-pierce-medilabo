FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY target/frontendservice-*.jar app.jar

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

