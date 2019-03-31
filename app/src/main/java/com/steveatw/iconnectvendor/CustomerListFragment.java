package com.steveatw.iconnectvendor;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class CustomerListFragment extends Fragment implements CustomerAdapter.CustomerAdapterListener {



    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Customer> customerList;
    private CustomerAdapter mAdapter;
    private SearchView searchView;
    Spinner spinner;

    // urls to fetch customer JSON json

    private String allCustomersURL;
    private String approvedCustomersURL;
    private String unapprovedCustomersURL;
    private OnFragmentInteractionListener mListener;

    public CustomerListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_list, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Customers");

        allCustomersURL = getResources().getString(R.string.allCustomersURL);
        approvedCustomersURL = getResources().getString(R.string.approvedCustomersURL);
        unapprovedCustomersURL = getResources().getString(R.string.unapprovedCustomersURL);

        recyclerView = view.findViewById(R.id.recycler_view);
        customerList = new ArrayList<>();
        mAdapter = new CustomerAdapter(getContext(), customerList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewCustomer newFragment = new NewCustomer();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment, "NewCustomer");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }


    private void fetchCustomers(String URL) {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getContext(), "Couldn't fetch the customers! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Customer> items = new Gson().fromJson(response.toString(), new TypeToken<List<Customer>>() {
                        }.getType());

                        // adding contacts to contacts list
                        customerList.clear();
                        customerList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if(menu!=null){menu.clear();}
        menuInflater.inflate(R.menu.menu_spinner, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
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

        spinner = (Spinner) menu.findItem(R.id.spinner).getActionView();

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.customer_types, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        fetchCustomers(approvedCustomersURL);
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        fetchCustomers(unapprovedCustomersURL);
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        fetchCustomers(allCustomersURL);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onCustomerSelected(Customer customer) {

        ApproveCustomerFragment newFragment = new ApproveCustomerFragment();

        Bundle arguments = new Bundle();
        arguments.putString("name", customer.getName());
        arguments.putString("email", customer.getEmail());
        arguments.putString("phone_number", customer.getPhone());
        arguments.putString("firebase_token", customer.getFirebase_token());;
        newFragment.setArguments(arguments);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, newFragment, "ApproveCustomerFragment");
        transaction.addToBackStack(null);
        transaction.commit();
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
        void onCustomerListFragmentInteraction(Uri uri);
    }

}
