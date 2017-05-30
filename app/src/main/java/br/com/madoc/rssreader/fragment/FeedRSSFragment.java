package br.com.madoc.rssreader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.madoc.rssreader.R;
import br.com.madoc.rssreader.activity.MainActivity;
import br.com.madoc.rssreader.adapter.ListRssAdapter;
import br.com.madoc.rssreader.model.FeedItem;
import br.com.madoc.rssreader.model.RSS;
import br.com.madoc.rssreader.model.RSSMenuItem;
import br.com.madoc.rssreader.service.RSSService;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Jeferson on 28/05/2017.
 */

public class FeedRSSFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static FeedRSSFragment newInstance(RSSMenuItem rssItem) {

        Bundle args = new Bundle();
        FeedRSSFragment fragment = new FeedRSSFragment();
        args.putSerializable("rssItem", rssItem);
        fragment.setArguments(args);
        return fragment;
    }

    private RSSMenuItem rssItem;

    @BindView(R.id.swipeRefreshLayoutRSS)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.cardListRSS)
    RecyclerView recyclerView;
    @BindView(R.id.errorLoadFeedMessage)
    TextView errorLoadFeedMessageView;

    private LinearLayoutManager linearLayoutManager;

    private ListRssAdapter listRssAdapter;
    private List<FeedItem> feedItems;

    private Retrofit retrofit;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_rss, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            rssItem = (RSSMenuItem) getArguments().getSerializable("rssItem");
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(rssItem.getTitle());
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        OkHttpClient client = new OkHttpClient();
        retrofit = new Retrofit.Builder().baseUrl("http://www.example.com.br").client(client).addConverterFactory(SimpleXmlConverterFactory.create()).build();

        syncFeed();

        return view;
    }

    public void syncFeed() {
        feedItems = new ArrayList<>();
        listRssAdapter = new ListRssAdapter(this, feedItems);
        recyclerView.setAdapter(listRssAdapter);

        RSSService rssService = retrofit.create(RSSService.class);
        Call<RSS> rss = rssService.getRssFeed(rssItem.getUrl());

        rss.enqueue(new Callback<RSS>() {
            @Override
            public void onResponse(Call<RSS> call, Response<RSS> response) {
                if (response.isSuccessful()) {
                    List<RSS> result = RSS.find(RSS.class, "url = ?", rssItem.getUrl());
                    RSS oldFeed = result == null || result.isEmpty() ? null : result.get(0);
                    if (oldFeed != null) {
                        oldFeed.deleteCascade();
                    }
                    RSS newFeed = response.body();
                    newFeed.setUrl(rssItem.getUrl());
                    newFeed.saveCascade();

                    feedItems.addAll(newFeed.getChannel().getItems());
                    recyclerView.postInvalidate();
                    listRssAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    errorLoadFeedMessageView.setVisibility(View.GONE);

                } else {
                    loadFromCache();
                }
            }

            @Override
            public void onFailure(Call<RSS> call, Throwable t) {
                loadFromCache();
            }
        });
    }

    public void loadFromCache() {

        List<RSS> result = RSS.find(RSS.class, "url = ?", rssItem.getUrl());
        RSS cachedFeed = result == null || result.isEmpty() ? null : result.get(0);
        if (cachedFeed != null) {
            feedItems.addAll(cachedFeed.getChannel().getItems());
            recyclerView.postInvalidate();
            listRssAdapter.notifyDataSetChanged();

            errorLoadFeedMessageView.setVisibility(View.GONE);
        } else {
            errorLoadFeedMessageView.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        syncFeed();
    }
}
