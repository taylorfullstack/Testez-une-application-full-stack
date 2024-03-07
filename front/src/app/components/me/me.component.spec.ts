import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';

import { Router } from '@angular/router';
import { of } from 'rxjs';
import { UserService } from '../../services/user.service';
import { MeComponent } from './me.component';
import { User } from "../../interfaces/user.interface";
import { expect } from '@jest/globals';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userServiceMock: Partial<UserService>;
  let sessionServiceMock: Partial<SessionService>;

  const routerMock : Partial<Router> = {
    navigate: jest.fn(),
  };

  const matSnackBarMock : Partial<MatSnackBar> = {
    open: jest.fn(),
  };
  const mockAdmin: User = {
    id: 1,
    email: "admin@test.com",
    lastName: "adminLastName",
    firstName: "adminFirstName",
    admin: true,
    password: "password",
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockUser: User = {
    ...mockAdmin,
    id: 2,
    email: "user@test.com",
    lastName: "userLastName",
    firstName: "userFirstName",
    admin: false,
  };

  function setupUserServiceMock(user: User): Partial<UserService> {
    return {
      getById: jest.fn(() => of(user)),
      delete: jest.fn(() => of(null)),
    };
  }

  function setupSessionServiceMock(user: User): Partial<SessionService> {
    return {
      sessionInformation: {
        id: user.id,
        admin: user.admin,
        token: 'mockToken',
        type: 'mockType',
        username: user.email,
        firstName: user.firstName,
        lastName: user.lastName
      },
      logOut: jest.fn(() => of({})),
      isLogged: true,
    };
  }

  function setupTestBed(user: User) {
    userServiceMock = setupUserServiceMock(user);
    sessionServiceMock = setupSessionServiceMock(user);

    TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [MatSnackBarModule],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: UserService, useValue: userServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: matSnackBarMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  describe('when user is an admin', () => {
    beforeEach(() => {
      setupTestBed(mockAdmin);
    });

    it('should display the admin data', () => {
      expect(component.user).toEqual(mockAdmin);
    });

    it('should not allow the admin to delete their account', () => {
      const buttons = Array.from(fixture.nativeElement.querySelectorAll('button'));
      const deleteButton = buttons.find(button => {
        if (button instanceof HTMLButtonElement) {
          return button.textContent?.includes('delete');
        }
        return false;
      });
      expect(deleteButton).toBeFalsy();
    });
  });

  describe('when user is a regular user', () => {
    beforeEach(async () => {
      setupTestBed(mockUser);
    });

    it('should display the user data', () => {
      expect(component.user).toEqual(mockUser);
    });

    it('should display the delete button', () => {
      const buttons = Array.from(fixture.nativeElement.querySelectorAll('button'));
      const deleteButton = buttons.find(button => {
        if (button instanceof HTMLButtonElement) {
          return button.textContent?.includes('delete');
        }
        return false;
      });
      expect(deleteButton).toBeTruthy();
    });

    it('should delete the user account and navigate to home when the delete button is clicked', (() => {
      const navigateSpy = jest.spyOn(routerMock, 'navigate');
      const snackBarSpy = jest.spyOn(matSnackBarMock, 'open');
      component.delete();
      expect(userServiceMock.delete).toHaveBeenCalledWith(mockUser.id.toString());
      expect(snackBarSpy).toHaveBeenCalledWith("Your account has been deleted !", 'Close', { duration: 3000 });
      expect(sessionServiceMock.logOut).toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledWith(['/']);
      navigateSpy.mockRestore();
    }));
  });

  it('should navigate back when back button is clicked', () => {
    const backSpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(backSpy).toHaveBeenCalled();
    backSpy.mockRestore();
  });
});
