# PhishGuard System

An AI-powered phishing simulation and training system designed to improve organizational cybersecurity awareness through realistic email simulations, employee training modules, and performance tracking.

## Table of Contents
- [Features](#features)
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [API Documentation](#api-documentation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Features

### Core Functionality
- **AI-Powered Email Generation**: Uses LLMs (Groq/Ollama) to create realistic phishing and legitimate emails
- **Email Campaign Management**: Send simulated phishing/legitimate emails to employees
- **Role-Based Access Control**: Secure authentication with Keycloak integration
- **Employee Training Modules**: Interactive security awareness training
- **Performance Leaderboard**: Track and rank employee performance in detecting phishing attempts
- **Reward System**: Incentivize good security practices

### Admin Capabilities
- Create and manage email campaigns
- Define target employee groups
- Monitor campaign results in real-time
- Configure LLM settings and email templates
- Manage user roles and permissions

### Employee Experience
- Receive simulated emails in their inbox
- Report suspicious emails through the system
- Complete training modules
- View personal performance statistics
- Compete on the leaderboard

## System Architecture

### Backend (Spring Boot)
- **Language**: Java 25
- **Framework**: Spring Boot 4.0.5
- **Architecture**: Microservices-ready modular design
- **Key Components**:
  - RESTful APIs for all frontend operations
  - Spring Data JPA for PostgreSQL persistence
  - Spring Security with OAuth2 Resource Server (Keycloak)
  - Spring WebFlux for reactive email processing
  - LLM Integration (Groq/Ollama clients)
  - OpenAPI documentation (SpringDoc)
  - Custom filters for LLM response processing
  - Event-driven architecture for email campaigns

### Frontend (React)
- **Language**: TypeScript
- **Framework**: React 19 with Vite
- **State Management**: React Query (TanStack)
- **Styling**: Tailwind CSS 4
- **Routing**: React Router DOM v7
- **HTTP Client**: Axios
- **Authentication**: Keycloak JavaScript Adapter
- **Features**:
  - Responsive design for all devices
  - Real-time updates via query invalidation
  - Form validation and error handling
  - Loading states and skeleton UIs
  - Accessibility-focused components

## Technology Stack

### Backend
- **Runtime**: JDK 25
- **Build Tool**: Gradle
- **Database**: PostgreSQL
- **Authentication**: Keycloak
- **LLM Providers**: 
  - Groq (Llama3, Mixtral models)
  - Ollama (Local LLM deployment)
- **API Documentation**: Swagger UI (SpringDoc)
- **Testing**: JUnit 5, Spring Boot Test
- **Code Quality**: Lombok, Checkstyle (configurable)

### Frontend
- **Build Tool**: Vite
- **Package Manager**: npm
- **Language**: TypeScript
- **UI Library**: Custom components with Tailwind CSS
- **HTTP**: Axios with interceptors
- **State**: React Query for server state
- **Routing**: React Router v6
- **Authentication**: Keycloak JS Adapter
- **Linting**: ESLint with TypeScript plugin
- **Form Handling**: Custom hooks (planned)

## Getting Started

### Prerequisites
- Java 25 JDK
- Node.js >= 20
- PostgreSQL >= 15
- Gradle 8+
- Docker (optional, for local development)
- Keycloak server (for authentication)

### Backend Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/your-org/phishguard-system.git
   cd phishguard-system/Backend
   ```

2. Configure the application:
   - Copy `src/main/resources/application-template.yml` to `src/main/resources/application.yml`
   - Update database credentials, Keycloak settings, and LLM API keys
   - For local development, you can use the provided Docker Compose (if available)

3. Build and run:
   ```bash
   ./gradlew clean build
   ./gradlew bootRun
   ```
   The backend will start on `http://localhost:8080`

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd ../frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Configure environment variables:
   - Create `.env` file based on `.env.example`
   - Set VITE_API_URL to point to your backend (default: `http://localhost:8080/api`)
   - Configure Keycloak realm and client settings

4. Start the development server:
   ```bash
   npm run dev
   ```
   The frontend will be available at `http://localhost:5173`

### Production Build
```bash
npm run build
# Preview production build
npm run preview
```

## API Documentation

Once the backend is running, access the interactive API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

The API is organized into the following modules:
- **Authentication**: Login, token refresh, user info
- **Users**: Employee and admin management
- **Emails**: Campaign creation, sending, tracking
- **Leaderboard**: Performance scoring and rankings
- **Rewards**: Incentive system management
- **Training**: Educational modules and progress tracking
- **Reports**: Analytics and export functionality

## Usage

### For Administrators
1. Log in to the admin portal at `http://localhost:5173/admin`
2. Navigate to Campaigns → Create New Campaign
3. Select email type (phishing/legitimate), target group, and schedule
4. Monitor results in real-time dashboard
5. Generate reports and assign training as needed

### For Employees
1. Access the employee portal at `http://localhost:5173`
2. Check your inbox for simulated emails (check spam folder too)
3. Use the "Report Phishing" button for suspicious emails
4. Complete assigned training modules
5. View your performance on the leaderboard
6. Earn rewards for correct identifications

## Project Structure
```
PhishGuard-System/
├── Backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/edu/nsbm/phishguard/
│   │   │   │   ├── controller/     # REST API endpoints
│   │   │   │   ├── service/        # Business logic interfaces
│   │   │   │   ├── service/impl/   # Service implementations
│   │   │   │   ├── repository/     # Data access layer
│   │   │   │   ├── dto/            # Data transfer objects
│   │   │   │   ├── model/          # JPA entities
│   │   │   │   ├── config/         # Spring configuration
│   │   │   │   ├── util/           # Utility classes
│   │   │   │   └── resolver/       # Custom argument resolvers
│   │   │   └── resources/          # Application configuration
│   │   └── test/                   # Unit and integration tests
│   ├── build.gradle                # Gradle build configuration
│   └── gradlew                     # Gradle wrapper
├── frontend/
│   ├── src/
│   │   ├── components/             # Reusable UI components
│   │   ├── pages/                  # Page-level components
│   │   ├── hooks/                  # Custom React hooks
│   │   ├── utils/                  # Utility functions
│   │   ├── services/               # API service layers
│   │   ├── context/                # React context providers
│   │   ├── routes/                 # Application routes
│   │   ├── assets/                 # Static assets
│   │   └── App.tsx                 # Root application component
│   ├── public/                     # Static public files
│   ├── package.json                # npm dependencies and scripts
│   ├── vite.config.ts              # Vite configuration
│   ├── tsconfig.json               # TypeScript configuration
│   └── eslint.config.js            # ESLint configuration
├── .gitignore                      # Git ignore rules
└── README.md                       # This file
```

## Contributing

We welcome contributions to improve PhishGuard! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow the existing code style and conventions
- Write unit tests for new backend functionality
- Add E2E tests for critical frontend flows
- Update documentation as needed
- Ensure all linting checks pass before submitting

### Reporting Issues
Please use the GitHub Issues tracker to report bugs or suggest features. Include:
- Clear description of the issue
- Steps to reproduce (if applicable)
- Expected vs actual behavior
- Screenshots or logs if relevant

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

- **Project Maintainer**: [Your Name/Team]
- **Institution**: NSBM Green University
- **Email**: phishguard@nsbm.ac.lk
- **Website**: https://phishguard.nsbm.ac.lk

---

**PhishGuard System** - Empowering organizations to defend against phishing attacks through education and simulation.

*Last updated: May 2026*
