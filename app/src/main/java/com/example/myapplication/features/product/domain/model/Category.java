package com.example.myapplication.features.product.domain.model;

public class Category {

    private final String id;
    private final String name;
    private final String image;

    public Category(
            String id,
            String name,
            String image
    ) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
