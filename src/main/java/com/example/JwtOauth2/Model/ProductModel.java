package com.example.JwtOauth2.Model;

public class ProductModel {

    private String name;
    private int price;

    private String description;

    private String thumbnail;

    public ProductModel() {
    }

    public ProductModel(String name, int price, String description, String thumbnail) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
