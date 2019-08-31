package com.dimitriongoua.numerss.model;

public class FeedItem {

    private String title, url, timeStamp, author, image, description;

    public FeedItem() {
    }

    public FeedItem(String title, String url, String timeStamp, String author, String image, String description) {
        this.title = title;
        this.url = url;
        this.timeStamp = timeStamp;
        this.author = author;
        this.image = image;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
