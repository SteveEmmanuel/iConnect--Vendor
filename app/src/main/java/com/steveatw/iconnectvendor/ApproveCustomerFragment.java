package com.steveatw.iconnectvendor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class ApproveCustomerFragment extends Fragment {

    private TextView name, email, phone_number;
    private Button approve, reject;

    private String approveUrl;
    private String rejectUrl;

    private OnFragmentInteractionListener mListener;

    public ApproveCustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_approve_customer, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Customer Details");

        approveUrl = getResources().getString(R.string.approveUrl);
        rejectUrl = getResources().getString(R.string.rejectUrl);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phone_number = view.findViewById(R.id.phone_number);

        approve = view.findViewById(R.id.approve);
        reject = view.findViewById(R.id.reject);

        name.setText(getArguments().getString("name"));
        email.setText(getArguments().getString("email"));
        phone_number.setText(getArguments().getString("phone_number"));

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCustomerApproval(getArguments().getString("firebase_token"), approveUrl);
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCustomerApproval(getArguments().getString("firebase_token"), rejectUrl);
            }
        });
        return view;
    }

    private void changeCustomerApproval(String firebase_token, String URL){
        Context mContext = getContext().getApplicationContext();

        try {
            JSONObject customer_detail_json = new JSONObject();
            customer_detail_json.put("firebase_token", firebase_token);


            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            // Initialize a new JsonObjectRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    customer_detail_json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            // Process the JSON

                            if(response.has("error")){
                                //not approved
                                try{
                                    Snackbar.make(getView().findViewById(R.id.customer_list_layout), response.getString("error"),
                                            Snackbar.LENGTH_LONG)
                                            .show();
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }
                            else{
                                try{
                                    Snackbar.make(getView().findViewById(R.id.approve_customer_relative_layout), response.getString("success"),
                                            Snackbar.LENGTH_LONG)
                                            .show();
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                                CustomerListFragment newFragment = new CustomerListFragment();

                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment, newFragment, "CustomerListFragment");

                                transaction.commit();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            // Do something when error occurred
                            Snackbar.make(
                                    getView().findViewById(R.id.approve_customer_relative_layout),
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onApproveCustomerFragmentInteraction(Uri uri);
    }
}
