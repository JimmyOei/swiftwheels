import { Component } from '@angular/core';

import * as L from 'leaflet';


@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent {
  ngOnInit(): void {
    const map = L.map('map', {
      center: [52.075184, 4.308190],    // Den Haag
      zoom: 13,
      maxBounds: L.latLngBounds(
        L.latLng(52.120474, 4.218868),
        L.latLng(52.0176, 4.4594)
      )
    });
  
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);
  }
}

