package io.github.lingnanlu.hustlibrary.Views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

public class ItemListActivity extends AppCompatActivity implements AbsListView.OnScrollListener{

    private static final String TAG = "ItemListActivity";


    private boolean mHasLoaded = false;

    private OkHttpClient mClient = new OkHttpClient();
    private Result mResult;
    private Handler mHandler;
    private ArrayList<Item> mBookItems;
    private Bitmap mPlaceHolderBitmap;
    private ItemAdapter mItemAdapter;
    private Map<String, Bitmap> mCachedBitmap;
    private String mKeyWord;
    @Bind(R.id.listView)
    ListView mListView;

    @Bind(R.id.myToolbar)
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        mItemAdapter = new ItemAdapter(this);
        mListView.setOnScrollListener(this);

        mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(), R
                .drawable.ic_book_black_36dp);

        mCachedBitmap = new HashMap<>();

        mKeyWord = getIntent().getStringExtra(MainActivity.DATA_KEYWORD);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int
            visibleItemCount, int totalItemCount) {

        Log.d(TAG, "onScroll() called with: " +
                "firstVisibleItem = [" + firstVisibleItem + "], " +
                "visibleItemCount = [" + visibleItemCount + "], " +
                "totalItemCount = [" + totalItemCount + "]");

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

    class ItemAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ItemAdapter(Context context) {

            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return mBookItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mBookItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Log.d(TAG, "position : " + position );
            ViewHolder viewHolder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.list_item, null);

                viewHolder = new ViewHolder();

                viewHolder.bookAuthor = (TextView) convertView.findViewById(R
                        .id.bookAuthor);
                viewHolder.bookTitle = (TextView) convertView.findViewById(R
                        .id.bookTitle);
//                viewHolder.bookPress = (TextView) convertView.findViewById(R
//                        .id.bookPress);
                viewHolder.bookCover = (ImageView) convertView.findViewById(R
                        .id.bookCover);

                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder)convertView.getTag();

            }

            Item item = mBookItems.get(position);

            Log.d(TAG, "position : " + position + " imgUrl " + item
                    .getImageUrl());
            viewHolder.bookAuthor.setText(item.getAuthor());
//            viewHolder.bookPress.setText(item.getPress());
            viewHolder.bookTitle.setText(item.getBookTitle());


//            new BookCoverDownloaderTask(viewHolder.bookCover).
//                    execute(item.getImageUrl());


            //有的item没有封面imgeUrl会是/screen/xxxxx
            if(item.getImageUrl().startsWith("http")) {
                if (mCachedBitmap.containsKey(item.getImageUrl())) {

                    viewHolder.bookCover.setImageBitmap(mCachedBitmap.get(item.getImageUrl()));

                } else {

                    loadBookCover(item.getImageUrl(), viewHolder.bookCover);

                }
            }

            return convertView;
        }

        private void loadBookCover(String imgUrl, ImageView imageView) {

            if(cancelPotentialWork(imageView) ) {

                final BookCoverDownloaderTask task = new BookCoverDownloaderTask
                        (imageView);

                final AsyncDrawable asyncDrawable = new AsyncDrawable
                        (getResources(), mPlaceHolderBitmap, task);

                imageView.setImageDrawable(asyncDrawable);

                task.execute(imgUrl);
            }

        }
        private boolean cancelPotentialWork(ImageView imageView) {

            final BookCoverDownloaderTask task = getBookCoverDownloaderTask
                    (imageView);

            if (task != null) {

                task.cancel(true);

            }

            return true;
        }

        class ViewHolder {

            TextView bookTitle;
            TextView bookAuthor;
            //            TextView bookPress;
            ImageView bookCover;

        }
    }



    public BookCoverDownloaderTask getBookCoverDownloaderTask
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

    class BookCoverDownloaderTask extends AsyncTask<String, Void,
            Bitmap> {

        private final WeakReference<ImageView> imageViewWeakReference;

        public BookCoverDownloaderTask(ImageView imageView) {

            imageViewWeakReference = new WeakReference<ImageView>(imageView);

        }

        @Override
        protected Bitmap doInBackground(String... params) {

            return downloadBookCover(params[0]);

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewWeakReference != null && bitmap != null) {

                final ImageView imageView = imageViewWeakReference.get();
                final BookCoverDownloaderTask bookCoverDownloaderTask =
                        getBookCoverDownloaderTask(imageView);

                if (imageView != null && this == bookCoverDownloaderTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }

        }

        private Bitmap downloadBookCover(String imageUrl) {


            Request request = new Request.Builder().url(imageUrl).build();


            try {

                Response response = mClient.newCall(request).execute();

                InputStream in = response.body().byteStream();

                if (in != null) {
                    Bitmap bookCover = BitmapFactory.decodeStream(in);

                    if(!mCachedBitmap.containsKey(imageUrl)) {
                        mCachedBitmap.put(imageUrl, bookCover);
                    }
                    return bookCover;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


    }

    class ListViewInitTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String requestUrl = RequestUrlBuilder.build(params[0]);

            Request request = new Request.Builder().url(requestUrl).build();

            Response response = null;
            try {
                response = mClient.newCall(request).execute();
                if (response != null) {

                    String content = response.body().string();
                    mResult = HtmlParser.parserResult(content);

                    // TODO: 2015/12/14
                    // 解析结果时，暂时还未fill keyWord
                    mResult.setKeyWord(mKeyWord);
                    mBookItems = HtmlParser.parserItems(content);


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
