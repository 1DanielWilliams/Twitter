package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    private final static String TAG = "Tweet";
    public String body;
    public String createdAt;
    public User user;
    public String mediaImageUrl;
    public String numLikes;
    public String numRetweets;
    public String numReplies;
    public long ID;
    public boolean isLiked;

    // Empty constructor needed by the Parceler library
    public Tweet(){}

    public static Tweet fromJson(JSONObject josnObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = josnObject.getString("text");
        tweet.createdAt = josnObject.getString("created_at");
        tweet.user = User.fromJson(josnObject.getJSONObject("user"));
        tweet.numLikes = josnObject.getString("favorite_count");
        tweet.numRetweets = josnObject.getString("retweet_count");
        tweet.ID = josnObject.getLong("id");
        tweet.isLiked = josnObject.getBoolean("favorited");


        JSONObject entities = josnObject.getJSONObject("entities");

        // Grabs picture from tweet if it exists
        if (entities.has("media")) {
            JSONArray media = entities.getJSONArray("media");
            tweet.mediaImageUrl = media.getJSONObject(0).getString("media_url_https");
        } else {
            tweet.mediaImageUrl = null;
        }

        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
              tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getRelativeTimeAgo(String jsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(jsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;

            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }

        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return " ";
    }
}
