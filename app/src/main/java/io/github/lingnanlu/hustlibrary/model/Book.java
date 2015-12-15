package io.github.lingnanlu.hustlibrary.model;

import java.util.ArrayList;

/**
 * Created by Rico on 2015/12/9.
 */
public class Book {

    private String mTitle;
    private String mAuthor;
    private String mTranslator;
    private String mPress;
    private String mAbstracts;
    private String mISBN;
    private String mLocation;
    private String mCallNumber;
    private ArrayList<String[]> mStoreInfos;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getTranslator() {
        return mTranslator;
    }

    public void setTranslator(String translator) {
        mTranslator = translator;
    }

    public String getPress() {
        return mPress;
    }

    public void setPress(String press) {
        mPress = press;
    }

    public String getAbstracts() {
        return mAbstracts;
    }

    public void setAbstracts(String abstracts) {
        mAbstracts = abstracts;
    }

    public String getISBN() {
        return mISBN;
    }

    public void setISBN(String iSBN) {
        mISBN = iSBN;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getCallNumber() {
        return mCallNumber;
    }

    public void setCallNumber(String callNumber) {
        mCallNumber = callNumber;
    }


    public ArrayList<String[]> getStoreInfos() {
        return mStoreInfos;
    }

    public void setStoreInfos(ArrayList<String[]> storeInfos) {
        mStoreInfos = storeInfos;
    }
}
