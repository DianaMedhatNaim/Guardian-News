package com.example.diana.guardiannews;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<List<News>> {
    /** URL  from The Guardian API */
    private static final String GUARDIAN_REQUEST_URL ="https://content.guardianapis.com/search";
    public static final String LOG_TAG = MainActivity.class.getName();
    /**
     * Constant value for the GUARDIAN_News_Loder_ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int GUARDIAN_News_Loder_ID=1;
    /** Adapter for the list of news */
    private NewsAdapter Adapter;
    /** TextView that is displayed when the list is empty */
    private TextView EmptyText;
    @Override
    protected void onResume() {
        super.onResume();
        // Listener for Shared Preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.OnSharedPreferenceChangeListener prefListener =new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) { getLoaderManager().restartLoader(GUARDIAN_News_Loder_ID, null, MainActivity.this);}};
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener);}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView newsListView = findViewById(R.id.list);
        EmptyText = findViewById(R.id.view);
        newsListView.setEmptyView(EmptyText);
        Adapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(Adapter);
        NewsAsyncTask task = new NewsAsyncTask();
        task.execute(GUARDIAN_REQUEST_URL);
        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = Adapter.getItem(position);
                // Convert the String URL into a URI object
                Uri newsUri = Uri.parse(currentNews.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);}});
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(GUARDIAN_News_Loder_ID, null, this); } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            EmptyText.setText(R.string.no_internet); } }
    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of news in the response.
     */
    private class NewsAsyncTask extends AsyncTask<String, Void, List<News>> {
        @Override
        protected List<News> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null; }
            List<News> result = QueryUtils.fetchNewsData(urls[0]);
            return result; }
        @Override
        protected void onPostExecute(List<News> data) {
            // Clear the adapter of previous news data
            Adapter.clear();
            // If there is a valid list of {@link News}, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                Adapter.addAll(data); } }}
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String SearchWord = sharedPrefs.getString(getString(R.string.settings_searchWord),getString(R.string.settings_defaultWord));
        // Parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Append query parameter and its value
        uriBuilder.appendQueryParameter("search word", SearchWord);
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("page", "1");
        uriBuilder.appendQueryParameter("page-size", "10");
        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("format", "json");
        // Return the completed uri
        return new NewsLoader(this, uriBuilder.toString());}
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        ConnectivityManager connMgr = (ConnectivityManager)
         getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        if (networkInfo != null && networkInfo.isConnected()) {
            // Set empty state text to display "No news found."
            EmptyText.setText(R.string.No_news_found);
            EmptyText.setGravity(Gravity.CENTER); } else {
            // Update empty state with no connection error message
            EmptyText.setText(R.string.no_internet);}
        Adapter.clear();
        if (news != null && !news.isEmpty()) {
            Adapter.addAll(news); }}
            @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Adapter.clear(); }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true; }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;}
        return super.onOptionsItemSelected(item);}}
