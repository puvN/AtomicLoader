FROM openjdk:17-jdk

WORKDIR /app

COPY target/my-app.jar /app

ENTRYPOINT ["java", "-jar", "/app/my-app.jar"]