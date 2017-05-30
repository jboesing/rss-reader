package br.com.madoc.rssreader.model;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Jeferson on 28/05/2017.
 */

public class RSSMenuItem extends SugarRecord implements Serializable {
    private String title;
    private String url;

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

    public RSSMenuItem(){
        super();
    }

    public RSSMenuItem(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
