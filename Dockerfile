# Use an Ubuntu base image
FROM ubuntu:20.04

# Install necessary packages
RUN apt-get update && apt-get install -y \
    openjdk-11-jdk \
    xorg \
    openbox \
    libxrender1 \
    libxtst6 \
    libxi6

# Add your jar file to the container
COPY /target/PIXL-POS-1.0-jar-with-dependencies.jar /usr/javafx-app/PIXL-POS-1.0-jar-with-dependencies.jar

# Set the working directory
WORKDIR /usr/javafx-app

# Run the JavaFX application
CMD ["java", "-jar", "PIXL-POS-1.0-jar-with-dependencies.jar"]
