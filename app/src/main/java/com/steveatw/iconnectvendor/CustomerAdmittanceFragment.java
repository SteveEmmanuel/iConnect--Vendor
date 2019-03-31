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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;


public class CustomerAdmittanceFragment extends Fragment {

    private String checkApprovalUrl;
    private static final int RC_BARCODE_CAPTURE = 9001;
    private OnFragmentInteractionListener mListener;
    private Button read_barcode, manual_admittance;
    final private String TAG = "CustomerAdmittance";
    private Context mContext;

    public CustomerAdmittanceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_customer_admittance, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Admit Customer");

        checkApprovalUrl = getString(R.string.checkApprovalUrl);

        read_barcode = view.findViewById(R.id.read_barcode);
        manual_admittance = view.findViewById(R.id.manual_admit);

        read_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        manual_admittance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManualAdmittanceListFragment newFragment = new ManualAdmittanceListFragment();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment, "ManualAdmittanceListFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Snackbar.make(getView().getRootView().findViewById(R.id.main_layout), "barcode detected",
                            Snackbar.LENGTH_LONG)
                            .show();
                    setApproval(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    Snackbar.make(getView().getRootView().findViewById(R.id.main_layout), "barcode failure",
                            Snackbar.LENGTH_LONG)
                            .show();
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {

                Snackbar.make(getView().getRootView().findViewById(R.id.main_layout), String.format("barcode_error",
                        CommonStatusCodes.getStatusCodeString(resultCode)),
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setApproval(final String firebase_token){

        mContext = getContext().getApplicationContext();
        try{

            JSONObject customer_detail_json = new JSONObject();
            customer_detail_json.put("firebase_token", firebase_token);

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            // Initialize a new JsonObjectRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    checkApprovalUrl,
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

                                ManualAdmittanceFragment newFragment = new ManualAdmittanceFragment();
                                try {
                                    Bundle arguments = new Bundle();
                                    arguments.putString("name", response.getString("name"));
                                    arguments.putString("email", response.getString("email"));
                                    arguments.putString("phone_number", response.getString("phone_number"));
                                    arguments.putString("firebase_token", response.getString("firebase_token"));
                                    newFragment.setArguments(arguments);
                                }
                                    catch (JSONException e){

                                }


                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment, newFragment, "ManualAdmittanceFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }

                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Snackbar.make(
                                    getView().getRootView().findViewById(R.id.main_layout),
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
            // Add JsonObjectRequest to the RequestQueue
            requestQueue.add(jsonObjectRequest);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCustomerAdimttanceFragmentInteraction(uri);
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
        void onCustomerAdimttanceFragmentInteraction(Uri uri);
    }
}
