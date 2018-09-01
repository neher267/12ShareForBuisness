package com.neher.ecl.share;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyRequestQueue {
    private static MyRequestQueue singleton;
    private RequestQueue requestQueue;
    private static Context context;


    private MyRequestQueue(Context context)
    {
        this.context = context;
        requestQueue = getRequestQueue();
    }


    private RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public static synchronized MyRequestQueue getInstance(Context context)
    {
        if (singleton == null)
        {
            singleton = new MyRequestQueue(context);
        }
        return singleton;
    }

    public<T> void addToRequestque(Request<T> request)
    {
        requestQueue.add(request);
    }
}
