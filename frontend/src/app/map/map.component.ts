import { Component, AfterViewInit } from '@angular/core';
import { VehicleService } from '../services/vehicle.service';
import { Vehicle } from '../interfaces/vehicle.interface';
import { MapBoundaries } from '../interfaces/mapboundaries.interface';

import * as L from 'leaflet';

// This fixes a known bug in leafleet where the 
// Leaflet's icon image location is been wrongly referenced during bundling
import { icon, Marker } from 'leaflet';
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

  constructor(private vehicleService: VehicleService) {}

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

  /* Puts markers on the map for all available vehicles */
  private addMarkers(): void {
    this.vehicleService.getAllAvailableVehicles().subscribe(
      (response) => {
        response.forEach(vehicle => {
          if(vehicle.available == true) {
            const popupContent = `<b>${vehicle.name}</b><br>Type: ${vehicle.type}<br>ID: ${vehicle.id}`;
            L.marker([vehicle.latitude, vehicle.longitude]).addTo(this.map)
              .bindPopup(popupContent);
          }
        });
      },
      (error) => {
        console.error(error.error.message);
      }
    );
  }

  /* Removes all exisiting markers on the map */
  private clearMarkers(): void {
    this.map.eachLayer(layer => {
      if (layer instanceof L.Marker) {
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