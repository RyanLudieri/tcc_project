# 🎓 projeto-tcc: Web Platform for Process Modeling & Discrete-Event Simulation

This project is a web platform developed for my undergraduate thesis (TCC).  
It helps users **model software processes**, configure **simulation parameters**, and run **discrete-event simulation (DES)** experiments to obtain performance metrics and visual insights.

At a high level, the platform follows this workflow:

1. **Process modeling** (create a structured process with phases/activities/tasks/roles)
2. **Simulation configuration** (define quantitative parameters needed for simulation)
3. **Model generation & execution**  
   The process model is mapped to **XACDML** (eXtensible Activity Cycle Diagram Markup Language), then transformed into an executable **Java simulation program** (via **XSLT**).  
   The simulation is executed and metrics are collected (e.g., duration, throughput, queues/bottlenecks).

---

## 🧱 Monorepo Structure

- `projeto-tcc-api/` → **Backend API** (Java 21 + Spring Boot) — runs on **port 8080**
- `projeto-tcc-app/` → **Frontend** (React + Vite) — served with **Nginx** on **port 5173**
- `docker-compose.yml` → Orchestrates **PostgreSQL + Backend + Frontend**
- `.env` → Database credentials used by Docker Compose (**you create this locally**)

---

## 🛠️ Technologies Used

### Backend
- Java 21
- Spring Boot (Web, Data JPA, Actuator)
- PostgreSQL
- Lombok
- Apache Commons Math3
- Maven (via Maven Wrapper `./mvnw`)

### Frontend
- React + Vite
- TailwindCSS
- Recharts
- Framer Motion
- Lucide React Icons

### Infrastructure
- Docker + Docker Compose
- Nginx (serving the production frontend build)

---

## 🚀 Running this project with Docker

1) **Open Docker Desktop** and make sure it’s running.

2) **Open a terminal** and go to the project root (the folder that contains `docker-compose.yml`):
```bash
cd ~/tcc_project
```

3) **Create the `.env` file** (database credentials):
```bash
cat > .env <<'EOF'
POSTGRES_DB=tcc
POSTGRES_USER=meu_usuario
POSTGRES_PASSWORD=minha_senha
EOF
```

4) **Start the full application** (PostgreSQL + backend + frontend):
```bash
docker compose up -d --build
```

5) **Open in your browser:**
- `http://localhost:5173`

6) **To stop everything:**
```bash
docker compose down
```

