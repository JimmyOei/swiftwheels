import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';


import { LocalStorageService } from 'ngx-webstorage';
import { map, catchError } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { logoutRequest } from '../interfaces/logoutrequest.interface';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private localStorage: LocalStorageService) { }

  register(registerRequest: any) {
    return this.http.post(`${this.baseUrl}/auth/register`, registerRequest).pipe(
      map((data: any) => {
        this.localStorage.store('username', data.username);
        this.localStorage.store('role', data.role);
        this.localStorage.store('token', data.token);

        return true;
      })
    );
  }

  authenticate(authRequest: any) {
    return this.http.post(`${this.baseUrl}/auth/authenticate`, authRequest).pipe(
      map((data: any) => {
        this.localStorage.store('username', data.username);
        this.localStorage.store('role', data.role);
        this.localStorage.store('token', data.token);

        return true;
      })
    );
  }

  logout() {
    var username = this.localStorage.retrieve('username');
    
    if(!username) {
      return;
    }

    const logoutRequest: logoutRequest = {
      username: username
    };

    this.http.post(`${this.baseUrl}/auth/logout`, logoutRequest).subscribe(
      (response: any) => {
        console.log(response.message);
      },
      (error) => {
        console.log(error.error);
      }
    );
    this.localStorage.clear();
  }

  isAuthorizedUser(): Observable<boolean> {
    const token = this.localStorage.retrieve('token');
  
    if(!token) {
      return of(false);
    }
  
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  
    return this.http.post(`${this.baseUrl}/for/user`, null, { headers, observe: 'response' }).pipe(
      map(response => {
        return response.status === 200;
      }),
      catchError(error => {
        return of(false);
      })
    );
  }

  isAuthorizedAdmin(): Observable<boolean> {
    const token = this.localStorage.retrieve('token');
  
    if(!token) {
      return of(false);
    }
  
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  
    return this.http.post(`${this.baseUrl}/for/admin`, null, { headers, observe: 'response' }).pipe(
      map(response => {
        return response.status === 200;
      }),
      catchError(error => {
        return of(false);
      })
    );
  }
}
