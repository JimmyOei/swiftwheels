import { Component } from '@angular/core';
import { UserService } from '../services/user.service';
import { User } from '../interfaces/user.interface';
import { Router } from '@angular/router';
import { EditUser } from '../interfaces/edituser.interface';

import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent {
  users: User[] = [];

  constructor(private userService: UserService, private router: Router) {
    this.updateUsersList();
  }

  goToAdminPage() {
    this.router.navigate(['/admin']);
  }

  updateUsersList() {
    const getResponse = this.userService.getAllUsers();
    if(!getResponse) {
      console.log("Failed fetching user database, because local token is missing.");
      return;
    }
  
    getResponse.subscribe(
      (response) => {
        this.users = response;
      },
      (error) => {
        console.error(error.error);
      }
    );
  }

  editUser(userId: number) {
    const foundUser = this.users.find((user) => user.id === userId);
    if (!foundUser) {
      console.log("User not found locally, failed to edit");
      return;
    }
  
    const editUser: EditUser = {
      user_id: foundUser.id,
      role: foundUser.role
    };
  
    this.userService.editUserRole(editUser)?.pipe(
      finalize(() => {
        this.updateUsersList();
      })
    ).subscribe(
      (response: any) => {
        console.log(response.message);
      },
      (error) => {
        console.error(error.error);
      }
    );
  }
  

  deleteUser(userId: number) {
    this.userService.deleteUser(userId)?.subscribe(
      (response: any) => {
        console.log(response.message);
        this.updateUsersList();
      },
      (error) => {
        console.error(error.error);
      }
    );
  }
}
