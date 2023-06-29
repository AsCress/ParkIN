package com.example.parkin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class ParkingActivity extends AppCompatActivity implements OnMapReadyCallback {

    Double latitude;

    Double longitude;

    GoogleMap map;

    LatLng mPosition;

    ImageButton btnBottomSheet;

    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;

    String strUrl;

    HttpURLConnection connection;

    ArrayList<ParkingModel> parkingModels;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);

        recyclerView = (RecyclerView)findViewById(R.id.parkingsView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setLayoutManager(layoutManager);



        //  btnBottomSheet = (ImageButton)findViewById(R.id.arrow_button);

        //layoutBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);

        String address = getIntent().getExtras().getString("Address");
        latitude = getIntent().getExtras().getDouble("Latitude");
        longitude = getIntent().getExtras().getDouble("Longitude");

        TextView titleAddress = (TextView)findViewById(R.id.titleAddress);
        if (address != null) {
            titleAddress.setText(address);
        }

        else
        {
            titleAddress.setText("Marker Location");
        }
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);

        mPosition = new LatLng(latitude, longitude);

        /*sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        btnBottomSheet.setImageResource(R.drawable.baseline_keyboard_arrow_down_black_24dp);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        btnBottomSheet.setImageResource(R.drawable.baseline_keyboard_arrow_up_black_24dp);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        btnBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btnBottomSheet.setImageResource(R.drawable.baseline_keyboard_arrow_down_black_24dp);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    btnBottomSheet.setImageResource(R.drawable.baseline_keyboard_arrow_up_black_24dp);
                }
            }
        });
        */

        new GetParkings().execute();
    }

    private class GetParkings extends AsyncTask<Void, Void, String>
    {
        InputStream inputStream;

        String result;

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected String doInBackground(Void... voids) {

            String BASE_URL = "https://parking-v2.cit.cc.api.here.com";
            String TYPE_PARKINGS = "/parking";
            String FACILITIES = "/facilities";
            String OUTPUT_JSON = ".json";
            String APP_ID = "DemoCredForAutomotiveAPI";
            String APP_CODE = "JZlojTwKtPLbrQ9fEGznlA";
            String DISTANCE = "100000";

            strUrl = BASE_URL+TYPE_PARKINGS+FACILITIES+OUTPUT_JSON+"?app_id="+APP_ID+"&app_code="+APP_CODE+"&prox="+latitude+","+longitude+","+DISTANCE;

            URL url = null;
            try {
                url = new URL(strUrl);
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

            try {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            parkingModels = new ArrayList<ParkingModel>();
            parkingModels.clear();

            try{
                JSONObject jsonObject = new JSONObject(s);
                JSONObject jsonObj = jsonObject.getJSONObject("facilities");
                JSONArray jsonArray = jsonObj.getJSONArray("facility");

                for (int i = 0; i <jsonArray.length(); i++)
                {
                    ParkingModel parkingModel = new ParkingModel();
                    parkingModel.setPlaceName(jsonArray.getJSONObject(i).getString("name"));
                    parkingModel.setPlaceDistance(jsonArray.getJSONObject(i).getString("distance"));
                    parkingModels.add(parkingModel);
                }
                setAdapter();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setAdapter()
    {
        ParkingAdapter adapter = new ParkingAdapter(ParkingActivity.this, parkingModels);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        UiSettings settings = map.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setRotateGesturesEnabled(false);

        map.addMarker(new MarkerOptions().position(mPosition));
        map.moveCamera(CameraUpdateFactory.newLatLng(mPosition));

        map.animateCamera(CameraUpdateFactory.zoomTo(10.0f));

    }

}
