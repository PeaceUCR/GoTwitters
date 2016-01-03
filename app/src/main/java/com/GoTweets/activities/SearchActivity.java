package com.GoTweets.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.GoTweets.R;
import com.GoTweets.adapters.TweetsArrayAdapter;
import com.GoTweets.adapters.TweetsArrayAdapter.ViewHolder;
import com.GoTweets.helpers.EndlessScrollListener;
import com.GoTweets.helpers.ParseRelativeDate;
import com.GoTweets.helpers.TwitterApplication;
import com.GoTweets.helpers.TwitterClient;
import com.GoTweets.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ping_He on 2015/12/27.
 */
public class SearchActivity extends AppCompatActivity {


    public static final int DEFAULT_MAX_ID = 0;
    private long oldMaxId = 0;
    private long maxId = 0;

    LinearLayout rootview;
    TwitterClient client;
    ListView slv;
    TweetsArrayAdapters aTweets;
    ArrayList<Tweet> tweets;
    SearchView searchv;
    String q;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        rootview = (LinearLayout)findViewById(R.id.rootview);

        q = getIntent().getStringExtra("query");

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.twitter_icon);
        toolbar.setNavigationIcon(R.drawable.ic_action_previous_item);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用返回键
                onBackPressed();
            }
        });


        tweets = new ArrayList<Tweet>();
        aTweets = new TweetsArrayAdapters(SearchActivity.this, tweets);
        slv = (ListView)findViewById(R.id.slv);
        client = TwitterApplication.getRestClient();


        submitsearch(q,DEFAULT_MAX_ID);

        slv.setAdapter(aTweets);

        slv.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(q,page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });



        }



    public void submitsearch(String query,long maxid)
    {
        //asynhttp refer to http://www.cnblogs.com/angeldevil/p/3729808.html
        client.search(query,maxid,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("Success", "statusCode" + statusCode);
                try {
                    String str = new String(responseBody, "UTF-8");
                    Log.e("response", str);
                    JSONObject obj = new JSONObject(str);
                    JSONArray ja = obj.getJSONArray("statuses");
                    tweets = Tweet.fromJSONArray(ja);
                    aTweets.addAll(tweets);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Fail","statusCode"+statusCode);
                try {
                    String str = new String(responseBody, "UTF-8");
                    Log.e("response", str);
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }

            }
        });
    }


    public void customLoadMoreDataFromApi(String query,int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        //Log.d("MOIZ MOIZ", "Offset value " +offset);
        Tweet lastTweet = aTweets.getItem(aTweets.getCount() - 1);
        maxId = lastTweet.getUid();
        if (maxId == oldMaxId){
            Log.e("Loadmore", "Scroll Listener called twice for same max_id");
            return;
        }
        oldMaxId = maxId;
        Log.e("Loadmore", "--------------------------------------id of last tweet: " + maxId);
        submitsearch(query,(maxId-1));
    }



