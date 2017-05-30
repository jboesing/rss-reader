package br.com.madoc.rssreader.model;

import com.orm.SugarRecord;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.io.Serializable;

/**
 * Created by Jeferson on 28/05/2017.
 */

@Root(name = "item", strict = false)
public class FeedItem extends SugarRecord implements Serializable {

    @Path("title")
    @Text(required = false)
    private String title;

    @Path("description")
    @Text(required = false)
    private String description;

    @Path("link")
    @Text(required = false)
    private String link;

    @Element(name = "pubDate", required = false)
    private String pubDate;

    // TODO: 29/05/2017 search a better approach to get some thumbnail  
    @Path(value = "media:thumbnail")
    @Attribute(name = "url", required = false)
    private String thumbnail;

    private Channel channel;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public FeedItem() {
        super();
    }
}
