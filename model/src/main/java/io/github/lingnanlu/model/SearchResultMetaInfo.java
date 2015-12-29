package io.github.lingnanlu.model;

/**
 * Created by Rico on 2015/12/13.
 */
public class SearchResultMetaInfo {

   // public static final int COUNT_PER_PAGE = 50;

    private String mKeyWord;
    private int mBegin;
    private int mEnd;
    private int mTotalCount;
    private String mCurrentPageUrl;

//    /**
//     * 返回下一页的url，如果当前页为最后一页，返回null
//     * @return
//     */
//    public String nextPageUrl() {
//
//        if( mEnd == mTotalCount ) {
//
//            //说明是最后一页了
//            return null;
//
//        } else {
//
//            mBegin = mEnd + 1;
//            mEnd += Math.min(COUNT_PER_PAGE, mTotalCount - mEnd);
//            return RequestUrlBuilder.build(mKeyWord, mBegin, mTotalCount);
//
//        }
//
//    }

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

    public String getCurrentPageUrl() {
        return mCurrentPageUrl;
    }

    public void setCurrentPageUrl(String currentPageUrl) {
        this.mCurrentPageUrl = currentPageUrl;
    }
}
