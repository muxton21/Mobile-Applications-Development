package com.example.comg3.toomuchstuff;

/**
 * Created by comg3 on 16/05/2018.
 */

public class Clothing {
    private String title;
    private byte[] image;

    public Clothing(String title, byte[] image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
