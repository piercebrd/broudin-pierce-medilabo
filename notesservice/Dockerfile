# ---- build ----
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
# show what's built (handy for debugging)
RUN mvn -q -DskipTests package && ls -la target

# ---- run ----
FROM eclipse-temurin:21-jre
WORKDIR /app
# copy *whatever* jar was produced
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

