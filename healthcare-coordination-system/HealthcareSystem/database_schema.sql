-- Select the database
USE healthcare_system;

-- Table 1: Users (for login and roles)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    enterprise_type VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: Patients
CREATE TABLE patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(200),
    blood_group VARCHAR(5),
    allergies TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 3: Appointments (Work Request 1)
CREATE TABLE appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason VARCHAR(200),
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    diagnosis TEXT,
    notes TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);

-- Table 4: Medications
CREATE TABLE medications (
    medication_id INT PRIMARY KEY AUTO_INCREMENT,
    medication_name VARCHAR(100) NOT NULL,
    generic_name VARCHAR(100),
    category VARCHAR(50),
    dosage_form VARCHAR(50),
    strength VARCHAR(50),
    manufacturer VARCHAR(100),
    unit_price DECIMAL(10,2),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 5: Prescriptions (Work Request 2 - Inter-enterprise)
CREATE TABLE prescriptions (
    prescription_id INT PRIMARY KEY AUTO_INCREMENT,
    appointment_id INT NOT NULL,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    medication_id INT NOT NULL,
    dosage_instructions VARCHAR(200),
    quantity INT NOT NULL,
    refills INT DEFAULT 0,
    status VARCHAR(30) DEFAULT 'PENDING',
    issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    filled_date TIMESTAMP NULL,
    pharmacist_id INT NULL,
    pharmacist_notes TEXT,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    FOREIGN KEY (medication_id) REFERENCES medications(medication_id),
    FOREIGN KEY (pharmacist_id) REFERENCES users(user_id)
);

-- Table 6: Pharmacy Inventory (Work Request 3)
CREATE TABLE pharmacy_inventory (
    inventory_id INT PRIMARY KEY AUTO_INCREMENT,
    medication_id INT NOT NULL,
    quantity_available INT NOT NULL DEFAULT 0,
    reorder_level INT DEFAULT 10,
    expiry_date DATE,
    last_restocked DATE,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (medication_id) REFERENCES medications(medication_id)
);

-- Table 7: Restock Requests (Work Request 4)
CREATE TABLE restock_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    medication_id INT NOT NULL,
    requested_quantity INT NOT NULL,
    current_stock INT NOT NULL,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    reason VARCHAR(200),
    status VARCHAR(20) DEFAULT 'PENDING',
    requested_by INT NOT NULL,
    requested_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_by INT NULL,
    approved_date TIMESTAMP NULL,
    manager_notes TEXT,
    FOREIGN KEY (medication_id) REFERENCES medications(medication_id),
    FOREIGN KEY (requested_by) REFERENCES users(user_id),
    FOREIGN KEY (approved_by) REFERENCES users(user_id)
);

-- Insert sample users (all passwords are: "pass123")
INSERT INTO users (username, password, full_name, role, enterprise_type, email, phone) VALUES
('dr.smith', 'pass123', 'Dr. Sarah Smith', 'DOCTOR', 'CLINIC', 'sarah.smith@clinic.com', '416-555-0101'),
('dr.jones', 'pass123', 'Dr. Michael Jones', 'DOCTOR', 'CLINIC', 'michael.jones@clinic.com', '416-555-0102'),
('admin.clinic', 'pass123', 'Jennifer Admin', 'CLINIC_ADMIN', 'CLINIC', 'admin@clinic.com', '416-555-0100'),
('pharm.wilson', 'pass123', 'David Wilson', 'PHARMACIST', 'PHARMACY', 'david.wilson@pharmacy.com', '416-555-0201'),
('pharm.garcia', 'pass123', 'Maria Garcia', 'PHARMACIST', 'PHARMACY', 'maria.garcia@pharmacy.com', '416-555-0202'),
('manager.pharm', 'pass123', 'Robert Manager', 'PHARMACY_MANAGER', 'PHARMACY', 'manager@pharmacy.com', '416-555-0200');

