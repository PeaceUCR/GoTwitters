package com.GoTweets.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by m.sonasath on 12/15/2015.
 */
public class NetworkUtils {
    public static boolean isConnectedToNetwork(ConnectivityManager connMgr) {
        //ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Log.e("NETWORK", "isConnectedToNetwork return true");
            return true;
        }
        else {
            Log.e("NETWORK", "isConnectedToNetwork return false");
            return false;
        }
    }
}
