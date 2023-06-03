# Plan

## Ideas
- A Felyx/Lime/Bird like vehicle renting service for one city (Den Haag)
- A parking lot/garage app
- An app for tracking available company cars

## General

### General
- 0 <= available vehicles <= total vehicles
- All events need to be logged
- A user login system
  - **Guest**:
    - Register an account
  - **User**:
    - Login
    - Logout
    - View all available vehicles
    - Reserve/use **one** specific available vehicle
    - Release current vehicle
  - **Administrator**:
    - Inherits user permissions
    - Modify vehicle data

### User Interface
- **Login** page:
  - Login: username, password
  - Go to *register* page
- **Register** page:
  - Register: username, password
  - Go to *login* page
- **Home** page:
  - View data of all vehicles
  - See total vehicles
  - See total available vehicles
  - Reserve a specific vehicle
  - Release current vehicle
  - See if and what vehicle user is currently using
- **Admin** page:
  - Modify vehicle data

### Business Logic
- Login functionality
- Register functionality
- Reserve vehicle functionality
- Release vehicle functionality
- Retrieve vehicle data
- Modify vehicle data

### Database
- **User**
  - Username
  - Reserved vehicle
  - Admin
- **Vehicle**
  - Name
  - Available

## Quality requirements
- Microservice
- Logging
- Monitoring
- Authorization and Authentication
- Dynamic Configuration
- Security
- Testing
- Dockerized