-- Insert sample patients
INSERT INTO patients (first_name, last_name, date_of_birth, gender, phone, email, address, blood_group, allergies) VALUES
('John', 'Doe', '1985-03-15', 'Male', '416-555-1001', 'john.doe@email.com', '123 Main St, Toronto', 'O+', 'Penicillin'),
('Jane', 'Smith', '1990-07-22', 'Female', '416-555-1002', 'jane.smith@email.com', '456 Oak Ave, Toronto', 'A+', 'None'),
('Robert', 'Brown', '1978-11-30', 'Male', '416-555-1003', 'robert.brown@email.com', '789 Pine Rd, Toronto', 'B+', 'Aspirin'),
('Emily', 'Davis', '1995-05-18', 'Female', '416-555-1004', 'emily.davis@email.com', '321 Elm St, Toronto', 'AB-', 'Latex'),
('Michael', 'Wilson', '1982-09-25', 'Male', '416-555-1005', 'michael.wilson@email.com', '654 Maple Dr, Toronto', 'O-', 'None');

-- Insert sample medications
INSERT INTO medications (medication_name, generic_name, category, dosage_form, strength, manufacturer, unit_price) VALUES
('Amoxicillin', 'Amoxicillin', 'Antibiotic', 'Capsule', '500mg', 'PharmaCorp', 15.99),
('Lisinopril', 'Lisinopril', 'Blood Pressure', 'Tablet', '10mg', 'MediLife', 12.50),
('Metformin', 'Metformin HCl', 'Diabetes', 'Tablet', '500mg', 'HealthPlus', 18.75),
('Atorvastatin', 'Atorvastatin', 'Cholesterol', 'Tablet', '20mg', 'CardioMed', 22.99),
('Omeprazole', 'Omeprazole', 'Acid Reducer', 'Capsule', '20mg', 'DigestCare', 14.25),
('Albuterol', 'Albuterol Sulfate', 'Asthma', 'Inhaler', '90mcg', 'RespiraPharma', 35.00),
('Levothyroxine', 'Levothyroxine Sodium', 'Thyroid', 'Tablet', '50mcg', 'EndoHealth', 16.50),
('Gabapentin', 'Gabapentin', 'Nerve Pain', 'Capsule', '300mg', 'NeuroCare', 28.00);

-- Insert pharmacy inventory
INSERT INTO pharmacy_inventory (medication_id, quantity_available, reorder_level, expiry_date, last_restocked) VALUES
(1, 150, 20, '2026-12-31', '2024-11-01'),
(2, 200, 30, '2026-06-30', '2024-11-01'),
(3, 180, 25, '2026-09-30', '2024-11-01'),
(4, 120, 20, '2026-08-31', '2024-11-01'),
(5, 90, 15, '2025-12-31', '2024-11-01'),
(6, 45, 10, '2026-03-31', '2024-11-01'),
(7, 160, 25, '2026-11-30', '2024-11-01'),
(8, 75, 15, '2026-07-31', '2024-11-01');

-- Insert sample appointments
INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason, status, diagnosis) VALUES
(1, 1, '2024-12-02', '09:00:00', 'Annual checkup', 'COMPLETED', 'Patient in good health. Recommended annual follow-up.'),
(2, 1, '2024-12-02', '10:30:00', 'Flu symptoms', 'COMPLETED', 'Influenza. Prescribed rest and medication.'),
(3, 2, '2024-12-03', '14:00:00', 'High blood pressure', 'SCHEDULED', NULL),
(4, 1, '2024-12-05', '11:00:00', 'Diabetes follow-up', 'SCHEDULED', NULL);

-- Insert sample prescriptions
INSERT INTO prescriptions (appointment_id, patient_id, doctor_id, medication_id, dosage_instructions, quantity, status) VALUES
(2, 2, 1, 1, 'Take 1 capsule every 8 hours for 7 days', 21, 'PENDING'),
(1, 1, 1, 7, 'Take 1 tablet daily in the morning', 30, 'FILLED');

-- Test query
SELECT * FROM users;
SELECT * FROM patients;
SELECT * FROM medications;
