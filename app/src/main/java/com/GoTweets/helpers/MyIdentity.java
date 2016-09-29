package com.GoTweets.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.GoTweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by Ping_He on 2015/12/27.
 */
public class MyIdentity {
        private TwitterClient client;
        public void verifyCredentials(final Context context) {
            client = TwitterApplication.getRestClient();
            client.getUserCredentials(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    User user = User.fromJSON(json);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                    Editor edit = pref.edit();
                    edit.putString("screen_name", user.getScreenName());
                    edit.apply();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(context, "HTTP REQUEST FAILED!", Toast.LENGTH_LONG).show();
                }
            });
        }
}
