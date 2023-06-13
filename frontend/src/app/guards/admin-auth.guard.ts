import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, catchError, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminAuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}


  canActivate(): Observable<boolean> {
    return this.authService.isAuthorizedAdmin().pipe(
      tap((response) => {
        if (!response) {
          this.router.navigate(['/login']);
        }
      })
    );
}
}
