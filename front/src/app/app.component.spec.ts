import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { AuthService } from './features/auth/services/auth.service';
import { SessionService } from './services/session.service';
import { expect } from '@jest/globals';
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { MatToolbarModule } from '@angular/material/toolbar';
import { NgZone } from "@angular/core";

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let sessionService: SessionService;
  let router: Router;
  let ngZone: NgZone;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule, MatToolbarModule],
      declarations: [AppComponent],
      providers: [AuthService, SessionService]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    ngZone = TestBed.inject(NgZone);
  });

  it('should create the app with $isLogged as false', () => {
    expect(component).toBeTruthy();
    component.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(false);
    });
  });

  it('should call logOut method of SessionService and navigate to the root route', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    jest.spyOn(sessionService, 'logOut');
    ngZone.run(() => {
      component.logout();
      expect(sessionService.logOut).toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledWith(['']);
    });
  });
});
