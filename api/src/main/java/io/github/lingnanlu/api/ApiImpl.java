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
import io.github.lingnanlu.model.SearchResultMetaInfo;

/**
 * Created by Administrator on 2015/12/28.
 *
 *  */
public class ApiImpl implements Api {

    private static Parser mParser = new Parser();
    private static UrlBuilder mUrlBuilder = new UrlBuilder();
    private static OkHttpClient mHttpClient = new OkHttpClient();

    private ArrayList<BookAbstract> mBookAbstracts;
    private MetaInfo mMetaInfo;

    @Override
    public ArrayList<BookAbstract> bookAbstractList(String keyword, int page) {

        //同一关键字,不同页数,此时,不需要更新totalCount和keyword
        if (keyword.equals(mMetaInfo.keyword) && page != mMetaInfo.currentPage) {
            mMetaInfo.currentPage = page;
        }

        //不同关键字,需要更新totalCount, keyword和page
        if (!keyword.equals(mMetaInfo.keyword)) {
            mMetaInfo.currentPage = page;
            mMetaInfo.keyword = keyword;
            mMetaInfo.totalCount = getTotalCount(keyword);
        }

        String url = mUrlBuilder.build(keyword, page, mMetaInfo.totalCount);

        Request.Builder builder = new Request.Builder();

        try {

            Response response = mHttpClient.newCall(builder.url(url).build()).execute();
            ArrayList<BookAbstract> result = mParser.parseBookAbstracts(response.body().string());
            mBookAbstracts = (ArrayList<BookAbstract>) result.clone();
            return result;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int getTotalCount(String keyWord) {

        String url = mUrlBuilder.build(keyWord);
        Request.Builder builder = new Request.Builder();

        try {
            Response response = mHttpClient.newCall(builder.url(url).build()).execute();
            SearchResultMetaInfo result = mParser.parseMetaInfo(response.body().string());
            return result.getTotalCount();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    ///search*chx?/X{u53D4}{u672C}{u534E}&SORT=D/X{u53D4}{u672C}{u534E}&SORT=D&SUBKEY=叔本华/1,87,87,B/frameset&FF=X{u53D4}{u672C}{u534E}&SORT=D&1,1,
    //如上,得到book信息的url需要先得到搜索列表
    @Override
    public Book bookDetail(String bookTitle) {

        String bookDetailUrl = null;
        for(BookAbstract bookAbstract : mBookAbstracts) {
            if (bookAbstract.getBookTitle() == bookTitle) {
                bookDetailUrl = bookAbstract.getUrl();
            }
        }

        Request.Builder builder = new Request.Builder();

        try {
            Response response = mHttpClient.newCall(builder.url(bookDetailUrl).build()).execute();
            return mParser.parseBookDetail(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private class MetaInfo {
        String keyword;
        int totalCount;
        int currentPage;
    }
}
