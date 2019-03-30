package com.steveatw.iconnectvendor;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

    public class CustomerAdmitListActivity extends AppCompatActivity implements CustomerAdapter.CustomerAdapterListener {
        private static final String TAG = MainActivity.class.getSimpleName();
        private RecyclerView recyclerView;
        private List<Customer> contactList;
        private CustomerAdapter mAdapter;
        private SearchView searchView;

        // url to fetch contacts json
        private static final String URL = "http://192.168.0.174:8080/getadmiteligiblecustomerlist";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_customer_admit);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Approved Customers");
            //toolbar.setNavigationIcon(R.drawable.common_google_signin_btn_icon_dark);
            setSupportActionBar(toolbar);
            // toolbar fancy stuff
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            recyclerView = findViewById(R.id.recycler_view);
            contactList = new ArrayList<>();
            mAdapter = new CustomerAdapter(this, contactList, this);

            // white background notification bar
            whiteNotificationBar(recyclerView);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
            recyclerView.setAdapter(mAdapter);

            fetchCustomers();
        }

        /**
         * fetches json by making http calls
         */
        private void fetchCustomers() {
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(URL,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            if (response == null) {
                                Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            List<Customer> items = new Gson().fromJson(response.toString(), new TypeToken<List<Customer>>() {
                            }.getType());

                            // adding contacts to contacts list
                            contactList.clear();
                            contactList.addAll(items);

                            // refreshing recycler view
                            mAdapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error in getting json
                    Log.e(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);

            // Associate searchable configuration with the SearchView
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.action_search)
                    .getActionView();
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setMaxWidth(Integer.MAX_VALUE);

            // listening to search query text change
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // filter recycler view when query submitted
                    mAdapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    // filter recycler view when text is changed
                    mAdapter.getFilter().filter(query);
                    return false;
                }
            });
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_search) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onBackPressed() {
            // close search view on back button pressed
            if (!searchView.isIconified()) {
                searchView.setIconified(true);
                return;
            }
            super.onBackPressed();
        }

        private void whiteNotificationBar(View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int flags = view.getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                view.setSystemUiVisibility(flags);
                getWindow().setStatusBarColor(Color.WHITE);
            }
        }

        @Override
        public void onCustomerSelected(Customer customer) {
            Toast.makeText(getApplicationContext(), "Selected: " + customer.getName() + ", " + customer.getPhone(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CustomerAdmitListActivity.this, AdmitCustomer.class);
            intent.putExtra("name", customer.getName());
            intent.putExtra("email", customer.getEmail());
            intent.putExtra("phone_number", customer.getPhone());
            intent.putExtra("firebase_token", customer.getFirebase_token());
            startActivity(intent);
            finish();
        }

        @Override
        protected void onResume() {
            super.onResume();

            fetchCustomers();
            mAdapter.notifyDataSetChanged();
        }
    }
