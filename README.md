# Booking REST API

Spring Boot booking API with JWT authentication, roles ADMIN/USER, reservations, filtering, pagination, and MySQL/Postgres persistence.

Default credentials (seeded):
- admin / adminpass (ADMIN)
- user / userpass (USER)

Run:

```bash
mvn spring-boot:run
```

Environment variables:
- `JDBC_DATABASE_URL` (default jdbc:mysql://localhost:3306/booking)
- `JDBC_DATABASE_USERNAME`
- `JDBC_DATABASE_PASSWORD`
- `APP_JWT_SECRET` (use a long secret for production)

Swagger UI: http://localhost:8080/swagger-ui.html
