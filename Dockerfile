FROM gradle:jdk17

WORKDIR /tmp

COPY ./build.gradle ./settings.gradle ./gradlew ./
COPY ./gradle ./gradle
COPY ./gradle/wrapper ./gradle/wrapper

COPY ./src ./src
COPY ./springboot.p12 .

RUN gradle bootJar

EXPOSE 3443

ENTRYPOINT ["java", "-jar", "/tmp/build/libs/app.jar"]

#FROM openjdk:17-jdk-slim
#
#COPY gradle/wrapper gradle/wrapper
#
#COPY gradlew .
#
#COPY build.gradle .
#COPY settings.gradle .
#COPY springboot.p12 .
#COPY src ./src
#
#RUN sh ./gradlew bootJar
#
#EXPOSE 3443
#ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]
