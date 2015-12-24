package io.github.lingnanlu.hustlibrary.Views;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.lingnanlu.hustlibrary.R;
import io.github.lingnanlu.hustlibrary.model.Item;
import io.github.lingnanlu.hustlibrary.model.Result;
import io.github.lingnanlu.hustlibrary.utils.BitmapCache;
import io.github.lingnanlu.hustlibrary.utils.HtmlParser;
import io.github.lingnanlu.hustlibrary.utils.RequestUrlBuilder;

public class ItemListActivity extends AppCompatActivity implements
        AbsListView.OnScrollListener, AdapterView.OnItemClickListener{

    private static final String TAG = "ItemListActivity";

    public static final String BOOK_URL = "io.github.lingnanlu" +
            ".hustlibrary.book_url";
    public static final String BOOK_COVER_URL = "io.github" +
            ".lingnanlu" +
            ".hustlibrary.book_cover_url";
    private boolean mHasLoaded = false;

    private OkHttpClient mClient = new OkHttpClient();
    private Result mResult;
    private ArrayList<Item> mBookItems;
    private ItemAdapter mItemAdapter;
    private String mKeyWord;
    private LoadMoreItemTask mPreTask;

    @Bind(R.id.listView)
    ListView mListView;

    @Bind(R.id.myToolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mItemAdapter = new ItemAdapter(this);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);

        mKeyWord = getIntent().getStringExtra(MainActivity.DATA_KEYWORD);


        new ListViewInitTask().execute(mKeyWord);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int
            scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int
            firstVisibleItem, int visibleItemCount, int totalItemCount) {


        Log.d(TAG, "onScroll() called with: " +
                "firstVisibleItem = [" + firstVisibleItem + "], " +
                "visibleItemCount = [" + visibleItemCount + "], " +
                "totalItemCount = [" + totalItemCount + "]");

        //所有的控制逻辑放在Activity中，不要放到AsyncTask中，保持AsyncTask任务的单一性

        Log.d(TAG, "onScroll() mPreTask " + mPreTask + " mHasLoaded" +
                " " +
                "" +
                mHasLoaded);

        if((firstVisibleItem + visibleItemCount) >= totalItemCount) {
            if ((mPreTask == null && mHasLoaded)
                    || (mHasLoaded && mPreTask.getStatus() == AsyncTask
                    .Status.FINISHED)) {

                LoadMoreItemTask task = new LoadMoreItemTask();
                mPreTask = task;
                task.execute(mResult.nextPageUrl());

            }
        }

    }

    /*
    以下两个方法将AsyncTask中代码移动到Activity下，这样更能体现Activity的Controller角色
    当某某事件发生时，Controller需要执行的动作就写成onXXX方法
    这也是为什么要使用Activity来实现OnScrollListenser的原因
     */
    private void onInitDataLoaded() {

        Log.d(TAG, "onInitDataLoaded: mBookItem size " + mBookItems.size());
        mListView.setAdapter(mItemAdapter);
        mHasLoaded = true;

    }

    private void onMoreDataLoaded(ArrayList<Item> items) {


        if(items != null) {

            Log.d(TAG, "onMoreDataLoaded: Before mBookItem Size " +
                    mBookItems.size());
            Log.d(TAG, "onMoreDataLoaded: items size " + items.size());
            mBookItems.addAll(items);

            Log.d(TAG, "onMoreDataLoaded: After mBookItem size " +
                    mBookItems.size());
            mItemAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int
            position, long id) {

        //使用该方法，会自动处理有header和footer的情况
        //不能直接使用adapter的getItem方法
        //注意position和id在有header和footer时不同。

        Item item = (Item) parent.getItemAtPosition(position);


        if(item != null) {
            Intent intent = new Intent(this,BookDetailActivity.class);

            String prefix = "http://ftp.lib.hust.edu.cn";
            intent.putExtra(BOOK_URL, prefix + item.getUrl());
            intent.putExtra(BOOK_COVER_URL, item.getImageUrl());

            startActivity(intent);
        }

    }

    private class ItemAdapter extends BaseAdapter {

        ImageLoader mImageLoader;
        private LayoutInflater inflater;

        public ItemAdapter(Context context) {

            inflater = LayoutInflater.from(context);
            RequestQueue queue = Volley.newRequestQueue(context);
            mImageLoader = new ImageLoader(queue, new BitmapCache());

        }

        @Override
        public int getCount() {
            if(mBookItems != null) {

                return mBookItems.size();
            }

            return 0;

        }

        @Override
        public Object getItem(int position) {

            if(mBookItems != null) {

                return mBookItems.get(position);

            }

            return null;

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout
                                .list_item,
                        null);

                viewHolder = new ViewHolder();

                viewHolder.bookAuthor = (TextView) convertView.findViewById(R
                        .id.bookAuthor);
                viewHolder.bookTitle = (TextView) convertView.findViewById(R
                        .id.bookTitle);
                viewHolder.bookPress = (TextView) convertView.findViewById(R
                        .id.bookPress);
                viewHolder.bookCover = (NetworkImageView) convertView
                        .findViewById(R
                        .id.bookCover);

                convertView.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolder)convertView.getTag();

            }

            Item item = mBookItems.get(position);

            viewHolder.bookAuthor.setText(item.getAuthor());
            viewHolder.bookPress.setText(item.getPress());
            viewHolder.bookTitle.setText(item.getBookTitle());


            //有的item没有封面imgeUrl会是/screen/xxxxx
            if(item.getImageUrl().startsWith("http")) {

//                Picasso.with(ItemListActivity.this)
//                        .load(item.getImageUrl())
//                        .placeholder(R.drawable.ic_book_black_36dp)
//                        .error(R.drawable.ic_book_black_36dp)
//                        .into(viewHolder.bookCover);

                viewHolder.bookCover.setDefaultImageResId(R.drawable
                        .ic_book_black_36dp);
                viewHolder.bookCover.setImageUrl(item.getImageUrl(),
                        mImageLoader);

            }

            return convertView;
        }


        class ViewHolder {

            TextView bookTitle;
            TextView bookAuthor;
            TextView bookPress;
            NetworkImageView bookCover;


        }
    }

    private class ListViewInitTask extends AsyncTask<String, Void,
            Void> {

        @Override
        protected Void doInBackground(String... params) {

            Log.d(TAG, "doInBackground() called with: " + params[0]);
            String requestUrl = RequestUrlBuilder.build(params[0]);

            Log.d(TAG, requestUrl);
            Request request = new Request.Builder().url(requestUrl).build();

            Response response;
            try {
                response = mClient.newCall(request).execute();

                Log.d(TAG, response.toString());



                    String content = response.body().string();
                    mResult = HtmlParser.parseResult(content);

                    // TODO: 2015/12/14
                    // 解析结果时，暂时还未fill keyWord
                    mResult.setKeyWord(mKeyWord);
                    mBookItems = HtmlParser.parseItems(content);


            } catch (IOException e) {

                Log.d(TAG, "can't get response " + e);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onInitDataLoaded();
        }
    }

    private class LoadMoreItemTask extends AsyncTask<String, Void,
            ArrayList<Item>> {

        private static final String TAG = "LoadMoreItemTask";
        @Override
        protected ArrayList<Item> doInBackground(String... params) {


            if(params[0] != null) {

                Request request = new Request.Builder().url(params[0])
                        .build();

                Response response;
                try {
                    response = mClient.newCall(request).execute();

                    if (response != null) {

                        String content = response.body().string();
                        return HtmlParser.parseItems(content);

                    }
                } catch (IOException e) {

                    Log.d(TAG, "can't get response " + e);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {

            onMoreDataLoaded(items);
        }
    }

}
