package br.com.madoc.rssreader.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.madoc.rssreader.R;
import br.com.madoc.rssreader.activity.DisplayActivity;
import br.com.madoc.rssreader.fragment.FeedRSSFragment;
import br.com.madoc.rssreader.model.FeedItem;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jeferson on 27/03/2017.
 */

public class ListRssAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private List<FeedItem> rssItems;
    private FeedRSSFragment fragment;


    public static class FeedItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbFeedItem)
        ImageView thumb;
        @BindView(R.id.titleFeedItem)
        TextView title;
        @BindView(R.id.publicationDateFeedItem)
        TextView publicationDate;
        @BindView(R.id.descriptionFeedItem)
        TextView description;
        public FeedItem feedItem;

        public FeedItemViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public ListRssAdapter(FeedRSSFragment fragment, List<FeedItem> rssItems) {
        this.rssItems = rssItems;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View feedItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rss_item_layout, parent, false);
        RecyclerView.ViewHolder vh = new FeedItemViewHolder(feedItemView);
        feedItemView.setOnClickListener(this);
        feedItemView.setTag(vh);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedItemViewHolder) {
            if (rssItems.size() > 0 && position < rssItems.size()) {
                FeedItem feedItem = rssItems.get(position);
                FeedItemViewHolder feedItemViewHolder = (FeedItemViewHolder) holder;
                feedItemViewHolder.feedItem = feedItem;
                feedItemViewHolder.title.setText(feedItem.getTitle());

                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

                try {
                    Date date = sdf.parse(feedItem.getPubDate());
                    sdf = new SimpleDateFormat("HH:mm | dd/MM/yyyy");

                    String formattedDate = sdf.format(date);
                    feedItemViewHolder.publicationDate.setText(formattedDate);
                } catch (ParseException e) {
                    feedItemViewHolder.publicationDate.setText(feedItem.getPubDate());
                }

                feedItemViewHolder.description.setText(Html.fromHtml(feedItem.getDescription()));
                if (feedItem.getThumbnail() != null) {
                    Glide.with(fragment).load(feedItem.getThumbnail()).into(feedItemViewHolder.thumb);
                } else {
                    feedItemViewHolder.thumb.setVisibility(View.GONE);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return rssItems.size();
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            FeedItemViewHolder feedItemViewHolder = (FeedItemViewHolder) v.getTag();
            Intent displayIntent = new Intent(fragment.getActivity(), DisplayActivity.class);
            displayIntent.putExtra("link", feedItemViewHolder.feedItem.getLink());
            fragment.startActivity(displayIntent);
        }
    }


}
