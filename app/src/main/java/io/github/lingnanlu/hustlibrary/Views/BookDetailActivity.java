package io.github.lingnanlu.hustlibrary.Views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import io.github.lingnanlu.hustlibrary.model.Book;
import io.github.lingnanlu.hustlibrary.utils.HtmlParser;

public class BookDetailActivity extends AppCompatActivity {

    private static final String TAG = "BookDetailActivity";

    private OkHttpClient mClient;

    @Bind(R.id.bookCoverImageView)
    ImageView mBookCover;

    @Bind(R.id.bookTitle)
    TextView mBookTitle;

    @Bind(R.id.bookCallNumber)
    TextView mBookCallNumber;

    @Bind(R.id.bookAuthor)
    TextView mBookAuthor;

    @Bind(R.id.bookISBN)
    TextView mBookISBN;

    @Bind(R.id.bookStoreInfos)
    ListView mBookStoreInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);

        mClient = new OkHttpClient();

        String bookInfoUrl = getIntent().getStringExtra
                (ItemListActivity
                .BOOK_URL);
        String bookCoverUrl = getIntent().getStringExtra
                (ItemListActivity.BOOK_COVER_URL);

        new BookInfoLoadTask().execute(bookInfoUrl, bookCoverUrl);

        Log.d(TAG, "onCreate: " + bookInfoUrl);
        Log.d(TAG, "onCreate: " + bookCoverUrl);


    }

    private void onBookInfoLoaded(Book book){

        mBookTitle.setText(book.getTitle());
        mBookAuthor.setText(book.getAuthor());
        mBookCallNumber.setText(book.getCallNumber());
        mBookISBN.setText(book.getISBN());
        mBookCover.setImageBitmap(book.getCover());

        BookStoreInfoAdapter adapter = new BookStoreInfoAdapter(book);

        mBookStoreInfos.setAdapter(adapter);


    }


    private class BookStoreInfoAdapter extends BaseAdapter{

        private ArrayList<String[]> mBookStoreInfos;
        private LayoutInflater inflater;

        public BookStoreInfoAdapter(Book book) {

            mBookStoreInfos = book.getStoreInfos();
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

            View container = inflater.inflate(R.layout
                            .list_item_book_store_info, null);

            TextView location = (TextView) container.findViewById(R.id.location);
            TextView state = (TextView) container.findViewById(R.id.state);

            location.setText(mBookStoreInfos.get(position)[0]);
            state.setText(mBookStoreInfos.get(position)[2]);

            if (position % 2 == 0) {
                container.setBackgroundColor(getResources()
                        .getColor(R.color.colorAccent));
            }
            return container;
        }

    }
    private class BookInfoLoadTask extends
    AsyncTask<String, Void, Book>{

        @Override
        protected Book doInBackground(String... params) {

            String bookInfoUrl = params[0];
            String bookCoverUrl = params[1];

            Request.Builder builder = new Request.Builder();

            Request bookInfoRequest = builder.url(bookInfoUrl)
                    .build();
            Request bookCoverRequest = builder.url(bookCoverUrl)
                    .build();

            Response response = null;
            Book book = null;

            try {
                response = mClient.newCall(bookInfoRequest).execute();

                if(response != null) {

                    book = HtmlParser.parseBook(response.body().string());

                }

                response = mClient.newCall(bookCoverRequest)
                        .execute();

                InputStream in = response.body().byteStream();

                if (in != null) {

                    Bitmap bookCover = BitmapFactory.decodeStream(in);
                    book.setCover(bookCover);

                }

                return book;

            } catch (IOException e) {
                Log.d(TAG, "get response error " + e);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Book book) {
            onBookInfoLoaded(book);
        }

    }
}
