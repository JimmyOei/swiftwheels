import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map } from 'rxjs/operators';
import { Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminAuthGuard implements CanActivate {
  
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): Observable<boolean> {
    return this.authService.isAuthorizedAdmin().pipe(
      map(isAuthorized => {
        if(isAuthorized) {
          console.log("Authorized admin");
          return true;
        } 
        else {
          console.log("Unauthorized admin");
          this.router.navigate(['/login']);
          return false;
        }
      })
    );
  }
}
