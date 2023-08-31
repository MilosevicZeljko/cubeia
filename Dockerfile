FROM openjdk:17-jdk-slim-buster

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Build the application
RUN ./mvnw package -DfinalName=app.jar -DskipTests

# Run the application
CMD ["java", "-jar", "target/cubeia.jar"]
