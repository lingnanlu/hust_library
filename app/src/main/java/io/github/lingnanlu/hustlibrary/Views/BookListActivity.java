package io.github.lingnanlu.hustlibrary.Views;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.debug.hv.ViewServer;
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
import io.github.lingnanlu.hustlibrary.model.BookAbstract;
import io.github.lingnanlu.hustlibrary.model.SearchResultMetaInfo;
import io.github.lingnanlu.hustlibrary.utils.BitmapCache;
import io.github.lingnanlu.hustlibrary.utils.HtmlParser;
import io.github.lingnanlu.hustlibrary.utils.RequestUrlBuilder;

public class BookListActivity extends AppCompatActivity implements
        AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener {

    private static final String TAG = "BookListActivity";
    public static final String EXTRA_BOOK_URL = "io.github.lingnanlu.hustlibrary.book_url";
    public static final String EXTRA_BOOK_COVER_URL =
            "io.github.lingnanlu.hustlibrary.book_cover_url";

    private boolean mHasLoaded = false;
    private OkHttpClient mClient = new OkHttpClient();
    private SearchResultMetaInfo mSearchResultMetaInfo;
    private ArrayList<BookAbstract> mBookAbstracts;
    private ItemAdapter mItemAdapter;
    private String mKeyWord;
    private LoadMoreItemTask mPreTask;
    private int mListScrollState = SCROLL_STATE_IDLE;
    @Bind(R.id.list_book_list)
    ListView mListView;

    @Bind(R.id.toolbar_custom)
    Toolbar mToolbar;

    LinearLayout mFooter;
    ProgressBar mLoadingMoreProgreeBar;
    TextView mNoMoreData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mItemAdapter = new ItemAdapter(this);

        mListView.setOnItemClickListener(this);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER)
        );
        progressBar.setIndeterminate(true);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
        mListView.setEmptyView(progressBar);

        mFooter = (LinearLayout)LayoutInflater.from(this).
                inflate(R.layout.footer_book_abstract_list,null, false);
        mLoadingMoreProgreeBar = (ProgressBar)mFooter.findViewById(R.id.progressbar_loading_more);
        mNoMoreData = (TextView)mFooter.findViewById(R.id.text_no_more_data);
        mListView.addFooterView(mFooter);

        mKeyWord = getIntent().getStringExtra(MainActivity.EXTRA_KEYWORD);

        new ListViewInitTask().execute(mKeyWord);


        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        mListScrollState = scrollState;
    }

    /*
     * 官方文档上说,"This will be called after the scroll has completed",即scroll has completed后,
     * 会调用该方法,但没有有说只有在这种情况下才调用该方法,the scroll has completed并不是调用onScroll的充分必要条件
     * 而且log显示在没有scroll之前该方法会调用多次.
     *
     * 另一个值得注意的地方的totalItemCount参数,官方中说明
     *
     * totalItemCount: the number of items in the list adaptor
     *
     * 即当设置了list adapter后,totalItemCount才有意义,而setOnScrollListener会调用onScroll,如果在调用setAdapter
     * 之前调用setOnScrollListener,则totalItemCount为0.
     */
    @Override
    public void onScroll(AbsListView view,
                         int firstVisibleItem,
                         int visibleItemCount,
                         int totalItemCount) {

        //所有的控制逻辑放在Activity中，不要放到AsyncTask中，保持AsyncTask任务的单一性

        Log.d(TAG, "onScroll() "
                + " firstVisibleItem " + firstVisibleItem
                + " visibleItemCount " + visibleItemCount
                + " totalItemCount " + totalItemCount
                + " mListScrollState " + mListScrollState
                );

        boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
        if(loadMore) {
            if ((mPreTask == null && mHasLoaded)
                    || (mHasLoaded && mPreTask.getStatus() == AsyncTask.Status.FINISHED)) {

                String nextPageUrl = mSearchResultMetaInfo.nextPageUrl();
                if (nextPageUrl != null) {
                    mLoadingMoreProgreeBar.setVisibility(View.VISIBLE);
                    LoadMoreItemTask task = new LoadMoreItemTask();
                    mPreTask = task;
                    task.execute(nextPageUrl);
                } else {
                    mLoadingMoreProgreeBar.setVisibility(View.GONE);
                    mNoMoreData.setVisibility(View.VISIBLE);
                }
            }
        }


    }

    /*
     *  以下两个方法将AsyncTask中代码移动到Activity下，这样更能体现Activity的Controller角色当某某事件发生时，
     *  Controller需要执行的动作就写成onXXX方法
     *  这也是为什么要使用Activity来实现OnScrollListenser的原因
     */
    private void onInitDataLoaded() {

        Log.d(TAG, "onInitDataLoaded: mBookItem size " + mBookAbstracts.size());
        mListView.setAdapter(mItemAdapter);
        mListView.setOnScrollListener(this);
        mHasLoaded = true;

    }

    private void onMoreDataLoaded(ArrayList<BookAbstract> bookAbstracts) {


        if (bookAbstracts != null) {

            Log.d(TAG, "onMoreDataLoaded: Before mBookItem Size " + mBookAbstracts.size());
            Log.d(TAG, "onMoreDataLoaded: bookAbstracts size " + bookAbstracts.size());
            mBookAbstracts.addAll(bookAbstracts);

            Log.d(TAG, "onMoreDataLoaded: After mBookItem size " + mBookAbstracts.size());
            mItemAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        /*
         * 使用该方法，会自动处理有header和footer的情况
         * 不能直接使用adapter的getItem方法
         * 注意position和id在有header和footer时不同。
         */
        BookAbstract bookAbstract = (BookAbstract) parent.getItemAtPosition(position);


        if (bookAbstract != null) {
            Intent intent = new Intent(this, BookDetailActivity.class);

            String prefix = "http://ftp.lib.hust.edu.cn";
            intent.putExtra(EXTRA_BOOK_URL, prefix + bookAbstract.getUrl());
            intent.putExtra(EXTRA_BOOK_COVER_URL, bookAbstract.getImageUrl());

            startActivity(intent);
        }

    }

    private class ItemAdapter extends BaseAdapter {

        private ImageLoader mImageLoader;
        private LayoutInflater mInflator;

        public ItemAdapter(Context context) {

            mInflator = LayoutInflater.from(context);
            RequestQueue queue = Volley.newRequestQueue(context);
            mImageLoader = new ImageLoader(queue, new BitmapCache());

        }

        @Override
        public int getCount() {
            if (mBookAbstracts != null) {

                return mBookAbstracts.size();
            }

            return 0;

        }

        @Override
        public Object getItem(int position) {

            if (mBookAbstracts != null) {

                return mBookAbstracts.get(position);

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

                convertView = mInflator.inflate(R.layout.item_book_abstract, null);

                viewHolder = new ViewHolder();

                viewHolder.bookAuthor = (TextView) convertView.findViewById(R.id.text_book_author);
                viewHolder.bookTitle = (TextView) convertView.findViewById(R.id.text_book_title);
                viewHolder.bookPress = (TextView) convertView.findViewById(R.id.text_book_press);
                viewHolder.bookCover = (NetworkImageView) convertView.findViewById(R.id.image_book_cover);

                convertView.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolder) convertView.getTag();

            }

            BookAbstract bookAbstract = mBookAbstracts.get(position);

            viewHolder.bookAuthor.setText(bookAbstract.getAuthor());
            viewHolder.bookPress.setText(bookAbstract.getPress());
            viewHolder.bookTitle.setText(bookAbstract.getBookTitle());


            //有的item没有封面imgeUrl会是/screen/xxxxx
            if (bookAbstract.getImageUrl().startsWith("http")) {

         /*       Picasso.with(BookListActivity.this)
                        .load(bookAbstract.getImageUrl())
                        .placeholder(R.drawable.ic_book_black_36dp)
                        .error(R.drawable.ic_book_black_36dp)
                        .into(viewHolder.bookCover);*/

                viewHolder.bookCover.setDefaultImageResId(R.drawable.ic_book_black_36dp);
                viewHolder.bookCover.setImageUrl(bookAbstract.getImageUrl(), mImageLoader);

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
