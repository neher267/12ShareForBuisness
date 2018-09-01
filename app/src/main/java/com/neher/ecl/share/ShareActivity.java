package com.neher.ecl.share;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShareActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, LocationSource.OnLocationChangedListener{

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    public static final int ERROR_DIALOG_REQUEST = 404;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 500;

    public boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    private AutoCompleteTextView mSearchText;
    private ImageView mMyLocationView;
    protected GeoDataClient mGeoDataClient;
    private ArrayList<CurrentRequest> currentRequests;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(20.797425846129503, 87.86243451562495), new LatLng(26.745824752661083, 92.70740521874995));


    //-----
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private TextView userNameView, userMobileView;
    private NavigationView navigationView;
    private Connectivity connectivity;

    private double lat, lng, des_lat, des_lng;
    private String countryCode, des_address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Log.d(TAG, "onClick: params: lat: "+String.valueOf(lat)+
                " lng: "+String.valueOf(lng)+
                " des lat: "+String.valueOf(des_lat)+
                " des lng: "+String.valueOf(des_lng)+
                " address: "+String.valueOf(des_address)
                );
                //sendRequest();
                if (lat != 0 && lng != 0 && des_lat != 0 && des_lng != 0 && countryCode != null){
                    Log.d(TAG, "onClick: all prams are ok");
                    //sendRequest();

                    Intent intent = new Intent(ShareActivity.this, ResultActivity.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    intent.putExtra("des_lat", des_lat);
                    intent.putExtra("des_lng", des_lng);
                    intent.putExtra("des_address", des_address);
                    intent.putExtra("countryCode", countryCode);

                    startActivity(intent);
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "onCreate: Android Version is M or later");
            getLocationPermission();
        } else {
            Log.d(TAG, "onCreate: Android version is below M");
            initMap();
            init();
        }

        navigationView.setNavigationItemSelectedListener(this);
        userNameView.setText(sharedPref.getString(Env.sp.user_name, ""));
        userMobileView.setText(sharedPref.getString(Env.sp.user_mobile, ""));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_settings) {
            //

        } else if (id == R.id.nav_about_us) {

        } else if (id == R.id.nav_help) {

        }else if (id == R.id.nav_term_con) {

        } else if (id == R.id.nav_sign_out) {
            editor.putString(Env.sp.access_token, "");
            editor.commit();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onMapReady: location permission is not granted");
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMarkerDragListener(this);
        }
    }

    private void init(){
        sharedPref = this.getSharedPreferences(Env.sp.sp_name, MODE_PRIVATE);
        editor = sharedPref.edit();
        currentRequests = new ArrayList<>();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        userNameView = headerView.findViewById(R.id.user_name_view);
        userMobileView = headerView.findViewById(R.id.user_mobile_view);



        //---------------------
        Log.d(TAG, "init: initializing");
        mSearchText = findViewById(R.id.input_search);
        mMyLocationView = findViewById(R.id.my_location);

        mGeoDataClient = Places.getGeoDataClient(this);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){

                    //executing our method for searching
                    geoLocate("");
                }
                return false;
            }
        });

        mSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
            }
        });

        mPlaceAutocompleteAdapter =  new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mMyLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

    }

    private void initMap() {
        Log.d(TAG, "initMap: initialize the map");
        if (isServicesOk()) {
            Toast.makeText(ShareActivity.this, "Map is Ready", Toast.LENGTH_LONG).show();
            Log.d(TAG, "initMap: Map is ready");
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void geoLocate(@Nullable String searchString){
        if (mMap != null){
            mMap.clear();
        }
        Log.d(TAG, "geoLocate: geolocating");
        if (searchString == null){
            searchString = mSearchText.getText().toString();
        }
        Geocoder geocoder = new Geocoder(ShareActivity.this);
        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.d(TAG, "geoLocate: exception: " + e.getMessage());
        }

        if (list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: Address: "+address.toString());
            if (address.hasLatitude() && address.hasLongitude()){
                Log.d(TAG, "geoLocate: has latitude and longitude");

                des_lat = address.getLatitude();
                des_lng = address.getLongitude();
                countryCode = address.getCountryCode();
                des_address = address.getAddressLine(0);


                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                moveCamera(latLng, DEFAULT_ZOOM, address.getAddressLine(0));
            }
            else {
                Toast.makeText(ShareActivity.this, "No Location Found", Toast.LENGTH_LONG).toString();
            }
        }

    }

    private void getDeviceLocation(){
        /*if (mMap != null){
            mMap.clear();
        }*/
        Log.d(TAG, "getDeviceLocation: getting the device current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if (mLocationPermissionGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Find Location");
                            Location currentLocation = (Location) task.getResult();
                            lat = currentLocation.getLatitude();
                            lng = currentLocation.getLongitude();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");

                        }
                        else{
                            Log.d(TAG, "onComplete: Could'n find location");
                            Toast.makeText(ShareActivity.this, "Unable to get current location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException: "+e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving camera: lat: "+latLng.latitude+", lng: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(!title.equals("My Location")){
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            markerOptions.draggable(true);

            mMap.addMarker(markerOptions);
        }
    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: Checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ShareActivity.this);

        if (ConnectionResult.SUCCESS == available){
            // Everything is fine and user can make map request
            Log.d(TAG, "isServicesOk: Google play services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // An error occured but we can resolve it
            Log.d(TAG, "isServicesOk: An error occured but we can fix it");

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ShareActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "You can't make maps request", Toast.LENGTH_LONG).show();
        }

        return false;
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting Location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(ShareActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            // Permission Granted
            Log.d(TAG, "getLocationPermission: Permission Granted");
            initMap();

        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    private void sendRequest(){
        Log.d(TAG, "sendRequest: Send Request is calling");

        /*JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, Env.remote.sharing_request_url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse: response:"+ response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: VolleyError: "+error);
                    }
                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {

                        return super.getHeaders();
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Log.d(TAG, "getParams: getParams method is calling");
                        Map<String,String> params = new HashMap<>();
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

        MyRequestQueue.getInstance(ShareActivity.this).addToRequestque(request);*/


        /*JsonObjectRequest sharingRequest =  new JsonObjectRequest(Request.Method.POST, Env.remote.sharing_request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: response: "+ response);
                        try {

                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i<jsonArray.length(); i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                double sharedLat = jsonObject.getDouble("lat");
                                double sharedLng = jsonObject.getDouble("lng");
                                double sharedDesLat = jsonObject.getDouble("des_lat");
                                double sharedDesLng = jsonObject.getDouble("des_lng");

                                float results[] = new float[10];
                                Location.distanceBetween(lat, lng, sharedLat, sharedLng, results);
                                Location.distanceBetween(des_lat, des_lng, sharedDesLat, sharedDesLng, results);

                                float latLngDis = results[0];
                                float desLatLngDis = results[0];

                                if (latLngDis < 120)
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
                                    Log.d(TAG, "onResponse: Total currentRequests: "+currentRequests.size());
                                    Log.d(TAG, "onResponse: distances: "+ currentRequest.getDesLat()+" : "+currentRequest.getDesLatLngDis());
                                }
                            }

                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: JSONException: "+e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, ": "+ String.valueOf(error));
                    }
                })            {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
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
        MyRequestQueue.getInstance(ShareActivity.this).addToRequestque(sharingRequest);*/

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

                                if (latLngDis < 120 && desLatLngDis < 500)
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

                            if (currentRequests.size()>1){
                                for(CurrentRequest request: currentRequests){
                                    Log.d(TAG, "onResponse: ");
                                }

                            }

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
                SharedPreferences sharedPref = ShareActivity.this.getSharedPreferences(Env.sp.sp_name, Context.MODE_PRIVATE);
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

        MyRequestQueue.getInstance(ShareActivity.this).addToRequestque(sharingRequest);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length>0){
                    for (int i = 0; i<grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            Log.d(TAG, "onRequestPermissionsResult: Permission Denied");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            geoLocate(primaryText.toString());

            Log.d(TAG, "Autocomplete item selected: " + primaryText);
        }
    };


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //Toast.makeText(this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        des_lat = marker.getPosition().latitude;
        des_lng = marker.getPosition().longitude;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            mMap.clear();
            addresses = geocoder.getFromLocation(des_lat, des_lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            Log.d(TAG, "onMarkerDragEnd: address: "+addresses);
            String address = addresses.get(0).getAddressLine(0);
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(des_lat, des_lng))
                    .title(address);
            mMap.addMarker(options);

            des_address = address;

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName()*/;





    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location changed", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onLocationChanged: latlng: "+location.getLatitude()+":"+location.getLongitude());
        lat = location.getLatitude();
        lng = location.getLongitude();
    }
}
