package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailActivity extends AppCompatActivity {

    private final String TAG = "TweetDetailActivity";
    TwitterClient client;
    Context context;

    Tweet tweet;
    ImageView ivProfileDetail;
    TextView tvProfileNameDetail;
    TextView tvOverviewDetail;
    ImageView ivImageDetail;
    TextView tvTimeDetail;
    TextView tvDateDetail;
    TextView tvNumRetweetsDetail;
    TextView tvNumLikesDetail;
    TextView tvSourceDetail;
    TextView tvUsernameDetail;
    ImageButton btnLikeDetail;
    ImageButton btnRetweetDetail;
    ImageView ivVerifiedDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        context = this;
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        //  Loads profile picture
        ivProfileDetail = findViewById(R.id.ivProfileDetail);
        Glide.with(this).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileDetail);

        // Loads screen name
        tvProfileNameDetail = findViewById(R.id.tvProfileNameDetail);
        tvProfileNameDetail.setText(tweet.user.screenName);

        // Loads overview text
        tvOverviewDetail = findViewById(R.id.tvOverviewDetail);
        tvOverviewDetail.setText(tweet.body);

        // Loads Image if the tweet has one
        ivImageDetail = findViewById(R.id.ivImageDetail);
        //checks if there is an image to display
        if (tweet.mediaImageUrl == null) {
            ivImageDetail.setVisibility(View.GONE);
        } else {
            ivImageDetail.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.mediaImageUrl).into(ivImageDetail);
        }

        // Loads time in detail view
        tvTimeDetail = findViewById(R.id.tvTimeDetail);
        String[] timeArray = tweet.createdAt.split(" ");
        tvTimeDetail.setText(timeArray[3]);

        // Loads the Date
        tvDateDetail = findViewById(R.id.tvDateDetail);
        tvDateDetail.setText(timeArray[1] + " " + timeArray[2] + " " + timeArray[5]);

        // Loads number of retweets
        tvNumRetweetsDetail = findViewById(R.id.tvNumRetweetsDetail);
        int numRetweets = Integer.parseInt(tweet.numRetweets);
        if (numRetweets == 0) {
            tvNumRetweetsDetail.setText(tweet.numRetweets + " Retweet");
        } else {
            tvNumRetweetsDetail.setText(tweet.numRetweets + " Retweets");
        }

        // Loads number of likes
        tvNumLikesDetail = findViewById(R.id.tvNumLikesDetail);
        int numLikes = Integer.parseInt(tweet.numLikes);
        if (numLikes == 0) {
            tvNumLikesDetail.setText(tweet.numLikes + " Like");
        } else {
            tvNumLikesDetail.setText(tweet.numLikes + " Likes");
        }

        // Loads the source of the tweet
        tvSourceDetail = findViewById(R.id.tvSourceDetail);
        String[] sourceArray = tweet.source.split(">");
        String source = sourceArray[1].replace("</a", "");
        tvSourceDetail.setText(source);

        // Loads the username
        tvUsernameDetail = findViewById(R.id.tvUsernameDetail);
        tvUsernameDetail.setText("@" + tweet.user.userName);

        // liking funcitonality
        btnLikeDetail = findViewById(R.id.btnLikeDetail);
        if(tweet.isLiked){
            btnLikeDetail.setColorFilter(Color.argb(255, 255, 0, 0));
        } else {
            btnLikeDetail.setColorFilter(Color.argb(0, 255, 0, 0));
        }
        onLiked(btnLikeDetail, tweet);

        // Retweeting functionality
        btnRetweetDetail = findViewById(R.id.btnRetweetDetail);
        onRetweet(btnRetweetDetail, tweet);

        // Checks if user is verified
        ivVerifiedDetail = findViewById(R.id.ivVerifiedDetail);
        if (tweet.user.isVerified) {
            ivVerifiedDetail.setVisibility(View.VISIBLE);
        } else {
            ivVerifiedDetail.setVisibility(View.GONE);
        }

    }

    private void onLiked(ImageButton btnLikeDetail, Tweet tweet) {
        btnLikeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long tweetID = tweet.ID;
                client = TwitterApp.getRestClient(context);

                // Sends a POST req to like tweet if tweet isn't liked
                if(!tweet.isLiked) {
                    client.likeTweet(tweetID, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            int numLikes = Integer.parseInt(tweet.numLikes);
                            numLikes++;
                            tweet.numLikes = String.valueOf(numLikes);
                            tvNumLikesDetail.setText(tweet.numLikes + " Likes");
                            btnLikeDetail.setColorFilter(Color.argb(255, 255, 0, 0));
                            tweet.isLiked = true;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        }
                    });
                } else {
                    client.unLikeTweet(tweetID, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            int numLikes = Integer.parseInt(tweet.numLikes);
                            numLikes--;
                            tweet.numLikes = String.valueOf(numLikes);
                            tvNumLikesDetail.setText(tweet.numLikes + " Likes");
                            btnLikeDetail.setColorFilter(Color.argb(0, 255, 0, 0));
                            tweet.isLiked = false;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    });
                }
            }
        });
    }
    private void onRetweet(ImageButton btnRetweetDetail, Tweet tweet) {
        if (tweet.isRetweeted) {
            btnRetweetDetail.setColorFilter(Color.argb(255, 68, 206, 17));
        } else {
            btnRetweetDetail.setColorFilter(Color.argb(0, 0, 0, 0));
        }

        btnRetweetDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long tweetID = tweet.ID;
                client = TwitterApp.getRestClient(context);
                if(!tweet.isRetweeted) {
                    client.retweet(tweetID, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.isRetweeted = true;
                            int numRetweets = Integer.parseInt(tweet.numRetweets);
                            numRetweets++;
                            tweet.numRetweets = String.valueOf(numRetweets);
                            tvNumRetweetsDetail.setText(tweet.numRetweets + " Retweets");
                            btnRetweetDetail.setColorFilter(Color.argb(255, 68, 206, 17));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        }
                    });
                } else {
                    client.unRetweet(tweetID, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.isRetweeted = false;
                            int numRetweets = Integer.parseInt(tweet.numRetweets);
                            numRetweets--;
                            tweet.numRetweets = String.valueOf(numRetweets);
                            tvNumRetweetsDetail.setText(tweet.numRetweets + " Retweets");
                            btnRetweetDetail.setColorFilter(Color.argb(0, 68, 206, 17));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        }
                    });
                }

            }
        });
    }
}