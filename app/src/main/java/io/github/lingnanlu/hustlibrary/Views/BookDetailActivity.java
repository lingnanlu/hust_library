package io.github.lingnanlu.hustlibrary.Views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.lingnanlu.hustlibrary.R;
import io.github.lingnanlu.hustlibrary.model.BookDetail;
import io.github.lingnanlu.hustlibrary.utils.HtmlParser;

public class BookDetailActivity extends AppCompatActivity {

    private static final String TAG = "BookDetailActivity";

    private OkHttpClient mClient;

    @Bind(R.id.image_book_cover)
    ImageView mBookCover;

    @Bind(R.id.text_book_title)
    TextView mBookTitle;

    @Bind(R.id.text_book_CallNumber)
    TextView mBookCallNumber;

    @Bind(R.id.text_book_author)
    TextView mBookAuthor;

    @Bind(R.id.text_book_ISBN)
    TextView mBookISBN;

    @Bind(R.id.list_book_StoreInfos)
    ListView mBookStoreInfos;

    @Bind(R.id.toolbar_custom)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mClient = new OkHttpClient();

        String bookInfoUrl = getIntent().getStringExtra(BookAbstractsActivity.EXTRA_BOOK_URL);
        String bookCoverUrl = getIntent().getStringExtra(
                BookAbstractsActivity.EXTRA_BOOK_COVER_URL);

        new BookInfoLoadTask().execute(bookInfoUrl, bookCoverUrl);

        Log.d(TAG, "onCreate: " + bookInfoUrl);
        Log.d(TAG, "onCreate: " + bookCoverUrl);


    }

    private void onBookInfoLoaded(BookDetail bookDetail){

        mBookTitle.setText(bookDetail.getTitle());
        mBookAuthor.setText(bookDetail.getAuthor());
        mBookCallNumber.setText(bookDetail.getCallNumber());
        mBookISBN.setText(bookDetail.getISBN());
        mBookCover.setImageBitmap(bookDetail.getCover());

        BookStoreInfoAdapter adapter = new BookStoreInfoAdapter(bookDetail);

        /*
         * findViewById() only works to find subviews of the object View.
         * It will not work on a layout id.
         */
        View header = getLayoutInflater().inflate(
                R.layout.item_header_book_store_info_list,
                mBookStoreInfos,
                false);
        mBookStoreInfos.addHeaderView(header, null, false);
        mBookStoreInfos.setAdapter(adapter);


    }


    private class BookStoreInfoAdapter extends BaseAdapter{

        private ArrayList<String[]> mBookStoreInfos;
        private LayoutInflater inflater;

        public BookStoreInfoAdapter(BookDetail bookDetail) {

            mBookStoreInfos = bookDetail.getStoreInfos();
            inflater = LayoutInflater.from(BookDetailActivity.this);

        }

        @Override
        public int getCount() {
            if(mBookStoreInfos != null)
                return mBookStoreInfos.size();

            return 0;
        }

        @Override
        public Object getItem(int position) {

            if(mBookStoreInfos != null)

                return mBookStoreInfos.get(position);

            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View container = inflater.inflate(R.layout.item_book_store_info, null);

            TextView location = (TextView) container.findViewById(R.id.text_location);
            TextView state = (TextView) container.findViewById(R.id.text_status);

            location.setText(mBookStoreInfos.get(position)[0]);
            state.setText(mBookStoreInfos.get(position)[2]);

            if (position % 2 == 0) {
                container.setBackgroundColor(getResources().getColor(R.color.grey_300));
            }
            return container;
        }

    }
    private class BookInfoLoadTask extends AsyncTask<String, Void, BookDetail>{

        @Override
        protected BookDetail doInBackground(String... params) {

            String bookInfoUrl = params[0];
            String bookCoverUrl = params[1];

            Request.Builder builder = new Request.Builder();

            Request bookInfoRequest = builder.url(bookInfoUrl).build();
            Request bookCoverRequest = builder.url(bookCoverUrl).build();

            Response response = null;
            BookDetail bookDetail = null;

            try {
                response = mClient.newCall(bookInfoRequest).execute();

                if(response != null) {

                    bookDetail = HtmlParser.parseBookDetail(response.body().string());

                }

                response = mClient.newCall(bookCoverRequest)
                        .execute();

                InputStream in = response.body().byteStream();

                if (in != null) {

                    Bitmap bookCover = BitmapFactory.decodeStream(in);
                    bookDetail.setCover(bookCover);

                }

                return bookDetail;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(BookDetail bookDetail) {
            onBookInfoLoaded(bookDetail);
        }

    }
}
