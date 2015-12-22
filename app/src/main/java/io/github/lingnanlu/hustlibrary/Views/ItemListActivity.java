package io.github.lingnanlu.hustlibrary.Views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.lingnanlu.hustlibrary.R;
import io.github.lingnanlu.hustlibrary.model.Item;
import io.github.lingnanlu.hustlibrary.model.Result;
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
    private Bitmap mPlaceHolderBitmap;
    private ItemAdapter mItemAdapter;
    private Map<String, Bitmap> mCachedBitmap;
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

        mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(), R
                .drawable.ic_book_black_36dp);

        mCachedBitmap = new HashMap<>();

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
            if ((mPreTask == null && mHasLoaded == true)
                    || (mHasLoaded == true && mPreTask.getStatus() == AsyncTask.Status.FINISHED)) {

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

//        if(item != null) {
//            Toast.makeText(ItemListActivity.this,item.getUrl() +
//                    item.getBookTitle() , Toast
//                    .LENGTH_SHORT).show();
//        }

        if(item != null) {
            Intent intent = new Intent(this,BookDetailActivity.class);

            String prefix = "http://ftp.lib.hust.edu.cn";
            intent.putExtra(BOOK_URL, prefix + item.getUrl());
            intent.putExtra(BOOK_COVER_URL, item.getImageUrl());

            startActivity(intent);
        }

    }
    private static BookCoverDownloaderTask getBookCoverDownloaderTask
            (ImageView imageView) {

        if (imageView != null) {

            final Drawable drawable = imageView.getDrawable();

            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable)drawable;
                return asyncDrawable.getBookCoverDownloaderTask();
            }
        }

        return null;

    }



    static class AsyncDrawable extends BitmapDrawable {

        private final WeakReference<BookCoverDownloaderTask>
                bookCoverDownloaderTaskWeakReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BookCoverDownloaderTask bookCoverDownloaderTask) {
            super(res, bitmap);

            bookCoverDownloaderTaskWeakReference = new WeakReference
                    <BookCoverDownloaderTask>(bookCoverDownloaderTask);

        }

        public BookCoverDownloaderTask getBookCoverDownloaderTask() {

            return bookCoverDownloaderTaskWeakReference.get();

        }
    }

    private class ItemAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ItemAdapter(Context context) {

            inflater = LayoutInflater.from(context);

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

           // Log.d(TAG, "position : " + position );
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
                viewHolder.bookCover = (ImageView) convertView.findViewById(R
                        .id.bookCover);

                convertView.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolder)convertView.getTag();

            }

            Item item = mBookItems.get(position);

           // Log.d(TAG, "position : " + position + " imgUrl " + item
            //        .getImageUrl());
            viewHolder.bookAuthor.setText(item.getAuthor());
            viewHolder.bookPress.setText(item.getPress());
            viewHolder.bookTitle.setText(item.getBookTitle());


//            new BookCoverDownloaderTask(viewHolder.bookCover).
//                    execute(item.getImageUrl());


            //有的item没有封面imgeUrl会是/screen/xxxxx
            if(item.getImageUrl().startsWith("http")) {
//                if (mCachedBitmap.containsKey(item.getImageUrl())) {
//
//                    viewHolder.bookCover.setImageBitmap(mCachedBitmap.get(item.getImageUrl()));
//
//                } else {
//
//                    loadBookCover(item.getImageUrl(), viewHolder.bookCover);
//
//                }

                String identifer = item.getImageUrl();
                Log.d(TAG, "position : " + position + "\nidentifer " +
                        identifer + "\n" + viewHolder.bookCover +
                        " " +
                        "\n" + convertView);
                viewHolder.bookCover.setTag(identifer);

                viewHolder.bookCover.setImageBitmap(mPlaceHolderBitmap);
                BookCoverDownloaderTask task = new
                        BookCoverDownloaderTask(identifer, viewHolder.bookCover);
                task.execute(item.getImageUrl());
            }

            return convertView;
        }

