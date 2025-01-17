# Copaco Configurator Backend

This repository hosts the backend implementation for the **Copaco Configurator** project. The goal of this project is to develop a scalable, user-friendly web application that allows users to configure end products based on component compatibility, enhancing efficiency and customer experience.

---

## Project Overview

The backend serves as the core API layer of the Configurator application. It handles data management, component compatibility rules, and communication with the frontend via RESTful APIs. The backend is built with scalability and security in mind to support a variety of industries and ensure compliance with GDPR regulations.

---

## Features

- **Product Configuration**: Enables users to configure products by selecting compatible components.
- **CRUD Operations**: Manage product templates, components, and configurations.
- **Scalability**: Designed to handle a growing number of products and users.
- **Security**: Implements secure data handling with measures to meet GDPR compliance.
- **Testing**: Includes unit tests, acceptance tests, and security tests.
- **CI/CD Integration**: Automated testing and deployment pipelines.

---

## Architecture

The backend is built with:
- **Language**: Java
- **Framework**: Spring Boot
- **Database**: Mssql
- **Versioning**: Flyway for database version control
- **Dependency Management**: Gradle

### Key Technologies
- **Spring Security**: For authentication and role-based access control.
- **Mssql**: Structured data storage.
- **Docker**: Containerized deployment.
- **GitLab**: Version control and CI/CD pipelines.

---

## Installation and Setup

1. Clone the repository:
   ```bash
   git clone git clone git@projects.fhict.nl:s3-cb/fall-24/cb02-copaco/group3/copacoproject.git
   cd Copaco/backend
   ./gradlew build
   ./gradlew bootRun

