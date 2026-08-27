# Smart Clinic Management System

Full-stack clinic portal for admins, doctors, and patients — Spring Boot backend, HTML/CSS/JS frontend, MySQL + MongoDB, Docker, and GitHub Actions CI.

## Stack

- **Backend:** Java 17, Spring Boot 3.4, JPA, JWT
- **Frontend:** HTML, CSS, JavaScript (Thymeleaf dashboards for admin/doctor)
- **Databases:** MySQL (relational), MongoDB (prescriptions)
- **DevOps:** Docker multi-stage build, GitHub Actions Maven CI

## Quick start

```bash
# Start databases + app (optional)
docker compose up --build

# Or run locally with MySQL/Mongo running
cd app
./mvnw spring-boot:run
```

Default seeded credentials (via `DataInitializer`):

| Role    | Username / Email                 | Password    |
|---------|----------------------------------|-------------|
| Admin   | `admin`                          | `admin123`  |
| Doctor  | `alice.carter@smartcare.com`     | `doctor123` |
| Patient | `john.smith@email.com`           | `patient123`|

## Key links (after push)

- User stories (Issues): see repository **Issues** tab
- Schema design: [`schema-design.md`](./schema-design.md)
- Architecture: [`schema-architecture.md`](./schema-architecture.md)
- SQL sample + procedures: [`app/src/main/resources/sql/sample_data_and_procedures.sql`](./app/src/main/resources/sql/sample_data_and_procedures.sql)
- Dockerfile: [`app/Dockerfile`](./app/Dockerfile)
- CI workflow: [`.github/workflows/java-ci.yml`](./.github/workflows/java-ci.yml)

## Module checklist

1. Architecture + user stories  
2. Schema design + JPA/Mongo models  
3. Sample data + stored procedures  
4. Frontend + MVC login dashboards  
5. REST APIs + Docker + GitHub Actions  
