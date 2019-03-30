package com.steveatw.iconnectvendor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

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

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class VerifyCustomer extends Activity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;
    private Context mContext;
    private String firebase_token;
    private String checkApprovalUrl= "http://192.168.0.174:8080/checkapproval";
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_customer);

        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.manual_admit).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
//            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
//            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
        if (v.getId() == R.id.manual_admit) {
            // launch customer list selector activity.
            Intent intent = new Intent(VerifyCustomer.this, CustomerAdmitListActivity.class);

            startActivity(intent);
            finish();
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Snackbar.make(findViewById(R.id.verify_customer_relative_layout), "barcode detected",
                            Snackbar.LENGTH_LONG)
                            .show();
                    setApproval(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    statusMessage.setText("barcode_failure");
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format("barcode_error",
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setApproval(final String firebase_token){

        mContext = getApplicationContext();
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
                                    Snackbar.make(findViewById(R.id.verify_customer_relative_layout), response.getString("error"),
                                            Snackbar.LENGTH_LONG)
                                            .show();
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }
                            else{
                                Intent intent = new Intent(VerifyCustomer.this, AdmitCustomer.class);
                                try {
                                    intent.putExtra("name", response.getString("name"));
                                    intent.putExtra("email", response.getString("email"));
                                    intent.putExtra("phone_number", response.getString("phone_number"));
                                    intent.putExtra("firebase_token", response.getString("firebase_token"));
                                }
                                catch (JSONException e){

                                }
                                startActivity(intent);
                                finish();
                            }

                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Snackbar.make(
                                    findViewById(R.id.verify_customer_relative_layout),
                                    "Error."+error,
                                    Snackbar.LENGTH_LONG
                            ).show();
                        }
                    }
            );

            // Add JsonObjectRequest to the RequestQueue
            requestQueue.add(jsonObjectRequest);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}