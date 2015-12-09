package io.github.lingnanlu.hustlibrary.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

import io.github.lingnanlu.hustlibrary.R;
import io.github.lingnanlu.hustlibrary.model.Item;
import io.github.lingnanlu.hustlibrary.utils.HtmlParser;
import io.github.lingnanlu.hustlibrary.utils.RequestMaker;

public class ItemListActivity extends AppCompatActivity {

    private static final String TAG = "ItemListActivity";

    private OkHttpClient mClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        String keyWord = getIntent().getStringExtra(MainActivity.DATA_KEYWORD);

        Log.d(TAG, keyWord);

        final Request request = RequestMaker.make("search*chx/X?SEARCH=" +
                keyWord);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = mClient.newCall(request).execute();

                    String content = response.body().string();
                    if (response != null)
                        Log.d(TAG, content);

                    ArrayList<Item> items = HtmlParser.parserItems(content);

                    Log.d(TAG, items.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }


}
