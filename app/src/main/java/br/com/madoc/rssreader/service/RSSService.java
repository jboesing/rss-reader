package br.com.madoc.rssreader.service;

import br.com.madoc.rssreader.model.RSS;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Jeferson on 28/05/2017.
 */

public interface RSSService {

    @GET
    Call<RSS> getRssFeed(@Url String feedUrl);

}
