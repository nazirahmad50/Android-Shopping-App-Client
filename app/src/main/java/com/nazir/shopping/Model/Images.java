package com.nazir.shopping.Model;

public class Images {

    private String imageLink;
    private String color;


    public Images() {
    }

    public Images(String imageLink, String color) {
        this.imageLink = imageLink;
        this.color = color;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
