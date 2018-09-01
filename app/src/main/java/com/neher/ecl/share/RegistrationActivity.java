package com.neher.ecl.share;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private Button registrationButton;
    private EditText nameView, mobileView, ageView, passwordView, passwordConView;
    private RadioGroup genderView, shareWithView;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();

        toolbar.setTitle("Registration");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptToRegister();
            }
        });

    }

    private void init(){
        toolbar = findViewById(R.id.toolbar);
        registrationButton = findViewById(R.id.button_register);
        nameView = findViewById(R.id.edit_text_name);
        mobileView = findViewById(R.id.edit_text_mobile);
        ageView = findViewById(R.id.edit_text_age);
        passwordView = findViewById(R.id.edit_text_password);
        passwordConView = findViewById(R.id.edit_text_password_con);
        genderView = findViewById(R.id.gender);
        shareWithView = findViewById(R.id.share_with);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void attemptToRegister(){
        final String name = nameView.getText().toString();
        final String mobile = mobileView.getText().toString();
        final String password = passwordView.getText().toString();
        final int age = Integer.valueOf(ageView.getText().toString());
        final String passwordCon = passwordConView.getText().toString();
        final String gender = getGender();
        final String shareWith = getShareWith();

        StringRequest RegisterRequest = new StringRequest(Request.Method.POST, Env.remote.register_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);

                        SharedPreferences sharedPref = RegistrationActivity.this.getSharedPreferences(Env.sp.sp_name, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        editor.putString(Env.sp.user_name, name);
                        editor.putString(Env.sp.user_mobile, mobile);
                        editor.putString(Env.sp.user_gender, gender);
                        editor.putInt(Env.sp.user_age, age);
                        editor.putString(Env.sp.user_password, password);
                        editor.putString(Env.sp.user_share_with, shareWith);
                        editor.putString(Env.sp.access_token, Env.sp.access_token_yes);
                        editor.commit();
                        startActivity(new Intent(RegistrationActivity.this, ShareActivity.class));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d(TAG, String.valueOf(error));
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("username", mobile);
                params.put("name", name);
                params.put("gender", gender);
                params.put("age", String.valueOf(age));
                params.put("password", password);
                params.put("share_with", shareWith);

                return params;
            }
        };

        MyRequestQueue.getInstance(RegistrationActivity.this).addToRequestque(RegisterRequest);


    }

    private String getGender(){
        String gender = "";

        if(R.id.gender_male == genderView.getCheckedRadioButtonId())
        {
            gender = Env.user.male;
        }
        else if(R.id.gender_female == genderView.getCheckedRadioButtonId()){
            gender = Env.user.female;
        }
        return gender;
    }

    private String getShareWith(){
        String shareWith = "";

        if(R.id.share_male == shareWithView.getCheckedRadioButtonId())
        {
            shareWith = Env.user.male;
        }
        else if(R.id.share_female == shareWithView.getCheckedRadioButtonId()){
            shareWith = Env.user.female;
        }
        else if(R.id.share_both == shareWithView.getCheckedRadioButtonId()){
            shareWith = Env.user.both;
        }
        return shareWith;
    }

}
