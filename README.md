# Chinook Web Application Security Project

## Prerequisiti

- Docker
- Docker Compose
- npm e Node.js
- java

## Come avviare il progetto

1. Clona la repository:

   ```bash
   git clone https://github.com/aspagnuolo1/chinook-security-project.git
   cd chinook-security-project/chinook
2. Costruisci e avvia il container
  
## **Avviare il progetto**

Una volta che hai configurato tutto, e con mvn clean e mnv install creato il .jar del backend, ogni volta che qualcuno clona il repository e avvia `docker-compose up --build`, Docker compila e avvia i vari servizi (backend e database) automaticamente.

Dopodiché, entra nella cartella /frontend e da terminale avvia l'installazione con `npm install` e poi potrai avviare il frontend con `npm run dev` (npm è richiesto per avviare il frontend)

Potrai poi andare su localhost:5174 per usare l'applicativo

## Conclusione

Se hai domande o hai bisogno di ulteriore aiuto, fammi sapere!
