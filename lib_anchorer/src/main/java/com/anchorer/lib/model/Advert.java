package com.anchorer.lib.model;


import java.io.Serializable;

/**
 * Model: Advert
 * 广告
 *
 * Created by Anchorer/duruixue on 2014/10/22.
 * @author Anchorer
 */
public class Advert implements Serializable {
    private int id;
    private String title;
    private String imageUrl;
    private String link;

	public Advert() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
