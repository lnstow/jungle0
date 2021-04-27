package com.lnstow.jungle0.bean;

import java.util.ArrayList;

public class MovieDetailBean {
    private String bigImageLink = "";
    private ArrayList<String> smallImageLink = new ArrayList<>(12);
    private String videoLink = "";
    private ArrayList<String> textKey = new ArrayList<>();
    private ArrayList<String[]> textValue = new ArrayList<>();
    private ArrayList<String[]> textLink = new ArrayList<>();
    private ArrayList<String> smallToBigImage = new ArrayList<>(13);

    public String getBigImageLink() {
        return bigImageLink;
    }

    public void setBigImageLink(String bigImageLink) {
        this.bigImageLink = bigImageLink;
    }

    public ArrayList<String> getSmallImageLink() {
        return smallImageLink;
    }

    public void setSmallImageLink(ArrayList<String> smallImageLink) {
        this.smallImageLink = smallImageLink;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public ArrayList<String> getTextKey() {
        return textKey;
    }

    public void setTextKey(ArrayList<String> textKey) {
        this.textKey = textKey;
    }

    public ArrayList<String[]> getTextValue() {
        return textValue;
    }

    public void setTextValue(ArrayList<String[]> textValue) {
        this.textValue = textValue;
    }

    public ArrayList<String[]> getTextLink() {
        return textLink;
    }

    public void setTextLink(ArrayList<String[]> textLink) {
        this.textLink = textLink;
    }

    public ArrayList<String> getSmallToBigImage() {
        return smallToBigImage;
    }

    public void setSmallToBigImage(ArrayList<String> smallToBigImage) {
        this.smallToBigImage = smallToBigImage;
    }
}
