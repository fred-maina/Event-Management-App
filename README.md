# Event Management Application

## Overview
The Event Management Application is a Spring Boot-based platform that enables users to create, manage, and view events. The application features secure authentication using JWT, efficient media storage with AWS S3, and a MySQL relational database for robust data handling.

## Features
- **Secure Authentication:** Implements JWT for user login and role-based access control.
- **Event Management:** Users can create, delete, and fetch events by creator or globally.
- **Scalable Media Storage:** AWS S3 is integrated for event poster uploads and retrieval.
- **Dynamic API:** Provides RESTful endpoints for event and user management.

## Tech Stack
- **Backend:** Spring Boot
- **Database:** MySQL
- **Cloud Storage:** AWS S3
- **Authentication:** JSON Web Tokens (JWT)

## Prerequisites
- **Java 17**
- **MySQL**
- **AWS Account** with an S3 bucket configured

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/fred-maina/event-management-app.git
cd event-management-app
```

### Configure the Application
1. **AWS Credentials:** Set the following environment variables:
   - `AWS_REGION`: Your AWS region (e.g., `us-east-1`)
   - `AWS_ACCESS_KEY_ID`: Your AWS access key
   - `AWS_SECRET_ACCESS_KEY`: Your AWS secret key

2. **MySQL Database:** Update the `application.properties` file with your MySQL configuration:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/event_management
   spring.datasource.username=YOUR_DB_USERNAME
   spring.datasource.password=YOUR_DB_PASSWORD
   spring.jpa.hibernate.ddl-auto=update
   ```

### Build and Run the Application
```bash
./mvnw spring-boot:run
```

## API Endpoints and Examples

### Authentication

#### Register User
**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securepassword",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "token": "<JWT_TOKEN>"
}
```

#### Login User
**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securepassword"
}
```

**Response:**
```json
{
  "success": true,
  "token": "<JWT_TOKEN>"
}
```

### Event Management

#### Create Event
**Endpoint:** `POST /api/events/create`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Request (Multipart Form Data):**
- `event`: JSON object for event details
  ```json
  {
    "eventName": "Tech Conference",
    "eventStartDate": "2025-01-10",
    "eventEndDate": "2025-01-12",
    "eventVenue": "Online",
    "eventCapacity": 500
  }
  ```
- `poster`: Event poster file

**Response:**
```json
{
  "success": true,
  "message": "Event created successfully",
  "data": {
    "id": "<EVENT_ID>",
    "eventName": "Tech Conference",
    "posterUrl": "<FILE_URL>"
  }
}
```

#### Fetch All Events
**Endpoint:** `GET /api/events/get/`

**Response:**
```json
{
  "success": true,
  "message": "Events Fetched Successfully",
  "data": [
    {
      "id": "<EVENT_ID>",
      "eventName": "Tech Conference",
      "eventStartDate": "2025-01-10",
      "eventEndDate": "2025-01-12",
      "eventVenue": "Online",
      "eventCapacity": 500
    }
  ]
}
```

#### Fetch Events by Creator
**Endpoint:** `GET /api/events/get/{creator_id}`

**Response:**
```json
{
  "success": true,
  "message": "Events fetched successfully",
  "data": [
    {
      "id": "<EVENT_ID>",
      "eventName": "Tech Conference",
      "eventStartDate": "2025-01-10",
      "eventEndDate": "2025-01-12",
      "eventVenue": "Online",
      "eventCapacity": 500
    }
  ]
}
```

#### Delete Event
**Endpoint:** `DELETE /api/events/delete/{id}`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response:**
```json
{
  "success": true,
  "message": "Event Deleted successfully"
}
```

#### Upload File to S3
**Endpoint:** `POST /api/events/upload`

**Request (Multipart Form Data):**
- `file`: File to upload

**Response:**
```json
{
  "url": "<FILE_URL>"
}
```

## Deployment
- Ensure your environment variables are set in your hosting platform (e.g., AWS Elastic Beanstalk, Heroku).
- Package the application:
  ```bash
  ./mvnw package
  ```
- Deploy the generated `.jar` file.

## Contributing
Contributions are welcome! Please create a pull request with a detailed explanation of your changes.

## Contact
**Fredrick Maina**
- Email: [fchege04@gmail.com](mailto:fchege04@gmail.com)
- GitHub: [fred-maina](https://github.com/fred-maina)
