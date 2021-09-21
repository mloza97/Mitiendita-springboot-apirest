FROM adoptopenjdk/openjdk11:ubi
COPY "./target/srping-boot-backend-apirest-mitiendita-0.0.1-SNAPSHOT.jar" "app.jar"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]