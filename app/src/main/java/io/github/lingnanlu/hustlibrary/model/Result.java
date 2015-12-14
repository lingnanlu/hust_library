package io.github.lingnanlu.hustlibrary.model;

/**
 * Created by Rico on 2015/12/13.
 */
public class Result {

    private String mKeyWord;
    private int mIndex;
    private int mTotalCount;
    private int mCurrentPageItemCount;

    public String nextPageUrl() {
        return null;
    }

    public String getKeyWord() {
        return mKeyWord;
    }

    public void setKeyWord(String keyWord) {
        mKeyWord = keyWord;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(int totalCount) {
        mTotalCount = totalCount;
    }

    public int getCurrentPageItemCount() {
        return mCurrentPageItemCount;
    }

    public void setCurrentPageItemCount(int currentPageItemCount) {
        mCurrentPageItemCount = currentPageItemCount;
    }
}
