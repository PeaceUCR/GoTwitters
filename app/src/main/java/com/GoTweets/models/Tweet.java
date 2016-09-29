package com.GoTweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ping_He on 2015/12/27.
 */

//parse Json + store data, encapsulate state logic or display logic
public class Tweet {
    private String body;
    private long uid; //unique id for tweet



    private User user; // store embedded user object
    private String createdAt;
    public String mediaImageUrl;
    public int retweetCount;
    public int favoriteCount;

    public Boolean isRetweeted;
    public Boolean isFavorited;

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public Boolean getIsRetweeted() {
        return isRetweeted;
    }

    public Boolean getIsFavorited() {
        return isFavorited;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getMediaImageUrl() {
        return mediaImageUrl;
    }

    //deserialize Json and build tweet objects
    //Tweet.fromJSON("{...}") => <Tweet>
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        //extract values from json and store
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.mediaImageUrl = null;

            JSONObject json = jsonObject.optJSONObject("entities");
            if (json != null) {
                JSONArray jsonArray = json.optJSONArray("media");
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String media_url = obj.optString("media_url");
                        if (null != media_url) {
                            tweet.mediaImageUrl = media_url;
                            break;
                        }
                    }
                }
            }

            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.isFavorited = jsonObject.getBoolean("favorited");
            tweet.isRetweeted = jsonObject.getBoolean("retweeted");;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    //Tweet.fromJSONArray([ {...}, {...} ]) => List<Tweet>
    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray){
        ArrayList<Tweet> tweets = new ArrayList<>();
        //iterate the json array and create tweets
        for (int i =0; i < jsonArray.length(); i++){
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    //Log.d("MOIZ", Long.toString(tweet.getUid()));
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        //return finished list
        return tweets;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "body='" + body + '\'' +
                ", user=" + user +
                ", createdAt='" + createdAt + '\'' +
                ", mediaImageUrl='" + mediaImageUrl + '\'' +
                '}';
    }
}
