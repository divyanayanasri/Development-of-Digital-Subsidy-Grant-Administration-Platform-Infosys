# Government Subsidy & Grant Disbursement Tracking System

Unified e-Governance platform for citizen grant applications, multi-tier officer eligibility verification, automated eligibility scoring, and staged milestone benefit disbursements.

---

## 📁 Integrated Project Structure

```
subsidy-tracker/                  <- Backend Project Root
├── pom.xml                       <- Maven Build Descriptor
├── README.md                     <- Startup & Verification Instructions
├── src/                          <- Spring Boot Java Backend
│   └── main/java/com/training/...
└── frontend/                     <- React (Vite) Frontend Subfolder
    ├── package.json
    ├── vite.config.js            <- Proxies /api and /disbursement to http://localhost:8081
    ├── index.html
    └── src/
```

---

## ⚙️ Prerequisites

1. **Java Development Kit (JDK):** Version 17 or higher
2. **Apache Maven:** Version 3.8+
3. **Node.js & npm:** Node 18+ and npm 9+
4. **MySQL Database Server:** Running locally on port `3306` with database `subsidydb` created

---

## 🚀 Startup Instructions

### 1. Database Setup (MySQL)

Create the `subsidydb` database in your local MySQL instance:

```sql
CREATE DATABASE IF NOT EXISTS subsidydb;
```

> **Note:** The backend database connection parameters are configured in `src/main/resources/application.properties`:
> - **URL:** `jdbc:mysql://localhost:3306/subsidydb`
> - **Username:** `root`
> - **Password:** `root` *(or set environment variables `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`)*

### 2. Start the Backend (Spring Boot)

From the project root directory (`subsidy-tracker/`):

```bash
mvn spring-boot:run
```

The Spring Boot backend will start on **`http://localhost:8081`**.

### 3. Start the Frontend (Vite Dev Server)

In a separate terminal, navigate into the `frontend/` directory and launch the dev server:

```bash
cd frontend
npm install
npm run dev
```

The React frontend server will run on **`http://localhost:5173`**.
Vite automatically proxies `/api/*` and `/disbursement/*` HTTP requests to `http://localhost:8081`.

---

## 🧪 End-to-End Workflow Testing Guide

Follow this step-by-step journey to verify full end-to-end integration and MySQL persistence:

### Step 1: Citizen Beneficiary Registration
1. Open `http://localhost:5173` in your browser.
2. Click **Register** on the landing page (navigates to `/signup`).
3. Fill out the registration form with valid beneficiary details:
   - **Name:** `Ramesh Kumar`
   - **Email:** `ramesh@example.com`
   - **Password:** `Password123`
   - **Aadhaar:** `123456789012` (12 digits)
   - **Annual Income:** `120000`
   - **Land Size:** `2.5`
   - **Category:** `AGRICULTURE`
   - **Address:** `123 Village Road, Central District`
4. Click **Complete Beneficiary Registration**. The record will be inserted into the MySQL `beneficiaries` table.

### Step 2: Submit Scheme Application
1. After registering, you will be redirected to the Citizen Portal (`/beneficiary/applications`).
2. Click **Apply for New Scheme** (`/beneficiary/apply`).
3. Select an active welfare scheme (e.g. *PM Kisan Agriculture Support*) and click **Select & Proceed to Apply**.
4. Submit the application declaration. The backend will evaluate eligibility, calculate an eligibility score, assign a workflow route (`FAST_TRACK` or `ESCALATED`), and insert the record into the MySQL `applications` table.

### Step 3: Officer Verification & Workflow Transition
1. Click **Sign Out** from the top right bar.
2. Click **Login** (`/login`) and log in as an authorized officer or Super Admin:
   - **Email:** `officer@gov.in` (or create an officer via Admin console)
3. Navigate to the **Verification Queue Register** (`/officer/queue`).
4. Select the submitted application file.
5. Perform verification transitions (e.g., transition from `SUBMITTED` -> `FIELD_REVIEW` -> `FINANCE_REVIEW` -> `APPROVED`).
6. System logs each transition into the verification audit trail in MySQL.

### Step 4: Staged Disbursement Release
1. Once the application reaches **`APPROVED`** status, navigate to the **Fund Release & Milestone Disbursement Ledger** panel on the case detail page.
2. Click **Create Disbursement Plan** to initialize staged milestone releases.
3. Click **Complete / Release Fund** for Stage 1.
4. Verify stage status updates to `RELEASED` with timestamp.

### Step 5: Database Verification
Verify row persistence directly in MySQL:

```sql
USE subsidydb;

-- Check registered beneficiary
SELECT * FROM beneficiaries ORDER BY id DESC LIMIT 5;

-- Check submitted application & eligibility score
SELECT id, beneficiary_id, scheme_id, eligibility_score, route_type, status FROM applications ORDER BY id DESC LIMIT 5;

-- Check verification history audit logs
SELECT * FROM application_history ORDER BY id DESC LIMIT 10;

-- Check milestone disbursement stages
SELECT * FROM disbursement_stages ORDER BY id DESC LIMIT 10;
```
