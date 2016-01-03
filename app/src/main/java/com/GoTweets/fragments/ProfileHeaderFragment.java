package com.GoTweets.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.GoTweets.helpers.TwitterApplication;
import com.GoTweets.helpers.TwitterClient;
import com.GoTweets.models.User;
import com.GoTweets.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by m.sonasath on 12/15/2015.
 */
public class ProfileHeaderFragment extends Fragment {
    private TwitterClient client;
    private User user;
    private ImageView ivProfileBackgroundImage;
    private ImageView ivProfileTopCardImage;
    private TextView tvProfileScreenName;
    private TextView tvProfileName;
    private TextView tvTagline;
    private TextView tvFollowersCount;
    private TextView tvFriendsCount;
    private TextView tvTweetsCount;
    private ImageView ivVerified;

    public static ProfileHeaderFragment newInstance(String screenName) {
        ProfileHeaderFragment frag = new ProfileHeaderFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_header, container, false);
        ivProfileBackgroundImage = (ImageView) v.findViewById(R.id.ivProfileBackgroundImage);
        ivProfileTopCardImage = (ImageView) v.findViewById(R.id.ivProfileTopCardImage);
        tvProfileScreenName = (TextView) v.findViewById(R.id.tvProfileScreenName);
        tvProfileName = (TextView) v.findViewById(R.id.tvProfileName);
        tvTagline = (TextView) v.findViewById(R.id.tvTagline);
        tvFollowersCount = (TextView) v.findViewById(R.id.tvFollowersCount);
        tvFriendsCount = (TextView) v.findViewById(R.id.tvFriendsCount);
        tvTweetsCount = (TextView) v.findViewById(R.id.tvTweetsCount);
        ivVerified = (ImageView) v.findViewById(R.id.ivVerified);

        String screenName = getArguments().getString("screen_name");
        if (screenName == null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            screenName = pref.getString("screen_name", null);
        }
        populateUserProfile(screenName);

        return v;
    }

    public void populateUserProfile(String screenName) {
        client = TwitterApplication.getRestClient();
        //get the account info
        client.getUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               user = User.fromJSON(response);
                if (user != null) {
                    ivProfileBackgroundImage.setImageResource(0);
                    ivProfileTopCardImage.setImageResource(0);

                    if (user.getUseBackgroundImage()) {
                        Picasso.with(getContext()).load(user.getProfileBackgroundImageUrl()).fit().into(ivProfileBackgroundImage);
                    }
                    Picasso.with(getContext()).load(user.getProfileImageUrl()).fit().placeholder(R.drawable.twitter_icon).into(ivProfileTopCardImage);
                    tvProfileName.setText(user.getName());
                    tvProfileScreenName.setText("@" + user.getScreenName());

                    tvTagline.setText(user.getDescription());
                    tvFollowersCount.setText(Integer.toString(user.getFollowersCount()));
                    tvFriendsCount.setText(Integer.toString(user.getFriendsCount()));
                    tvTweetsCount.setText(Integer.toString(user.getStatusesCount()));

                    if (!user.getIsVerified()) {
                        ivVerified.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                    errorResponse) {
                Toast.makeText(getActivity(), "HTTP REQUEST FAILED!", Toast.LENGTH_LONG).show();
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
