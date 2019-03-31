package com.steveatw.iconnectvendor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
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


public class ManualAdmittanceFragment extends Fragment {

    private TextView name, email, phone_number;
    private Button admit, reject;

    private String admitUrl;

    private OnFragmentInteractionListener mListener;

    public ManualAdmittanceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_manual_admittance, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Customer Details");

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phone_number = view.findViewById(R.id.phone_number);

        admitUrl = getResources().getString(R.string.admitUrl);

        admit = view.findViewById(R.id.admit);
        reject = view.findViewById(R.id.reject);

        name.setText(getArguments().getString("name"));
        email.setText(getArguments().getString("email"));
        phone_number.setText(getArguments().getString("phone_number"));

        admit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                admitCustomer(getArguments().getString("firebase_token"));
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdmitListFragment newFragment = new AdmitListFragment();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment, "ManualAdmittanceListFragment");
                transaction.commit();
            }
        });

        return view;
    }

    private void admitCustomer(String firebase_token){
        Context mContext = getContext().getApplicationContext();

        try {
            JSONObject customer_detail_json = new JSONObject();
            customer_detail_json.put("firebase_token", firebase_token);


            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.setContentView(R.layout.progress_bar);
            progressDialog.getWindow().setGravity(Gravity.CENTER);
            progressDialog.show();

            // Initialize a new JsonObjectRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    admitUrl,
                    customer_detail_json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            // Do something with response
                            // Process the JSON
                            Snackbar.make(getView().getRootView().findViewById(R.id.main_layout), "Customer admitted successfully",
                                    Snackbar.LENGTH_LONG)
                                    .show();
                            CustomerAdmittanceFragment newFragment = new CustomerAdmittanceFragment();

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment, newFragment, "CustomerAdmittanceFragment");
                            transaction.commit();
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            // Do something when error occurred
                            Snackbar.make(
                                    getView().getRootView().findViewById(R.id.approve_customer_relative_layout),
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
        void onManualAdmittanceFragmentInteraction(Uri uri);
    }
}
