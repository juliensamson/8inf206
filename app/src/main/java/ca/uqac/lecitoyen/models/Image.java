package ca.uqac.lecitoyen.models;


public class Image {

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
