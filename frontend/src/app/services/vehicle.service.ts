import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Vehicle } from '../interfaces/vehicle.interface';
import { MapBoundaries } from '../interfaces/mapboundaries.interface';


import { LocalStorageService } from 'ngx-webstorage';
import { map, catchError } from 'rxjs/operators';
import { Observable, of } from 'rxjs';

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
}
