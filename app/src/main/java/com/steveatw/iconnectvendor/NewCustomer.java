package com.steveatw.iconnectvendor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;


public class NewCustomer extends Fragment {
    EditText name, email, phone_number;
    Button create;
    String uuid;
    String createCustomerURL;
    final String TAG = "NewCustomer";

    private OnFragmentInteractionListener mListener;

    public NewCustomer() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_customer, container, false);

        createCustomerURL = getString(R.string.createCustomerURL);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phone_number = view.findViewById(R.id.phone_number);

        uuid = UUID.randomUUID().toString()+UUID.randomUUID().toString();

        create = view.findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject customer_detail_json = new JSONObject();
                try{
                    customer_detail_json.put("name", name.getText().toString());
                    customer_detail_json.put("email", email.getText().toString());
                    customer_detail_json.put("phone_number", phone_number.getText().toString());
                    customer_detail_json.put("firebase_token", uuid);
                }catch (JSONException e){

                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        createCustomerURL,
                        customer_detail_json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Do something with response
                                // Process the JSON

                                // Get the JSON array
                                if(response.has("error")){
                                    //not approved
                                    try{
                                        Snackbar.make(getView().getRootView().findViewById(R.id.main_layout), response.getString("error"),
                                                Snackbar.LENGTH_LONG)
                                                .show();
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }

                                }
                                else{
                                    try {
                                        ApproveCustomerFragment newFragment = new ApproveCustomerFragment();

                                        Bundle arguments = new Bundle();
                                        arguments.putString("name", response.getString("name"));
                                        arguments.putString("email", response.getString("email"));
                                        arguments.putString("phone_number", response.getString("phone_number"));
                                        arguments.putString("firebase_token", response.getString("firebase_token"));
                                        ;
                                        newFragment.setArguments(arguments);

                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.fragment, newFragment, "ApproveCustomerFragment");
                                        transaction.commit();
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }

                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                Snackbar.make(
                                        getView().getRootView().findViewById(R.id.verify_customer_relative_layout),
                                        "Error."+error,
                                        Snackbar.LENGTH_LONG
                                ).show();
                            }
                        }
                );

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);



            }



        });

        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNewCustomerFragmentInteraction(Uri uri);
    }
}
