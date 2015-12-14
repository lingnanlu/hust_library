package io.github.lingnanlu.hustlibrary.model;

import io.github.lingnanlu.hustlibrary.utils.RequestUrlBuilder;

/**
 * Created by Rico on 2015/12/13.
 */
public class Result {

    private String mKeyWord;
    private int mIndex;
    private int mTotalCount;
    private int mStep;

    public String nextPageUrl() {
        return RequestUrlBuilder.build(mKeyWord, mIndex + mStep, mTotalCount);
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


    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        mStep = step;
    }
}
