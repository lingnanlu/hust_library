package io.github.lingnanlu.hustlibrary.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.github.lingnanlu.hustlibrary.api.Api;
import io.github.lingnanlu.hustlibrary.api.ApiImpl;
import io.github.lingnanlu.hustlibrary.bean.Book;
import io.github.lingnanlu.hustlibrary.bean.BookAbstract;

/**
 * Created by Administrator on 2015/12/28.
 * 所有界面需要的操作(除数据绑定与用户交互,都有该模块执行
 * 该模块利用api取得数据,并利用回调接口通过UI层
 */
public class AppActionImpl implements AppAction {

    private static final String TAG = "AppActionImpl";
    private static AppActionImpl  instance;
    private Api mApi = new ApiImpl();
    private OkHttpClient mHttpClient = new OkHttpClient();
    public AppActionImpl(){}

//    public static AppAction getInstance() {
//
//        if (instance == null) {
//            return new AppActionImpl();
//        }
//        return instance;
//
//    }

    @Override
    public void loadBookList(final String keyword, final int page,
                             final CallBack<ArrayList<BookAbstract>> listener) {

        new AsyncTask<String, Void, ArrayList<BookAbstract>>() {

            @Override
            protected ArrayList<BookAbstract> doInBackground(String... params) {

                Log.d(TAG, "doInBackground() called with: " + keyword + " " + page);
                return mApi.bookAbstractList(keyword, page);

            }

            @Override
            protected void onPostExecute(ArrayList<BookAbstract> bookAbstracts) {

                if (bookAbstracts != null) {
                    listener.onSuccess(bookAbstracts);
                } else {
                    listener.onError();
                }
            }
        }.execute();
    }

    @Override
    public void loadBook(final String bookTitle, final CallBack<Book> listener) {

        new AsyncTask<String, Void, Book>() {

            @Override
            protected Book doInBackground(String... params) {

                Book book = mApi.bookDetail(bookTitle);

                Request.Builder builder = new Request.Builder();
                Request bookCoverRequest = builder.url(book.getImgUrl()).build();

                Response response = null;
                try {

                    response = mHttpClient.newCall(bookCoverRequest).execute();

                    InputStream in = response.body().byteStream();

                    if (in != null) {

                        Bitmap bookCover = BitmapFactory.decodeStream(in);
                        book.setCover(bookCover);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return book;

            }


            @Override
            protected void onPostExecute(Book book) {

                if (book != null) {
                    listener.onSuccess(book);
                } else {
                    listener.onError();
                }
            }

        }.execute();
    }




//    private class ListViewInitTask extends AsyncTask<String, Void,
//            Void> {
//
//        @Override
//        protected Void doInBackground(String... params) {
//
//            Log.d(TAG, "doInBackground() called with: " + params[0]);
//            String requestUrl = RequestUrlBuilder.build(params[0]);
//
//            Log.d(TAG, requestUrl);
//            Request request = new Request.Builder().url(requestUrl).build();
//
//            Response response = null;
//            try {
//                response = mClient.newCall(request).execute();
//
//                Log.d(TAG, response.toString());
//
//                String content = response.body().string();
//                mSearchResultMetaInfo = HtmlParser.parseMetaInfo(content);
//
//                // TODO: 2015/12/14
//                // 解析结果时，暂时还未fill keyWord
//                mSearchResultMetaInfo.setKeyWord(mKeyWord);
//                mBookAbstracts = HtmlParser.parseBookAbstracts(content);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            onInitDataLoaded();
//        }
//    }
//
//    private class LoadMoreItemTask extends AsyncTask<String, Void, ArrayList<BookAbstract>> {
//
//        private static final String TAG = "LoadMoreItemTask";
//
//        @Override
//        protected ArrayList<BookAbstract> doInBackground(String... params) {
//
//            if (params[0] != null) {
//
//                Request request = new Request.Builder().url(params[0]).build();
//
//                Response response;
//                try {
//                    response = mClient.newCall(request).execute();
//
//                    if (response != null) {
//
//                        String content = response.body().string();
//                        return HtmlParser.parseBookAbstracts(content);
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<BookAbstract> bookAbstracts) {
//
//            onMoreDataLoaded(bookAbstracts);
//        }
//    }
}
