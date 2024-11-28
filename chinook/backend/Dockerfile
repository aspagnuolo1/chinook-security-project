# Usa un'immagine base di Java
FROM openjdk:21-jre-slim

# Copia il file JAR dal progetto (compilato in fase di build)
COPY target/my-backend.jar /usr/app/my-backend.jar

# Imposta il working directory
WORKDIR /usr/app

# Comando per avviare l'applicazione
CMD ["java", "-jar", "my-backend.jar"]
