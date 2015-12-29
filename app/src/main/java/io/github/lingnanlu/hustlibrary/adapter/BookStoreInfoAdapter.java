package io.github.lingnanlu.hustlibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.lingnanlu.hustlibrary.R;
import io.github.lingnanlu.model.Book;

/**
 * Created by Administrator on 2015/12/29.
 */
public class BookStoreInfoAdapter extends BaseAdapter {

    private ArrayList<String[]> mBookStoreInfos;
    private LayoutInflater inflater;
    private Context mContext;

    public BookStoreInfoAdapter(Context context, Book book) {
        mContext = context;
        mBookStoreInfos = book.getStoreInfos();
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        if (mBookStoreInfos != null)
            return mBookStoreInfos.size();

        return 0;
    }

    @Override
    public Object getItem(int position) {

        if (mBookStoreInfos != null)

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
            container.setBackgroundColor(mContext.getResources().getColor(R.color.grey_300));
        }
        return container;
    }
}
