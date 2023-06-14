import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map } from 'rxjs/operators';

import { LocalStorageService } from 'ngx-webstorage';
import { ReservedVehicleResponse } from '../interfaces/reservedvehicleresponse';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient, private localStorage: LocalStorageService) { }

  getReservedVehicle() {
    const token = this.localStorage.retrieve('token');
    const username = this.localStorage.retrieve('username');

    if(!token || !username) {
      return null;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<ReservedVehicleResponse>(`${this.baseUrl}/reserved`, username, { headers }).pipe(
        map((data) => {
          if(data && data.vehicle_reserved) {
            this.localStorage.store('vehicle_reserved', data.vehicle_reserved);
            this.localStorage.store('vehicle_name', data.vehicle_name);
            this.localStorage.store('vehicle_id', data.vehicle_id);
          } 
          else {
            this.localStorage.store('vehicle_reserved', data.vehicle_reserved);
            this.localStorage.clear('vehicle_name');
            this.localStorage.clear('vehicle_id');
          }
  
          return true;
        })
      );
  }
}
