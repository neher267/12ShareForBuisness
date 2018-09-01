package com.neher.ecl.share;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "ResultActivity";
    TextView textView;
    private ArrayList<CurrentRequest> currentRequests;

    private double lat, lng, des_lat, des_lng;
    private String des_address, countryCode;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //textView = findViewById(R.id.result);
        currentRequests = new ArrayList<>();

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lat = extras.getDouble("lat");
            lng = extras.getDouble("lng");
            des_lat = extras.getDouble("des_lat");
            des_lng = extras.getDouble("des_lng");
            des_address = extras.getString("des_address");
            countryCode = extras.getString("countryCode");

            sendRequest();
        }


        Log.d(TAG, "onCreate: lat: "+lat+" :: lng: "+lng+" :: des_lat: "+des_lat+" :: des_lng: "+des_lng);




    }

    private void sendRequest(){
        Log.d(TAG, "sendRequest: Send Request is calling");

        StringRequest sharingRequest = new StringRequest(Request.Method.POST, Env.remote.sharing_request_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: response: "+ response);
                        try {

                            JSONObject jsonObject1 = new JSONObject(response);
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            int index = 0;
                            for (int i = 0; i<jsonArray.length(); i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                double sharedLat = Double.valueOf(jsonObject.getString("lat"));
                                double sharedLng = Double.valueOf(jsonObject.getString("lng"));
                                double sharedDesLat = Double.valueOf(jsonObject.getString("des_lat"));
                                double sharedDesLng = Double.valueOf(jsonObject.getString("des_lng"));

                                float latLngDisResults[] = new float[10];
                                float desLatLngDisResults[] = new float[10];

                                Location.distanceBetween(lat, lng, sharedLat, sharedLng, latLngDisResults);
                                Location.distanceBetween(des_lat, des_lng, sharedDesLat, sharedDesLng, desLatLngDisResults);

                                float latLngDis = latLngDisResults[0];
                                float desLatLngDis = desLatLngDisResults[0];

                                Log.d(TAG, "onResponse: distances: "+ latLngDis+" : "+desLatLngDis);


                                if (latLngDis < 250 && desLatLngDis < 1000)
                                {
                                    CurrentRequest currentRequest = new CurrentRequest(
                                            jsonObject.getInt("user_id"),
                                            sharedLat,
                                            sharedLng,
                                            sharedDesLat,
                                            sharedDesLng,
                                            latLngDis,
                                            desLatLngDis
                                    );

                                    currentRequests.add(currentRequest);

                                    Log.d(TAG, "onResponse: distances: "+ currentRequest.getLatLngDis()+" : "+currentRequest.getDesLatLngDis());

                                }
                            }

                            if (currentRequests.size()>0)
                            {
                                changeUserRequestStatus();
                                Collections.sort(currentRequests, new Comparator<CurrentRequest>() {
                                    @Override
                                    public int compare(CurrentRequest o1, CurrentRequest o2) {

                                        Float o1des = o1.getDesLatLngDis();
                                        Float o2des = o2.getDesLatLngDis();
                                        return o1des.compareTo(o2des);
                                    }
                                });

                                setUpRecyclerView(currentRequests);

                                for (CurrentRequest currentRequest: currentRequests) {
                                    Log.d(TAG, "Des: "+currentRequest.getDesLatLngDis());
                                }
                            }



                            /*String results = "\n";
                            if (currentRequests.size()>1){
                                for(CurrentRequest request: currentRequests){
                                    results+= "Distance: "+request.getLatLngDis()+" :: Destination Distance: "+request.getDesLatLngDis()+"\n";
                                }

                                textView.setText(results);

                            }*/

                            Log.d(TAG, "onResponse: Total currentRequests: "+currentRequests.size());

                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: JSONException: "+e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: "+ String.valueOf(error));
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                SharedPreferences sharedPref = ResultActivity.this.getSharedPreferences(Env.sp.sp_name, Context.MODE_PRIVATE);
                Log.d(TAG, "getParams: user name: "+ sharedPref.getString(Env.sp.user_mobile, ""));
                Log.d(TAG, "getParams: password: "+ sharedPref.getString(Env.sp.user_password, ""));
                params.put("username", sharedPref.getString(Env.sp.user_mobile, ""));
                params.put("password", sharedPref.getString(Env.sp.user_password, ""));
                params.put("share_with", sharedPref.getString(Env.sp.user_share_with, ""));
                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));
                params.put("des_lat", String.valueOf(des_lat));
                params.put("des_lng", String.valueOf(des_lng));
                params.put("country_code", countryCode);
                params.put("address", des_address);

                return params;
            }
        };

        MyRequestQueue.getInstance(ResultActivity.this).addToRequestque(sharingRequest);
    }

    public void setUpRecyclerView(ArrayList<CurrentRequest> list)
    {
        Log.d(TAG, "setUpRecyclerView() calling");

        mAdapter = new MyAdapter(list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void changeUserRequestStatus() {
        Log.d(TAG, "sendRequest: Send Request is calling");

        new StringRequest(Request.Method.POST, Env.remote.change_status_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: status changed successfully!");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: "+ String.valueOf(error));
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                SharedPreferences sharedPref = ResultActivity.this.getSharedPreferences(Env.sp.sp_name, Context.MODE_PRIVATE);
                params.put("username", sharedPref.getString(Env.sp.user_mobile, ""));
                params.put("password", sharedPref.getString(Env.sp.user_password, ""));
                params.put("status", Env.request.success);

                return params;
            }
        };
    }

}
