package com.GoTweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.GoTweets.R;
import com.GoTweets.fragments.ProfileHeaderFragment;
import com.GoTweets.fragments.UserTimelineFragment;
import com.GoTweets.helpers.TwitterClient;
import com.GoTweets.models.User;

public class ProfileActivity extends AppCompatActivity {
    private TwitterClient client;
    User user;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        String screenName = getIntent().getStringExtra("screen_name");
        Log.e("Profileactivity: ", "screenName: " + screenName);
        UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
        //Create the user Profile header fragment
        ProfileHeaderFragment fragmentProfileHeader = ProfileHeaderFragment.newInstance(screenName);
        //Display user timeline fragment within this activity (dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flProfileHeaderContainer, fragmentProfileHeader, "profileHeader");
        ft.replace(R.id.flProfileTimelineContainer, fragmentUserTimeline, "profileTimeline");
        ft.commit(); //changes the fragment
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            // overridePendingTransition(R.animator.anim_left, R.animator.anim_right);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
