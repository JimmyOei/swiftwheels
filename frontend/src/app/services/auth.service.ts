import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';


import { LocalStorageService } from 'ngx-webstorage';
import { map, catchError } from 'rxjs/operators';
import { Observable, of } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient, private localStorage: LocalStorageService) { }

  register(registerRequest: any) {
    return this.http.post(`${this.baseUrl}/register`, registerRequest);
  }

  authenticate(authRequest: any) {
    return this.http.post(`${this.baseUrl}/authenticate`, authRequest).pipe(
      map((data: any) => {
        this.localStorage.store('username', data.usernmae);
        this.localStorage.store('role', data.role);
        this.localStorage.store('token', data.token);

        return true;
      })
    );
  }

  isAuthorizedUser(): Observable<boolean> {
    const token = this.localStorage.retrieve('token');

    if(!token) {
      return of(false);
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post(`${this.baseUrl}/forUser`, null, { headers }).pipe(
      map(() => true),
      catchError(() => of(false))
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

    return this.http.post(`${this.baseUrl}/forAdmin`, null, { headers }).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }
}
