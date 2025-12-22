# Stage 1: Build
FROM maven:3.9.6-amazoncorretto-21 AS build

WORKDIR /opt/app

COPY ./src src
COPY pom.xml .

# Build the JAR
RUN mvn -f pom.xml clean install -DskipTests

# Stage 2: Run
FROM azul/zulu-openjdk-alpine:21

# Copy the built jar from the build stage
COPY --from=build /opt/app/target/koyeb_template*.jar koyeb_template.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "koyeb_template.jar"]
