package com.example.model;

/**
 * Model class for image
 */
public class Image {

    private String image_name;
    private String image_url;
    private String user_name;

    public Image() {
    }

    public Image(String user_name, String image_name, String image_url) {
        if (image_name.trim().equals("")) {
            this.image_name = "No name";
        } else {
            this.image_name = image_name;
        }
        this.image_url = image_url;
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getImage_name() {
        return image_name;
    }

    public String getImage_url() {
        return image_url;
    }
}
