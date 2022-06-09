 package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

 public class TimelineActivity extends AppCompatActivity {

     public static final String TAG = "TimelineActivity";
     private final int REQUEST_CODE = 20;
     private SwipeRefreshLayout swipeContainer;
     private EndlessRecyclerViewScrollListener scrollListener;


    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });
        // Configures the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.white);


        client = TwitterApp.getRestClient(this);

        // Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        //init the list of tweets and adapter
        tweets =  new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // Recycler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                long tweetID = (tweets.get(tweets.size() - 1)).ID;
                loadNextDataFromApi(tweetID - 1);
            }
        };

        rvTweets.addOnScrollListener(scrollListener);

        populateHomeTimeLine();
    }

     // Append the next page of data into the adapter
     // This method probably sends out a network request and appends new data items to your adapter.
     public void loadNextDataFromApi(long offset) {
         // Send an API request to retrieve appropriate paginated data
         client.getHomeTimelineEndless(offset, new JsonHttpResponseHandler() {
             @Override
             public void onSuccess(int statusCode, Headers headers, JSON json) {
                 JSONArray jsonArray = json.jsonArray;
                 Log.i(TAG, "onSuccess: " + String.valueOf(offset));
                 try {
                     tweets.addAll(Tweet.fromJsonArray(jsonArray));
                     adapter.notifyDataSetChanged();
                 } catch (JSONException e) {
                 }
             }

             @Override
             public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                 Log.e(TAG, "onFailureEndless: ",throwable );
             }
         });
     }

     public void fetchTimelineAsync(int page) {
         // Send the network request to fetch the updated data
         // `client` here is an instance of Android Async HTTP
         client.getHomeTimeline(1, new JsonHttpResponseHandler() {
             @Override
             public void onSuccess(int statusCode, Headers headers, JSON json) {
                    // Clears out old items before appending in the new ones
                    adapter.clear();
                    // adds the new items to the adapter
                    try {
                        adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Signal refresh has finished
                    swipeContainer.setRefreshing(false);
             }

             @Override
             public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

             }
         });
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_main, menu);
         return true; //have to return true for the menu to be displayed
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.compose:
                //compose icon has be selected
                //navigate to the compose activity
                Intent i = new Intent(this, ComposeActivity.class);
                startActivityForResult(i, REQUEST_CODE);
                return true;
        }
         return super.onOptionsItemSelected(item);
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) { //checks if the result succeeded with the correct code
             // Get data from the intent (tweet)
             Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
             // Update the RV with the tweet
             // Modify data source of the tweets
             tweets.add(0, tweet);
             // Update the adapter
             adapter.notifyItemInserted(0);
             // Smooth scroll so that a user does not have to scroll up to see tweet
             rvTweets.smoothScrollToPosition(0);
         }
        super.onActivityResult(requestCode, resultCode, data);
     }

     private void populateHomeTimeLine() {
        client.getHomeTimeline(1, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailurePopulate: ", throwable);
            }
        });
    }

    public void onLogoutButton(View view) { //on clicks need parameter view
        finish();
        // forget who's logged in
        TwitterApp.getRestClient(this).clearAccessToken();

        // navigate backwards to login screen
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

     MenuItem miActionProgressItem;

     @Override
     public boolean onPrepareOptionsMenu(Menu menu) {
         miActionProgressItem = menu.findItem(R.id.miActionProgress);
         return super.onPrepareOptionsMenu(menu);
     }
     public void showProgressBar() {
         // Show progress item
         miActionProgressItem.setVisible(true);
     }

     public void hideProgressBar() {
         // Hide progress item
         miActionProgressItem.setVisible(false);
     }
 }