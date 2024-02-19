package com.example.JwtOauth2.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private int price;

    private String description;

    private String thumbnail;


    public Products() {
    }

    public Products(int id, String name, int price, String description, String thumbnail) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
