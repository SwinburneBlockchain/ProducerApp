package com.swinblockchain.producerapp;

/**
 * Created by john on 1/10/17.
 */

public class Ack {
    String title;
    String url;

    public Ack(String title, String url) {
        this.title = title;
        this.url = url;
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
}
