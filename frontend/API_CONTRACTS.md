# fin API contracts

This inventory reflects the controllers under `backend/src/main/java/ai/fin/controller`.
The frontend API base path defaults to `/api/v1`. Authenticated endpoints require
`Authorization: Bearer <accessToken>`.

## Common response formats

Successful JSON endpoints return:

```json
{
  "success": true,
  "status": 200,
  "code": "OK",
  "message": "Request completed",
  "data": {},
  "meta": {
    "timestamp": "2026-10-06T02:00:00Z",
    "path": "/api/v1/example",
    "requestId": "request-id"
  }
}
```

Error endpoints return:

```json
{
  "success": false,
  "status": 400,
  "code": "VALIDATION_FAILED",
  "message": "One or more request fields are invalid.",
  "errors": [
    {
      "field": "email",
      "rejectedValue": "invalid",
      "reason": "Must be a valid email address"
    }
  ],
  "meta": {
    "timestamp": "2026-10-06T02:00:00Z",
    "path": "/api/v1/example",
    "requestId": "request-id"
  }
}
```

Paginated `data` is `{ "items": [], "pagination": { "page", "size", "totalElements", "totalPages", "first", "last", "hasNext", "hasPrevious" } }`.
Endpoints documented as returning `Void` use the success envelope with `"data": null`,
except `POST /users/logout`, which responds with HTTP 204 and no response body.
CSV export is the exception: it returns a `text/csv` attachment, not JSON.

## Authentication and account

| Method and path                                             | Request                                                                      | Success `data`       |
| ----------------------------------------------------------- | ---------------------------------------------------------------------------- | -------------------- |
| `POST /auth/register` (`/auth/signup` alias)                | `{ "fullName": "Jane Doe", "email": "jane@example.com", "password": "..." }` | `AuthResponse`       |
| `POST /auth/login`                                          | `{ "email": "jane@example.com", "password": "..." }`                         | `AuthResponse`       |
| `POST /auth/refresh-token` (`/auth/refresh` alias)          | `{ "refreshToken": "..." }`                                                  | `TokenResponse`      |
| `POST /auth/send-otp` (`/auth/send-verification` alias)     | `{ "email": "jane@example.com" }`                                            | `null`               |
| `POST /auth/resend-otp` (`/auth/resend-verification` alias) | `{ "email": "jane@example.com" }`                                            | `null`               |
| `POST /auth/verify-otp` (`/auth/verify-email` alias)        | `{ "email": "jane@example.com", "otp": "123456" }`                           | `null`               |
| `POST /users/logout`                                        | `{ "refreshToken": "..." }`                                                  | No body (204)        |
| `GET /users/me`                                             | —                                                                            | `UserProfileDetails` |
| `PATCH /users/me`                                           | `{ "fullName": "Jane Doe" }`                                                 | `UserProfileDetails` |
| `DELETE /users/me`                                          | `{ "password": "..." }`                                                      | `null`               |

`AuthResponse` is `{ "user": { "id", "role", "status" }, "token": { "accessToken", "refreshToken", "tokenType", "expiresInMillis" } }`.
`UserProfileDetails` is `{ "id", "fullName", "email", "status" }`. The account status
values include `INACTIVE`, `PENDING_VERIFICATION`, `ACTIVE`, and `DELETED`.

## Transactions, categories, and payment modes

| Method and path                | Request                                                                                                                      | Success `data`                  |
| ------------------------------ | ---------------------------------------------------------------------------------------------------------------------------- | ------------------------------- |
| `GET /transactions`            | Query: optional `type`, `categoryId`, `dateFrom`, `dateTo`, `page` (0), `size` (20), `sortBy` (`DATE`), `direction` (`DESC`) | Paginated `TransactionResponse` |
| `GET /transactions/{id}`       | —                                                                                                                            | `TransactionResponse`           |
| `POST /transactions`           | `{ "amount", "type", "date", "note", "categoryId", "paymentModeId" }`                                                        | `TransactionResponse` (201)     |
| `PUT /transactions/{id}`       | `{ "amount", "type", "date", "note", "categoryId", "paymentModeId" }`; amount is required, other update fields may be null   | `TransactionResponse`           |
| `DELETE /transactions/{id}`    | —                                                                                                                            | `null`                          |
| `GET /transactions/export/csv` | —                                                                                                                            | UTF-8 CSV download              |
| `GET /categories`              | Optional query `type` (`INCOME` or `EXPENSE`)                                                                                | `CategoryResponse[]`            |
| `POST /categories`             | `{ "name", "categoryType" }`                                                                                                 | `CategoryResponse` (201)        |
| `PUT /categories/{id}`         | `{ "name", "categoryType" }`                                                                                                 | `CategoryResponse`              |
| `DELETE /categories/{id}`      | —                                                                                                                            | `null`                          |
| `GET /payment-modes`           | —                                                                                                                            | `PaymentModeResponse[]`         |
| `POST /payment-modes`          | `{ "name" }`                                                                                                                 | `PaymentModeResponse` (201)     |
| `PUT /payment-modes/{id}`      | `{ "name" }`                                                                                                                 | `PaymentModeResponse`           |
| `DELETE /payment-modes/{id}`   | —                                                                                                                            | `null`                          |

