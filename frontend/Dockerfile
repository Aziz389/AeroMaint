# ---------- Stage 1: build the Angular app with Node ----------
FROM node:24-alpine AS build
WORKDIR /app

# Install dependencies first (cached until package.json / package-lock.json change).
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npx ng build --configuration production

# ---------- Stage 2: serve the static files with Nginx ----------
# The final image contains only Nginx and the compiled HTML/JS/CSS: no Node, no source code.
FROM nginx:1.29-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/aeromaint-frontend/browser /usr/share/nginx/html

EXPOSE 80
