FROM openjdk:17-jdk-slim

COPY gradle/wrapper gradle/wrapper

COPY gradlew .

COPY build.gradle .
COPY settings.gradle .
COPY springboot.p12 .
COPY src ./src

RUN ./gradlew bootJar

EXPOSE 3443
ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]
