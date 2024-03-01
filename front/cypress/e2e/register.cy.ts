/// <reference types="Cypress" />
describe('Register spec', () => {
  const user = {
    id: 1,
    email: 'email@test.com',
    firstName: 'Emma',
    lastName: 'Lee',
    password: 'pass!1234',
    admin: false,
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  beforeEach(() => {
    cy.visit('/register');
  });

  it('should indicate an error if required fields are missing', () => {
    cy.get('input[formControlName=firstName]').type(user.firstName)
    cy.get('input[formControlName=lastName]').type(user.lastName)
    cy.get('input[formControlName="email"]').type(`{enter}`);
    cy.get('input[formControlName=password]').type(user.password)
    cy.get('input[formControlName="email"]').should('have.class', 'ng-invalid');

    cy.get('button[type=submit]').should('be.disabled')
  })

  it('should register, login, and check user account details', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
      body: user,
    }).as('register')

    cy.get('input[formControlName=firstName]').type(user.firstName)
    cy.get('input[formControlName=lastName]').type(user.lastName)
    cy.get('input[formControlName=email]').type(user.email)
    cy.get('input[formControlName=password]').type(user.password)

    cy.get('button[type=submit]').click()

    cy.url().should('include', '/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 201,
      body: user,
    }).as('login')

    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: user,
    }).as('getSession')

    cy.get('input[formControlName=email]').type(user.email)
    cy.get('input[formControlName=password]').type(`${user.password}{enter}{enter}`)

    cy.intercept('GET', `/api/user/${user.id}`, {
      statusCode: 200,
      body: user,
    }).as('getUser')

    cy.get('span[routerLink=me]').click();

    cy.url().should('include', '/me');

    cy.get('.m3 mat-card-content p').contains(`Name: ${user.firstName} ${user.lastName.toUpperCase()}`).should('exist');
    cy.get('.m3 mat-card-content p').contains(`Email: ${user.email}`).should('exist');
    cy.get('.m3 mat-card-content div.my2').should('exist');
  });
})

