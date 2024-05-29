package com.example.delice.ui.friends;

public class Friend {

    private String id;
    private String name;
    private String username;

    public Friend(String id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }
}
