package com.example.diana.guardiannews;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.diana.guardiannews.MainActivity.LOG_TAG;
public final class QueryUtils {
    private QueryUtils() { }
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {url = new URL(stringUrl);} catch (MalformedURLException e) {Log.e(LOG_TAG, "there are a Problem in building the URL ", e); }
        return url;}
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) { output.append(line);
            line = reader.readLine();}}return output.toString();}
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {return jsonResponse;}
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);} else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());} } catch (IOException e) {
            Log.e(LOG_TAG, "there are a Problem in retrieving the news JSON results.", e); } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();}
            if (inputStream != null) {
                inputStream.close();}}return jsonResponse;}
    /**
     * Query the USGS dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);} catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);}
        List<News> news = extractResultsFromJson(jsonResponse);
        return news;}
    private static List<News> extractResultsFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;}
        // Create an empty ArrayList that we can start adding news to
        List<News> news = new ArrayList<>();
        // Try to parse the JSON response string.
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentNews = resultsArray.getJSONObject(i);
                String sectionName = currentNews.getString("sectionName");
                JSONArray tagsArray = currentNews.getJSONArray("tags");
                JSONObject currentTagsObject = tagsArray.getJSONObject(0);
                String authorName = currentTagsObject.getString("webTitle");
                String webTitle = currentNews.getString("webTitle");
                String webPublicationDate = currentNews.getString("webPublicationDate");
                String webUrl = currentNews.getString("webUrl");
                // Create a new {@link News} object.
                News newsObj = new News(sectionName, authorName, webTitle, webPublicationDate, webUrl);
                // Add the new {@link News} to the list of news.
                news.add(newsObj);}
        } catch (JSONException e) {
            Log.e("QueryUtils", "there are a Problem in parsing the news JSON results", e);}return news;}}
