package com.example.accountingandmanagement;

import android.graphics.Bitmap;

public class ImageItem {
    Bitmap image;
    String titel;
    String detalse;

    public ImageItem (Bitmap image, String titel, String detalse){
        this.image = image;
        this.titel = titel;
        this.detalse = detalse;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public String getTitel() {
        return this.titel;
    }

    public String getDetalse() {
        return this.detalse;
    }

}
