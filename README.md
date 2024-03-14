# NumDev - Yoga App
![image](https://github.com/taylorfullstack/Testez-une-application-full-stack/assets/76629753/9d04e5a4-abe1-43b5-a499-4c1201355aea)

## Prerequisites

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/)
- [Node.js and npm](https://nodejs.org/en/)

## Application Installation

1. Fork this repository

    - Click on the fork button in the top right corner of [this repository page](https://github.com/taylorfullstack/Testez-une-application-full-stack).

2. Clone the forked respository to your local machine

    - Replace the url below with the url of your forked repository.

        `https://github.com/your-username/project-name.git`

    - Open a terminal window and navigate to the directory that will store your project.

        ```bash
        cd project-name
        ```

    - In your terminal, run the command below. The dot at the end will clone the project into the current directory.

        ```bash
        git clone https://github.com/your-username/project-name.git .
        ```

3. Install the frontend dependencies

    - In your terminal, run the command below to install the dependencies listed in the package.json file.
    
        ```bash
        cd front
        npm install
        ```

4. Install the backend dependencies

    - In your terminal, run the command below to install the dependencies listed in the pom.xml file.
    
      ```bash
      cd back
      mvn clean install
      ```
      
## Database Setup

This project uses MySQL for its database. Follow these steps to set up the database:

1. Install MySQL on your system.

2. Once MySQL is installed, create a new database for the project.

   - Login into MySQL and running the command `CREATE DATABASE <database_name>;`, replacing `<database_name>` with the name of your database.

3.  Configure the `back/src/main/resources/application.properties` file for the database connection.
  
    - You need to ensure that the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` properties match your MySQL setup.

         ```properties
         spring.datasource.url=jdbc:mysql://localhost:3306/<database_name>?allowPublicKeyRetrieval=true
         spring.datasource.username=<your_username>
         spring.datasource.password=<your_password>
         ```

    - Replace `<database_name>`, `<your_username>`, and `<your_password>` with the name of your database, your MySQL username, and your MySQL password respectively.

4. Set up the database schema and initial data in your MySQL environment.
 
     The SQL script to create the necessary tables and populate them with initial data is located at `ressources/sql/script.sql`
     
     - Log into MySQL with your username and password.
     - Select your database by running the command ` USE <database_name>;`, replacing `<database_name>` with the name of your database.
     - Run the script by typing `source <path_to_script.sql>;`, replacing `<path_to_script.sql>` with the path to the script.sql file.
     
     For example, if your project is located in the `project_name` directory in your home directory, the command would be source `~/project_name/ressources/sql/script.sql;`
   
---

## Launch the application

1. Frontend
   
   - In your terminal, run the command below.
    
        ```bash
        cd front
        npm run start
        ```

     The frontend will launch in your browser at `http://localhost:4200`
  
2. Backend

     - In a separate terminal, run the command below.

          ```bash
          cd back
          mvn spring-boot:run
          ```

        The backend server will launch at `http://localhost:8080`

---

## Launch Tests

### Frontend - Unit and Integration

1. Run the tests and generate a coverage report.

    ```bash
    cd front
    npm run test
    ```

2. Open the coverage report by navigating to the `front/coverage/jest/lcov-report` directory and opening the `index.html` file in a browser.

### Frontend - End-to-End

1. Run the tests in the Cypress Test Runner.

    ```bash
    cd front
    npm run e2e
    ```

2. Run all of the end-to-end tests in the terminal and generate a coverage report.

    ```bash
    cd front
    npm run e2e:ci
    npm run e2e:coverage
    ```

### Backend - Unit and integration

1. Run the tests and generate a coverage report.

    ```bash
    cd back
    mvn clean test
    ```
    
2. Open the coverage report by navigating to the `back/target/site/jacoco` directory and opening the `index.html` file in a browser.
   
---

## Technologies
- Java
- Spring Boot
- Mockito
- Angular
- MySQL
- Jest
- Cypress
