package com.example.delice.ui.recipe;

public class Comment {
    private String userName; // Stores the user's name
    private String comment;  // Stores the actual comment text
    private float rating;    // Stores the user's rating as a floating-point value

    // Constructor to initialize the comment object
    public Comment(String userName, String comment, float rating) {
        this.userName = userName;
        this.comment = comment;
        this.rating = rating;
    }

    // Getter for user name
    public String getUserName() {
        return userName;
    }

    // Getter for comment text
    public String getComment() {
        return comment;
    }

    // Getter for rating
    public float getRating() {
        return rating;
    }

    // Optionally, you might want to include setters if you need to modify the fields later
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
