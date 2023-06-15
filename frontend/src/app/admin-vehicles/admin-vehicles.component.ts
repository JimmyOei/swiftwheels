import { Component } from '@angular/core';
import { Vehicle } from '../interfaces/vehicle.interface';
import { Router } from '@angular/router';
import { VehicleService } from '../services/vehicle.service';
import { AddVehicle } from '../interfaces/addVehicle.interface';
import { EditVehicle } from '../interfaces/editvehicle.interface';

import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-admin-vehicles',
  templateUrl: './admin-vehicles.component.html',
  styleUrls: ['./admin-vehicles.component.css']
})
export class AdminVehiclesComponent {
  vehicles: Vehicle[] = []; // Replace Vehicle with your vehicle model/interface

  newVehicle: AddVehicle = {
    vehicle_name: '',
    vehicle_type: 'CAR',
    latitude: 0,
    longitude: 0
  };

  constructor(private router: Router, private vehicleService: VehicleService) {
    this.updateVehiclesList();
  }

  goToAdminPage() {
    this.router.navigate(['/admin']);
  }

  editVehicle(vehicleId: number) {
    const vehicle = this.vehicles.find((vehicle) => vehicle.id === vehicleId);
    if (!vehicle) {
      console.log("Vehicle not found locally, failed to edit");
      return;
    }
  
    const editVehicleRequest: EditVehicle = {
      vehicle_id: vehicle.id,
      vehicle_name: vehicle.name,
      vehicle_type: vehicle.type,
      longitude: vehicle.longitude,
      latitude: vehicle.latitude
    };
  
    this.vehicleService.editVehicle(editVehicleRequest)?.pipe(
      finalize(() => {
        this.updateVehiclesList();
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
  

  deleteVehicle(vehicleId: number) {
    this.vehicleService.deleteVehicle(vehicleId)?.subscribe(
      (response: any) => {
        console.log(response.message);
        this.updateVehiclesList();
      },
      (error) => {
        console.error(error.error);
      }
    );
  }

  addVehicle() {
    this.vehicleService.addVehicle(this.newVehicle)?.subscribe(
      (response: any) => {
        console.log(response.message);
        this.updateVehiclesList();
      },
      (error) => {
        console.error(error.error);
      }
    );
  }

  updateVehiclesList() {
    const getResponse = this.vehicleService.getAllVehicles();
    if(!getResponse) {
      console.log("Failed fetching vehicle database, because local token is missing.");
      return;
    }
  
    getResponse.subscribe(
      (response) => {
        this.vehicles = response;
      },
      (error) => {
        console.error(error.error);
      }
    );
  }
}
