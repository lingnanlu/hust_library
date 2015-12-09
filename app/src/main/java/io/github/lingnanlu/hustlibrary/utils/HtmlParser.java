package io.github.lingnanlu.hustlibrary.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.github.lingnanlu.hustlibrary.model.Book;
import io.github.lingnanlu.hustlibrary.model.Item;

/**
 * Created by Administrator on 2015/12/9.
 */
public class HtmlParser {

    private static final String TAG = "HtmlParser";


    public static ArrayList<Item> parserItems(String html) {

        ArrayList<Item> items = new ArrayList<>();

        Document document = Jsoup.parse(html);

        Elements books = document.select(".briefCitRow");

        Log.d(TAG, books.toString());


        return null;
    }


    public static Book parseBook(String html) {
        return null;
    }


}