`TransactionResponse` fields: `id`, `amount`, `type`, `date`, `note`, `categoryId`,
`categoryName`, `paymentModeId`, `paymentModeName`, `embeddingId`.
`CategoryResponse` fields: `id`, `name`, `categoryType`, `isDefault`.
`PaymentModeResponse` fields: `id`, `name`, `isDefault`.
Amounts are JSON numbers; dates use ISO date strings (`YYYY-MM-DD`), and budget
months use `YYYY-MM`.

## Budgets, savings, and recurring transactions

| Method and path                        | Request                                                                                                     | Success `data`                       |
| -------------------------------------- | ----------------------------------------------------------------------------------------------------------- | ------------------------------------ |
| `POST /budgets`                        | `{ "categoryId", "month": "YYYY-MM", "limitAmount", "alertThreshold" }` (`alertThreshold` defaults to 80)   | `BudgetResponse` (201)               |
| `GET /budgets`                         | Required query `month=YYYY-MM`                                                                              | `BudgetResponse[]`                   |
| `GET /budgets/{id}/status`             | —                                                                                                           | `BudgetStatusResponse`               |
| `DELETE /budgets/{id}`                 | —                                                                                                           | `null`                               |
| `POST /savings-goals`                  | `{ "title", "targetAmount", "deadline": "YYYY-MM-DD", "note" }`                                             | `GoalProgressResponse` (201)         |
| `GET /savings-goals`                   | —                                                                                                           | `GoalProgressResponse[]`             |
| `PATCH /savings-goals/{id}/contribute` | `{ "amount" }`                                                                                              | `GoalProgressResponse`               |
| `GET /savings-goals/{id}/progress`     | —                                                                                                           | `GoalProgressResponse`               |
| `DELETE /savings-goals/{id}`           | —                                                                                                           | `null`                               |
| `POST /recurring-transactions`         | `{ "title", "amount", "type", "frequency", "startDate", "endDate", "note", "categoryId", "paymentModeId" }` | `RecurringTransactionResponse` (201) |
| `GET /recurring-transactions`          | —                                                                                                           | `RecurringTransactionResponse[]`     |
| `GET /recurring-transactions/{id}`     | —                                                                                                           | `RecurringTransactionResponse`       |
| `PATCH /recurring-transactions/{id}`   | Partial `{ "title", "amount", "frequency", "endDate", "note", "categoryId", "paymentModeId" }`              | `RecurringTransactionResponse`       |
| `DELETE /recurring-transactions/{id}`  | —                                                                                                           | `null` (deactivates the rule)        |
| `GET /recurring-transactions/forecast` | Query `days` (1–365; default 30)                                                                            | `ForecastSummaryResponse`            |

`BudgetResponse`: `id`, `categoryName`, `categoryId`, `month`, `limitAmount`,
`alertThreshold`. `BudgetStatusResponse`: `budgetId`, `categoryName`, `month`,
`limitAmount`, `spentAmount`, `remainingAmount`, `usagePercentage`,
`thresholdBreached`, `limitBreached`. `GoalProgressResponse`: `id`, `title`,
`targetAmount`, `savedAmount`, `remainingAmount`, `progressPercentage`,
`daysRemaining`, `status`, `deadline`. Frequencies are `DAILY`, `WEEKLY`, `MONTHLY`,
`YEARLY`. `RecurringTransactionResponse`: `id`, `title`, `amount`, `type`,
`frequency`, `startDate`, `endDate`, `nextExecutionDate`, `isActive`, `note`,
`categoryName`, `paymentModeName`, `createdAt`. `ForecastSummaryResponse`:
`forecastDays`, `projectedIncome`, `projectedExpense`, `projectedNetBalance`, and
`entries`; each entry has `projectedDate`, `title`, `amount`, `type`, and
`categoryName`.

