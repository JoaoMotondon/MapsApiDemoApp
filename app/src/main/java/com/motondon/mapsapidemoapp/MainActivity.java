package com.motondon.mapsapidemoapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MainActivity.class.getSimpleName();

    class MyPair {

        String first;
        String second;

        public MyPair(String first, String second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return first;
        }
    }

    private GoogleMap googleMap;
    private Boolean mapReady = false;
    private LatLng mSelectedLatLng;
    private String mSelectedCityAndCountry;

    @BindView(R.id.spMapPlaces) Spinner spMapPlaces;

    @BindView(R.id.tvStatus) TextView tvStatus;
    @BindView(R.id.tvCurrentTarget) TextView tvCurrentTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        updateViews(false);

        List<MyPair> mapPlaces = new ArrayList<>();
        mapPlaces.add(new MyPair("Florianopolis, Brazil", "-27.593500, -48.558540"));
        mapPlaces.add(new MyPair("Paris, France", "48.856614,2.352222"));
        mapPlaces.add(new MyPair("Beijing, China", "39.904211,116.407395"));
        mapPlaces.add(new MyPair("London, UK", "51.507351,-0.127758"));
        mapPlaces.add(new MyPair("Johannesburg, South Africa", "-26.204103,28.047305"));
        mapPlaces.add(new MyPair("Jakarta, Indonesia", "-6.208763,106.845599"));
        mapPlaces.add(new MyPair("San Francisco, EUA", "37.773972, -122.431297"));
        mapPlaces.add(new MyPair("Tokyo, Japan", "35.709026,139.731992"));
        mapPlaces.add(new MyPair("Athens, Greece", "37.983810,23.727539"));
        mapPlaces.add(new MyPair("Barcelona, Spain", "41.3948975,2.173403"));
        mapPlaces.add(new MyPair("Melbourne, Australia", "-37.813628,144.963058"));
        mapPlaces.add(new MyPair("Guadalajara, Mexico", "20.659699,-103.349609"));
        mapPlaces.add(new MyPair("Munich, Germany", "48.135125,11.581981"));
        mapPlaces.add(new MyPair("Vienna, Austria", "48.208174,16.373819"));
        mapPlaces.add(new MyPair("Rome, Italy", "41.902783,12.496366"));

        ArrayAdapter<MyPair> mapPlacesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, mapPlaces);
        mapPlacesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMapPlaces.setAdapter(mapPlacesAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_map_type_normal:
                if (mapReady) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                return true;

            case R.id.menu_map_type_satellite:
                if (mapReady) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                return true;

            case R.id.menu_map_type_hybrid:
                if (mapReady) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                return true;

            case R.id.menu_map_style_day:
                // Sets map style to null will set default style
                googleMap.setMapStyle(null);
                return true;

            case R.id.menu_map_style_night:

                // Night style does not work with satellite nor hybrid map types. So force it to normal type prior to use night mode.
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Sets the night style via raw resource JSON.
                MapStyleOptions mapStyleDay = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
                googleMap.setMapStyle(mapStyleDay);
                return true;

            default:
                return true;
        }
    }

    @OnItemSelected(R.id.spMapPlaces)
    public void spinnerMapPlacesItemSelected(Spinner spinner, int position) {
        MyPair item = (MyPair) spinner.getAdapter().getItem(position);
        mSelectedCityAndCountry = item.first;
        String[] latLng = item.second.split(",");
        Double lat = new Double(latLng[0]);
        Double lng = new Double(latLng[1]);
        mSelectedLatLng = new LatLng(lat, lng);

        Log.d(TAG, "onBtnFlyToClicked() - Flying to: " + mSelectedCityAndCountry + " - lat/lng: " + mSelectedLatLng);
        navigateTo(mSelectedCityAndCountry, mSelectedLatLng);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapReady = true;
        googleMap = map;

        tvStatus.setText("Status: Flying...");
        navigateTo("Florianopolis, Brazil", new LatLng(-27.593500, -48.558540)); // These are from Florianopolis/Brazil

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnCameraIdleListener(() -> {
            LatLng target = googleMap.getCameraPosition().target;
            Log.d(TAG, "GoogleMap::setOnCameraIdleListener() - Target: " + target);

            tvStatus.setText("Status: Ready");

            updateViews(true);
        });

        googleMap.setOnCameraMoveStartedListener(i -> {
            Log.d(TAG, "GoogleMap::setOnCameraMoveStartedListener() - i: " + i);

            tvStatus.setText("Status: Flying...");

            updateViews(false);
        });

        googleMap.setOnCameraMoveListener(() -> {
            LatLng target = googleMap.getCameraPosition().target;
            // Log.d(TAG, "GoogleMap::setOnCameraMoveListener() - Target: " + target);

            tvCurrentTarget.setText(String.valueOf(target));
        });

        googleMap.setOnMapLongClickListener(latLng -> {
            Log.d(TAG, "GoogleMap::setOnMapLongClickListener()");
            showContextMenu(latLng);
        });

        googleMap.setOnInfoWindowClickListener(marker -> {
            Log.d(TAG, "GoogleMap::getInfoWindow() - You clicked on '" + marker.getTitle() + "' marker located at: " + marker.getTag());
        });

        googleMap.setOnCircleClickListener(circle -> {
            Log.d(TAG, "GoogleMap::getInfoWindow() - You clicked on a circle located at: " + circle.getCenter());

        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d(TAG, "GoogleMap::onMarkerDragStart() - Start dragging '" + marker.getTitle() + "' marker located at: " + marker.getTag());
                // Do nothing here. We will update marker when end dragging it.
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.d(TAG, "GoogleMap::onMarkerDragStart() - Moving '" + marker.getTitle() + "' marker at: " + marker.getTag());

                // While moving a marker, move maps also.
                //marker.setSnippet(String.valueOf(marker.getPosition().latitude));
                map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }

            /**
             * Optimized way to move marker:
             *
             * After adding marker to map. while change the location you just need to set position rather than remove marker and add again.
             * This will reduce the code from remove and add marker to direct set position and also reduce complexity.
             *
             * Hint from: http://stackoverflow.com/questions/16312754/move-marker-on-google-maps-api-2
             *
             * @param marker
             */
            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d(TAG, "GoogleMap::onMarkerDragStart() - End dragging '" + marker.getTitle() + "' marker. Adding it to: " + marker.getTag());
                marker.setPosition(marker.getPosition());
               // addMarker(marker.getPosition(), marker.getTitle());
            }
        });
        updateViews(true);
    }

    private void navigateTo(String cityAndCountry, LatLng latLng) {

        if (mapReady) {
            CameraPosition target = CameraPosition.builder()
                    .target(latLng)
                    .bearing(0) // Direction which the camera points at
                    .tilt(45)
                    .zoom(11)
                    .build();

            // This will give some animations when moving the camera
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 5000, new GoogleMap.CancelableCallback() {

                // This handles when camera finish moving
                @Override
                public void onFinish() {
                    addMarker(latLng, cityAndCountry, 0);

                    switch(cityAndCountry) {
                        case "Florianopolis, Brazil":
                            Toast.makeText(getApplicationContext(), "Hey. This is where I live. It is a nice place!", Toast.LENGTH_LONG).show();
                            break;
                        case "Athens, Greece":
                            Toast.makeText(getApplicationContext(), "You are on Athens. Such a wonderful place", Toast.LENGTH_LONG).show();
                            break;
                        case "Rome, Italy":
                            Toast.makeText(getApplicationContext(), "You are on Rome. Do not forget to visit the Colosseum", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }

    private void updateViews(Boolean state) {
        spMapPlaces.setEnabled(state);
    }

    public void showContextMenu(LatLng target) {
        Log.d(TAG, "showContextMenu()");

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogNewMaker = inflater.inflate(R.layout.context_menu, null);
        final TextView tvAddMarker = (TextView) dialogNewMaker.findViewById(R.id.tvAddMarker);
        final TextView tvAddCircle = (TextView) dialogNewMaker.findViewById(R.id.tvAddCircle);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogNewMaker);

        AlertDialog dialog = builder.create();

        tvAddMarker.setOnClickListener(view -> {
            dialog.cancel();
            showAddMarkerDialog(target);
        });

        tvAddCircle.setOnClickListener(view -> {
            dialog.cancel();
            showAddCircleDialog(target);
        });

        dialog.show();
    }

    public void showAddMarkerDialog(LatLng target) {
        Log.d(TAG, "showAddMarkerDialog()");

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogNewMaker = inflater.inflate(R.layout.dialog_new_marker, null);
        final EditText etMakerTitle = (EditText) dialogNewMaker.findViewById(R.id.etMarkerTitle);
        final Spinner spMarkerColor = (Spinner) dialogNewMaker.findViewById(R.id.spMarkerColor);

        // Allows user to choose a color for the marker.
        List<MyPair> markerColorList = new ArrayList<>();
        markerColorList.add(new MyPair("Red", "0.0F"));
        markerColorList.add(new MyPair("Orange", "30.0F"));
        markerColorList.add(new MyPair("Yellow", "60.0F"));
        markerColorList.add(new MyPair("Green", "120.0F"));
        markerColorList.add(new MyPair("Cyan", "180.0F"));
        markerColorList.add(new MyPair("Azure", "210.0F"));
        markerColorList.add(new MyPair("Blue", "240.0F"));
        markerColorList.add(new MyPair("Violet", "270.0F"));
        markerColorList.add(new MyPair("Magenta", "300.0F"));
        markerColorList.add(new MyPair("Rose", "330.0F"));

        ArrayAdapter<MyPair> markerColorAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, markerColorList);
        markerColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMarkerColor.setAdapter(markerColorAdapter);
        spMarkerColor.setSelection(0);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogNewMaker).setTitle("Create Marker");

        builder
            .setPositiveButton("Create", (dialog, id) -> {
                String markerName = etMakerTitle.getText().toString();
                MyPair seletecdMarkerColor = (MyPair) spMarkerColor.getSelectedItem();
                
                Log.d(TAG, "showAddMarkerDialog() - Creating a marker options. Title: " + markerName + ". Target: " + target);

                // Second attribute from MyPair contains the float code for the marker color
                addMarker(target, markerName, Float.parseFloat(seletecdMarkerColor.second));

            })
            .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showAddCircleDialog(LatLng target) {
        Log.d(TAG, "showAddCircleDialog()");

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogNewCircle = inflater.inflate(R.layout.dialog_new_circle, null);
        final Spinner spCircleRadiusValue = (Spinner) dialogNewCircle.findViewById(R.id.spCircleRadiusValue);

        // Populate our radius spinner with values from 1 to 5
        List<String> mapTypes = Arrays.asList("1Km", "2Km", "3Km", "4Km", "5Km");
        ArrayAdapter<String> mapTypesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, mapTypes);
        mapTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCircleRadiusValue.setAdapter(mapTypesAdapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogNewCircle).setTitle("Create Circle");

        builder
            .setPositiveButton("Create", (dialog, id) -> {
                Integer radius = spCircleRadiusValue.getSelectedItemPosition();

                Log.d(TAG, "showAddCircleDialog() - Creating a circle. Radius: " + radius + "Km. Target: " + target);

                googleMap.addCircle(new CircleOptions()
                    .center(target)
                    .radius(Double.valueOf(radius) * 1000)
                    .strokeColor(Color.BLUE)
                    .strokeWidth((float) 0.1)
                    .fillColor(Color.argb(64,0,0,255)));

            })
            .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addMarker(LatLng target, String markerName, float markerColor) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(target)
                .title(markerName)
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                .draggable(true);

        googleMap.addMarker(markerOptions);
    }
}
