import { Injectable } from '@angular/core';

@Injectable()
export class ImageManipulationService {
  /* Utility function to convert a canvas to a BLOB */
  dataURLToBlob(dataURL: any) {
    let BASE64_MARKER = ';base64,';
    if (dataURL.indexOf(BASE64_MARKER) == -1) {
      var parts = dataURL.split(',');
      var contentType = parts[0].split(':')[1];
      var raw = parts[1];

      return new Blob([raw], { type: contentType });
    }

    var parts = dataURL.split(BASE64_MARKER);
    var contentType = parts[0].split(':')[1];
    var raw: any = window.atob(parts[1]);
    let rawLength = raw.length;

    let uInt8Array = new Uint8Array(rawLength);

    for (let i = 0; i < rawLength; ++i) {
      uInt8Array[i] = raw.charCodeAt(i);
    }

    return new Blob([uInt8Array], { type: contentType });
  }

  resize(img: any, MAX_WIDTH: number, MAX_HEIGHT: number, callback: any) {
    return (img.onload = () => {
      let width = img.width;
      let height = img.height;
      if (width > height) {
        if (width > MAX_WIDTH) {
          height *= MAX_WIDTH / width;
          width = MAX_WIDTH;
        }
      } else {
        if (height > MAX_HEIGHT) {
          width *= MAX_HEIGHT / height;
          height = MAX_HEIGHT;
        }
      }
      // create a canvas object
      var canvas = document.createElement('canvas');
      // Set the canvas to the new calculated dimensions
      canvas.width = width;
      canvas.height = height;
      var ctx = canvas.getContext('2d');
      if (ctx != null) {
        ctx.drawImage(img, 0, 0, width, height);
      }

      var dataUrl = canvas.toDataURL('image/jpeg'); // - base64 - url
      var resizedImage = this.dataURLToBlob(dataUrl);
      // callback with the results
      callback(dataUrl, resizedImage);
    });
  }
}
