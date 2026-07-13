# ── Στάδιο 1: Build ──────────────────────────────────────────────
# Multi-stage build (§2.3.2): το Node.js toolchain χρειάζεται μόνο
# για το build, όχι για να σερβίρει τα static αρχεία.
FROM node:18-alpine AS build

WORKDIR /app

# Layer caching: αντιγραφή package.json πρώτα (§2.3.2)
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# ── Στάδιο 2: Runtime (σερβίρισμα static αρχείων) ────────────────
FROM nginx:alpine

# Αντιγραφή του παραγόμενου build στον φάκελο που σερβίρει το nginx
COPY --from=build /app/build /usr/share/nginx/html

# Ελαφριά ρύθμιση nginx μόνο για SPA routing (fallback σε index.html)
# Σημείωση: αυτό ΔΕΝ είναι το κύριο nginx του docker-compose (που κάνει
# TLS/SSL termination) — είναι εσωτερικό nginx μόνο για να σερβίρει το SPA.
RUN echo 'server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80 || exit 1

CMD ["nginx", "-g", "daemon off;"]
