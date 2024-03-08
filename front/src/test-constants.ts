// Mocks for testing purposes
import { User } from "./app/interfaces/user.interface";
import { SessionInformation } from "./app/interfaces/sessionInformation.interface";
import { LoginRequest } from "./app/features/auth/interfaces/loginRequest.interface";
import {Teacher} from "./app/interfaces/teacher.interface";

export const mockAdmin: User = {
  id: 1,
  email: "admin@test.com",
  lastName: "adminLastName",
  firstName: "adminFirstName",
  admin: true,
  password: "password",
  createdAt: new Date(),
  updatedAt: new Date(),
};

export const mockUser: User = {
  ...mockAdmin,
  id: 2,
  email: "user@test.com",
  lastName: "userLastName",
  firstName: "userFirstName",
  admin: false,
};

export const mockSessionInformation: SessionInformation = {
  id: 1,
  admin: true,
  token: "mockToken",
  type: "mockType",
  username: "mockUsername",
  firstName: "mockFirstName",
  lastName: "mockLastName",
}

export const mockTeacher: Teacher = {
  id: 1,
  lastName: "teacherLastName",
  firstName: "teacherFirstName",
  createdAt: new Date(),
  updatedAt: new Date(),
}

export const mockLoginRequest: LoginRequest = {
  email: "email@test.com",
  password: "password"
}
