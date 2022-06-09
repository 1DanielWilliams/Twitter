package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailActivity extends AppCompatActivity {

    private final String TAG = "TweetDetailActivity";
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        //  Loads profile picture
        ivProfileDetail = findViewById(R.id.ivProfileDetail);
        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileDetail);

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

    }
}