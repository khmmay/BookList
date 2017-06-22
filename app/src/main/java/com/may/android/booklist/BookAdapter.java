package com.may.android.booklist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Henrik on 22.06.2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book> books){
        super(context, 0, books);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(currentBook.getTitle());
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        authorView.setText(currentBook.getAuthor());
        ImageView coverView = (ImageView) listItemView.findViewById(R.id.cover);
        URL url = null;
        try {
            url = new URL(currentBook.getImageURI());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream content = null;
        try {
            content = (InputStream)url.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(content , "src");
        coverView.setImageDrawable(d);
        TextView priceView=(TextView) listItemView.findViewById(R.id.prize);
        priceView.setText(currentBook.getPrice());

        // Return the list item view that is now showing the appropriate data
        return listItemView;


    }
}
