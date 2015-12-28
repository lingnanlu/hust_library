package io.github.lingnanlu.core;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

import io.github.lingnanlu.model.BookAbstract;

/**
 * Created by Administrator on 2015/12/28.
 */
public class AppActionImpl implements AppAction {

    private static AppActionImpl  instance;
    private AppActionImpl(){}

    public static AppAction getInstance() {

        if (instance == null) {
            return new AppActionImpl();
        }
        return instance;

    }
    @Override
    public void loadBooks(String keyWord, int page, CallBackListener<ArrayList<BookAbstract>>
            listener) {

    }


    private class ListViewInitTask extends AsyncTask<String, Void,
            Void> {

        @Override
        protected Void doInBackground(String... params) {

            Log.d(TAG, "doInBackground() called with: " + params[0]);
            String requestUrl = RequestUrlBuilder.build(params[0]);

            Log.d(TAG, requestUrl);
            Request request = new Request.Builder().url(requestUrl).build();

            Response response = null;
            try {
                response = mClient.newCall(request).execute();

                Log.d(TAG, response.toString());

                String content = response.body().string();
                mSearchResultMetaInfo = HtmlParser.parseMetaInfo(content);

                // TODO: 2015/12/14
                // 解析结果时，暂时还未fill keyWord
                mSearchResultMetaInfo.setKeyWord(mKeyWord);
                mBookAbstracts = HtmlParser.parseBookAbstracts(content);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onInitDataLoaded();
        }
    }

    private class LoadMoreItemTask extends AsyncTask<String, Void, ArrayList<BookAbstract>> {

        private static final String TAG = "LoadMoreItemTask";

        @Override
        protected ArrayList<BookAbstract> doInBackground(String... params) {

            if (params[0] != null) {

                Request request = new Request.Builder().url(params[0]).build();

                Response response;
                try {
                    response = mClient.newCall(request).execute();

                    if (response != null) {

                        String content = response.body().string();
                        return HtmlParser.parseBookAbstracts(content);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<BookAbstract> bookAbstracts) {

            onMoreDataLoaded(bookAbstracts);
        }
    }
}
