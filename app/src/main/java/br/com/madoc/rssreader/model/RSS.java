package br.com.madoc.rssreader.model;

import com.orm.SugarRecord;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by Jeferson on 28/05/2017.
 */

@Root(name = "rss", strict = false)
public class RSS extends SugarRecord implements Serializable {

    @Element(name = "channel")
    private Channel channel;

    private String url;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RSS() {
        super();
    }

    public void saveCascade(){
        channel.save();
        for(FeedItem feedItem : channel.getParsedItems()){
            feedItem.setChannel(channel);
            feedItem.save();
        }
        save();
    }

    public void deleteCascade(){
        for(FeedItem feedItem : channel.getItems()){
            feedItem.delete();
        }
        channel.delete();
        delete();
    }

}
