package com.example.myapplication.home;

public class Category {

    public Category(){}

    private String name;
    private String image;

    public Category(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName(){
        return name;
    }

    public String getImage(){
        return image;
    }

    public void setName(String name){this.name = name;}

    public void setImage(String image) {
        this.image = image;
    }
}