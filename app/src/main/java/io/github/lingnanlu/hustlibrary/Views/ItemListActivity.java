package io.github.lingnanlu.hustlibrary.Views;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.github.lingnanlu.hustlibrary.R;
import io.github.lingnanlu.hustlibrary.model.Item;
import io.github.lingnanlu.hustlibrary.utils.HtmlParser;
import io.github.lingnanlu.hustlibrary.utils.RequestMaker;

public class ItemListActivity extends AppCompatActivity {

    private static final String TAG = "ItemListActivity";

    private OkHttpClient mClient = new OkHttpClient();

    private Handler mHandler;

    public static final String[] strs = new String[]{
            "first", "second", "third", "fourch", "fifth"
    };

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        String keyWord = getIntent().getStringExtra(MainActivity.DATA_KEYWORD);

        Log.d(TAG, keyWord);

        final Request request = RequestMaker.make("search*chx/X?SEARCH=" +
                keyWord);

        mHandler = new Handler();

        mListView = (ListView) findViewById(R.id.listView);

        //启动另一个线程从网络中读数据并解析，解析完后，发送一条消息到UI线程，由UI线程更新UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;

                try {
                    response = mClient.newCall(request).execute();

                    String content = response.body().string();
                    if (response != null)
                        Log.d(TAG, content);

                    final ArrayList<Item> items = HtmlParser.parserItems
                            (content);


                    //将一条Message post到UI线程的MessageQueue,
                    // 并在UI线程中执行run方法，所以该方法体中要更新UI
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "" + items.size());

                            ArrayList<HashMap<String, String>> list = new
                                    ArrayList<HashMap<String, String>>();

                            for(Item item : items) {

                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("TITLE", item.getBookTitle());
                                map.put("AUTHOR", item.getAuthor());
                                map.put("PRESS", item.getPress());
                                list.add(map);

                            }

                            SimpleAdapter adapter = new SimpleAdapter(
                                    ItemListActivity.this,
                                    list,
                                    R.layout.list_item,
                                    new String[] {"TITLE", "AUTHOR", "PRESS"},
                                    new int[] { R.id.listItemBookTitle,
                                                R.id.listItemBookAuthor,
                                                R.id.listItemBookPress});

                            mListView.setAdapter(adapter);
                        }
                    });

                    // Log.d(TAG, items.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();


    }


}