//when touch the url in searchresult, the error can't solve ,I have to define the adapter in this activity again!
    public class TweetsArrayAdapters extends ArrayAdapter<Tweet> {


        public TweetsArrayAdapters(Context context, List<Tweet> tweets){
            super(context, android.R.layout.simple_list_item_1, tweets);
        }

        //overide and setup custom template
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 1. get the tweet
            final Tweet tweet = getItem(position);
            // 2. find or inflate the template
            final TweetsArrayAdapter.ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new TweetsArrayAdapter.ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
                // 3. find the subviews to fill the data in the template
                viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
                viewHolder.tvUserScreenName = (TextView) convertView.findViewById(R.id.tvUserScreenName);
                viewHolder.tvUserProfileName = (TextView) convertView.findViewById(R.id.tvUserProfileName);
                viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
                viewHolder.tvBody.setTextColor(Color.BLACK);//set color to help show info, prevent vague!
                viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
                viewHolder.ivMediaImage = (ImageView) convertView.findViewById(R.id.ivMediaImage);
                viewHolder.ivReplyIcon = (ImageView) convertView.findViewById(R.id.ivReplyIcon);
                viewHolder.ivRetweetIcon = (ImageView) convertView.findViewById(R.id.ivRetweetIcon);
                viewHolder.ivFavoriteIcon = (ImageView) convertView.findViewById(R.id.ivFavoriteIcon);
                //viewHolder.ivAddContactIcon = (ImageView) convertView.findViewById(R.id.ivAddContactIcon);
                viewHolder.tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
                viewHolder.tvFavoriteCount = (TextView) convertView.findViewById(R.id.tvFavoriteCount);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // 4. populate data into subviews
            viewHolder.tvUserScreenName.setText("@" + tweet.getUser().getScreenName());
            viewHolder.tvUserProfileName.setText(tweet.getUser().getName());
            viewHolder.tvBody.setText(tweet.getBody());

            if (tweet.getMediaImageUrl() != null && !tweet.getMediaImageUrl().isEmpty()) {
                viewHolder.ivMediaImage.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).load(tweet.getMediaImageUrl()).fit().placeholder(R.drawable.twitter_icon).into(viewHolder.ivMediaImage);
            } else {
                viewHolder.ivMediaImage.setVisibility(View.GONE);
            }

            viewHolder.tvRetweetCount.setVisibility(View.VISIBLE);
            viewHolder.tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));

            viewHolder.tvFavoriteCount.setVisibility(View.VISIBLE);
            viewHolder.tvFavoriteCount.setText(Integer.toString(tweet.getFavoriteCount()));


            if (!tweet.getIsRetweeted()) {
                if (tweet.getRetweetCount() == 0) {
                    viewHolder.tvRetweetCount.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.tvRetweetCount.setTextColor(Color.parseColor("#A4A4A4"));
                }
                viewHolder.ivRetweetIcon.setImageResource(R.drawable.retweet_gray);
            } else {
                viewHolder.tvRetweetCount.setTextColor(Color.parseColor("#4AA44A"));
                viewHolder.ivRetweetIcon.setImageResource(R.drawable.retweet_green);
            }

            if (!tweet.getIsFavorited()) {
                if (tweet.getFavoriteCount() == 0) {
                    viewHolder.tvFavoriteCount.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.tvFavoriteCount.setTextColor(Color.parseColor("#A4A4A4"));
                }
                viewHolder.ivFavoriteIcon.setImageResource(R.drawable.star_gray);
            } else {
                viewHolder.tvFavoriteCount.setTextColor(Color.parseColor("#FAA628"));
                viewHolder.ivFavoriteIcon.setImageResource(R.drawable.star_orange);
            }

            String relativeTimespan = ParseRelativeDate.getRelativeTimeAgo(tweet.getCreatedAt());
            Log.d("Load_Time", relativeTimespan + "    " + tweet.getCreatedAt());
            String[] timespanParts = relativeTimespan.split(" ");
            String formattedTimespan=null;
            if (timespanParts.length == 1) {
                if (timespanParts[0].contains("Yesterday")) {
                    formattedTimespan = "1d";
                }
            } else if (timespanParts.length > 1) {
                if (timespanParts[0].contains("In")) {
                    formattedTimespan = timespanParts[1] + timespanParts[2].charAt(0);
                } else {
                    formattedTimespan = timespanParts[0] + timespanParts[1].charAt(0);
                }
            }
            viewHolder.tvCreatedAt.setText(formattedTimespan);

            viewHolder.ivProfileImage.setImageResource(android.R.color.transparent); // clear out the old image for a recycle view
            Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).fit().placeholder(R.drawable.twitter_icon).into(viewHolder.ivProfileImage);

            viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("screen_name", tweet.getUser().getScreenName());
                    getContext().startActivity(intent);
                }
            });




            // 5. return the view to be inserted into the list
            return convertView;
        }


    }
    }
