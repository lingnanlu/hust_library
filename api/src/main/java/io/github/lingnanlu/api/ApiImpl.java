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
 * 该Api并不是无状态,你无法直接调用bookDetail来获得一本书的信息,这是由于图书馆网站的请求url中包含着totalCount和页数
 * 信息所导致的.所以在请求一本书之前必须获得totalCount和该书所在结果页数.
 *
 * 如果设计成无状态的,则在调用bookDetail必须通过bookAbstractList得到totalCount和page信息,这样会做大量无用请求.
 * 消耗流量.
 *
 * 在这里设计成有状态的.在调用bookDetail前,一定会调用bookAbstractList, 因为应用的操作逻辑是先搜索得到结果列表,
 * 再点某一条目来得到书的详细信息.
 *
 * 针对所有可能的操作情况,写了相应的测试用例
 *
 * 1. 同一关键字,不同页数
 * 2. 不同关键字
 * 3. 同一关键字,不同页数下选取一本书查看其详细信息.
 *
 * 以上可知,由于图书馆网站没有api,暂时只能设计成这样.
 *
 *  */
public class ApiImpl implements Api {

    private static Parser mParser = new Parser();
    private static UrlBuilder mUrlBuilder = new UrlBuilder();
    private static OkHttpClient mHttpClient = new OkHttpClient();
    public static final String PREFIX = "http://ftp.lib.hust.edu.cn";

    private ArrayList<BookAbstract> mBookAbstracts;
    private MetaInfo mMetaInfo = new MetaInfo();

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
            mBookAbstracts = result;
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

        if(BuildConfig.DEBUG) {
            System.out.println(bookTitle + "\n");
        }
        String bookDetailUrl = null;
        for(BookAbstract bookAbstract : mBookAbstracts) {

            if(BuildConfig.DEBUG) {
                System.out.println(bookAbstract.getBookTitle());

            }

            if (bookAbstract.getBookTitle().equals(bookTitle)) {
                bookDetailUrl = PREFIX + bookAbstract.getUrl();
                break;
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

    // TODO: 2015/12/29 暂时方法,测试的时候使用
    public MetaInfo getMetaInfo() {
        return mMetaInfo;
    }

    public static class MetaInfo {
        String keyword;
        int totalCount;
        int currentPage;
    }
}
