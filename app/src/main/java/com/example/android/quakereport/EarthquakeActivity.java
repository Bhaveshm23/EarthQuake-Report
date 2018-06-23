/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.AsyncTaskLoader;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.R.id.empty;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();


    //TextView is displayed when the list is empty
    private TextView mEmptyStateTextView;

    // Earthquake loader id

    private static final int  EARTHQUAKE_LOADER_ID=1;

            /*
        When we get to the onPostExecute() method, we need to update the ListView.
         The only way to update the contents of the list is to update the data set within the EarthquakeAdapter.
         To access and modify the instance of the EarthquakeAdapter, we need to make it a global variable in the EarthquakeActivity.

         */

    /**
     * Adapter for the list of earthquakes
     */
    private EarthquakeAdapter mAdapter;

    //URL for earthquake data from the USGS dataset
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);


        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView=(TextView)findViewById(R.id.noText);
        earthquakeListView.setEmptyView(mEmptyStateTextView);


        /*
        We can remove the code that called QueryUtils.extractEarthquakes() to get a list of earthquakes
        from the hardcoded response. Instead we are going to initialize the adapter with an empty list.

         */
        //create a new adapter that takes empty list as input

        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Find the current earthquake that was clicked
                Earthquake currentEarthquake = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);


            }
        });

               // Get a reference to the ConnectivityManager to check state of network connectivity
                        ConnectivityManager connMgr = (ConnectivityManager)
                                getSystemService(Context.CONNECTIVITY_SERVICE);

                        // Get details on the currently active default data network
                                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                       // If there is a network connection, fetch data
                                if (networkInfo != null && networkInfo.isConnected()) {
                       // Get a reference to the LoaderManager, in order to interact with loaders.
                                LoaderManager loaderManager = getLoaderManager();

                                // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                                        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                                              // because this activity implements the LoaderCallbacks interface).
                                    loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
                    } else {
                        // Otherwise, display error
                                // First, hide loading indicator so error message will be visible
                                        View loadingIndicator = findViewById(R.id.loading_spinner);
                        loadingIndicator.setVisibility(View.GONE);

                                // Update empty state with no connection error message
                                        mEmptyStateTextView.setText(R.string.no_internet_connection);
                    }
    }





    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }
    @Override

    public void onLoadFinished(Loader<List<Earthquake>> loader,List<Earthquake>earthquakes){

        //setting visibilty of progress bar to gone

      View loadingIndicator=(ProgressBar)findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_earthquakes);


        //Clear the adapter with previous earthquake data

        mAdapter.clear();
        //If there is valid list eathquakes then add them to adapters data set

        if(earthquakes!=null && !earthquakes.isEmpty()){
            mAdapter.addAll(earthquakes);
        }


    }

    @Override

    public void onLoaderReset(Loader<List<Earthquake>>loader)
    {
        //loader reset so we clear out existing data

        mAdapter.clear();
    }


    // for the menu

    @Override

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return  true;
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        if(id==R.id.action_settings){
            Intent settingsIntent =new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }
}
