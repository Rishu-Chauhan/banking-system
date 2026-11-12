# 🏦 UniBankX – Bank Information Aggregator System

UniBankX is a full-stack web application that helps users explore and compare banking products and services from different banks in one place.

Users can compare Fixed Deposit (FD) rates, home loan rates, personal loan rates, account types, minimum balance requirements, and government schemes. An admin dashboard allows administrators to manage bank information dynamically.

## 🚀 Features

### 👤 User Features
- View information about multiple banks.
- Compare Fixed Deposit interest rates.
- Compare home loan and personal loan interest rates.
- Explore different bank account types.
- Check minimum balance requirements.
- View available government schemes.
- Access detailed information about individual banks.

### 🔐 Admin Features
- Secure admin login.
- Add new bank details.
- Update existing bank information.
- Delete bank records.
- Manage bank logos and details.
- Maintain banking information dynamically.

## 🛠️ Tech Stack

**Frontend**
- React.js
- Vite
- HTML5
- CSS3
- JavaScript

**Backend**
- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- REST APIs

**Database**
- MySQL

**Tools**
- IntelliJ IDEA
- Visual Studio Code
- MySQL Workbench
- Postman
- Git & GitHub

## 🏗️ Project Architecture

```text
UniBankX/
│
├── frontend/
│   ├── src/
│   │   ├── assets/
│   │   ├── components/
│   │   ├── pages/
│   │   └── App.jsx
│   └── package.json
│
├── backend/
│   ├── src/main/java/
│   │   └── ...
│   └── src/main/resources/
│       └── application.properties
│
└── README.md
```

*Adjust the folder names above to match your actual repository structure.*

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
cd UniBankX
```

### 2. Database Setup

- Install and start MySQL.
- Create a database:

```sql
CREATE DATABASE unibankx;
```

- Configure your database URL, username, and password in the backend's `application.properties`.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/unibankx
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Run the Backend

Open the backend project in IntelliJ IDEA and run the Spring Boot application.

Or use Maven:

```bash
mvn spring-boot:run
```

### 4. Run the Frontend

Open a terminal inside the frontend directory:

```bash
npm install
npm run dev
```

The Vite development server will display the frontend URL in your terminal, usually:

```text
http://localhost:5173
```

## 🔄 Application Workflow

1. Users open the UniBankX homepage.
2. Users explore available banks.
3. Users compare banking rates and account information.
4. Users open individual bank details.
5. Admin logs in to the dashboard.
6. Admin manages bank information through CRUD operations.

## 🎯 Project Objective

The main objective of UniBankX is to simplify banking information discovery by providing a centralized platform where users can explore and compare banking products without visiting multiple bank websites.

## 👨‍💻 Developer

**Rishu Chauhan**

- GitHub: [Your GitHub Profile](https://github.com/)
- LinkedIn: [Your LinkedIn Profile](https://www.linkedin.com/)

---

⭐ If you find this project useful, consider giving it a star!

**Note:** This project is developed for educational and demonstration purposes. Banking rates and schemes should be verified with the respective banks before making financial decisions.
