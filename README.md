# JBoss EAP Quickstart Parent Modernized

This repository contains the modernized version of the JBoss EAP Quickstart applications, updated to use Spring Boot. The goal is to provide a seamless transition from JBoss EAP to a modern Spring Boot-based architecture.

## Table of Contents
- Introduction
- Prerequisites
- Running the Application
- Installation
- Access the Application
- API Documentation

## Introduction
This project demonstrates the modernization of JBoss EAP Quickstart applications to Spring Boot. 
Project is build on Spring Boot 3 with Java 21 and Gradle 8, showcasing a comprehensive and fully-functional backend microservice that incorporates various features and best practices. 
Leveraging MongoDB as its primary database, this application demonstrates the benefits of migrating to Spring Boot, including improved flexibility, streamlined development, and a modernized architecture.

## Prerequisites
- **Java 21**: Ensure you have JDK 21 installed on your machine.
    - [Download JDK 21](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)
- **Gradle 8.8 or later**: Ensure you have Gradle 8.8 or later installed.
    - [Download Gradle](https://gradle.org/install/)
- An IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Running Application

Follow the following steps to run the application locally:
1. Clone the repository:
  ```sh
  git clone https://github.com/Parthmalhotra96/jboss-eap-quickstart-parent-modernized.git
  cd jboss-eap-quickstart-parent-modernized
  ```

2. Build the project using Gradle:
  ```sh
  ./gradlew clean build
  ```

3. Running the Application
To run the application locally, use the following command:
  ```sh
  ./gradlew bootRun
  ```

4. Running Tests
To run unit tests, execute the following command:
  ```
  ./gradlew test
  ./gradlew integrationTest
  ```

### Setup MongoDB
To set up MongoDB on your local machine, follow these steps:

1. **Install MongoDB**

  - **On macOS**: You can use Homebrew to install MongoDB. Run the following command:

    ```bash
    brew tap mongodb/brew
    brew install mongodb-community
    ```

  - **On Windows**: Download the MongoDB installer from the [official MongoDB website](https://www.mongodb.com/try/download/community) and follow the installation instructions.

2. **Start MongoDB Service**

  - **On macOS** (using Homebrew services):

    ```bash
    brew services start mongodb-community
    ```

  - **On Windows**:

    Open Command Prompt or PowerShell as Administrator and run:

    ```bash
    net start MongoDB
    ```

3. **Verify MongoDB is Running**

   To ensure MongoDB is running correctly, you can use the following command to connect to the MongoDB shell:

   ```bash
   mongo
  
Update the following MongoDB connection details in application.properties located in src/main/resources/.
```
kitchensink.mongodb.hostname
kitchensink.mongodb.port
kitchensink.mongodb.databasename
```
The default configuration is set to connect to a MongoDB instance running on localhost with the hostname localhost, port 27017, 
databasename members.

Alternatively you can use 
```
spring.data.mongodb.uri=mongodb://localhost:27017/members
```

## Access the Application
Once the application is running, you can access the API at http://localhost:8090. 
Use tools like Postman or curl to interact with the endpoints.

## API Documentation

The API documentation for this application can be accessed through Swagger UI. 
You can browse and test the API endpoints directly using the following link:
  [Swagger UI](http://localhost:8081/kitchensink/swagger-ui/index.html)

### Endpoints
  #### **Get All Members**
  
  * **Endpoint**: `GET http://localhost:8090/rest/members`
  * **Description**: Returns a list of all members
  * **Response**:
  	+ 200: Members found
  	+ 500: Internal server error
  
  #### **Create a New Member**
  
  * **Endpoint**: `POST http://localhost:8090/rest/members`
  * **Description**: Creates a new member
  * **Request Body**: `MemberRequestDTO` (JSON)
  * **Response**:
  	+ 201: Member created
  	+ 400: Invalid request
  	+ 409: Email already taken
  	+ 500: Internal server error
  
  #### **Get Member by ID**
  
  * **Endpoint**: `GET http://localhost:8090/rest/members/{id}`
  * **Description**: Returns a member by ID
  * **Path Parameters**:
  	+ `id`: ID of member to be retrieved (integer)
  * **Response**:
  	+ 200: Member found
  	+ 404: Member not found
  	+ 500: Internal server error
  
  #### **Delete a Member**
  
  * **Endpoint**: `DELETE http://localhost:8090/rest/members/{id}`
  * **Description**: Deletes a member by ID
  * **Path Parameters**:
  	+ `id`: ID of member to be deleted (integer)
  * **Response**:
  	+ 200: Member deleted
  	+ 404: Member not found
  	+ 500: Internal server error
  
  #### **Get All Members (UI)**
  
  * **Endpoint**: `GET http://localhost:8090/members`
  * **Description**: Returns a list of all members (HTML)
  * **Response**:
  	+ 200: Members found
  	+ 500: Internal server error
  
  #### **Create a New Member (UI)**
  
  * **Endpoint**: `POST http://localhost:8090/members`
  * **Description**: Creates a new member (UI)
  * **Request Parameters**:
  	+ `member`: Member details (JSON)
  * **Response**:
  	+ 201: Member created
  	+ 400: Invalid request
  	+ 409: Email already taken
  	+ 500: Internal server error
  
  #### **Delete a Member (UI)**
  
  * **Endpoint**: `POST http://localhost:8090/members/{id}/delete`
  * **Description**: Deletes a member by ID (HTML)
  * **Path Parameters**:
  	+ `id`: ID of member to be deleted (integer)
  * **Response**:
  	+ 200: Member deleted
  	+ 404: Member not found
  	+ 500: Internal server error

### API Security

APIs are secured through Basic Authentication.
Following table depicts the role and their Accessible Endpoints

Role | Accessible REST Endpoint | Accessible UI Endpoints 
--- | --- | ---
USER | `GET http://localhost:8090/rest/members`<br /> `POST http://localhost:8090/rest/members`<br/> `GET http://localhost:8090/rest/members/{id}`<br/> | `GET http://localhost:8090/members`<br/> `POST http://localhost:8090/members`
ADMIN | `GET http://localhost:8090/rest/members`<br /> `POST http://localhost:8090/rest/members`<br/> `GET http://localhost:8090/rest/members/{id}`<br/> `DELETE http://localhost:8090/rest/members/{id}` | `GET http://localhost:8090/members`<br/> `POST http://localhost:8090/members`<br/> `POST http://localhost:8090/members/{id}/delete` 
