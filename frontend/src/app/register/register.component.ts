import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerRequest: any = {};
  message: string = '';

  constructor(private authService: AuthService, private router: Router) { }

  register() {
    this.authService.register(this.registerRequest).subscribe(
      (response) => {
        // Registration successful
        console.log('Registration successful', response);
        this.router.navigate(['/login'])
      },
      (error) => {
        // Registration failed
        console.error('Registration failed', error);
        this.message = error.error.message || 'Registration failed';
      }
    );
  }
  
}
