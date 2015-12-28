package io.github.lingnanlu.api;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

import io.github.lingnanlu.api.net.Parser;
import io.github.lingnanlu.api.net.UrlBuilder;
import io.github.lingnanlu.model.Book;
import io.github.lingnanlu.model.BookAbstract;

/**
 * Created by Administrator on 2015/12/28.
 */
public class ApiImpl implements Api {

    private static Parser mParser = new Parser();
    private static UrlBuilder mUrlBuilder = new UrlBuilder();
    private static OkHttpClient mHttpClient = new OkHttpClient();

    @Override
    public ArrayList<BookAbstract> bookAbstractList(String keyWord, int page) {

        String url = mUrlBuilder.build(keyWord, page);
        Request.Builder builder = new Request.Builder();

        try {
            Response response = mHttpClient.newCall(builder.url(url).build()).execute();
            return mParser.parseBookAbstracts(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Book bookDetail(String bookTitle) {

        return null;
    }
}
