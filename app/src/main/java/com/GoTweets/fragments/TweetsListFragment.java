package com.GoTweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.GoTweets.R;
import com.GoTweets.adapters.TweetsArrayAdapter;
import com.GoTweets.models.Tweet;

import java.util.ArrayList;

/**
 * Created by Ping_He on 2015/12/27.
 */
public class TweetsListFragment extends Fragment {
    private ArrayList<Tweet> tweets;
    public TweetsArrayAdapter aTweets;
    public ListView lvTweets;
    public SwipeRefreshLayout swipeContainer;
    public FloatingActionButton fab;

    // inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        //find the listview
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        //connect adapter to listview
        lvTweets.setAdapter(aTweets);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        fab =(FloatingActionButton)v.findViewById(R.id.fab);
        return v;
    }

    //creation lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create the arraylist (data source)
        tweets = new ArrayList<>();
        //construct the adapter from data source
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
    }

    public TweetsArrayAdapter getAdapter() {
        return aTweets;
    }
}
