package com.example.parkin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.media.audiofx.Equalizer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    private ListView drawerList;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;

    LatLng position;

    ActionBar actionBar;

    int currentPosition = 0;

    Location currentLoc;

    int PERMISSION_ID = 44;

    int reference;

    ProgressDialog progressDialog;

    TextView addressField;

    String mAddress;

    GoogleApiClient apiClient;

    HttpURLConnection connection;

    String urlStr;

    FloatingActionButton FAB;

    FloatingActionButton FAB1;

    ProgressBar progressBar;

    CardView cardView;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();

        getWindow().setEnterTransition(fade);

        getWindow().setExitTransition(fade);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String BASE_URL = "https://maps.googleapis.com/maps/api/geocode";
                String OUT_JSON = "/json";
                String LAT_LNG = "?latlng=" + currentLoc.getLatitude() +","+ currentLoc.getLongitude();
                String KEY = "&key="+ "AIzaSyAf1DuQjNy2Lylu3f2yxD6l7Cd-SGzzvBE";

                urlStr = BASE_URL+OUT_JSON+LAT_LNG+KEY;

                URL url = null;
                try {
                    url = new URL(urlStr);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    connection = (HttpURLConnection)url.openConnection();
                    connection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);

                try {
                    connection.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            }
        });


        reference = 0;

        buildClient();

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        FAB1 = (FloatingActionButton)findViewById(R.id.myLocationButton);
        cardView = (CardView)findViewById(R.id.addressCard);

        FAB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();

                if (currentLoc != null) {
                    mMap.clear();
                    position = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f), 500, null);

                    new GetAddress().execute();
                }
            }
        });

        addressField = (TextView)findViewById(R.id.addressField);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FAB = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ParkingActivity.class);
                intent.putExtra("Address", mAddress);
                intent.putExtra("Latitude", currentLoc.getLatitude());
                intent.putExtra("Longitude",currentLoc.getLongitude());
                startActivity(intent);
            }
        });

        actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        String[] titles = getResources().getStringArray(R.array.titles);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        navigationView = (NavigationView)findViewById(R.id.drawer);

        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        {
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view)
            {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);

                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, cardView, ViewCompat.getTransitionName(cardView));

                startActivity(intent);
            }
        });

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        mMap.setMyLocationEnabled(true);
        // mMap.setPadding(0,0,0,180);

        UiSettings settings = mMap.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setRotateGesturesEnabled(false);
        settings.setMyLocationButtonEnabled(false);

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (currentLoc != null) {
                    currentLoc.setLatitude(cameraPosition.target.latitude);
                    currentLoc.setLongitude(cameraPosition.target.longitude);
                    new GetAddress().execute();
                }
            }
        });

    }

    private boolean checkPermissions()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            return true;
        }
        else {
            return false;
        }
    }

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled()
    {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == PERMISSION_ID) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
            else
            {
                Toast.makeText(this, "This app can't work without permission to access your location.", Toast.LENGTH_SHORT).show();
                requestPermissions();
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation()
    {
        if(checkPermissions()) {
            if(isLocationEnabled()) {

                final LocationRequest request = new LocationRequest();

                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                request.setInterval(0);
                request.setNumUpdates(1000);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(request);

                PendingResult<LocationSettingsResult> results = LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());

                results.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                        final Status status = locationSettingsResult.getStatus();


                        switch (status.getStatusCode())
                        {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED: {
                                try {
                                    status.startResolutionForResult(MainActivity.this, 1);
                                } catch (IntentSender.SendIntentException e) {
                                    // e.printStackTrace();
                                }
                            }
                            break;
                            case LocationSettingsStatusCodes.SUCCESS: {

                                    LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, MainActivity.this);

                                    currentLoc = LocationServices.FusedLocationApi.getLastLocation(apiClient);

                            }
                            break;

                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            {
                                Toast.makeText(MainActivity.this, "Settings unavailable", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }
                });

            }
            else {
                Toast.makeText(this, "Please Turn On Location.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
        else {
            requestPermissions();
        }
    }


    private void buildClient() {

        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        apiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLoc = location;
        ImageView marker = (ImageView)findViewById(R.id.marker);

        if (currentLoc != null) {
            if (reference == 0) {
                position = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());

                progressBar.setVisibility(View.INVISIBLE);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f), 500, null);

                new GetAddress().execute();

                marker.setVisibility(View.VISIBLE);
            }
            reference++;
        }
    }

    private class GetAddress extends AsyncTask<Void, Void, String>
    {
        InputStream inputStream;

        String result;

        String resultAddress;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);

            FAB1.setVisibility(View.INVISIBLE);

            addressField.setText("Loading...");

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String temp;

                    while((temp = reader.readLine()) != null)
                    {
                        stringBuilder.append(temp);
                    }
                    result = stringBuilder.toString();
                }
                else
                {
                    result = "error";
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                if (jsonObject.getString("status").equalsIgnoreCase("OK")) {
                    resultAddress = jsonArray.getJSONObject(1).getString("formatted_address");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resultAddress != null) {

                resultAddress = mAddress;
                addressField.setText(mAddress);
                progressBar.setVisibility(View.INVISIBLE);
                FAB1.setVisibility(View.VISIBLE);

            } else {
                new GetLocationFromGeocoder().execute();
            }
        }
    }

    private class GetLocationFromGeocoder extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(currentLoc.getLatitude(), currentLoc.getLongitude(), 1);
                mAddress = addresses.get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mAddress;
        }


        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            s = mAddress;
            if (mAddress != null)
            {
                addressField.setText(mAddress);
            }
            else
            {
                addressField.setText("Your Location");
            }
            progressBar.setVisibility(View.INVISIBLE);
            FAB1.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getLastLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position)
    {
        currentPosition = position;

        switch(position)
        {
            case 1:
            {
                // do something
            }
            break;

            case 2:
            {
                // do something different
            }
            break;

            case 3:
            {
                // do something else
            }
            break;

            default:
            {

            }
            break;
        }

        drawerLayout.closeDrawer(drawerList);

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration)
    {
        super.onConfigurationChanged(configuration);
        drawerToggle.onConfigurationChanged(configuration);
    }

}

