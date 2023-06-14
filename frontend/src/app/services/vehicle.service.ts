import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Vehicle } from '../interfaces/vehicle.interface';
import { MapBoundaries } from '../interfaces/mapboundaries.interface';


import { LocalStorageService } from 'ngx-webstorage';
import { ReserveVehicleRequest } from '../interfaces/reservevehiclerequest.interface';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {
  private baseUrl = 'http://localhost:8080/api/vehicles';

  constructor(private http: HttpClient, private localStorage: LocalStorageService) { }

  getMapBounderies() {
    return this.http.get<MapBoundaries>(`${this.baseUrl}/bounds`);
  }

  getAllAvailableVehicles() {
    return this.http.get<Vehicle[]>(`${this.baseUrl}/available`);
  }

  reserveVehicle(id: number) {
    const token = this.localStorage.retrieve('token');

    if(!token) {
      return null;
    }

    let request: ReserveVehicleRequest = {
      token: token,
      vehicle_id: id
    };

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post(`${this.baseUrl}/reserve`, request, { headers });
  }

  releaseVehicle() {
    if(this.localStorage.retrieve('vehicle_reserved') == false) {
      return null;
    }
    const vehicle_id = this.localStorage.retrieve('vehicle_id')
    const token = this.localStorage.retrieve('token');

    if(!token || !vehicle_id) {
      return null;
    }

    let request: ReserveVehicleRequest = {
      token: token,
      vehicle_id: vehicle_id
    };

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post(`${this.baseUrl}/release`, request, { headers });
  }
}
