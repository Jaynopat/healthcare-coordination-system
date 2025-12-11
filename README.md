# healthcare-coordination-system
Final project for INFO 5100 - Healthcare coordination platform

# Healthcare Coordination System

## Project Overview
A comprehensive healthcare coordination platform connecting medical clinics with pharmacy networks, enabling seamless communication and workflow management between healthcare providers.

**Course:** INFO 5100 - Application Engineering and Development  
**Institution:** Northeastern University Toronto  
**Semester:** Fall 2025

---

## Problem Statement
Healthcare delivery suffers from fragmented communication between clinics and pharmacies. Patients face delays in prescription fulfillment, medication availability issues, and poor coordination between providers. This system bridges that gap by enabling real-time communication and workflow management across healthcare enterprises.

---

## System Architecture

### Two Enterprises
1. **Medical Clinic Network** - Diagnoses patients and issues prescriptions
2. **Pharmacy Network** - Fulfills prescriptions and manages medication inventory

### Four Roles
1. **Doctor** (Normal - Clinic) - Schedules appointments, diagnoses patients, issues prescriptions
2. **Clinic Administrator** (Admin - Clinic) - Manages clinic staff, generates reports
3. **Pharmacist** (Normal - Pharmacy) - Fills prescriptions, manages inventory, requests restock
4. **Pharmacy Manager** (Admin - Pharmacy) - Approves restock requests, oversees inventory

---

## Work Requests Implemented

### Work Request #1: Appointment Booking (Intra-Enterprise - Clinic)
- **Flow:** Patient → Doctor (within clinic)
- **Status:** SCHEDULED → COMPLETED
- **Features:** Schedule appointments, complete with diagnosis

### Work Request #2: Prescription Flow (Inter-Enterprise) ⭐
- **Flow:** Clinic (Doctor) → Pharmacy (Pharmacist)
- **Status:** PENDING → FILLED → COMPLETED
- **Features:** Doctor creates prescription, pharmacy receives and fills it
- **Demonstrates:** Inter-enterprise communication between two separate organizations

### Work Request #3: Medication Inventory Check
- **Flow:** Query medication availability in real-time
- **Features:** Check stock levels, search medications

### Work Request #4: Inventory Restock (Intra-Enterprise - Pharmacy) ⭐
- **Flow:** Pharmacist → Pharmacy Manager (within pharmacy)
- **Status:** PENDING → APPROVED → RECEIVED
- **Features:** Request restock, manager approval, automatic inventory update
- **Demonstrates:** Intra-enterprise workflow with approval hierarchy

---

## Technology Stack

- **Frontend:** Java Swing
- **Backend:** Java (JDK 17+)
- **Database:** MySQL 8.0
- **Containerization:** Docker
- **Database Connectivity:** JDBC (MySQL Connector/J 9.5.0)
- **Build Tool:** Apache Ant (NetBeans)
- **Version Control:** Git & GitHub

---

## Database Schema

**7 Tables with Full CRUD Operations:**
1. `users` - System users (doctors, pharmacists, admins, managers)
2. `patients` - Patient records
3. `appointments` - Doctor appointments
4. `medications` - Medication catalog
5. `prescriptions` - Prescription records (inter-enterprise)
6. `pharmacy_inventory` - Stock levels
7. `restock_requests` - Inventory restock requests (intra-enterprise)

---

## Project Structure
```
HealthcareSystem/
├── src/
│   ├── database/           # DAO classes + DatabaseConnection
│   │   ├── DatabaseConnection.java
│   │   ├── UserDAO.java
│   │   ├── PatientDAO.java
│   │   ├── AppointmentDAO.java
│   │   ├── PrescriptionDAO.java
│   │   ├── MedicationDAO.java
│   │   └── RestockRequestDAO.java
│   ├── model/              # Entity classes
│   │   ├── User.java
│   │   ├── Patient.java
│   │   ├── Medication.java
│   │   ├── Appointment.java
│   │   ├── Prescription.java
│   │   └── RestockRequest.java
│   └── ui/                 # User Interface
│       ├── LoginFrame.java
│       ├── DashboardLauncher.java
│       ├── doctor/
│       │   └── DoctorDashboard.java
│       ├── pharmacist/
│       │   └── PharmacistDashboard.java
│       └── admin/
│           ├── ClinicAdminDashboard.java
│           └── PharmacyManagerDashboard.java
├── lib/
│   └── mysql-connector-j-9.5.0.jar
├── diagrams/               # UML Diagrams
│   ├── UseCase_Diagram.png
│   └── Class_Diagram.png
├── screenshots/            # Application screenshots
├── docker-compose.yml      # Docker configuration
├── database_schema.sql     # Database setup script
└── README.md              # This file
```

