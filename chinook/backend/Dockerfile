# Usa un'immagine base di Java
FROM openjdk:21-jdk-slim

# Copia il file JAR dal progetto (compilato in fase di build)
COPY ./target/backend-1.0-SNAPSHOT.jar /usr/app/backend-1.0-SNAPSHOT.jar
COPY target/*.jar /usr/app/

# Imposta il working directory
WORKDIR /usr/app

EXPOSE 8080

# Comando per avviare l'applicazione
CMD ["java", "-jar", "backend-1.0-SNAPSHOT.jar"]
