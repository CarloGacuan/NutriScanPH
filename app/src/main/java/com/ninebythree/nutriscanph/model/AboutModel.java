package com.ninebythree.nutriscanph.model;

public class AboutModel {
    private int image;
    private String name;
    private String description;

    public AboutModel(int image, String name, String description) {
        this.image = image;
        this.name = name;
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
