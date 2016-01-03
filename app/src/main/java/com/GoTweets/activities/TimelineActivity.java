package com.GoTweets.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.GoTweets.R;
import com.GoTweets.drawer.DragLayout;
import com.GoTweets.fragments.HomeTimelineFragment;
import com.GoTweets.fragments.MentionsTimelineFragment;
import com.GoTweets.helpers.MyIdentity;
import com.GoTweets.helpers.NetworkUtils;
import com.GoTweets.helpers.TwitterApplication;
import com.GoTweets.helpers.TwitterClient;
import com.GoTweets.helpers.myViewPager;
import com.GoTweets.models.User;
import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {
    public static final int REQ_CODE_COMPOSE_TWEET = 1;
    TweetsPagerAdapter tweetsPagerAdapter;

    //my updates
    public static DragLayout dl;
    private ListView lv;
    ArrayList<String> lvlist = new ArrayList<String>();
    SearchView searchv;

    TwitterClient client;

    ImageView ivprofile;
    TextView tvname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ivprofile=(ImageView)findViewById(R.id.iv_profile);
        tvname=(TextView)findViewById(R.id.tvname);

        //save screen name for my account
        MyIdentity myIdentity = new MyIdentity();
        myIdentity.verifyCredentials(getApplicationContext());

        //my updates





        // my own updates
        dl =(DragLayout)findViewById(R.id.dl);
        lvlist.add("Tweets: ");
        lvlist.add("Following: ");
        lvlist.add("Followers: ");


        lv = (ListView) findViewById(R.id.lv);


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.twitter_icon);
        toolbar.setNavigationIcon(R.drawable.ic_profile);


        // toolbar.setOnMenuItemClickListener(onMenuItemClick);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dl.open();
            }
        });




        //get the viewpager
        myViewPager vp = (myViewPager) findViewById(R.id.viewpager);
        //set the viewpager adapter for the pager
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(tweetsPagerAdapter);
        //find the sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //Attach the tabstrip to viewpager
        tabStrip.setViewPager(vp);


        setprofile();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        MenuItem searchi = menu.findItem(R.id.searchv);
        searchv = (SearchView) MenuItemCompat.getActionView(searchi);
        //please refer to searchView doc to find more!!!!!!
        searchv.setSubmitButtonEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.searchv:
                //don't need to ckeck whether the query is null!!!!
                searchv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            Toast.makeText(TimelineActivity.this, "submit the query" + searchv.getQuery(), Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(TimelineActivity.this,SearchActivity.class);
                            i.putExtra("query",searchv.getQuery().toString());
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            return false;
                        }
                    });
                break;


            case R.id.miCompose:
                //Toast.makeText(this, "Compose selected", Toast.LENGTH_SHORT)
                 //       .show();
                composeTweet();
                break;

            case R.id.miProfile:
                Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT)
                      .show();
                composeProfile();
                break;

        }
        return true;
    }

    public void composeTweet() {
        //first check if network connection is available
        if(NetworkUtils.isConnectedToNetwork((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)) == false) {
            Toast.makeText(getApplicationContext(), "Please check your network connection!", Toast.LENGTH_LONG).show();
            return;
        }
            Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
            startActivityForResult(i, REQ_CODE_COMPOSE_TWEET);
    }

    public void composeProfile() {
        //first check if network connection is available
        if(NetworkUtils.isConnectedToNetwork((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)) == false) {
            Toast.makeText(this.getApplicationContext(), "Please check your network connection!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("screen_name", (String) null);
        startActivity(i);
    }

    public void setprofile()
    {
        client = TwitterApplication.getRestClient();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(TimelineActivity.this.getApplicationContext());
        String screenName = pref.getString("screen_name", null);
        client.getUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               User user = User.fromJSON(response);
                if (user != null) {
                    ivprofile.setImageResource(0);
                    Picasso.with(TimelineActivity.this).load(user.getProfileImageUrl()).fit().placeholder(R.drawable.twitter_icon).into(ivprofile);
                    tvname.setText(user.getName());

                    lvlist.set(0, lvlist.get(0) + user.getStatusesCount());
                    lvlist.set(1, lvlist.get(1) + user.getFriendsCount());
                    lvlist.set(2, lvlist.get(2) + user.getFollowersCount());

                    lv.setAdapter(new lvadapter());
                    ivprofile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            composeProfile();
                        }
                    });

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                    errorResponse) {
                Toast.makeText(TimelineActivity.this, "HTTP REQUEST FAILED!", Toast.LENGTH_LONG).show();
                Log.e("DEBUG", errorResponse.toString());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_COMPOSE_TWEET && resultCode == RESULT_OK) {
            //HomeTimelineFragment fragmentHomeTimeline = (HomeTimelineFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
            HomeTimelineFragment fragmentHomeTimeline = (HomeTimelineFragment) tweetsPagerAdapter.getRegisteredFragment(0);
            fragmentHomeTimeline.refereshTimeline();
            Toast.makeText(this.getApplicationContext(), "Successfully Tweeted!!", Toast.LENGTH_SHORT).show();
        }
    }

    //return the order of fragments in viewpager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"TimeLine", "Mentions"};
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        //adapter gets manager to insert/remove fragments from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        //return tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        //how many fragments to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }






    class lvadapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return lvlist.size();
        }

        @Override
        public Object getItem(int position) {
            return lvlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
            {

                convertView = View.inflate(TimelineActivity.this, R.layout.drawer_list, null);

            }

            TextView tv1 = (TextView)convertView.findViewById(R.id.text1);
            String s = lvlist.get(position);
            tv1.setText(s);
            return convertView;
        }
    }
}
