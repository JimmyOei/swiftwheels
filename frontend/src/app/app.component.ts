import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LocalStorageService } from 'ngx-webstorage';

import { AuthService } from './services/auth.service';
import { UserService } from './services/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Swift Wheels'
  isMenuOpen = false;

  constructor(private router: Router, private authService: AuthService, private localStorage: LocalStorageService) {}

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  isLoggedIn(): boolean {
    return this.localStorage.retrieve('username') != null;
  }

  logout() {
    this.toggleMenu();
    this.router.navigate(['/login']);
    this.authService.logout();
  }
}
