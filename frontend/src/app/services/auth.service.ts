import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/v1/auth';
  private authenticated = false;
  private admin = false;

  constructor(private http: HttpClient) { }

  register(registerRequest: any) {
    return this.http.post(`${this.baseUrl}/register`, registerRequest);
  }

  authenticate(authRequest: any) {
    return this.http.post(`${this.baseUrl}/authenticate`, authRequest);
  }

  public setAuthenticated(authenticated: boolean) {
    this.authenticated = authenticated;
  }

  public isAuthenticated(): boolean {
    return this.authenticated;
  }

  public isAdmin(): boolean {
    return this.admin;
  }
}
