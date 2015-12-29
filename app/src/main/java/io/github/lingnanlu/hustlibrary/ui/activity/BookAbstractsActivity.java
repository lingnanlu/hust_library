package io.github.lingnanlu.hustlibrary.ui.activity;

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
import android.widget.Toast;

import com.android.debug.hv.ViewServer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.lingnanlu.hustlibrary.core.AppAction;
import io.github.lingnanlu.hustlibrary.core.CallBack;
import io.github.lingnanlu.hustlibrary.HustLibApplication;
import io.github.lingnanlu.hustlibrary.R;
import io.github.lingnanlu.hustlibrary.ui.adapter.BookListAdapter;
import io.github.lingnanlu.hustlibrary.bean.BookAbstract;

public class BookAbstractsActivity extends AppCompatActivity implements
        AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener {

    private static final String TAG = "BookAbstractsActivity";
    public static final String EXTRA_BOOK_URL = "io.github.lingnanlu.hustlibrary.book_url";
    public static final String EXTRA_BOOK_TITLE = "io.github.lingnanlu.hustlibrary.book_title";
    public static final String EXTRA_BOOK_COVER_URL =
            "io.github.lingnanlu.hustlibrary.book_cover_url";

    private BookListAdapter mBookListAdapter;
    private String mKeyWord;
    private AppAction mAppAction;
    private Boolean mIsLoadingMore = false;
    private Boolean mHasMoreData = true;
    private int mCurrentPage = 1;

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

        initToolbar();
        initList();

        mKeyWord = getIntent().getStringExtra(MainActivity.EXTRA_KEYWORD);

        HustLibApplication application = (HustLibApplication)this.getApplication();
        mAppAction = application.getAppAction();

        mAppAction.loadBookList(mKeyWord, mCurrentPage, new CallBack<ArrayList<BookAbstract>>() {

            @Override
            public void onSuccess(ArrayList<BookAbstract> data) {

                mBookListAdapter = new BookListAdapter(BookAbstractsActivity.this, data);
                mListView.setAdapter(mBookListAdapter);
                mListView.setOnScrollListener(BookAbstractsActivity.this);

            }

            @Override
            public void onError() {
                Toast.makeText(BookAbstractsActivity.this, "no data fetched", Toast.LENGTH_SHORT)
                        .show();
            }
        });

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
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

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

        Log.d(TAG, "onScroll() "
                        + " firstVisibleItem " + firstVisibleItem
                        + " visibleItemCount " + visibleItemCount
                        + " totalItemCount " + totalItemCount
                        + " isLoadingMore " + mIsLoadingMore
        );

        boolean reachEnd = firstVisibleItem + visibleItemCount >= totalItemCount;

        if (reachEnd && !mIsLoadingMore && mHasMoreData) {

            mCurrentPage++;
            mIsLoadingMore = true;

            //到达底端时,显示ProgressBar
            mLoadingMoreProgreeBar.setVisibility(View.VISIBLE);

            mAppAction.loadBookList(mKeyWord, mCurrentPage, new CallBack<ArrayList<BookAbstract>>
                    () {

                @Override
                public void onSuccess(ArrayList<BookAbstract> data) {

                    mLoadingMoreProgreeBar.setVisibility(View.GONE);
                    mBookListAdapter.addData(data);
                    mBookListAdapter.notifyDataSetChanged();
                    mIsLoadingMore = false;
                }

                @Override
                public void onError() {

                    Log.d(TAG, "onError()");
                    //如果不再返回数据,就显示no more data
                    mHasMoreData = false;
                    mIsLoadingMore = false;
                    mLoadingMoreProgreeBar.setVisibility(View.GONE);
                    mNoMoreData.setVisibility(View.VISIBLE);
                }
            });

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

//            String prefix = "http://ftp.lib.hust.edu.cn";
//            intent.putExtra(EXTRA_BOOK_URL, prefix + bookAbstract.getUrl());
//            intent.putExtra(EXTRA_BOOK_COVER_URL, bookAbstract.getImageUrl());

            intent.putExtra(EXTRA_BOOK_TITLE, bookAbstract.getBookTitle());
            startActivity(intent);
        }

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
                inflate(R.layout.footer_book_abstract_list, null, false);
        mLoadingMoreProgreeBar = (ProgressBar) mFooter.findViewById(R.id.progressbar_loading_more);
        mNoMoreData = (TextView) mFooter.findViewById(R.id.text_no_more_data);
        mListView.addFooterView(mFooter);

        //registe listener
        mListView.setOnItemClickListener(this);

    }

    private void initToolbar() {

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

}
