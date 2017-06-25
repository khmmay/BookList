package com.may.android.booklist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    boolean searched = false;
    boolean queryChanged = false;

    private static final String REQUEST_URL_BASE =
            "https://www.googleapis.com/books/v1/volumes"/*+"?q=swarm&maxResults=1"*/;

    private static final int BOOK_LOADER_ID = 1;

    /** Adapter for the list of books */
    private BookAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LOG_TAG,"Test: MainActivity onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadList();

        EditText qEdit = (EditText) findViewById(R.id.seachView);
        qEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetQuery();
            }
        });
        TextView reload =(TextView) findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoaderManager loaderManager = getLoaderManager();
                if(!(loaderManager.getLoader(BOOK_LOADER_ID)==null)){
                    Log.i(LOG_TAG,"TEST: restartLoader() is called");
                    loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                }
                uploadList();
            }
        });



    }

    public void uploadList(){
        View view = MainActivity.super.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        searched=true;
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);



        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mEmptyStateTextView.setText("");
        bookListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new BookAdapter(getBaseContext(), new ArrayList<Book>());

        bookListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)

                if (!(currentBook.getLink()==null)){
                    Uri bookUri = Uri.parse(currentBook.getLink());
                    // Create a new intent to view the book URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Log.i(LOG_TAG,"Test: Loadermanager.initLoader() called");
            loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG,"Test: onCreateLoader() called");
        Uri baseUri = Uri.parse(REQUEST_URL_BASE);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        EditText qText= (EditText) super.findViewById(R.id.seachView);
        String query= qText.getText().toString();


        uriBuilder.appendQueryParameter("q", query);
        uriBuilder.appendQueryParameter("maxResults", "10");
        uriBuilder.appendQueryParameter("orderby", "relevance");


        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        Log.i(LOG_TAG,"Test: onLoaderFinished() called");
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_books);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.i(LOG_TAG,"Test: onLoaderReset() called");
        mAdapter.clear();
    }

    void resetQuery() {
        EditText searchView = (EditText) findViewById(R.id.seachView);
        if (!queryChanged) {
            searchView.setText("");
            queryChanged = true;
        }
    }

}
