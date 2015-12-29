package io.github.lingnanlu.hustlibrary;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.lingnanlu.core.AppAction;
import io.github.lingnanlu.core.CallBack;
import io.github.lingnanlu.hustlibrary.adapter.BookStoreInfoAdapter;
import io.github.lingnanlu.hustlibrary.utils.BitmapCache;
import io.github.lingnanlu.model.Book;

public class BookDetailActivity extends AppCompatActivity {

    private static final String TAG = "BookDetailActivity";

    private AppAction mAppAction;
    private ImageLoader mImageLoader;
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

        initToolbar();

        RequestQueue queue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(queue, new BitmapCache());

        String bookTitle = getIntent().getStringExtra(BookAbstractsActivity.EXTRA_BOOK_TITLE);


        HustLibApplication application = (HustLibApplication)this.getApplication();
        mAppAction = application.getAppAction();

        mAppAction.loadBook(bookTitle, new CallBack<Book>() {

            @Override
            public void onSuccess(Book book) {

                mBookTitle.setText(book.getTitle());
                mBookAuthor.setText(book.getAuthor());
                mBookCallNumber.setText(book.getCallNumber());
                mBookISBN.setText(book.getISBN());
                mBookCover.setImageBitmap(book.getCover());
                BookStoreInfoAdapter adapter =
                        new BookStoreInfoAdapter(BookDetailActivity.this, book);

                /*
                    * findViewById() only works to find subviews of the object View.
                    * It will not work on a layout id.
                */

                View header = getLayoutInflater().inflate(
                        R.layout.header_book_store_info_list,
                        mBookStoreInfos,
                        false);

                mBookStoreInfos.addHeaderView(header, null, false);
                mBookStoreInfos.setAdapter(adapter);
            }

            @Override
            public void onError() {
                Toast.makeText(BookDetailActivity.this, "no book info found", Toast.LENGTH_SHORT)
                        .show();
            }
        });



    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

//    private void onBookInfoLoaded(BookDetail bookDetail){
//
//        mBookTitle.setText(bookDetail.getTitle());
//        mBookAuthor.setText(bookDetail.getAuthor());
//        mBookCallNumber.setText(bookDetail.getCallNumber());
//        mBookISBN.setText(bookDetail.getISBN());
//        mBookCover.setImageBitmap(bookDetail.getCover());
//
//        BookStoreInfoAdapter adapter = new BookStoreInfoAdapter(bookDetail);
//
//        /*
//         * findViewById() only works to find subviews of the object View.
//         * It will not work on a layout id.
//         */
//        View header = getLayoutInflater().inflate(
//                R.layout.header_book_store_info_list,
//                mBookStoreInfos,
//                false);
//        mBookStoreInfos.addHeaderView(header, null, false);
//        mBookStoreInfos.setAdapter(adapter);
//
//
//    }
//
//
//    private class BookStoreInfoAdapter extends BaseAdapter{
//
//        private ArrayList<String[]> mBookStoreInfos;
//        private LayoutInflater inflater;
//
//        public BookStoreInfoAdapter(BookDetail bookDetail) {
//
//            mBookStoreInfos = bookDetail.getStoreInfos();
//            inflater = LayoutInflater.from(BookDetailActivity.this);
//
//        }
//
//        @Override
//        public int getCount() {
//            if(mBookStoreInfos != null)
//                return mBookStoreInfos.size();
//
//            return 0;
//        }
//
//        @Override
//        public Object getItem(int position) {
//
//            if(mBookStoreInfos != null)
//
//                return mBookStoreInfos.get(position);
//
//            return null;
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
//            View container = inflater.inflate(R.layout.item_book_store_info, null);
//
//            TextView location = (TextView) container.findViewById(R.id.text_location);
//            TextView state = (TextView) container.findViewById(R.id.text_status);
//
//            location.setText(mBookStoreInfos.get(position)[0]);
//            state.setText(mBookStoreInfos.get(position)[2]);
//
//            if (position % 2 == 0) {
//                container.setBackgroundColor(getResources().getColor(R.color.grey_300));
//            }
//            return container;
//        }
//
//    }
//    private class BookInfoLoadTask extends AsyncTask<String, Void, BookDetail>{
//
//        @Override
//        protected BookDetail doInBackground(String... params) {
//
//            String bookInfoUrl = params[0];
//            String bookCoverUrl = params[1];
//
//            Request.Builder builder = new Request.Builder();
//
//            Request bookInfoRequest = builder.url(bookInfoUrl).build();
//            Request bookCoverRequest = builder.url(bookCoverUrl).build();
//
//            Response response = null;
//            BookDetail bookDetail = null;
//
//            try {
//                response = mClient.newCall(bookInfoRequest).execute();
//
//                if(response != null) {
//
//                    bookDetail = HtmlParser.parseBookDetail(response.body().string());
//
//                }
//
//                response = mClient.newCall(bookCoverRequest)
//                        .execute();
//
//                InputStream in = response.body().byteStream();
//
//                if (in != null) {
//
//                    Bitmap bookCover = BitmapFactory.decodeStream(in);
//                    bookDetail.setCover(bookCover);
//
//                }
//
//                return bookDetail;
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//
//        }
//
//        @Override
//        protected void onPostExecute(BookDetail bookDetail) {
//            onBookInfoLoaded(bookDetail);
//        }
//
//    }
}