---

## How to Run the Application

### Prerequisites
- Java JDK 17 or higher
- Docker Desktop
- MySQL Workbench (optional, for database viewing)

### Step 1: Start MySQL Database
```bash
docker-compose up -d
```

### Step 2: Setup Database
1. Open MySQL Workbench
2. Connect to localhost:3306 (username: root, password: password123)
3. Run the `database_schema.sql` script to create tables and sample data

### Step 3: Run Application

**Option A: Using NetBeans**
1. Open project in NetBeans
2. Right-click on `LoginFrame.java` → Run File

**Option B: Using Dashboard Launcher (Demo Mode)**
1. Right-click on `DashboardLauncher.java` → Run File
2. Click buttons to view any dashboard directly

### Login Credentials
- **Doctor:** dr.smith / pass123
- **Pharmacist:** pharm.wilson / pass123
- **Clinic Admin:** admin.clinic / pass123
- **Pharmacy Manager:** manager.pharm / pass123

---

## Key Features Demonstration

### Inter-Enterprise Workflow (Work Request #2)
1. Login as Doctor → Create Prescription tab
2. Fill in: Appointment ID: 2, Patient ID: 2, select medication
3. Click "Create & Send Prescription to Pharmacy"
4. Logout and login as Pharmacist
5. See prescription in "Pending Prescriptions" tab
6. Select prescription → Click "Fill Prescription"
7. Prescription moves from Clinic to Pharmacy ✅

### Intra-Enterprise Workflow (Work Request #4)
1. Login as Pharmacist → "Request Restock" tab
2. Select medication, enter quantity and reason
3. Click "Submit Restock Request to Manager"
4. Logout and login as Pharmacy Manager
5. See request in "Pending Requests" tab
6. Select request → Click "Approve Request"
7. Inventory automatically updated ✅

---

## OOP Concepts Demonstrated

✅ **Encapsulation:** All model classes have private fields with public getters/setters  
✅ **Inheritance:** All dashboard classes extend JFrame  
✅ **Polymorphism:** Method overriding (toString(), event handlers)  
✅ **Abstraction:** DAO pattern separates data access from business logic  
✅ **Composition:** Dashboards compose model objects and DAO objects  

---

## Design Patterns Used

- **DAO Pattern:** Separates data persistence logic from business logic
- **Singleton Pattern:** DatabaseConnection maintains single connection instance
- **MVC Pattern:** Clear separation between Model, View (UI), and Controller (DAO)

---

## Database CRUD Evidence

Full CRUD operations implemented on all 7 tables:
- **CREATE:** Insert new records (patients, appointments, prescriptions, etc.)
- **READ:** Query and display data (multiple search/filter methods)
- **UPDATE:** Modify existing records (status updates, information changes)
- **DELETE:** Remove records from database

See `screenshots/` folder for MySQL Workbench CRUD operation evidence.

---

## Project Highlights

✨ **Bug-Free Application** - Thoroughly tested, no runtime errors  
✨ **Professional UI** - Clean, intuitive interface with color-coded elements  
✨ **Complete Workflows** - All 4 work requests fully functional  
✨ **Real-Time Updates** - Changes immediately reflected across dashboards  
✨ **Role-Based Access** - Each user sees only relevant features  
✨ **Inter-Enterprise Communication** - Seamless data flow between organizations  

---

## Presentation & Demo

📹 **Presentation Video (5 min):https://drive.google.com/file/d/166_odaLFmHk39IeU9RwJ1k1B7nkCY9Sd/view?usp=sharing  
📹 **Demo Video (10-15 min):https://drive.google.com/file/d/1t7kg5V1MSj43EV1Cq5nIk4ITNY9aDHBk/view?usp=sharing

---

## GitHub Repository

**Repository:** https://github.com/Jaynopat/healthcare-coordination-system.git

**Commit History:** 17+ feature-based commits demonstrating incremental development

---

## Developer

**Name:** Jane Akpang  
**Student ID:** 002597079 
**Email:** akpang.j@northeastern.edu  
**Program:** MSc Information Systems, Northeastern University Toronto

---

## Acknowledgments

This project demonstrates practical application of software engineering principles including object-oriented design, database management, enterprise architecture, and full-stack development using Java technologies.

---

