package com.example.diana.guardiannews;
/**
 * The {@link News} class represents some data about news.
 */
public class News {
    private String mWriter;
    private String mTitle;
    private String mTopic;
    private String mUrl;
    private String mDate;
    /**
     * Create the {@link News} object.
     * * @param Writer is the name of news author.
     * @param title is the Guardian news title.
     * @param Topic is the Guardian news category name.
     * @param url is the Guardian news url.
     * @param Date is the Guardian news date.
     */
    public News(String Topic, String Writer, String title, String Date, String url) {
        mTopic = Topic;
        mWriter = Writer;
        mTitle = title;
        mDate = Date;
        mUrl = url; }
    public String getTopic() {
        return mTopic;
    }
    public String getWriterName() {
        return mWriter;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getDate() {
        return mDate;
    }
    public String getUrl() {
        return mUrl;
    }}
