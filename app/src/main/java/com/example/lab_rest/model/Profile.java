package com.example.lab_rest.model;

public class Profile {
    private int id;
    private int user_id;
    private String first_name;
    private String last_name;
    private String image;
    private String theme_bg;
    private String theme_col;

    // Constructors
    public Profile() {
    }

    public Profile(int id) {
        this.id = id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTheme_bg(String theme_bg) {
        this.theme_bg = theme_bg;
    }

    public String getTheme_bg() {
        return theme_bg;
    }

    public String getTheme_col() {
        return theme_col;
    }

    public void setTheme_col(String theme_col) {
        this.theme_col = theme_col;
    }


}
