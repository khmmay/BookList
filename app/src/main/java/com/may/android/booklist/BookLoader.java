package com.may.android.booklist;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Henrik on 22.06.2017.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i("wtf","TEST: onStartLoading is called");
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        Log.i("wtf","TEST: loadInBackground is called");
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of book.
        List<Book> books = QueryUtils.fetchbookData(mUrl);
        return books;
    }
}
