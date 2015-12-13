package io.github.lingnanlu.hustlibrary.model;

import java.util.ArrayList;

/**
 * Created by Rico on 2015/12/13.
 */
public class Result {

    private ArrayList<Item> mItems;
    private ArrayList<String> mUrls;


    public ArrayList<Item> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<Item> items) {
        mItems = items;
    }

    public ArrayList<String> getUrls() {
        return mUrls;
    }

    public void setUrls(ArrayList<String> urls) {
        mUrls = urls;
    }
}
