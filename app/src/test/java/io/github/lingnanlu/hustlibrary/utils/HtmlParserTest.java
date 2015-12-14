package io.github.lingnanlu.hustlibrary.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.junit.Before;
import org.junit.Test;

import io.github.lingnanlu.hustlibrary.model.Result;

import static org.junit.Assert.assertEquals;

/**
 * Created by Administrator on 2015/12/14.
 */
public class HtmlParserTest {

    private HtmlParser mHtmlParser;

    private OkHttpClient mClient;

    private Response mResponse;
    @Before
    public void setUp() throws Exception {

        mHtmlParser = new HtmlParser();
        mClient = new OkHttpClient();

        Request request = new Request.Builder().url(RequestUrlBuilder.build
                ("叔本华")).build();

        mResponse = mClient.newCall(request).execute();
    }

    @Test
    public void first_page() throws Exception {


        Result result = HtmlParser.parserResult(mResponse.body().string());

        assertEquals(1,result.getIndex());
        assertEquals(50, result.getStep());
        assertEquals(87, result.getTotalCount());

    }

    @Test
    public void not_first_page() throws Exception {

        Result result = HtmlParser.parserResult(mResponse.body().string());

        result.setKeyWord("叔本华");

//        String url = "http://ftp.lib.hust.edu" +
//                ".cn/search*chx?/X{u53D4}{u672C}{u534E}&SORT=D/X{u53D4}{u672C" +
//                "}{u534E}&SORT=D&SUBKEY=%E5%8F%94%E6%9C%AC%E5%8D%8E/1%2C87%2C87%2CB/browse";
        Request request = new Request.Builder().url(result.nextPageUrl()).build();

        Response html = mClient.newCall(request).execute();

        result = HtmlParser.parserResult(html.body().string());

        assertEquals(51,result.getIndex());
        assertEquals(37, result.getStep());
        assertEquals(87, result.getTotalCount());

    }

}