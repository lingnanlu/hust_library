package io.github.lingnanlu.hustlibrary.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import io.github.lingnanlu.hustlibrary.model.Book;
import io.github.lingnanlu.hustlibrary.model.Result;

import static org.junit.Assert.assertEquals;

/**
 * Created by Administrator on 2015/12/14.
 */
public class HtmlParserTest {


    private OkHttpClient mClient;


    @Before
    public void setUp() throws Exception {

        mClient = new OkHttpClient();

    }

    @Test
    public void parseResult_first_page() throws Exception {

        Request request = new Request.Builder().url
                (RequestUrlBuilder.build
                        ("叔本华")).build();

        Response response = mClient.newCall(request).execute();

        Result result = HtmlParser.parserResult(response.body()
                .string());

        assertEquals(1, result.getBegin());
        assertEquals(50, result.getEnd());
        assertEquals(87, result.getTotalCount());

    }

    @Test
    public void parseResult_last_page() throws Exception {

        Request request = new Request.Builder().url
                (RequestUrlBuilder.build
                        ("叔本华")).build();

        Response response = mClient.newCall(request).execute();

        Result result = HtmlParser.parserResult(response.body()
                .string());

        result.setKeyWord("叔本华");

//        String url = "http://ftp.lib.hust.edu" +
//                ".cn/search*chx?/X{u53D4}{u672C}{u534E}&SORT=D/X
// {u53D4}{u672C" +
//                "}{u534E}&SORT=D&SUBKEY=%E5%8F%94%E6%9C%AC%E5%8D
// %8E/1%2C87%2C87%2CB/browse";
        request = new Request.Builder().url(result
                .nextPageUrl()).build();

        Response html = mClient.newCall(request).execute();

        result = HtmlParser.parserResult(html.body().string());

        assertEquals(51, result.getBegin());
        assertEquals(87, result.getEnd());
        assertEquals(87, result.getTotalCount());

    }

    @Test
    public void parseBook_shubenhua_jinxinke() throws IOException {

        Request request = new Request.Builder().url("http://ftp.lib" +
                ".hust.edu" +
                ".cn/search*chx?/X{u53D4}{u672C}{u534E}&SORT=D/X" +
                "{u53D4}{u672C}{u534E}&SORT=D&SUBKEY=%E5%8F%94%E6" +
                "%9C%AC%E5%8D%8E/1%2C87%2C87%2CB/frameset&FF=X" +
                "{u53D4}{u672C}{u534E}&SORT=D&1%2C1%2C").build();

        Response response = new OkHttpClient().newCall(request)
                .execute();

        //System.out.println(response.body().string());
        Book book = HtmlParser.parseBook(response.body().string());

        assertEquals("叔本华静心课 / (德) 叔本华著", book.getTitle());
        assertEquals("B516.41 37", book.getCallNumber());
        assertEquals("叔本华 (Schopenhauer, Arthur), 1788-1860 著",
                book.getAuthor());
        assertEquals("978-7-229-09153-8 CNY32.80", book.getISBN());

        assertEquals("中文图书阅览室（C区2楼，3楼，5楼）", book.getStoreInfos()
                .get(0)[0]);
        assertEquals("馆内阅览", book.getStoreInfos().get(0)[2]);
        assertEquals("流通书库(B区)", book.getStoreInfos().get
                (1)[0]);
        assertEquals("在架上", book.getStoreInfos().get
                (1)[2]);


    }

    @Test
    public void parseBook_effective_cplusplus() throws IOException {

        Request request = new Request.Builder().url("http://ftp.lib" +
                ".hust.edu.cn/search*chx?/XEffective+C%2B%2B&SORT=D" +
                "/XEffective+C%2B%2B&SORT=D&SUBKEY=Effective+C%2B" +
                "%2B/1%2C17%2C17%2CB/frameset&FF=XEffective+C%2B%2B" +
                "&SORT=D&1%2C1%2C").build();

        Response response = new OkHttpClient().newCall(request)
                .execute();

        //System.out.println(response.body().string());
        Book book = HtmlParser.parseBook(response.body().string());

        assertEquals("Effective C++ : 55 specific ways to improve your programs and designs = 改善程序与设计的55个具体做法 / Scott Meyers.", book.getTitle());
        assertEquals("TP312C++ W47/3A", book.getCallNumber());
        assertEquals("Meyers, Scott (Scott Douglas)",
                book.getAuthor());
        assertEquals("9787121133763", book.getISBN());

        assertEquals("外文图书阅览室（B608）", book.getStoreInfos()
                .get(0)[0]);
        assertEquals("馆内阅览", book.getStoreInfos().get(0)[2]);

        assertEquals("流通书库(B区)", book.getStoreInfos().get
                (1)[0]);
        assertEquals("在架上", book.getStoreInfos().get
                (1)[2]);

        assertEquals("流通书库(B区)", book.getStoreInfos().get
                (2)[0]);
        assertEquals("上架中", book.getStoreInfos().get
                (2)[2]);

        assertEquals("东校区分馆阅览室", book.getStoreInfos().get
                (3)[0]);
        assertEquals("馆内阅览", book.getStoreInfos().get
                (3)[2]);

        assertEquals("东校区分馆借还处", book.getStoreInfos().get
                (4)[0]);
        assertEquals("到期 15-12-22", book.getStoreInfos().get
                (4)[2]);


    }

}