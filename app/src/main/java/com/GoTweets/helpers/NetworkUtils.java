package com.GoTweets.helpers;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Ping_He on 2015/12/27.
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
