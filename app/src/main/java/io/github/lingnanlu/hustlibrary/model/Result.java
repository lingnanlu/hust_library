package io.github.lingnanlu.hustlibrary.model;

import io.github.lingnanlu.hustlibrary.utils.RequestUrlBuilder;

/**
 * Created by Rico on 2015/12/13.
 */
public class Result {

    private String mKeyWord;

    private int mBegin;
    private int mEnd;

    private int mTotalCount;


    private boolean moveToNextPage() {

        if(mEnd == mTotalCount) {

            //说明是最后一页了
            return false;

        } else {

            mBegin = mEnd + 1;
            mEnd += Math.min(50, mTotalCount - mEnd);
            return true;

        }


    }
    public String nextPageUrl() {

        if(moveToNextPage()) {
            return RequestUrlBuilder.build(mKeyWord, mBegin, mTotalCount);
        }
        return null;

    }

    public String getKeyWord() {
        return mKeyWord;
    }

    public void setKeyWord(String keyWord) {
        mKeyWord = keyWord;
    }


    public int getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(int totalCount) {
        mTotalCount = totalCount;
    }


    public int getBegin() {
        return mBegin;
    }

    public void setBegin(int begin) {
        mBegin = begin;
    }

    public int getEnd() {
        return mEnd;
    }

    public void setEnd(int end) {
        mEnd = end;
    }
}
