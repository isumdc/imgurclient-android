package mobiledev.club.imgur.models;

import java.io.Serializable;

/**
 * Created by Ethan on 3/26/2015.
 */
public class ImgurImage implements Serializable {

    public String title;
    public String url;

    public ImgurImage() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
