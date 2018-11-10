package ca.uqac.lecitoyen.models;


import java.io.Serializable;

public class Image implements Serializable{

    private String imageId;

    public Image(){}

    public Image(String imageId) {
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
