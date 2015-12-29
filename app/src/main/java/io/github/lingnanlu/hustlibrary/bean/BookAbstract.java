package io.github.lingnanlu.hustlibrary.bean;

/**
 * Created by Administrator on 2015/12/28.
 */
public class BookAbstract {

    private String mUrl;
    private String mBookTitle;
    private String mAuthor;
    private String mPress;
    private String mImageUrl;

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getPress() {
        return mPress;
    }

    public void setPress(String mPress) {
        this.mPress = mPress;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    public void setBookTitle(String mBookTitle) {
        this.mBookTitle = mBookTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
