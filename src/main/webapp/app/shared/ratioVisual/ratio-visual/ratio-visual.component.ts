import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-ratio-visual',
  templateUrl: './ratio-visual.component.html',
  styleUrls: ['./ratio-visual.component.scss'],
})
export class RatioVisualComponent implements OnInit {
  @Input() value: number = 0; // Value between 0 and 100

  constructor() {}

  ngOnInit(): void {}

  get batteryLevel(): number {
    return this.value; // Adjust based on your desired percentage
  }
}
