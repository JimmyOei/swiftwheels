import { Component, AfterViewInit } from '@angular/core';
import { VehicleService } from '../services/vehicle.service';
import { Vehicle } from '../interfaces/vehicle.interface';
import { MapBoundaries } from '../interfaces/mapboundaries.interface';

import * as L from 'leaflet';

// This fixes a known bug in leafleet where the 
// Leaflet's icon image location is been wrongly referenced during bundling
import { icon, Marker } from 'leaflet';
import { LocalStorageService } from 'ngx-webstorage';
import { UserService } from '../services/user.service';
const iconRetinaUrl = 'assets/marker-icon-2x.png';
const iconUrl = 'assets/marker-icon.png';
const shadowUrl = 'assets/marker-shadow.png';
const iconDefault = icon({
  iconRetinaUrl,
  iconUrl,
  shadowUrl,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  tooltipAnchor: [16, -28],
  shadowSize: [41, 41]
});
Marker.prototype.options.icon = iconDefault;

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements AfterViewInit {
  private map!: L.Map;
  message: string = '';
  vehicleReserved: boolean = false;
  vehicleName: string = '';
  vehicleId: number = 0;

  constructor(private vehicleService: VehicleService,  private userService: UserService, private localStorage: LocalStorageService) {
    this.updateVehicleReserved();
  }

  updateVehicleReserved() {
    const reservedResponse = this.userService.getReservedVehicle();
    if(!reservedResponse) {
      console.log("Failed getting reserved vehicle data.");
      return;
    }
  
    reservedResponse.subscribe(() => {
      this.vehicleReserved = this.localStorage.retrieve('vehicle_reserved');
      if(this.vehicleReserved) {
        this.vehicleName = this.localStorage.retrieve('vehicle_name');
        this.vehicleId = this.localStorage.retrieve('vehicle_id');
      } 
      else {
        this.vehicleName = '';
        this.vehicleId = 0;
      }
    }, 
    (error) => {
      console.log(error);
    });
  }

  /* Initiates the map and adds markers */
  ngAfterViewInit(): void {
    this.initMap();
    this.addMarkers();
  }

  /* Initiates the map */
  private initMap(): void {
        this.map = L.map('map', {
      center: [52.075184, 4.308190],    // Den Haag
      zoom: 13,
      minZoom: 12
    });

    this.vehicleService.getMapBounderies().subscribe(
      (response) => {
        this.map.setMaxBounds(L.latLngBounds(
          L.latLng(response.max_latitude, response.min_longitude),      // Northwest boundary point
          L.latLng(response.min_latitude, response.max_longitude)       // Southeast boundary point
        ))
      },
      (error) => {
        console.error(error.error.message);
      }
    );

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(this.map);
  }

  releaseVehicle() {
    const releaseResponse = this.vehicleService.releaseVehicle();
    if(!releaseResponse) {
      console.log("Release failed because of local missing reserved vehicle.");
      return;
    }
  
    releaseResponse.subscribe(
      (response: any) => {
        this.updateVehicleReserved();
        this.updateMarkers();
      },
      (error) => {
        console.log(error.error);
      }
    );

  }

  reserveVehicle(id: number) {
    const reserveResponse = this.vehicleService.reserveVehicle(id);
    if(!reserveResponse) {
      console.log("Reservation failed because of missing local username or token.");
      return;
    }
  
    reserveResponse.subscribe(
      (response: any) => {
        this.message = response;
        this.updateMarkers();
        this.updateVehicleReserved();
      },
      (error) => {
        console.log(error.error);
      }
    );
  }

  /* Puts markers on the map for all available vehicles */
  private addMarkers(): void {
    this.vehicleService.getAllAvailableVehicles().subscribe(
      (response) => {
        response.forEach(vehicle => {
          if(vehicle.available == true) {
          const popupContent = document.createElement('div');
          popupContent.innerHTML = `
            <b>${vehicle.name}</b>
            <br>Type: ${vehicle.type}
            <br>ID: ${vehicle.id}
            <br>
          `;

          const button = document.createElement('button');
          button.textContent = 'Reserve Vehicle';
          button.addEventListener('click', () => {
            this.reserveVehicle(vehicle.id);
          });

          popupContent.appendChild(button);

          const marker = L.marker([vehicle.latitude, vehicle.longitude]).addTo(this.map)
            .bindPopup(popupContent);
          }
        });
      },
      (error) => {
        console.error(error.error);
      }
    );
  }

  /* Removes all exisiting markers on the map */
  private clearMarkers(): void {
    this.map.eachLayer(layer => {
      if(layer instanceof L.Marker) {
        this.map.removeLayer(layer);
      }
    });
  }

  /* Updates markers by cleaning the map and readding markers */
  updateMarkers(): void {
    this.clearMarkers();
    this.addMarkers();
  }
}