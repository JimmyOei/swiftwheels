import { Component, AfterViewInit } from '@angular/core';

import * as L from 'leaflet';


@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements AfterViewInit {
  private map!: L.Map;

  constructor() {}

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
      maxBounds: L.latLngBounds(
        L.latLng(52.120474, 4.218868),  // Northwest boundary point
        L.latLng(52.0176, 4.4594)       // Southeast boundary point
      ),
      minZoom: 12
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(this.map);
  }

  /* Puts markers on the map for all available vehicles */
  private addMarkers(): void {
    const vehicles = [
      { id: 1, name: 'Vehicle 1', available: true, lat: 52.0803, lng: 4.3118 },
      { id: 2, name: 'Vehicle 2', available: true, lat: 52.0735, lng: 4.3022 },
      { id: 3, name: 'Vehicle 3', available: true, lat: 52.0535, lng: 4.3022 },
      { id: 4, name: 'Vehicle 4', available: false, lat: 52.0635, lng: 4.3422 },
    ]; // TEST DATA

    vehicles.forEach(vehicle => {
      if(vehicle.available == true) {
        L.marker([vehicle.lat, vehicle.lng]).addTo(this.map)
          .bindPopup(vehicle.name);
      }
    });
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