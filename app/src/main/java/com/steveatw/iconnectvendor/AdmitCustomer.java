package com.steveatw.iconnectvendor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AdmitCustomer extends AppCompatActivity {

    private TextView name, email, phone_number;
    private Button admit, reject;

    private final String admitUrl = "http://192.168.0.174:8080/admit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admit_customer);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone_number = findViewById(R.id.phone_number);

        admit = findViewById(R.id.admit);
        reject = findViewById(R.id.reject);

        final Intent intent = getIntent();

        name.setText(intent.getStringExtra("name"));
        email.setText(intent.getStringExtra("email"));
        phone_number.setText(intent.getStringExtra("phone_number"));

        admit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                admitCustomer(intent.getStringExtra("firebase_token"));
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdmitCustomer.this, VerifyCustomer.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void admitCustomer(String firebase_token){
        Context mContext = getApplicationContext();

        try {
            JSONObject customer_detail_json = new JSONObject();
            customer_detail_json.put("firebase_token", firebase_token);


            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            // Initialize a new JsonObjectRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    admitUrl,
                    customer_detail_json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            // Process the JSON
                            Intent intent = new Intent(AdmitCustomer.this, VerifyCustomer.class);
                            startActivity(intent);
                            finish();
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            // Do something when error occurred
                            Snackbar.make(
                                    findViewById(R.id.approve_customer_relative_layout),
                                    "Error.",
                                    Snackbar.LENGTH_LONG
                            ).show();
                        }
                    }
            );

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Add JsonObjectRequest to the RequestQueue
            requestQueue.add(jsonObjectRequest);

            Log.d("output", customer_detail_json.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
