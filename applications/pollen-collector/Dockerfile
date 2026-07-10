FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
RUN mvn -pl applications/pollen-collector -am -DskipTests package
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/applications/pollen-collector/target/pollen-collector-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
