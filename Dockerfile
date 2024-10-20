FROM debian:bullseye-slim

# Update package lists and install wget
RUN apt update && apt install -y wget openjdk-17-jdk

RUN wget https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_107.0.5304.121-1_amd64.deb

RUN dpkg -i ./google-chrome-stable_107.0.5304.121-1_amd64.deb ; apt install -f -y ; apt install --fix-missing


EXPOSE 8090

RUN apt-get update && apt-get install -y \
    x11vnc \
    xvfb

RUN mkdir ~/.vnc
RUN x11vnc -storepasswd 1234 ~/.vnc/passwd

# Start Xvfb in the background
CMD ["Xvfb", ":1", "-screen", "0", "1024x768x24", "&"]

# Start X11vnc in the foreground
CMD ["x11vnc", "-forever", "-usepw", "-create"]



# Stage 1: Build the Spring Boot app
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-alpine

WORKDIR /app
COPY --from=build /app/target/Heroku_Application.jar /app/app.jar
COPY BiswajitJARSeleniumDockerIsworkingorNot.jar /app/BiswajitJARSeleniumDockerIsworkingorNot.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
