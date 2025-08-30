# Mini ATM Simulation (Java)

**Java Console-Based ATM Application with MySQL Database Integration**

---

## Overview
This project is a **console-based ATM simulation** built in Java.  
It demonstrates fundamental programming and software development skills including:

- **Object-Oriented Programming (OOP)**  
- **Java Collections (ArrayList, HashMap, etc.)**  
- **Database Connectivity (JDBC + MySQL)**  
- **CRUD operations & Transaction Management**  
- **Exception Handling**

The application supports both **User** and **Admin** functionalities and stores all data **persistently** in a MySQL database.

---

## Features

### User Features
- Login using **Account Number & PIN**  
- Check account balance  
- Deposit money into account  
- Withdraw money from account  
- View **transaction history**  

### Admin Features
- Login using admin password (`admin123`)  
- Create new user accounts with initial balance  
- View all existing accounts  

### Enhancements
- Persistent storage using MySQL database  
- Transaction logging with timestamps  
- Robust exception handling for database operations  

---

## Technology Stack
- **Language:** Java  
- **Database:** MySQL  
- **Library/API:** JDBC  
- **IDE:** IntelliJ, Eclipse, or VS Code  

---

## Database Setup
1. Create database and tables:

```sql
CREATE DATABASE atm_simulation;

USE atm_simulation;

CREATE TABLE accounts (
    account_number VARCHAR(20) PRIMARY KEY,
    pin VARCHAR(10) NOT NULL,
    balance DOUBLE NOT NULL
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20),
    type VARCHAR(20),
    amount DOUBLE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);

-- Sample accounts
INSERT INTO accounts VALUES ('1001', '1234', 5000);
INSERT INTO accounts VALUES ('1002', '4321', 3000);
