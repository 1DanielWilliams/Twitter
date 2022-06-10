package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import javax.security.auth.login.LoginException;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> { //after defining the viewholder, extend the TweetsAdapter class (bc now it has something to reference)

    private final String TAG = "TweetsAdapter";
    Context context;
    List<Tweet> tweets;
    TwitterClient client;


    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    //for each row, inflate a layout for a tweet
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    //bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get the data at position
        Tweet tweet = tweets.get(position);
        tweet.position = position;
        holder.rootView.setTag(tweet);
        // Bind the tweet with the view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        View rootView;
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView ivMediaImage;
        TextView tvRelativeTime;
        TextView tvNumLikes;
        TextView tvNumRetweets;
        ImageButton btnLike;
        ImageButton btnRetweet;
        TextView tvUsername;
        ImageView ivVerified;

        public ViewHolder(@NonNull View itemView) { //itemView passed in is a representation of one row of the recycler viewn
            super(itemView);
            rootView = itemView;
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivMediaImage = itemView.findViewById(R.id.ivMediaImage);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            tvNumRetweets = itemView.findViewById(R.id.tvNumRetweets);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivVerified = itemView.findViewById(R.id.ivVerified);


            btnLike = itemView.findViewById(R.id.btnLike);
            likeButtonListener(btnLike);

            btnRetweet = itemView.findViewById(R.id.btnRetweet);
            retweetButtonListener(btnRetweet);


            tvBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tweet tweet = (Tweet) rootView.getTag();
                    Intent i = new Intent(context, TweetDetailActivity.class);
                    i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    context.startActivity(i);
                }
            });
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvRelativeTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
            tvNumLikes.setText(tweet.numLikes);
            tvNumRetweets.setText(tweet.numRetweets);
            tvUsername.setText( "@"+ tweet.user.userName);


            Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileImage);
            //checks if the tweet has an image to display
            if (tweet.mediaImageUrl == null) {
                ivMediaImage.setVisibility(View.GONE);
            } else {
                ivMediaImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.mediaImageUrl)
                        .into(ivMediaImage);
            }

            // If a tweet is liked set color of text and icon to red
            if(tweet.isLiked) {
                btnLike.setColorFilter(Color.argb(255, 255, 0, 0));
                tvNumLikes.setTextColor(Color.argb(255, 255, 0, 0));
            } else {
                tvNumLikes.setTextColor(Color.argb(255, 0, 0, 0));
                btnLike.setColorFilter(Color.argb(0, 255, 0, 0));
            }

            // if a tweet has been retweeted, set color of text and icon to green
            if (tweet.isRetweeted) {
                btnRetweet.setColorFilter(Color.argb(255, 68, 206, 17));
                tvNumRetweets.setTextColor(Color.argb(255, 68, 206, 17));
            } else {
                btnRetweet.setColorFilter(Color.argb(0, 0, 0, 0));
                tvNumRetweets.setTextColor(Color.argb(255, 0, 0, 0));
            }

            if (tweet.user.isVerified) {
                ivVerified.setVisibility(View.VISIBLE);
            } else {
                ivVerified.setVisibility(View.GONE);
            }
        }

        public void likeButtonListener(ImageButton btnLike) {
            btnLike.setOnClickListener(view -> {
                Tweet tweet = (Tweet) rootView.getTag();
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
                            tweet.isLiked = true;

                            notifyItemChanged(tweet.position);
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
                            tweet.isLiked = false;
                            notifyItemChanged(tweet.position);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    });
                }
            });
        }

        public void retweetButtonListener(ImageButton btnRetweet) {
            btnRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tweet tweet = (Tweet) rootView.getTag();
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
                                tvNumRetweets.setText(tweet.numRetweets);
                                notifyItemChanged(tweet.position);
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
                                tvNumRetweets.setText(tweet.numRetweets);
                                notifyItemChanged(tweet.position);
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
}
