package com.GoTweets.models;


import org.json.JSONException;
import org.json.JSONObject;


import java.io.Serializable;

/*
"user": {
      "name": "OAuth Dancer",
      "profile_sidebar_fill_color": "DDEEF6",
      "profile_background_tile": true,
      "profile_sidebar_border_color": "C0DEED",
      "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
      "created_at": "Wed Mar 03 19:37:35 +0000 2010",
      "location": "San Francisco, CA",
      "follow_request_sent": false,
      "id_str": "119476949",
      "is_translator": false,
      "profile_link_color": "0084B4",
      "entities": {
        "url": {
          "urls": [
            {
              "expanded_url": null,
              "url": "http://bit.ly/oauth-dancer",
              "indices": [
                0,
                26
              ],
              "display_url": null
            }
          ]
        },
        "description": null
      },
      "default_profile": false,
      "url": "http://bit.ly/oauth-dancer",
      "contributors_enabled": false,
      "favourites_count": 7,
      "utc_offset": null,
      "profile_image_url_https": "https://si0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
      "id": 119476949,
      "listed_count": 1,
      "profile_use_background_image": true,
      "profile_text_color": "333333",
      "followers_count": 28,
      "lang": "en",
      "protected": false,
      "geo_enabled": true,
      "notifications": false,
      "description": "",
      "profile_background_color": "C0DEED",
      "verified": false,
      "time_zone": null,
      "profile_background_image_url_https": "https://si0.twimg.com/profile_background_images/80151733/oauth-dance.png",
      "statuses_count": 166,
      "profile_background_image_url": "http://a0.twimg.com/profile_background_images/80151733/oauth-dance.png",
      "default_profile_image": false,
      "friends_count": 14,
      "following": false,
      "show_all_inline_media": false,
      "screen_name": "oauth_dancer"
    },

 */

/**
 * Created by m.sonasath on 12/9/2015.
 */
public class User implements Serializable{
    // list attributes
    private String name;
    private long uid;
    private String screenName;
    private String profileImageUrl;
    public Boolean useBackgroundImage;
    public String profileBackgroundImageUrl;
    public String profileBackgroundColor;
    public int followersCount;
    public int statusesCount;
    public int friendsCount;
    public Boolean isVerified;
    public Boolean isFollowing;
    public String description;

    public Boolean getUseBackgroundImage() {
        return useBackgroundImage;
    }

    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    public String getProfileBackgroundColor() {
        return profileBackgroundColor;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getStatusesCount() {
        return statusesCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public Boolean getIsFollowing() {
        return isFollowing;
    }

    public String getDescription() {
        return description;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    // Deserialize the user json => <User>
    public static User fromJSON(JSONObject jsonObject){
        User user = new User();
        //extract values from json and store
        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.profileBackgroundColor = jsonObject.getString("profile_background_color");
            user.useBackgroundImage = jsonObject.getBoolean("profile_use_background_image");
            user.profileBackgroundImageUrl = jsonObject.getString("profile_background_image_url");
            user.followersCount = jsonObject.getInt("followers_count");
            user.statusesCount = jsonObject.getInt("statuses_count");
            user.friendsCount = jsonObject.getInt("friends_count");
            user.isVerified = jsonObject.getBoolean("verified");
            user.isFollowing = jsonObject.getBoolean("following");
            user.description = jsonObject.getString("description");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
