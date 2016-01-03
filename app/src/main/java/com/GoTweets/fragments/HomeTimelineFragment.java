package com.GoTweets.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.GoTweets.activities.TimelineActivity;
import com.GoTweets.helpers.EndlessScrollListener;
import com.GoTweets.helpers.NetworkUtils;
import com.GoTweets.helpers.TwitterApplication;
import com.GoTweets.helpers.TwitterClient;
import com.GoTweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by m.sonasath on 12/15/2015.
 */
public class HomeTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    public static final int DEFAULT_SINCE_ID = 0;
    public static final int DEFAULT_MAX_ID = 0;
    private long oldMaxId = 0;
    private long maxId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient(); //singleton client
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                Toast.makeText(getActivity(), "Refreshing!", Toast.LENGTH_LONG).show();
                refereshTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        populateTimeline(DEFAULT_SINCE_ID, DEFAULT_MAX_ID);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimelineActivity t =(TimelineActivity)getActivity();
                t.composeTweet();
            }
        });
        return v;
    }

    //send API request to get JSON response
    //fill the listview by creating tweet objects from JSON
    private void populateTimeline(final long since_id, final long max_id) {
        //first check if network connection is available
        if(NetworkUtils.isConnectedToNetwork((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)) == false) {
            Toast.makeText(getActivity(), "Please check your network connection!", Toast.LENGTH_LONG).show();
            swipeContainer.setRefreshing(false);
            return;
        }
        client.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            //sucess
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                //Log.d("Moiz", json.toString());
                //Deserialize Json
                //Create models and them to the adapter
                //Load model data into listview
                if (since_id > 0) {
                    // not first time to get
                    ArrayList<Tweet> newTweets = Tweet.fromJSONArray(json);
                    for (int i = newTweets.size() - 1; i >= 0; i--) {
                        getAdapter().insert(newTweets.get(i), 0);
                    }
                } else {
                    //first time to get
                    getAdapter().addAll(Tweet.fromJSONArray(json));
                }
                swipeContainer.setRefreshing(false);
            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "HTTP REQUEST FAILED!", Toast.LENGTH_LONG).show();
                Log.e("HTTP_error", errorResponse.toString());
                Snackbar.make(TimelineActivity.dl, "API Rate limits,wait 15 minutes",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();

                swipeContainer.setRefreshing(false);
                //Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    // Referesh timeline with new tweets at the top
    public void refereshTimeline() {
        if (!aTweets.isEmpty()) {
            Tweet firstTweet = aTweets.getItem(0);
            long sinceId = firstTweet.getUid();
            //Log.d("MOIZ", "id of first tweet: " + sinceId);
            populateTimeline(sinceId, DEFAULT_MAX_ID);
        }
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
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
        populateTimeline(DEFAULT_SINCE_ID, (maxId - 1));
    }
}
