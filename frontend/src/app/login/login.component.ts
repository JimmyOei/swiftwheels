import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { LocalStorageService } from 'ngx-webstorage';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  authRequest: any = {};
  message: string = ''

  constructor(private authService: AuthService, private router: Router, private localStorage: LocalStorageService) {}

  login() {
    this.authService.authenticate(this.authRequest).subscribe(
      (response) => {
        // Login successful
        console.log('Login successful', response);
        const role = this.localStorage.retrieve('role');
        if(role != null && role == 'ROLE_ADMIN') {
          this.router.navigate(['/admin']);
        }
        else {
          this.router.navigate(['/home']);
        }
      },
      (error) => {
        // Login failed
        console.error('Login failed', error);
        this.message = error.error.message || 'Login failed';
      }
    );
  }
}