//        private void loadBookCover(String imgUrl, ImageView imageView) {
//
//            if(cancelPotentialWork(imageView) ) {
//
//                final BookCoverDownloaderTask task = new BookCoverDownloaderTask
//                        (imageView);
//
//                final AsyncDrawable asyncDrawable = new AsyncDrawable
//                        (getResources(), mPlaceHolderBitmap, task);
//
//                imageView.setImageDrawable(asyncDrawable);
//
//                task.execute(imgUrl);
//            }
//
//        }
//        private boolean cancelPotentialWork(ImageView imageView) {
//
//            final BookCoverDownloaderTask task = getBookCoverDownloaderTask
//                    (imageView);
//
//            if (task != null) {
//
//                task.cancel(true);
//
//            }
//
//            return true;
//        }


        class ViewHolder {

            TextView bookTitle;
            TextView bookAuthor;
            TextView bookPress;
            ImageView bookCover;


        }
    }

    private class BookCoverDownloaderTask extends AsyncTask<String,
            Void,
            Bitmap> {

        String mIdentifer;
        ImageView mImageView;
//        private final WeakReference<ImageView> imageViewWeakReference;

        public BookCoverDownloaderTask(String identifer, ImageView
                                       imageView) {

            mIdentifer = identifer;
            mImageView = imageView;
//            imageViewWeakReference = new WeakReference<ImageView>(imageView);

        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String imageUrl = params[0];
            Request request = new Request.Builder().url(imageUrl).build();

            try {

                Response response = mClient.newCall(request).execute();

                InputStream in = response.body().byteStream();

                if (in != null) {
                    Bitmap bookCover = BitmapFactory.decodeStream(in);

//                    if(!mCachedBitmap.containsKey(imageUrl)) {
//                        mCachedBitmap.put(imageUrl, bookCover);
//                    }
                    return bookCover;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            String identifer = (String)mImageView.getTag();

            Log.d(TAG, "onPostExecute mIdentifer " + mIdentifer +
                    "\n" +
                    "identifer " + identifer);

            //这里有个问题，有可能有多个任务为一个imageView加载图片，如果一个是正确的，如何取消其它的？
            //产生这个问题的原因是有多个任务为一个imageView加载图片，而imageView
            // 并不知道有多少个worker为其服务，所以无法取消特定的worker.
            // 不用取消，如果不是为其加载的work，即identifer不同，则什么都不做就行了
            // 如果使用一个worker，通过队列的方式来做就没问题了，因为其处理流程是从队列中取消息，虽然会有多个消息为
            // 同一个imageView服务，但是如果执行该消息时发现tag不符合，什么也不做就可以了。
            if(mIdentifer.equals(identifer)) {
                mImageView.setImageBitmap(bitmap);
            }

//            if (isCancelled()) {
//                bitmap = null;
//            }
//
//            if (imageViewWeakReference != null && bitmap != null) {
//
//                final ImageView imageView = imageViewWeakReference.get();
//                final BookCoverDownloaderTask bookCoverDownloaderTask =
//                        getBookCoverDownloaderTask(imageView);
//
//                if (imageView != null && this == bookCoverDownloaderTask) {
//                    imageView.setImageBitmap(bitmap);
//                }
//            }

        }



    }

//    private class ItemAdapter extends BaseAdapter {
//
//        private LayoutInflater inflater;
//
//        public ItemAdapter(Context context) {
//
//            inflater = LayoutInflater.from(context);
//
//        }
//
//        @Override
//        public int getCount() {
//            if(mBookItems != null) {
//
//                return mBookItems.size();
//            }
//
//            return 0;
//
//        }
//
//        @Override
//        public Object getItem(int position) {
//
//            if(mBookItems != null) {
//
//                return mBookItems.get(position);
//
//            }
//
//            return null;
//
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            Log.d(TAG, "position : " + position );
//            ViewHolder viewHolder;
//
//            if (convertView == null) {
//
//                convertView = inflater.inflate(R.layout
//                                .list_item,
//                        null);
//
//                viewHolder = new ViewHolder();
//
//                viewHolder.bookAuthor = (TextView) convertView.findViewById(R
//                        .id.bookAuthor);
//                viewHolder.bookTitle = (TextView) convertView.findViewById(R
//                        .id.bookTitle);
//                viewHolder.bookPress = (TextView) convertView.findViewById(R
//                        .id.bookPress);
//                viewHolder.bookCover = (ImageView) convertView.findViewById(R
//                        .id.bookCover);
//
//                convertView.setTag(viewHolder);
//
//            } else {
//
//                viewHolder = (ViewHolder)convertView.getTag();
//
//            }
//
//            Item item = mBookItems.get(position);
//
//            Log.d(TAG, "position : " + position + " imgUrl " + item
//                    .getImageUrl());
//            viewHolder.bookAuthor.setText(item.getAuthor());
//            viewHolder.bookPress.setText(item.getPress());
//            viewHolder.bookTitle.setText(item.getBookTitle());
//
//
////            new BookCoverDownloaderTask(viewHolder.bookCover).
////                    execute(item.getImageUrl());
//
//
//            //有的item没有封面imgeUrl会是/screen/xxxxx
//            if(item.getImageUrl().startsWith("http")) {
//                if (mCachedBitmap.containsKey(item.getImageUrl())) {
//
//                    viewHolder.bookCover.setImageBitmap(mCachedBitmap.get(item.getImageUrl()));
//
//                } else {
//
//                    loadBookCover(item.getImageUrl(), viewHolder.bookCover);
//
//                }
//            }
//
//            return convertView;
//        }
//
//        private void loadBookCover(String imgUrl, ImageView imageView) {
//
//            if(cancelPotentialWork(imageView) ) {
//
//                final BookCoverDownloaderTask task = new BookCoverDownloaderTask
//                        (imageView);
//
//                final AsyncDrawable asyncDrawable = new AsyncDrawable
//                        (getResources(), mPlaceHolderBitmap, task);
//
//                imageView.setImageDrawable(asyncDrawable);
//
//                task.execute(imgUrl);
//            }
//
//        }
//        private boolean cancelPotentialWork(ImageView imageView) {
//
//            final BookCoverDownloaderTask task = getBookCoverDownloaderTask
//                    (imageView);
//
//            if (task != null) {
//
//                task.cancel(true);
//
//            }
//
//            return true;
//        }
//
//
//        class ViewHolder {
//
//            TextView bookTitle;
//            TextView bookAuthor;
//            TextView bookPress;
//            ImageView bookCover;
//
//
//        }
//    }
//
//    private class BookCoverDownloaderTask extends AsyncTask<String,
//            Void,
//            Bitmap> {
//
//        private final WeakReference<ImageView> imageViewWeakReference;
//
//        public BookCoverDownloaderTask(ImageView imageView) {
//
//            imageViewWeakReference = new WeakReference<ImageView>(imageView);
//
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//
//            return downloadBookCover(params[0]);
//
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//
//            if (isCancelled()) {
//                bitmap = null;
//            }
//
//            if (imageViewWeakReference != null && bitmap != null) {
//
//                final ImageView imageView = imageViewWeakReference.get();
//                final BookCoverDownloaderTask bookCoverDownloaderTask =
//                        getBookCoverDownloaderTask(imageView);
//
//                if (imageView != null && this == bookCoverDownloaderTask) {
//                    imageView.setImageBitmap(bitmap);
//                }
//            }
//
//        }
//
//        private Bitmap downloadBookCover(String imageUrl) {
//
//
//            Request request = new Request.Builder().url(imageUrl).build();
//
//
//            try {
//
//                Response response = mClient.newCall(request).execute();
//
//                InputStream in = response.body().byteStream();
//
//                if (in != null) {
//                    Bitmap bookCover = BitmapFactory.decodeStream(in);
//
//                    if(!mCachedBitmap.containsKey(imageUrl)) {
//                        mCachedBitmap.put(imageUrl, bookCover);
//                    }
//                    return bookCover;
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//
//    }

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

                if (response != null) {

                    String content = response.body().string();
                    mResult = HtmlParser.parseResult(content);

                    // TODO: 2015/12/14
                    // 解析结果时，暂时还未fill keyWord
                    mResult.setKeyWord(mKeyWord);
                    mBookItems = HtmlParser.parseItems(content);


                }
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

                Response response = null;
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
