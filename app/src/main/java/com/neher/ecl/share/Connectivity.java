package com.neher.ecl.share;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 4/28/2018.
 */

public class Connectivity {
    private ConnectivityManager cm;
    private NetworkInfo activeNetwork;

    public Connectivity(Context context)
    {
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
    }

    public boolean getConnectionStatus()
    {
        boolean status = false;
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting())
        {
            status = true;
        }
        return status;
    }

    public boolean getWifiConnectionStatus()
    {
        boolean status = false;
        if(getConnectionStatus())
        {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                status = true;
            }
        }
        return status;
    }
}
