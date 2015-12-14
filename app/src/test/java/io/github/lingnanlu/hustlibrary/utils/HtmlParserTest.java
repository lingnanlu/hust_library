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
    @Before
    public void setUp() throws Exception {

        mHtmlParser = new HtmlParser();
        mClient = new OkHttpClient();

    }

    @Test
    public void testParserResult() throws Exception {

        Request request = new Request.Builder().url(RequestUrlBuilder.build
                ("叔本华")).build();

        Response html = mClient.newCall(request).execute();

        Result result = HtmlParser.parserResult(html.body().string());

        assertEquals(1,result.getIndex());
        assertEquals(50, result.getStep());
        assertEquals(87, result.getTotalCount());

    }
}