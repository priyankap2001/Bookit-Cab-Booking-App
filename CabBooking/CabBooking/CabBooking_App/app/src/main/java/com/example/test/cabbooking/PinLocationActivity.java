package com.example.test.cabbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PinLocationActivity extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener,
    ResultCallback<Status> {

        private GoogleMap map;
        private GoogleApiClient googleApiClient;
        private Location lastLocation;
        private MapFragment mapFragment;
        int i=0;
        Button btnSaveLocation;
        String Latitude,Longitude;
        String type;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pin_location);
            //textLat = (TextView) findViewById(R.id.lat);
            //textLong = (TextView) findViewById(R.id.lon);

            Intent intent = getIntent();
            type = intent.getStringExtra("type");

            btnSaveLocation = findViewById(R.id.btnSaveLocation);
            // initialize GoogleMaps
            initGMaps();

            // create GoogleApiClient
            createGoogleApi();

            btnSaveLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(PinLocationActivity.this,FindCabActivity.class);
//                    intent.putExtra("Latitude",Latitude);
//                    intent.putExtra("Longitude",Longitude);
//                    intent.putExtra("type",type);
                    //finish();
                    //startActivity(intent);



                    SharedPreferences.Editor editor = getSharedPreferences("location", MODE_PRIVATE).edit();
                    if (type.equals("source")){
                        editor.putString("SourceLatitude",Latitude);
                        editor.putString("SourceLongitude",Longitude);
                    }
                    else if (type.equals("dest")){
                        editor.putString("DestLatitude",Latitude);
                        editor.putString("DestLongitude",Longitude);
                    }
                    //editor.putString("type",type);
                    editor.apply();
                    finish();
                    startActivity(new Intent(PinLocationActivity.this,FindCabActivity.class));
                }
            });
        }



        // Create GoogleApiClient instance
        private void createGoogleApi() {
            //Log.d(TAG, "createGoogleApi()");
            if ( googleApiClient == null ) {
                googleApiClient = new GoogleApiClient.Builder( this )
                        .addConnectionCallbacks( this )
                        .addOnConnectionFailedListener( this )
                        .addApi( LocationServices.API )
                        .build();
            }
        }

        @Override
        protected void onStart() {
            super.onStart();

            // Call GoogleApiClient connection when starting the Activity
            googleApiClient.connect();
        }

        @Override
        protected void onStop() {
            super.onStop();

            // Disconnect GoogleApiClient when stopping Activity
            googleApiClient.disconnect();
        }


        private final int REQ_PERMISSION = 999;

        // Check for permission to access Location
        private boolean checkPermission() {
            //Log.d(TAG, "checkPermission()");
            // Ask for permission if it wasn't granted yet
            return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED );
        }

        // Asks for permission
        private void askPermission() {
            //Log.d(TAG, "askPermission()");
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    REQ_PERMISSION
            );
        }

        // Verify user's response of the permission requested
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            //Log.d(TAG, "onRequestPermissionsResult()");
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch ( requestCode ) {
                case REQ_PERMISSION: {
                    if ( grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                        // Permission granted
                        getLastKnownLocation();

                    } else {
                        // Permission denied
                        permissionsDenied();
                    }
                    break;
                }
            }
        }

        // App cannot work without the permissions
        private void permissionsDenied() {
            //Log.w(TAG, "permissionsDenied()");
            // TODO close app and warn user
        }

        // Initialize GoogleMaps
        private void initGMaps(){
            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        // Callback called when Map is ready
        @Override
        public void onMapReady(GoogleMap googleMap) {
            //Log.d(TAG, "onMapReady()");
            map = googleMap;
            map.setOnMapClickListener(this);
            map.setOnMarkerClickListener(this);

        }

        @Override
        public void onMapClick(LatLng latLng) {
            //Log.d(TAG, "onMapClick("+latLng +")");
            markerLocation(new LatLng(latLng.latitude, latLng.longitude));
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            //Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
            return false;
        }

        private LocationRequest locationRequest;
        // Defined in mili seconds.
        // This number in extremely low, and should be used only for debug
        private final int UPDATE_INTERVAL =  1000;
        private final int FASTEST_INTERVAL = 900;

        // Start location Updates
        private void startLocationUpdates(){
            //Log.i(TAG, "startLocationUpdates()");
            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_INTERVAL);

            if ( checkPermission() )
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

        @Override
        public void onLocationChanged(Location location) {
            //Log.d(TAG, "onLocationChanged ["+location+"]");
            lastLocation = location;
            writeActualLocation(location);
        }

        // GoogleApiClient.ConnectionCallbacks connected
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            //Log.i(TAG, "onConnected()");
            getLastKnownLocation();

        }

        // GoogleApiClient.ConnectionCallbacks suspended
        @Override
        public void onConnectionSuspended(int i) {
            //Log.w(TAG, "onConnectionSuspended()");
        }

        // GoogleApiClient.OnConnectionFailedListener fail
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            //Log.w(TAG, "onConnectionFailed()");
        }

        // Get last known location
        private void getLastKnownLocation() {
           // Log.d(TAG, "getLastKnownLocation()");
            if ( checkPermission() ) {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if ( lastLocation != null ) {

                    writeLastLocation();
                    startLocationUpdates();
                } else {
                    //Log.w(TAG, "No location retrieved yet");
                    startLocationUpdates();
                }
            }
            else askPermission();
        }

        private void writeActualLocation(Location location) {
//            textLat.setText( "Lat: " + location.getLatitude() );
//            textLong.setText( "Long: " + location.getLongitude() );
            //Toast.makeText(getApplicationContext(),String.valueOf(location.getLatitude()+" "+location.getLongitude()),Toast.LENGTH_LONG).show();
            i++;
            if (i < 2) {
                markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
            }

        }

        private void writeLastLocation() {
            writeActualLocation(lastLocation);
        }

        private Marker locationMarker;
        private void markerLocation(LatLng latLng) {

            String title = latLng.latitude + ", " + latLng.longitude;
            Latitude = String.valueOf(latLng.latitude);
            Longitude = String.valueOf(latLng.longitude);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            if ( map!=null ) {
                if ( locationMarker != null )
                    locationMarker.remove();
                locationMarker = map.addMarker(markerOptions);
                float zoom = 14f;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
                map.animateCamera(cameraUpdate);
            }
        }

    private Marker geoFenceMarker;
    private void markerForGeofence(LatLng latLng) {
        //Log.i(TAG, "markerForGeofence("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if ( map!=null ) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = map.addMarker(markerOptions);

        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        if ( status.isSuccess() ) {
        } else {
            // inform about fail
        }
    }
}
