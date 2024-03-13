# NumDev - Yoga App
![image](https://github.com/taylorfullstack/Testez-une-application-full-stack/assets/76629753/9d04e5a4-abe1-43b5-a499-4c1201355aea)


## Prerequisites

- 🚧

## Database Installation

- 🚧

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
