# Use the official image as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Package the application
RUN ./mvnw package

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]