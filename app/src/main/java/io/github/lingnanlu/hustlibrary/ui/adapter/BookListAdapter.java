package io.github.lingnanlu.hustlibrary.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import io.github.lingnanlu.hustlibrary.R;
import io.github.lingnanlu.hustlibrary.utils.BitmapCache;
import io.github.lingnanlu.hustlibrary.bean.BookAbstract;

/**
 * Created by Administrator on 2015/12/28.
 */
public class BookListAdapter extends BaseAdapter {

    private ImageLoader mImageLoader;
    private LayoutInflater mInflator;
    private ArrayList<BookAbstract> mBookAbstracts;

    public BookListAdapter(Context context, ArrayList<BookAbstract>
            bookAbstracts) {

        mInflator = LayoutInflater.from(context);
        RequestQueue queue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(queue, new BitmapCache());
        mBookAbstracts = bookAbstracts;

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

         /*       Picasso.with(BookAbstractsActivity.this)
                        .load(bookAbstract.getImageUrl())
                        .placeholder(R.drawable.ic_book_black_36dp)
                        .error(R.drawable.ic_book_black_36dp)
                        .into(viewHolder.bookCover);*/

            viewHolder.bookCover.setDefaultImageResId(R.drawable.ic_book_black_36dp);
            viewHolder.bookCover.setImageUrl(bookAbstract.getImageUrl(), mImageLoader);

        }

        return convertView;
    }

    public void addData(ArrayList<BookAbstract> data) {
        mBookAbstracts.addAll(data);
    }

    class ViewHolder {

        TextView bookTitle;
        TextView bookAuthor;
        TextView bookPress;
        NetworkImageView bookCover;

    }
}
