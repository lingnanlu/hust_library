package io.github.lingnanlu.hustlibrary.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2015/12/14.
 */
public class RequestUrlBuilderTest {

    private RequestUrlBuilder mRequestUrlBuilder;


    @Before
    public void setUp() throws Exception {

        mRequestUrlBuilder = new RequestUrlBuilder();
    }


    @Test
    public void build_first_keyword_only_chinese() throws Exception {

        String actual = mRequestUrlBuilder.build("叔本华");

        String expected = "http://ftp.lib.hust.edu" +
                ".cn/search*chx/X?SEARCH=%E5%8F%94%E6%9C%AC%E5%8D%8E";


        assertEquals(expected, actual);
    }


    @Test
    public void build_keyword_only_chinese() throws Exception {

        String actual = mRequestUrlBuilder.build("叔本华", 1, 87);

        String expected = "http://ftp.lib.hust.edu" +
                ".cn/search*chx?/X{u53D4}{u672C}{u534E}&SORT=D/X{u53D4}{u672C" +
                "}{u534E}&SORT=D&SUBKEY=%E5%8F%94%E6%9C%AC%E5%8D%8E/1%2C87" +
                "%2C87%2CB/browse";


        assertEquals(expected, actual);
    }


    @Test
    public void build_keyword_chinese_english_mix() throws Exception {

        String actual = mRequestUrlBuilder.build("java编程", 1, 1807);

        String expected = "http://ftp.lib.hust.edu" +
                ".cn/search*chx?/Xjava{u7F16}{u7A0B}&SORT=D/Xjava{u7F16" +
                "}{u7A0B}&SORT=D&SUBKEY=java%E7%BC%96%E7%A8%8B/1%2C1807" +
                "%2C1807%2CB/browse";


        assertEquals(expected, actual);
    }


    @Test
    public void build_first_keyword_punctuations() throws Exception {

        String actual = mRequestUrlBuilder.build("C++");

        String expected = "http://ftp.lib.hust.edu" +
                ".cn/search*chx/X?SEARCH=C%2B%2B";

        assertEquals(expected, actual);
    }

    @Test
    public void build_keyword_punctuations() throws Exception {

        String actual = mRequestUrlBuilder.build("C++", 1, 2090);

        String expected = "http://ftp.lib.hust.edu" +
                ".cn/search*chx?/XC%2B%2B&SORT=D/XC%2B%2B&SORT=D&SUBKEY=C%2B" +
                "%2B/1%2C2090%2C2090%2CB/browse";


        assertEquals(expected, actual);
    }

    @Test
    public void build_keyword_chinese_number_mix() throws Exception {

        String actual = mRequestUrlBuilder.build("第2版", 1, 1037);

        String expected = "http://ftp.lib.hust.edu" +
                ".cn/search*chx?/X{u7B2C}2{u7248}&SORT=D/X{u7B2C}2{u7248" +
                "}&SORT=D&SUBKEY=%E7%AC%AC2%E7%89%88/1%2C1037%2C1037%2CB" +
                "/browse";

        assertEquals(expected, actual);
    }




}