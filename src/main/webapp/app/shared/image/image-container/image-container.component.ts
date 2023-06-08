import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'jhi-image-container',
  templateUrl: './image-container.component.html',
  styleUrls: ['./image-container.component.scss'],
})
export class ImageContainerComponent implements OnInit {
  @Input() uploadVisible: boolean | undefined | null;
  @Input() image: string | undefined | ArrayBuffer | null;
  @Output() imageEmitter = new EventEmitter<string>();
  selectedImage: string | ArrayBuffer | undefined | null = null;

  constructor() {}

  ngOnInit(): void {
    this.selectedImage = this.image;
  }

  onImageSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedImage = reader.result as string;
        this.cropImage();
        this.imageEmitter.emit(this.selectedImage as string);
      };
      reader.readAsDataURL(file);
    }
  }

  getCroppedImageStyle(): any {
    return {
      objectFit: 'cover',
      objectPosition: 'center center',
    };
  }

  cropImage() {
    const img = new Image();
    img.src = this.selectedImage as string;
    img.onload = () => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      const width = 300;
      const height = 300;
      canvas.width = width;
      canvas.height = height;
      ctx?.drawImage(img, 0, 0, width, height);
      const croppedImage = canvas.toDataURL('image/jpeg');
    };
  }
}
