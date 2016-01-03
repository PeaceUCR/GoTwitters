package com.GoTweets.helpers;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.io.File;
import java.io.FileNotFoundException;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "wOXL0s2TYmYss6x9BuEKfPV2d";       // Change this
	public static final String REST_CONSUMER_SECRET = "0owQg0PKVfgbzCemKCXd7YNEiDCSi4Jwjb8CqQ7USWwuJoMrH4"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://simpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// Method == Endpoint

	// HomeTimeline: gets us the home timeline
	//GET statuses/home_timeline.json
	//		count=25
	//		since_id=1

	public void getHomeTimeline(long since_id, long max_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		//specify the params
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if (max_id > 0) {
			params.put("max_id", String.valueOf(max_id));
		} else if (since_id > 0) {
			params.put("since_id", String.valueOf(since_id));
		}
		params.put("include_entities", true);
		//execute the request
		client.get(apiUrl, params, handler);
	}

	//Compose tweet
	//This is using the Post METHOD
	//without the image attchment will cause error,i don't know why?
	public void postTweet(String body,String imagepath, AsyncHttpResponseHandler handler) {

        String apiUrl;
		RequestParams params = new RequestParams();
		params.put("status", body);

		if(!imagepath.equals("image_attachment"))
		{
            apiUrl = getApiUrl("statuses/update_with_media.json");
			File f = new File(imagepath);
			try
			{
				params.put("media", f);
			}catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			params.put("Content-Type","multipart/form-data");
		}
        else {
            apiUrl = getApiUrl("statuses/update.json");
        }

		getClient().post(apiUrl, params, handler);
	}

	public void getMentionsTimeline(long since_id, long max_id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		//specify the params
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if (max_id > 0) {
			params.put("max_id", String.valueOf(max_id));
		} else if (since_id > 0) {
			params.put("since_id", String.valueOf(since_id));
		}
		params.put("include_entities", true);
		//execute the request
		client.get(apiUrl, params, handler);
	}

	public void getUserTimeline(String screenName, long since_id, long max_id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		params.put("count", 25);
		if (max_id > 0) {
			params.put("max_id", String.valueOf(max_id));
		} else if (since_id > 0) {
			params.put("since_id", String.valueOf(since_id));
		}
		params.put("include_entities", true);
		client.get(apiUrl, params, handler);
	}

	public void getUserInfo(String screenName, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = null;
		if (screenName != null && !screenName.isEmpty()) {
			params = new RequestParams();
			params.put("screen_name", screenName);
		}
		client.get(apiUrl, params, handler);
	}

	public void getUserCredentials(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, null, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

	public void search(String q,long MAX_ID,AsyncHttpResponseHandler handler)
	{
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		params.put("q",q);
		params.put("result_type", "recent");

		if (MAX_ID > 0) {
			params.put("max_id", String.valueOf(MAX_ID));
		}

		params.put("count", 5);

		params.put("include_entities", true);

		client.get(apiUrl, params, handler);
	}
}