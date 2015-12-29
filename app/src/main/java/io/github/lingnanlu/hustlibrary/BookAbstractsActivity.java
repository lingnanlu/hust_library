package io.github.lingnanlu.hustlibrary;

import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.debug.hv.ViewServer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.lingnanlu.core.AppAction;
import io.github.lingnanlu.core.AppActionImpl;
import io.github.lingnanlu.core.CallBackListener;
import io.github.lingnanlu.hustlibrary.adapter.ItemAdapter;
import io.github.lingnanlu.model.BookAbstract;

public class BookAbstractsActivity extends AppCompatActivity implements
        AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener {

    private static final String TAG = "BookAbstractsActivity";
    public static final String EXTRA_BOOK_URL = "io.github.lingnanlu.hustlibrary.book_url";
    public static final String EXTRA_BOOK_COVER_URL =
            "io.github.lingnanlu.hustlibrary.book_cover_url";

    private ItemAdapter mItemAdapter;
    private String mKeyWord;
    private AppAction mAppAction;
    private Boolean mIsLoadingMore = false;
    private int mNextPage = 1;

    @Bind(R.id.list_book_abstracts)
    ListView mListView;

    @Bind(R.id.toolbar_custom)
    Toolbar mToolbar;

    LinearLayout mFooter;
    ProgressBar mLoadingMoreProgreeBar;
    TextView mNoMoreData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_abstracts);
        ButterKnife.bind(this);

        //init toolbar
        initToolbar();

        initList();

        mKeyWord = getIntent().getStringExtra(MainActivity.EXTRA_KEYWORD);

        mAppAction = AppActionImpl.getInstance();

        mAppAction.loadBooks(mKeyWord, mNextPage, new CallBackListener<ArrayList<BookAbstract>>() {

            @Override
            public void onSuccess(ArrayList<BookAbstract> data) {

                mItemAdapter = new ItemAdapter(BookAbstractsActivity.this, data);
                mListView.setAdapter(mItemAdapter);
                mListView.setOnScrollListener(BookAbstractsActivity.this);

            }

            @Override
            public void onError() {

            }
        });

        ViewServer.get(this).addWindow(this);
    }

    private void initList() {

        //set empty view
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

        //set footer
        mFooter = (LinearLayout) LayoutInflater.from(this).
                inflate(R.layout.footer_book_abstract_list,null, false);
        mLoadingMoreProgreeBar = (ProgressBar)mFooter.findViewById(R.id.progressbar_loading_more);
        mNoMoreData = (TextView)mFooter.findViewById(R.id.text_no_more_data);
        mListView.addFooterView(mFooter);

        //registe listener
        mListView.setOnItemClickListener(this);

    }

    private void initToolbar() {

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
                );

        boolean reachEnd = firstVisibleItem + visibleItemCount >= totalItemCount;
        if(reachEnd && !mIsLoadingMore) {

            mNextPage++;
            mIsLoadingMore = true;
            mLoadingMoreProgreeBar.setVisibility(View.VISIBLE);
            mAppAction.loadBooks(mKeyWord, mNextPage, new CallBackListener<ArrayList<BookAbstract>>() {

                @Override
                public void onSuccess(ArrayList<BookAbstract> data) {
                    mItemAdapter.addData(data);
                    mItemAdapter.notifyDataSetChanged();
                    mIsLoadingMore = false;
                }

                @Override
                public void onError() {
                    mNoMoreData.setVisibility(View.VISIBLE);
                }
            });
//                if (nextPageUrl != null) {
//
//                    LoadMoreItemTask task = new LoadMoreItemTask();
//                    mPreTask = task;
//                    task.execute(nextPageUrl);
//                } else {
//                    mLoadingMoreProgreeBar.setVisibility(View.GONE);
//                    mNoMoreData.setVisibility(View.VISIBLE);
//                }

        }


    }

    /*
     *  以下两个方法将AsyncTask中代码移动到Activity下，这样更能体现Activity的Controller角色当某某事件发生时，
     *  Controller需要执行的动作就写成onXXX方法
     *  这也是为什么要使用Activity来实现OnScrollListenser的原因
     */

//    private void onMoreDataLoaded(ArrayList<BookAbstract> bookAbstracts) {
//
//        if (bookAbstracts != null) {
//
//            Log.d(TAG, "onMoreDataLoaded: Before mBookItem Size " + mBookAbstracts.size());
//            Log.d(TAG, "onMoreDataLoaded: bookAbstracts size " + bookAbstracts.size());
//            mBookAbstracts.addAll(bookAbstracts);
//
//            Log.d(TAG, "onMoreDataLoaded: After mBookItem size " + mBookAbstracts.size());
//            mItemAdapter.notifyDataSetChanged();
//        }
//
//
//    }

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



}
