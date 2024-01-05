FROM eclipse-temurin:17
VOLUME /tmp
COPY build/libs/*.jar app.jar
COPY src/main/resources/springboot.p12 springboot.p12
ENTRYPOINT ["java","-jar","/app.jar"]
