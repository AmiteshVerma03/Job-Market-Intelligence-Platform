# JobIntel — Job Market Intelligence Platform

A full-stack job market analytics platform with a Spring Boot backend and HTML/CSS/JS frontend.

---

## Project Structure

```
JobIntel-Platform/
├── backend/      Spring Boot REST API (Java 17, MySQL, JWT)
└── frontend/     Vanilla HTML/CSS/JS dashboard
```

---

## Backend Setup

### Prerequisites
- Java 17+
- MySQL running locally
- Maven (or use the included `./mvnw`)

### 1. Create the database
```sql
CREATE DATABASE job_analyzer;
CREATE USER 'user1'@'localhost' IDENTIFIED BY 'kiit';
GRANT ALL PRIVILEGES ON job_analyzer.* TO 'user1'@'localhost';
```

### 2. Set environment variables
```bash
# Windows (PowerShell)
$env:DB_URL="jdbc:mysql://localhost:3306/job_analyzer"
$env:DB_USERNAME="user1"
$env:DB_PASSWORD="kiit"
$env:JWT_SECRET="ThisIsMyVerySecretKeyThatIsLongEnoughForHS256Algorithm123"

# Mac/Linux
export DB_URL=jdbc:mysql://localhost:3306/job_analyzer
export DB_USERNAME=user1
export DB_PASSWORD=kiit
export JWT_SECRET=ThisIsMyVerySecretKeyThatIsLongEnoughForHS256Algorithm123
```

### 3. Run
```bash
cd backend
./mvnw spring-boot:run
```
Server starts on http://localhost:8080

---

## Frontend Setup

No build step needed.

1. Open `frontend/index.html` in your browser  
   *(use VS Code Live Server or any static file server)*
2. Register an account → Login → Explore the dashboard

> **Important:** The backend must be running on `localhost:8080` before opening the frontend.

---

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/auth/register` | Public | Register new user |
| POST | `/auth/login` | Public | Login, returns JWT |
| GET | `/jobs` | JWT | Paginated job list |
| GET | `/jobs/search/location?location=` | JWT | Search by location |
| GET | `/jobs/search/company?company=` | JWT | Search by company |
| GET | `/jobs/search/skill?skill=` | JWT | Search by skill |
| GET | `/analytics/top-skills?limit=` | JWT | Top skills ranking |
| GET | `/analytics/top-skills-by-location/{loc}` | JWT | Skills by location |
| GET | `/analytics/top-companies` | JWT | Companies by job count |
| GET | `/analytics/salary-by-skill?skill=` | JWT | Avg salary for skill |
| GET | `/analytics/average-salary-by-location` | JWT | Salary by location |
| POST | `/admin/run-scraper` | ADMIN | Trigger Indeed scraper |

---

## Frontend Pages

| Page | Description |
|------|-------------|
| Dashboard | KPIs + live charts for skills, companies, salary |
| Browse Jobs | Search by location / company / skill / all (paginated) |
| Top Skills | Ranked skills with animated bars, filter by location |
| Companies | Companies ranked by job posting count |
| Salary Intel | Avg salary by skill lookup + salary-by-location chart |
| Admin | Manual scraper trigger (ADMIN role only) |
