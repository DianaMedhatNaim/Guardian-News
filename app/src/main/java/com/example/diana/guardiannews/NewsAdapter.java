package com.example.diana.guardiannews;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
public class NewsAdapter extends ArrayAdapter<News> {
    static class ViewHolder {
        private TextView TopicTextView;
        private TextView WriterTextView;
        private TextView titleTextView;
        private TextView dateTextView;
        private TextView timeTextView;}
    /**
     * Create a new {@link NewsAdapter} object.
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param object is the list of {@link News} to be displayed.
     */
    public NewsAdapter(Context context, ArrayList<News> object) {
        super(context, 0, object);
    }
    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     * @param position The position in the list of data that should be displayed in the list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Create a View object.
        ViewHolder holder;
        // Check if the existing view is being reused, otherwise inflate the view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
            holder = new ViewHolder();
            holder.TopicTextView = convertView.findViewById(R.id.Topic);
            holder.WriterTextView = convertView.findViewById(R.id.Writer);
            holder.titleTextView = convertView.findViewById(R.id.title);
            holder.dateTextView = convertView.findViewById(R.id.date);
            holder.timeTextView = convertView.findViewById(R.id.time);
            convertView.setTag(holder);} else {
            holder = (ViewHolder) convertView.getTag();}
        // Get the News object located at this position .
        News currentNews = getItem(position);
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateObject = null;
        try { dateObject = simpleDateFormat.parse(currentNews.getDate()); } catch (ParseException e) { e.printStackTrace();}
        // Format the date string.
        String formattedDate = formatDate(dateObject);
        // Format the time string (i.e. "4:30PM").
        String formattedTime = formatTime(dateObject);
        // Set proper data in news_list_item by using ViewHolder.
        holder.TopicTextView.setText(currentNews.getTopic());
        holder.WriterTextView.setText(currentNews.getWriterName());
        holder.titleTextView.setText(currentNews.getTitle());
        holder.dateTextView.setText(formattedDate);
        holder.timeTextView.setText(formattedTime);
        return convertView;}
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(dateObject);}
    /**
     * Return the formatted date string (i.e. "8:00 AM") from a Date object.
     * @param dateObject is the time of news in ISO-8601 format0.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
        timeFormat.setTimeZone(TimeZone.getDefault());
        return timeFormat.format(dateObject);}}

