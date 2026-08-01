FROM maven:3.9.12-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -ntp -pl payment-service -am -DskipTests package

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY --from=build /workspace/payment-service/target/payment-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/app.jar"]
