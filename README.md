# fin.ai

AI-powered personal finance assistant for tracking spending, budgets, savings goals, and smart money insights.

![Status](https://img.shields.io/badge/status-In%20Development-orange)
![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16%2B-4169E1?logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?logo=redis&logoColor=white)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)

## Table of Contents

- [About the Project](#about-the-project)
- [Key Features](#key-features)
- [Demo / Screenshots](#demo--screenshots)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Environment Variables / Configuration](#environment-variables--configuration)
  - [How to Run the Project](#how-to-run-the-project)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Deployment](#deployment)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [Troubleshooting / FAQ](#troubleshooting--faq)
- [License](#license)
- [Author / Contact](#author--contact)
- [Acknowledgements](#acknowledgements)

## About the Project

fin.ai is an AI-powered personal finance platform designed to help people manage their money in one place. It combines everyday finance tracking with an intelligent assistant that can answer questions using a user’s actual spending, budgets, savings goals, and recurring transactions.

Instead of checking several tools or spreadsheets, users can log their transactions, understand monthly trends, set budgets, and ask simple questions such as:

- “How much did I spend on food this month?”
- “Am I on track with my savings goal?”
- “Which category has grown the most this quarter?”

The goal is to make personal finance clearer, more actionable, and easier to understand for everyday users.

## Key Features

- AI financial insights based on the user’s own data
- Personal dashboard with income, expenses, and balance summaries
- Monthly category-based budgets and usage alerts
- Savings goals with progress tracking and contributions
- Recurring transaction scheduling for regular income and expenses
- CSV export for transaction history and analysis
- Secure authentication and account verification via email OTP
- Real-time notifications and reminder support
- Chat-based finance assistant for natural-language questions

## Demo / Screenshots

- Demo URL: https://fin-phi-plum.vercel.app/home

## Tech Stack

| Area                   | Technology                   | Purpose                                              |
| ---------------------- | ---------------------------- | ---------------------------------------------------- |
| Frontend               | React 19 + TypeScript + Vite | User interface and app experience                    |
| Styling                | Tailwind CSS                 | Fast, modern UI design                               |
| Backend                | Java 25 + Spring Boot 4      | REST API, business logic, security, and integrations |
| AI                     | Spring AI + OpenAI           | Chat assistant and vector-based financial insights   |
| Database               | PostgreSQL + PGVector        | Main application data and AI retrieval storage       |
| Cache / Session Helper | Redis                        | Caching and supporting runtime data                  |
| Authentication         | JWT + Spring Security        | Secure login and token-based access                  |
| Notifications          | Firebase Cloud Messaging     | Push notifications and device token support          |
| Email                  | SMTP / Gmail                 | OTP and notification emails                          |
| Migrations             | Flyway                       | Database schema management                           |
| Build Tools            | Maven + npm                  | Backend and frontend dependency management           |
| Infra / Local Dev      | Docker Compose               | PostgreSQL and Redis local setup                     |

## Project Structure

```text
fin.ai/
├── backend/                     # Spring Boot backend
│   ├── src/main/java/ai/fin/    # Application source code
│   ├── src/main/resources/     # Config, templates, properties, keys, prompts
│   │   ├── db/migration/       # Flyway SQL migrations
│   │   ├── keys/               # RSA key files for JWT signing
│   │   ├── templates/          # Email templates
│   │   └── prompts/            # AI prompt templates
│   ├── pom.xml                 # Maven build configuration
│   ├── mvnw                    # Maven wrapper
│   └── mvnw.cmd                # Windows Maven wrapper
├── frontend/                    # React frontend
│   ├── src/                    # App pages, routes, services, UI components
│   ├── public/                 # Static assets and public files
│   ├── .env.example            # Frontend environment variables
│   ├── package.json            # npm scripts and dependencies
│   ├── vite.config.ts          # Vite configuration
│   └── tailwind.config.js     # Tailwind configuration
├── docker/
│   └── compose/dev/            # Local Docker Compose setup for PostgreSQL and Redis
├── public/                     # Project docs, diagrams, and shared assets
├── LICENCE                     # Apache License 2.0
├── README.md                   # Project documentation
├── .gitignore                  # Git ignore rules
├── .github/                    # GitHub settings, workflows, and automation
└── .idea/                      # IDE config (local project metadata)
```

## Getting Started

### Prerequisites

Before you start, make sure you have:

- Java 25
- Maven 3.9+ or use the Maven wrapper included in `backend/`
- Node.js 22+
- npm 10+
- Docker Desktop or Docker Engine running
- A modern browser such as Chrome or Edge

### Installation

1. Clone the repository:

```bash
git clone https://github.com/ripanbaidya/fin.ai.git
cd fin.ai
```

2. Install backend dependencies:

```bash
cd backend
./mvnw install
```

3. Install frontend dependencies:

```bash
cd ../frontend
npm install
```

4. Start the local infrastructure services:

```bash
cd ../docker/compose/dev
docker compose up -d
```

This starts:

- PostgreSQL on `localhost:5432`
- Redis on `localhost:6379`

### Environment Variables / Configuration

Create local environment files before running the app.

#### Backend environment variables

Set these in your IDE run configuration or shell before starting the backend:

| Variable              | Required | Description                                            |
| --------------------- | -------- | ------------------------------------------------------ |
| `OPENAI_API_KEY`      | Yes      | API key for the OpenAI-powered finance assistant       |
| `MAIL_USERNAME`       | Yes      | Gmail address for email verification and notifications |
| `MAIL_PASSWORD`       | Yes      | App password or SMTP password for email sending        |
| `RAZORPAY_KEY_ID`     | Optional | Razorpay public key for payment/subscription features  |
| `RAZORPAY_KEY_SECRET` | Optional | Razorpay secret key for payment/subscription features  |
| `SUBSCRIPTION_AMOUNT` | Optional | Subscription charge amount                             |
| `SUBSCRIPTION_DAYS`   | Optional | Billing cycle in days                                  |

The project also uses default local database settings in `backend/src/main/resources/application.yaml`:

- PostgreSQL: `localhost:5432` / database `app`
- Redis: `localhost:6379`
- JWT keys: loaded from `backend/src/main/resources/keys/`

#### Frontend environment variables

Copy the example file and fill in values:

```bash
cd frontend
cp .env.example .env
```

Example variables:

| Variable                            | Required               | Description                                                       |
| ----------------------------------- | ---------------------- | ----------------------------------------------------------------- |
| `VITE_API_BASE_URL`                 | Yes                    | Base URL for the backend API, for example `http://localhost:8080` |
| `VITE_FIREBASE_API_KEY`             | Yes, for notifications | Firebase client configuration                                     |
| `VITE_FIREBASE_AUTH_DOMAIN`         | Yes                    | Firebase auth domain                                              |
| `VITE_FIREBASE_PROJECT_ID`          | Yes                    | Firebase project ID                                               |
| `VITE_FIREBASE_STORAGE_BUCKET`      | Yes                    | Firebase storage bucket                                           |
| `VITE_FIREBASE_MESSAGING_SENDER_ID` | Yes                    | Firebase Cloud Messaging sender ID                                |
| `VITE_FIREBASE_APP_ID`              | Yes                    | Firebase app ID                                                   |
| `VITE_FIREBASE_VAPID_KEY`           | Yes                    | VAPID key for web push notifications                              |

### How to Run the Project

#### Development mode

Start PostgreSQL and Redis:

```bash
cd docker/compose/dev
docker compose up -d
```

Start the backend:

```bash
cd backend
./mvnw spring-boot:run
```

Start the frontend:

```bash
cd frontend
npm run dev
```

Then open:

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

#### Production build

Build the backend:

```bash
cd backend
./mvnw clean package
```

Build the frontend:

```bash
cd ../frontend
npm run build
```

Then deploy the backend JAR and frontend static files to your hosting environment.

## Usage

After starting the app, sign up or log in and begin tracking your finances.

### Common workflows

1. Create an account and verify your email.
2. Add income and expense transactions.
3. Set monthly budgets by category.
4. Create savings goals and add contributions.
5. Schedule recurring expenses or income.
6. Ask the AI assistant questions in plain English.

### Example prompts

```text
How much did I spend on groceries this month?
What is my total savings progress right now?
Which category is over budget?
Show my recurring expenses for the next 30 days.
```

### Example output

```text
You spent 3,450 INR on groceries this month.
Your food budget is 70% used.
You are 2,300 INR away from your travel savings goal.
```

## API Documentation

This project exposes API documentation through Swagger/OpenAPI.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

The API includes sections for:

- Authentication
- Transactions
- Budgets
- Savings Goals
- Recurring Transactions
- Chat / AI Assistant
- User profile and account management

Example request pattern:

```http
GET /api/transactions
Authorization: Bearer <token>
```

```json
{
  "page": 0,
  "size": 20,
  "month": "2026-10"
}
```

> Exact routes and payloads are defined in the backend controllers and are best reviewed directly in Swagger UI during development.

## Testing

Run backend tests:

```bash
cd backend
./mvnw test
```

Run frontend linting:

```bash
cd frontend
npm run lint
```

Run a production build check:

```bash
cd frontend
npm run build
```

## Deployment

This project is set up as a frontend + backend application and can be deployed in multiple ways.

### Recommended local deployment pattern

- Backend: deploy as a Spring Boot service on a VM, Docker container, or cloud host
- Frontend: deploy as a static app with Vercel, Netlify, or a web server
- Database: PostgreSQL instance
- Cache: Redis instance
- AI features: OpenAI API key configured in the backend environment

### Example deployment checklist

- [ ] Configure production environment variables
- [ ] Set up PostgreSQL and Redis in a managed environment
- [ ] Ensure the frontend points to the correct production API URL
- [ ] Enable HTTPS and secure secret management
- [ ] Run database migrations and verify health checks

## Roadmap

- [ ] Improve onboarding and first-run user guidance
- [ ] Add deeper automated budget alerts and recommendations
- [ ] Expand AI summaries for spending patterns and monthly insights
- [ ] Add smart category suggestions from transaction history
- [ ] Support export/import of finance data
- [ ] Improve analytics and forecasting models
- [ ] Add more notification channels and reminder settings
- [ ] Improve admin monitoring and user management tools

## Contributing

Contributions are welcome.

1. Fork the repository.
2. Create a feature branch:

```bash
git checkout -b feature/my-improvement
```

3. Make your changes and keep commits clear.
4. Run relevant tests and linting checks.
5. Commit your work:

```bash
git add .
git commit -m "Add my improvement"
```

6. Push the branch:

```bash
git push origin feature/my-improvement
```

7. Open a pull request with a short description of the change.

## Troubleshooting / FAQ

### Q: The app cannot connect to PostgreSQL or Redis

Check that Docker is running and that the services are started:

```bash
cd docker/compose/dev
docker compose ps
```

If needed, restart them:

```bash
docker compose down
docker compose up -d
```

### Q: Frontend shows API errors

Check your `.env` file and make sure `VITE_API_BASE_URL` matches the backend URL.

### Q: AI chat does not work

Confirm that `OPENAI_API_KEY` is set correctly in the backend environment.

### Q: Email verification does not send

Verify `MAIL_USERNAME` and `MAIL_PASSWORD` are valid. If using Gmail, remember to use an app password if required.

### Q: Java version mismatch

Use Java 25 for this project. If your system has another version, switch your local Java runtime before running the backend.

### Q: Port already in use

Stop the existing service using the port, or change the configured ports in the project settings.

## License

This project is licensed under the Apache License 2.0.

See the [LICENCE](LICENCE) file for full terms and conditions.

## Author / Contact

- Name: Ripan Baidya
- GitHub: [https://github.com/ripanbaidya](https://github.com/ripanbaidya)
- LinkedIn: https://linkedin.com/in/ripanbaidya
- Email: connect.ripanbaidya@gmail.com

## Acknowledgements

- Spring Boot for the backend foundation
- React and Vite for the frontend experience
- PostgreSQL and Redis for data and caching
- OpenAI for AI-powered insights
- Firebase for push notifications
- Flyway for database migrations
- The open-source community for the tools and libraries used in this project
