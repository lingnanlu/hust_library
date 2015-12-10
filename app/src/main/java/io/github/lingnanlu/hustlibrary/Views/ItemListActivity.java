package io.github.lingnanlu.hustlibrary.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.lingnanlu.hustlibrary.R;
import io.github.lingnanlu.hustlibrary.model.Item;
import io.github.lingnanlu.hustlibrary.utils.HtmlParser;
import io.github.lingnanlu.hustlibrary.utils.RequestMaker;

public class ItemListActivity extends AppCompatActivity {

    private static final String TAG = "ItemListActivity";

    private OkHttpClient mClient = new OkHttpClient();

    private Handler mHandler;

    @Bind(R.id.listView)
    ListView mListView;

    private ArrayList<Item> mBookItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        ButterKnife.bind(this);

        fillListView();
    }


    private void fillListView() {

        String keyWord = getIntent().getStringExtra(MainActivity.DATA_KEYWORD);

        Log.d(TAG, keyWord);

        final Request request = RequestMaker.make("search*chx/X?SEARCH=" +
                keyWord);

        mHandler = new Handler();

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

                    mBookItems = HtmlParser.parserItems(content);


                    //将一条Message post到UI线程的MessageQueue,
                    // 并在UI线程中执行run方法，所以该方法体中要更新UI
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "" + mBookItems.size());

                            ItemAdapter adapter = new ItemAdapter(ItemListActivity.this);
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

    public class ItemAdapter extends BaseAdapter {

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
                viewHolder.bookPress = (TextView) convertView.findViewById(R
                        .id.bookPress);
                viewHolder.bookCover = (ImageView) convertView.findViewById(R
                        .id.bookCover);

                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder)convertView.getTag();

            }

            Item item = mBookItems.get(position);
            viewHolder.bookAuthor.setText(item.getAuthor());
            viewHolder.bookPress.setText(item.getPress());
            viewHolder.bookTitle.setText(item.getBookTitle());
            

            new BookCoverDownloaderTask(viewHolder.bookCover).
                    execute(item.getImageUrl());

            return convertView;
        }

        public class ViewHolder {

            TextView bookTitle;
            TextView bookAuthor;
            TextView bookPress;
            ImageView bookCover;

        }
    }


    private class BookCoverDownloaderTask extends AsyncTask<String, Void,
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

            if (imageViewWeakReference != null) {
                ImageView imageView = imageViewWeakReference.get();

                if (imageView != null && bitmap != null) {
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
                    return bookCover;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



}
