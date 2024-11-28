# Usa un'immagine base di Node.js
FROM node:18

# Imposta la directory di lavoro
WORKDIR /app

# Copia il package.json e installa le dipendenze
COPY package.json package-lock.json ./
RUN npm install

# Copia il resto del codice sorgente
COPY . .

# Compila il progetto React
RUN npm run build

# Usa un server HTTP per servire i file
RUN npm install -g serve

EXPOSE 5173

# Esegui il frontend
CMD ["serve", "-s", "build", "-l", "5173"]
