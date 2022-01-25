package com.ikea.myapp;

public class SliderItem {
    //Here you van use a string variable to store the url for an online image
    private int image;
    private String placeName, tripLength;

    public SliderItem(int image, String placeName, String tripLength) {
        this.image = image;
        this.placeName = placeName;
        this.tripLength = tripLength;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getTripLength() {
        return tripLength;
    }

    public int getImage() {
        return image;
    }
}
