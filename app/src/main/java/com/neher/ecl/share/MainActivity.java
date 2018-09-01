package com.neher.ecl.share;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button loginButton, registrationButton;
    private EditText mobileView, passwordView;
    private SharedPreferences sharedPref;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        if(sharedPref.getString(Env.sp.access_token, "").equals(Env.sp.access_token_yes)){
            startActivity(new Intent(MainActivity.this, ShareActivity.class));
        }

        loginButton.setOnClickListener(this);
        registrationButton.setOnClickListener(this);
    }

    private void init(){
        sharedPref = this.getSharedPreferences(Env.sp.sp_name, Context.MODE_PRIVATE);
        mobileView = findViewById(R.id.edit_text_mobile);
        passwordView = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_login);
        registrationButton = findViewById(R.id.button_registration);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_registration){
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
        }else if(v.getId() == R.id.button_login){
            attemptToLogin();
            Toast.makeText(this, "Login..", Toast.LENGTH_LONG).show();
        }
    }

    private void attemptToLogin(){
        final String mobile = mobileView.getText().toString();
        final String password = passwordView.getText().toString();

        StringRequest logInRequest = new StringRequest(Request.Method.POST, Env.remote.login_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: success: "+ response);
                        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(Env.sp.sp_name, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = new JSONObject(response);
                            JSONObject object = jsonObject.getJSONObject("data");
                            if (object != null)
                            {
                                editor.putString(Env.sp.user_name, object.getString(Env.sp.user_name));
                                editor.putString(Env.sp.user_gender, object.getString(Env.sp.user_gender));
                                editor.putString(Env.sp.user_age, object.getString(Env.sp.user_age));
                                editor.putString(Env.sp.user_share_with, object.getString(Env.sp.user_share_with));
                                editor.putString(Env.sp.user_mobile, mobile);
                                editor.putString(Env.sp.user_password, password);
                                editor.putString(Env.sp.access_token, Env.sp.access_token_yes);
                                editor.commit();

                                startActivity(new Intent(MainActivity.this, ShareActivity.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: jesson exception: "+e.getMessage());
                        } catch (NullPointerException e){
                            Log.d(TAG, "onResponse: exception: "+e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: volly error: "+ error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d(TAG, "getParams: AuthFailureError: ");
                Map<String,String> params = new HashMap<>();

                params.put("username", mobile);
                params.put("password", password);

                return params;
            }
        };

        MyRequestQueue.getInstance(MainActivity.this).addToRequestque(logInRequest);

    }

}
