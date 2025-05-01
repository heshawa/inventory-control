# Inventory control

Spring boot project to illustrate spring boot capabilities

## 📦 Technologies Used

- Java 17
- Spring Boot 3.x
- Gradle (or Maven)

## 🏁 Getting Started

### ✅ Prerequisites

Make sure you have:

- Java 17+
- Gradle installed (`./gradlew` wrapper is also included)
- Add Lombok plugin to your IDE to avoid Lombok IDE error indications


### 🔧 Running the App

- If your default java version is not Java 17
  - Update .project-env file with your java 17 path
  - Execute command
      ```bash 
      source .project-env
      ```
- Execute following commands
     ```bash
     ./gradlew clean build
     ./gradlew bootRun
    ```

### 📖 API Guide

#### hello API
- **GET** `/hello`  
  Returns a simple "Hello, World!" message.

- **GET** `/hello/{name}`  
  Returns a personalized greeting message with the provided `name`.

#### inventory API
- **POST** `/inventory/add` - HTTP Response Code: **200**
  Creates and adding a new inventory item. Returns the created item details.
  - Request sample 1 - success
    ```javascript
    Request Body
    {
      "name":"Screw driver",
      "description": "3mm flower head",
      "price": 15.50,
      "quantity":25
    }
    ```
    ```javascript
    Response - HTTP/1.1 200
    Content-Type: application/json
    {
      "name": "Sample Item",
      "description": "This is a sample item.",
      "price": "19.99",
      "quantity": "10"
    }
    ```
  - Request sample 2 - no content
    ```javascript
    Request Body
    {}
    ```
    ```javascript
    HTTP/1.1 204
    ```



Clone the repository:

```bash
git clone https://github.com/heshawa/inventory-control.git
cd your-repo
