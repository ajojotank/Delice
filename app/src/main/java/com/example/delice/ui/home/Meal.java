package com.example.delice.ui.home;

public class Meal {

    String id;
    private String title;
    private String description;
    private String author;
    private String imageUrl;

    public Meal(String title, String description, String author, String imageUrl, String id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId(){return  id;}
}
