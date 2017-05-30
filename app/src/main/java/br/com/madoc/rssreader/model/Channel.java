package br.com.madoc.rssreader.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jeferson on 28/05/2017.
 */

@Root(name = "channel", strict = false)
public class Channel extends SugarRecord implements Serializable {

    @ElementList(entry = "item", inline = true, required = false)
    @Ignore
    private List<FeedItem> items;


    @Element(name = "title",required = false)
    private String title;

    @Element(required = false)
    @Path("channel/link")
    private String link;

    @Element(required = false)
    @Path("channel/description")
    private String description;

    protected List<FeedItem> getParsedItems(){
        return items;
    }

    public List<FeedItem> getItems() {
        return FeedItem.find(FeedItem.class, "channel = ?", String.valueOf(this.getId()));
    }

    public void setItems(List<FeedItem> items) {
        this.items = items;
    }

    public Channel() {
        super();
    }
}
