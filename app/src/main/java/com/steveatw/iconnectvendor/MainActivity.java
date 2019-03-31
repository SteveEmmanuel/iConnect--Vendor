package com.steveatw.iconnectvendor;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    CustomerListFragment.OnFragmentInteractionListener,
                    CustomerAdmittanceFragment.OnFragmentInteractionListener,
                    ApproveCustomerFragment.OnFragmentInteractionListener,
                    ManualAdmittanceFragment.OnFragmentInteractionListener,
                    NewCustomer.OnFragmentInteractionListener,
                    AdmitListFragment.OnFragmentInteractionListener{

    boolean doubleBackToExitPressedOnce = false;
    private static final String DEBUG_TAG = "iConnect-Vendor";

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }

    @Override
    public void onBackPressed() {
        Log.v(DEBUG_TAG, "backPressed"+ Integer.toString(getSupportFragmentManager().getBackStackEntryCount()));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Checking for fragment count on backstack
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                Log.v(DEBUG_TAG, "backPressed"+ "greater than 0");
                getSupportFragmentManager().popBackStackImmediate();
            } else if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
                return;
            }
        }

//        if(searchView != null){
//            if (!searchView.isIconified()) {
//                searchView.setIconified(true);
//                return;
//            }
//        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.customer_list) {
            CustomerListFragment newFragment = new CustomerListFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, newFragment, "CustomerListFragment");

            transaction.commit();
        }
        else if (id == R.id.admit_list) {
            AdmitListFragment newFragment = new AdmitListFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, newFragment, "AdmitListFragment");

            transaction.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCustomerListFragmentInteraction(Uri uri){

    }

    @Override
    public void onCustomerAdimttanceFragmentInteraction(Uri uri){

    }

    @Override
    public void onApproveCustomerFragmentInteraction(Uri uri){

    }

    @Override
    public void onManualAdmittanceFragmentInteraction(Uri uri){

    }

    @Override
    public void onNewCustomerFragmentInteraction(Uri uri){

    }

    @Override
    public void onAdmitListFragmentInteraction(Uri uri){

    }
}
