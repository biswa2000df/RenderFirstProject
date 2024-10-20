# Stage 1: Build the Spring Boot app
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-alpine

# Install necessary tools and Chrome
RUN apt-get update && apt-get install -y \
    wget \
    openjdk-17-jdk \
    xvfb \
    && wget https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_107.0.5304.121-1_amd64.deb \
    && dpkg -i google-chrome-stable_107.0.5304.121-1_amd64.deb || apt-get install -f -y \
    && rm google-chrome-stable_107.0.5304.121-1_amd64.deb

# Set display port
ENV DISPLAY=:99

WORKDIR /app
COPY --from=build /app/target/Heroku_Application.jar /app/app.jar
COPY BiswajitJARSeleniumDockerIsworkingorNot.jar /app/BiswajitJARSeleniumDockerIsworkingorNot.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
