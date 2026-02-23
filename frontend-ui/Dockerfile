# ETAPA 1: Construcción
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build --configuration=production

# ETAPA 2: Servidor Web Nginx
FROM nginx:alpine
# Copiamos la aplicación Angular
COPY --from=builder /app/dist/frontend-ui/browser /usr/share/nginx/html
# Copiamos el archivo de configuración del proxy inverso
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80