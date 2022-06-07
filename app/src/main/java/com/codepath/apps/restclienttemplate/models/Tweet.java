package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {
    public String body;
    public String createdAt;
    public User user;

    public static Tweet fromJson(JSONObject josnObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = josnObject.getString("text");
        tweet.createdAt = josnObject.getString("created_at");
        tweet.user = User.fromJson(josnObject.getJSONObject("user"));

        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
              tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
