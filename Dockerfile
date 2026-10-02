FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
ARG MODULE
RUN mvn -q -pl ${MODULE} -am package -DskipTests

FROM eclipse-temurin:17-jre
ARG MODULE
RUN useradd --system app
USER app
COPY --from=build /app/${MODULE}/target/${MODULE}-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
