# QoS-Aware Multi-Cloud Autoscaling & Monitoring Platform

Πλατφόρμα διαχείρισης εφαρμογών σε Kubernetes clusters πολλαπλών νεφών
(AWS EKS / Azure AKS) με αυτόματη κλιμάκωση βάσει προσαρμοσμένων κανόνων
ποιότητας υπηρεσίας (QoS) και ενοποιημένη παρακολούθηση μέσω Prometheus/Grafana.

Διπλωματική εργασία — ΠΜΣ Πληροφοριακά και Επικοινωνιακά Συστήματα,
Πανεπιστήμιο Αιγαίου, 2026.
**Φοιτητής:** Κοσμόπουλος Ανδρέας · **Επόπτης:** Κυριάκος Κρητικός

## Τεχνολογίες

| Επίπεδο | Τεχνολογίες |
|---|---|
| Frontend | React.js 18 + TypeScript 5 + Tailwind CSS + Recharts + React Query |
| Backend | Spring Boot 3.2 (Java 17) |
| Security | Spring Security 6 + JWT (jjwt) + BCrypt |
| DB | PostgreSQL 15 + Spring Data JPA + Flyway |
| Monitoring | Prometheus + Grafana + Alertmanager |
| IaC | Terraform + Helm + kubectl |
| Containerization | Docker + Docker Compose + Nginx |
| Testing | JUnit 5 + Mockito + Testcontainers + GitHub Actions |

## Αρχιτεκτονική

```
Browser → Nginx (reverse proxy) → React SPA (frontend)
                                 → Spring Boot REST API (backend)
                                        ↓
                              PostgreSQL · Terraform · Prometheus
                                        ↓
                            AWS EKS  /  Azure AKS  (managed apps)
```

## Γρήγορη Εκκίνηση (Docker Compose)

```bash
git clone https://github.com/<username>/qos-multicloud-platform.git
cd qos-multicloud-platform
cp .env.example .env      # συμπλήρωσε τα δικά σου credentials
docker compose up --build
```

Η πλατφόρμα είναι διαθέσιμη στο **http://localhost**
Backend API docs (Swagger): **http://localhost/api/swagger-ui.html**

## Τοπική Ανάπτυξη (χωρίς Docker)

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

## Δοκιμή σε Πραγματικό Kubernetes Cluster (Minikube)

Δείγματα YAML για μια demo εφαρμογή βρίσκονται στο `k8s-samples/`:

```bash
minikube start --driver=docker --cpus=4 --memory=6144
kubectl apply -f k8s-samples/deployment.yaml
kubectl apply -f k8s-samples/hpa.yaml
```

## Δομή Repository

```
├── backend/          Spring Boot REST API
├── frontend/          React.js SPA
├── nginx/              Reverse proxy config
├── k8s-samples/    Δείγματα Kubernetes YAML
├── .github/workflows/  CI/CD pipeline
└── docker-compose.yml
```

## Τεκμηρίωση

Πλήρης τεκμηρίωση σχεδίασης, αρχιτεκτονικής (UML/ER διαγράμματα) και
αξιολόγησης απόδοσης διαθέσιμη στη διπλωματική εργασία.

## Άδεια Χρήσης

Ακαδημαϊκό έργο
