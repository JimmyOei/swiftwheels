import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, catchError, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}


  canActivate(): Observable<boolean> {
      return this.authService.isAuthorizedUser().pipe(
        tap((response) => {
          if (!response) {
            this.router.navigate(['/login']);
          }
        })
      );
  }
}
