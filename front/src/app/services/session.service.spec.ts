import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { take } from 'rxjs/operators';
import { SessionService } from './session.service';
import { subscribeSpyTo } from "@hirez_io/observer-spy";
import { SessionInformation } from "../interfaces/sessionInformation.interface";
import { mockSessionInformation } from "../../test-constants";

describe('SessionService', () => {
  let service: SessionService;
  let testUser: SessionInformation;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SessionService]
    });

    service = TestBed.inject(SessionService);

    testUser = mockSessionInformation;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('logIn', () => {
    it('should set user session information and change logged in status to true', () => {
      service.logIn(testUser);

      expect(service.isLogged).toBe(true);
      expect(service.sessionInformation).toEqual(testUser);
    });
  });

  describe('logOut', () => {
    it('should clear user session information and change logged in status to false', () => {
      service.logOut();

      expect(service.isLogged).toBe(false);
      expect(service.sessionInformation).toBeUndefined();
    });
  });

  describe('$isLogged', () => {
    it('should emit the logged in status as an Observable boolean', async () => {
      const observerSpy = subscribeSpyTo(service.$isLogged().pipe(take(3)));

      expect(observerSpy.getValues()).toEqual([false]);

      service.logIn(testUser);
      expect(observerSpy.getValues()).toEqual([false, true]);

      service.logOut();
      expect(observerSpy.getValues()).toEqual([false, true, false]);

      await observerSpy.onComplete();
    });
  });
});
