# MyTable App

create a resturant reservation system for me a webapp

This project was built with [Lovable](https://lovable.dev).

## Build with Lovable

Continue developing this project in the [Lovable editor](https://lovable.dev/projects/83fd434a-8ee8-49b7-8208-e9890f4eb199).

- **Ship faster**: describe what you want to build and Lovable handles the code.
- **Stay in sync**: every change made in Lovable is committed straight to this repository.
- **Full ownership**: this code is yours. Push to `main` on GitHub and your changes sync back into Lovable, ready for your next prompt.

## Development

Prefer working locally? You need Node.js and npm — [install with nvm](https://github.com/nvm-sh/nvm#installing-and-updating).

```sh
git clone <this-repository-url>
cd <repository-name>
npm i
npm run dev
```

## Spring Boot backend

The reservation API lives in [`backend/`](./backend/). It uses Java 21, Spring Boot,
Spring Data JPA, and an H2 file database by default. A PostgreSQL connection can be
provided through the standard `SPRING_DATASOURCE_*` environment variables.

From the repository root, run:

```sh
cd backend
mvn spring-boot:run
```

The API is available at `http://localhost:8080/api/reservations`:

- `GET /api/reservations` lists reservations in creation order.
- `POST /api/reservations` creates a reservation.
- `DELETE /api/reservations/{id}` cancels a reservation.

Example request:

```json
{
  "name": "Elena Marchetti",
  "phone": "+1 415 555 0182",
  "date": "2026-10-10",
  "time": "19:30",
  "partySize": 2,
  "seating": "Window",
  "requests": "Window seat"
}
```

Set `APP_CORS_ALLOWED_ORIGIN` when the frontend is hosted somewhere other than
`http://localhost:5173`.

### Supabase PostgreSQL

The backend uses local H2 by default. To use the project's Supabase database,
copy [`backend/.env.example`](./backend/.env.example) to `backend/.env` and
replace the password with the database password from Supabase Dashboard →
Project Settings → Database. Do not commit `backend/.env`.

Alternatively, export the same variables in the shell before running the
backend. The supported variables are:

```text
SUPABASE_DB_URL
SUPABASE_DB_USERNAME
SUPABASE_DB_PASSWORD
```

Use the connection string shown by Supabase if the project's database host or
pooler region differs from the example. The JDBC URL must include
`sslmode=require`.

### Restaurant database schema

When the backend starts with `SPRING_JPA_HIBERNATE_DDL_AUTO=update`, Hibernate
creates or updates these Supabase tables:

- `roles` and `users`
- `categories` and `menu_items`
- `restaurant_tables` and `reservations`
- `restaurant_orders` and `order_items`
- `payments`
- `reviews`
- `notifications`

The relationships follow the supplied ERD: users can have roles, reservations
can reference users and restaurant tables, menu items belong to categories,
orders reference users and tables, order items reference orders and menu items,
payments reference orders, and reviews/notifications reference users.

The restaurant table API is available at:

- `GET /api/tables`
- `POST /api/tables`
- `PUT /api/tables/{id}`
- `DELETE /api/tables/{id}`

### Admin authentication

The admin dashboard at `/admin` requires an authenticated administrator. The
backend creates the configured admin account on startup if it does not already
exist, using BCrypt password hashing:

```dotenv
APP_ADMIN_EMAIL=admin@your-domain.example
APP_ADMIN_PASSWORD=replace-with-a-strong-admin-password
APP_SECURITY_JWT_SECRET=replace-with-a-base64-encoded-32-byte-secret
APP_SECURITY_JWT_EXPIRATION=PT8H
```

Set these values in `backend/.env` before using the dashboard. The development
fallback credentials are intentionally only for local setup and should be
replaced. `POST /api/auth/login` returns a signed token for users with the
`ADMIN` role. Admin tokens are required for table management and reservation
cancellation; public reservation listing and creation remain available to
guests.

Create a table with:

```json
{
  "tableNumber": "T-01",
  "capacity": 4,
  "status": "AVAILABLE"
}
```

### ERD resource APIs

The remaining schema resources expose the same validated JSON CRUD pattern
(`GET` collection, `POST`, `PUT /{id}`, and `DELETE /{id}`):

- `/api/roles` and `/api/users`
- `/api/categories` and `/api/menu-items`
- `/api/orders` and `/api/order-items`
- `/api/payments`
- `/api/reviews`
- `/api/notifications`

Relationship fields use UUIDs: users accept `roleId`, menu items accept
`categoryId`, orders accept `userId` and optional `tableId`, order items accept
`orderId` and `menuItemId`, payments accept `orderId`, and reviews and
notifications accept `userId` (reviews also accept `menuItemId`). All endpoints
validate request bodies and allow the configured frontend origin via
`APP_CORS_ALLOWED_ORIGIN`.