## Dashboard and chat

| Method and path                           | Request                                                       | Success `data`              |
| ----------------------------------------- | ------------------------------------------------------------- | --------------------------- |
| `GET /dashboard`                          | Optional query `month=YYYY-MM`; defaults to the current month | `DashboardResponse`         |
| `GET /chat/sessions`                      | —                                                             | `ChatSessionResponse[]`     |
| `POST /chat/sessions`                     | Optional `{ "title" }` (the body itself may be omitted)       | `ChatSessionResponse` (201) |
| `DELETE /chat/sessions/{sessionId}`       | —                                                             | `null`                      |
| `GET /chat/sessions/{sessionId}/messages` | —                                                             | `ChatMessageResponse[]`     |
| `POST /chat/sessions/{sessionId}/query`   | `{ "question" }`                                              | `{ "answer" }`              |

`DashboardResponse` fields: `month`, `summary` (`totalIncome`, `totalExpense`,
`netBalance`), `expenseByCategory` and `incomeByCategory` (each item has
`categoryName`, `amount`, `percentage`), `dailyTrend` (each item has `date`,
`income`, `expense`), and `topExpenses` (each item has `id`, `amount`,
`categoryName`, `note`, `date`). Chat sessions have `id`, `title`, `createdAt`,
`updatedAt`; chat messages have `id`, `role`, `content`, `createdAt`. Message roles
are `USER` and `ASSISTANT`.

## Notifications and push device tokens

| Method and path                   | Request                                                                            | Success `data`                   |
| --------------------------------- | ---------------------------------------------------------------------------------- | -------------------------------- |
| `GET /notifications`              | Optional query `unreadOnly`, `type`, `page` (0), `size` (20), `direction` (`DESC`) | Paginated `NotificationResponse` |
| `GET /notifications/unread-count` | —                                                                                  | `{ "unreadCount": number }`      |
| `PATCH /notifications/{id}/read`  | —                                                                                  | `NotificationResponse`           |
| `PATCH /notifications/read-all`   | —                                                                                  | `null`                           |
| `DELETE /notifications/{id}`      | —                                                                                  | `null`                           |
| `DELETE /notifications`           | Query `readOnly` (default `false`)                                                 | `null`                           |
| `POST /devices/tokens`            | `{ "token", "platform": "WEB" }`                                                   | `null`                           |
| `DELETE /devices/tokens`          | `{ "token" }`                                                                      | `null`                           |

`NotificationResponse` fields: `id`, `title`, `message`, `type`, `isRead`, `readAt`,
`data`, `createdAt`, `updatedAt`. Types include `BUDGET_EXCEEDED`, `BUDGET_WARNING`,
`SAVINGS_GOAL_ACHIEVED`, `SAVINGS_GOAL_FAILED`, `RECURRING_TRANSACTION_DUE`,
`PAYMENT_SUCCESS`, `SECURITY_ALERT`, `GENERAL`, and `SYSTEM`.

## Admin

These routes require an authenticated administrator.

| Method and path          | Request                                                      | Success `data`                 |
| ------------------------ | ------------------------------------------------------------ | ------------------------------ |
| `GET /admin/users`       | Query `page` (0), `size` (20, max 100), `direction` (`DESC`) | Paginated `UserProfileDetails` |
| `GET /admin/users/{id}`  | —                                                            | `UserProfileDetails`           |
| `GET /admin/users/count` | Required query `role` and `accountStatus`                    | Number                         |

## Frontend notes

- The frontend uses the controller paths above; subscription and Google-auth routes are
  not exposed by the current controller set and are intentionally absent.
- After sign-in, users can explicitly enable push notifications from the notification
  panel. The frontend requests browser permission, obtains an FCM Web token, and
  registers it through `POST /devices/tokens` using the authenticated user session.
  Firebase Web configuration and the Web Push VAPID public key must be configured
  with the `VITE_FIREBASE_*` variables shown in `.env.example`. Tokens are not
  registered until the user enables push notifications on that device.
