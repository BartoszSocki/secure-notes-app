FROM gradle:jdk17 AS build

WORKDIR /tmp

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

COPY src ./src

RUN ./gradlew bootJar -x test

FROM openjdk:19
WORKDIR /app

COPY springboot.p12 .
COPY --from=build /tmp/build/libs/app.jar app.jar

EXPOSE 3443

ENTRYPOINT ["java", "-jar", "app.jar"]

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
