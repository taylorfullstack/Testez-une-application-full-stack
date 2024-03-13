# NumDev - Yoga App
![image](https://github.com/taylorfullstack/Testez-une-application-full-stack/assets/76629753/9d04e5a4-abe1-43b5-a499-4c1201355aea)

## Database Setup

This project uses MySQL for its database. Follow these steps to set up the database:

1. Install MySQL on your system. You can download it from the official MySQL website and follow the instructions there to install it.

2. Once MySQL is installed, you need to create a new database for the project. You can do this by logging into MySQL and running the command `CREATE DATABASE <database_name>;`, replacing `<database_name>` with the name of your database.

3. The `application.properties` file in the `back/src/main/resources` directory contains the configuration for the database connection. You need to ensure that the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` properties match your MySQL setup. The current configuration is as follows:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/<database_name>?allowPublicKeyRetrieval=true
    spring.datasource.username=<your_username>
    spring.datasource.password=<your_password>
    ```

    Replace `<database_name>`, `<your_username>`, and `<your_password>` with the name of your database, your MySQL username, and your MySQL password respectively.

## Application Installation

1. Fork this repository

    Click on the fork button in the top right corner of the [repository](https://github.com/taylorfullstack/Testez-une-application-full-stack) page.

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

## Launch the application

1. Frontend

    ```bash
    cd front
    npm run start
    ```

    The frontend will launch in your browser at `http://localhost:4200`
  
2. Backend

      ```bash
      cd back
      mvn spring-boot:run
      ```

    - The backend server will launch at `http://localhost:8080`

---

## Launch Tests - with coverage

### Frontend - Unit and Integration

```bash
cd front
npm run test
```


### Frontend - End-to-End

```bash
cd front
npm run e2e
```

### Backend - Unit and integration

1. Run the tests and generate a coverage report in the target/site/jacoco directory.

    ```bash
    cd back
    mvn test
    ```
    
2. Open the coverage report by navigating to the `target/site/jacoco` directory and opening the `index.html` file in a browser.
---

## Technologies
- Java
- Spring Boot
- Mockito
- Angular
- MySQL
- Jest
- Cypress
