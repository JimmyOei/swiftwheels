import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  message: string = ''

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    // Call the AuthService to send a POST request with the user's credentials
    this.authService.login(this.username, this.password)
      .subscribe(
        () => {
          // Login successful, navigate to the desired page
          this.router.navigate(['/home']);
        },
        error => {
          // Login failed, display error message or perform other actions
          console.error('Login failed:', error);
          this.message = error.error.message || 'Login failed';
        }
      );
  }
}
