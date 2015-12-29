package io.github.lingnanlu.hustlibrary.api;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import io.github.lingnanlu.model.Book;
import io.github.lingnanlu.model.BookAbstract;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rico on 2015/12/29.
 */
public class ApiImplTest {

    ApiImpl api;

    @Before
    public void setUp() throws Exception {
        api = new ApiImpl();
    }

    @Test
    public void testBookAbstractList_diff_keyword() throws Exception {

        ApiImpl.MetaInfo metaInfo = api.getMetaInfo();
        assertEquals(null, metaInfo.keyword);
        assertEquals(0, metaInfo.currentPage);
        assertEquals(0, metaInfo.totalCount);

        ArrayList<BookAbstract> result = api.bookAbstractList("叔本华", 1);

        metaInfo = api.getMetaInfo();
        assertEquals("叔本华", metaInfo.keyword);
        assertEquals(1, metaInfo.currentPage);
        assertEquals(87, metaInfo.totalCount);
        assertEquals(50, result.size());

        result = api.bookAbstractList("java", 1);

        metaInfo = api.getMetaInfo();
        assertEquals("java", metaInfo.keyword);
        assertEquals(1, metaInfo.currentPage);
        assertEquals(2740, metaInfo.totalCount);
        assertEquals(50, result.size());

    }

    @Test
    public void testBookAbstractList_same_keyword_diff_page() throws Exception {

        ApiImpl.MetaInfo metaInfo = api.getMetaInfo();
        assertEquals(null, metaInfo.keyword);
        assertEquals(0, metaInfo.currentPage);
        assertEquals(0, metaInfo.totalCount);

        ArrayList<BookAbstract> result = api.bookAbstractList("叔本华", 1);

        metaInfo = api.getMetaInfo();
        assertEquals("叔本华", metaInfo.keyword);
        assertEquals(1, metaInfo.currentPage);
        assertEquals(87, metaInfo.totalCount);
        assertEquals(50, result.size());

        result = api.bookAbstractList("叔本华", 2);

        metaInfo = api.getMetaInfo();
        assertEquals("叔本华", metaInfo.keyword);
        assertEquals(2, metaInfo.currentPage);
        assertEquals(87, metaInfo.totalCount);
        assertEquals(37, result.size());
    }

    @Test
    public void testBookAbstractList_last_page_return_null() throws Exception {


        ArrayList<BookAbstract> result = api.bookAbstractList("叔本华", 3);

        assertEquals(null, result);
    }
    @Test
    public void testBookDetail() throws Exception {


        //测试第一页的一本书
        api.bookAbstractList("叔本华", 1);

        Book book = api.bookDetail("叔本华 / (美) S. 杰克·奥德尔著");

        assertEquals("叔本华 / (美) S. 杰克·奥德尔著", book.getTitle());
        assertEquals("B516.41 15/2", book.getCallNumber());
        assertEquals("奥德尔 (Odell, S. Jack) 著", book.getAuthor());
        assertEquals("978-7-101-09790-0", book.getISBN());
        assertEquals("中文图书阅览室（C区2楼，3楼，5楼）", book.getStoreInfos().get(0)[0]);
        assertEquals("http://202.114.9.17/bibimage/zycover.php?isbn=9787101097900", book.getImgUrl());
        assertEquals("馆内阅览", book.getStoreInfos().get(0)[2]);
        assertEquals("流通书库(B区)", book.getStoreInfos().get(1)[0]);
        assertEquals("在架上", book.getStoreInfos().get(1)[2]);

        //测试第二页的一本书
        api.bookAbstractList("叔本华", 2);

        book = api.bookDetail("人生的智慧 / (德)叔本华(Schopenhauer, A.)著");

        assertEquals("人生的智慧 / (德)叔本华(Schopenhauer, A.)著", book.getTitle());
        assertEquals("B516.41 3A", book.getCallNumber());
        assertEquals("叔本华 (Schopenhauer, Arthur), 1788-1860 著", book.getAuthor());
        assertEquals("7-5008-0066-5", book.getISBN());
        assertEquals("http://202.114.9.17/bibimage/zycover.php?isbn=7500800665", book.getImgUrl());
        assertEquals("流通三线书库", book.getStoreInfos().get(0)[0]);
        assertEquals("打捆,不外借", book.getStoreInfos().get(0)[2]);
        assertEquals("流通三线书库", book.getStoreInfos().get(1)[0]);
        assertEquals("打捆,不外借", book.getStoreInfos().get(1)[2]);
    }
}