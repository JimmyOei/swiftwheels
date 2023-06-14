import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  authRequest: any = {};
  message: string = ''

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.authService.authenticate(this.authRequest).subscribe(
      (response) => {
        // Login successful
        console.log('Login successful', response);
        this.router.navigate(['/home'])
      },
      (error) => {
        // Login failed
        console.error('Login failed', error);
        this.message = error.error.message || 'Login failed';
      }
    );
  }
}
