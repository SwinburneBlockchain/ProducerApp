package com.swinblockchain.producerapp;

/**
 * Holds information about an acknowledgement
 *
 * @author John Humphrys
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
