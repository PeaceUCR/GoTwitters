package com.GoTweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.GoTweets.R;
import com.GoTweets.activities.ProfileActivity;
import com.GoTweets.helpers.ParseRelativeDate;
import com.GoTweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by m.sonasath on 12/9/2015.
 */
//take Tweet objects and turn them into views displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    public static class ViewHolder {
       public ImageView ivProfileImage;
       public TextView tvUserScreenName;
       public TextView tvUserProfileName;
       public TextView tvBody;
       public TextView tvCreatedAt;
       public ImageView ivMediaImage;
       public ImageView ivReplyIcon;
       public ImageView ivRetweetIcon;
       public ImageView ivFavoriteIcon;
        //ImageView ivAddContactIcon;
       public TextView tvRetweetCount;
       public TextView tvFavoriteCount;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets){
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    //overide and setup custom template
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. get the tweet
        final Tweet tweet = getItem(position);
        // 2. find or inflate the template
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
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
