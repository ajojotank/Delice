package com.example.delice.ui.recipe;

import java.util.List;
import java.io.Serializable;

public class Recipe implements Serializable{
    private String title;
    private String description;
    private boolean favourite;
    private List<String> ingredients;
    private List<String> instructions;
    private String author;
    private String imageURL;

    public Recipe(String title, String description, boolean favourite, List<String> ingredients, List<String> instructions, String author, String imageURL) {
        this.title = title;
        this.description = description;
        this.favourite = favourite;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.author = author;
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void toggleFavourite() {
        this.favourite = !this.favourite; // Toggle the state
    }

}