package io.github.lingnanlu.hustlibrary.model;

/**
 * Created by Administrator on 2015/12/9.
 */
public class Item {

    private String mUrl;
    private String mBookTitle;
    private String mAuthor;
    private String mPress;

    public String getmPress() {
        return mPress;
    }

    public void setmPress(String mPress) {
        this.mPress = mPress;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmBookTitle() {
        return mBookTitle;
    }

    public void setmBookTitle(String mBookTitle) {
        this.mBookTitle = mBookTitle;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
