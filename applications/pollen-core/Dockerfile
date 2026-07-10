FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
RUN mvn -pl applications/pollen-core -am -DskipTests package
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/applications/pollen-core/target/pollen-core-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
