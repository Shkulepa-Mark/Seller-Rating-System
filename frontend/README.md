# SellerRank — frontend

React client for the Rating System backend: an independent rating of in-game item sellers
built on moderated comments.

## Features

- Seller registration with email confirmation, login, password reset
- Top sellers with filtering by game and rating range, pagination, filters synced with the URL
- Seller profile with moderated comments (create, edit, delete)
- Suggesting a new seller together with a comment
- Game objects: list for everyone, create / edit / delete for sellers
- Moderation panel for administrators: pending sellers and comments

## Stack

React 18, React Router 6, Vite 5, ESLint 9. No UI libraries, styling is plain CSS with custom properties.

## Getting started

Start the backend first (`docker compose up` or Tomcat, see the backend README), then:

```bash
cp .env.example .env
npm install
npm run dev
```

The dev server runs on `http://localhost:5173` and proxies `/api/*` to
`http://localhost:8080/seller-rating-system/*`, so no CORS configuration is needed during development.

| Script            | Description                  |
| ----------------- | ---------------------------- |
| `npm run dev`     | Start the dev server         |
| `npm run build`   | Production build into `dist` |
| `npm run preview` | Preview the production build |
| `npm run lint`    | Run ESLint                   |

### Production

`VITE_API_URL` defaults to `/api`. For a deployment where the frontend and the backend live on different
origins, set it to the full backend URL (for example `https://example.com/seller-rating-system`) and enable
CORS on the backend for the frontend origin.

## Project structure

```
src/
  api/          HTTP client, endpoint map and resource modules
  components/   Reusable UI building blocks
  constants/    Routes, roles, rating values
  context/      Authentication state
  hooks/        useFetch, useSubmit, useGames
  pages/        Route-level components
  styles/       Global stylesheet and design tokens
  utils/        Formatting and storage helpers
```

## API used

| Purpose                     | Endpoint                                                                 |
| --------------------------- | ------------------------------------------------------------------------ |
| Login                       | `POST /auth/login` → `{ email, token }`                                  |
| Registration                | `POST /auth/register`                                                    |
| Email confirmation          | `GET /auth/confirm?token=`                                               |
| Password reset              | `POST /auth/forgot_password`, `GET /auth/check_code`, `POST /auth/reset` |
| Games                       | `GET /games`                                                             |
| Sellers (paged, filterable) | `GET /sellers?gameName=&minRating=&maxRating=&page=&size=&sort=rating,desc` |
| Seller profile              | `GET /sellers/:id`                                                       |
| Seller + comment request    | `POST /sellers/requests`                                                 |
| Comments                    | `GET/POST /sellers/:id/comments`, `PUT/DELETE /sellers/:id/comments/:commentId` |
| Game objects                | `GET/POST /game-objects`, `PUT/DELETE /game-objects/:id`                 |
| Moderation                  | `GET /admin/users/pending`, `POST /admin/users/:id/approve\|decline`, `GET /admin/comments/pending`, `POST /admin/comments/:id/approve\|decline` |

Authenticated requests send `Authorization: Bearer <token>`.

## Notes on the current backend contract

- The login response contains only the email and the token, so the client detects the role by calling
  an admin endpoint once after login: `200` means administrator, `403` means seller. Adding `role` to the
  login response (or a `GET /auth/me` endpoint) would remove this extra request.
- Comments and game objects expose only the author's display name, not an id. The UI therefore shows the
  edit and delete controls to any authenticated user (comments) or seller (objects) and relies on the
  backend, which rejects actions on other people's records.
- The confirmation link in the registration email points to the API (`.../auth/confirm?token=`). To land on the
  frontend page instead, point the link to `<frontend-origin>/confirm?token=`.
- Login errors for unconfirmed or unapproved accounts come back as "Invalid email or password".
