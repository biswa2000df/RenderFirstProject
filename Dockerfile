
# Stage 1: Build the Spring Boot app
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-alpine

# Install required packages
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    && ln -sf /usr/bin/chromium-browser /usr/bin/chromium \
    && ln -sf /usr/bin/chromedriver /usr/bin/chromedriver

WORKDIR /app
COPY --from=build /app/target/Heroku_Application.jar /app/app.jar
COPY BiswajitJARSeleniumDockerIsworkingorNot.jar /app/BiswajitJARSeleniumDockerIsworkingorNot.jar

# Set the DISPLAY environment variable for Chrome
ENV DISPLAY=:0

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
