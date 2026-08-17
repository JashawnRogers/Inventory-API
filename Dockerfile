FROM eclipse-temurin:26-jre

COPY target/*.jar inventory-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "inventory-api.jar"]