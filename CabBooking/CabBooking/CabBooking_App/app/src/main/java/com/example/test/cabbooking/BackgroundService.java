package com.example.test.cabbooking;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.example.test.cabbooking.model.ResponseMsg;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BackgroundService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Handler mHandler;
    public static final long INTERVAL = 15 * 1000;  // 15 secs

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;

    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;
    private final int REQ_PERMISSION = 999;

    double latitude;
    double longitude;

    ApiInterface apiInterface;
    Retrofit retrofit;
    String UserID;
    //int i=0;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            UserID = prefs.getString("UserID","");
            retrofit = ApiClient.getClient();
            apiInterface = retrofit.create(ApiInterface.class);
            createGoogleApi();
            syncData();
            // Repeat this runnable code block again every ... min
            mHandler.postDelayed(runnableService,INTERVAL);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the Handler object

        mHandler = new Handler();
        // Execute a runnable task as soon as possible
        mHandler.post(runnableService);

        return START_STICKY;
    }

    private synchronized void syncData() {
        //i++;
        //if (i >= 1) {
        //Toast.makeText(getApplicationContext(),String.valueOf(latitude),Toast.LENGTH_LONG).show();
            Call<ResponseMsg> call = apiInterface.updateLocation(String.valueOf(UserID),String.valueOf(latitude),
                    String.valueOf(longitude));
            call.enqueue(new Callback<ResponseMsg>() {
                @Override
                public void onResponse(Call<ResponseMsg> call, Response<ResponseMsg> response) {
                    if (response.isSuccessful()){
                        String message = response.body().getMessage();
                        //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        //if (message.trim().equalsIgnoreCase("Found"))
                            //createNotification("Alert","Covid Patient near by",BackgroundService.this,1);
                    }
                }

                @Override
                public void onFailure(Call<ResponseMsg> call, Throwable t) {
                    Toast.makeText(BackgroundService.this,t.toString(),Toast.LENGTH_LONG).show();
                }
            });
        //}
//            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notif);
//            contentView.setImageViewResource(R.id.imageView2, R.mipmap.ic_launcher);
//            contentView.setTextViewText(R.id.textView, "Custom notification");
//            contentView.setTextViewText(R.id.textView2, "This is a custom layout");
//
//            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//            String channelId = "Default";
//            NotificationCompat.Builder builder = new  NotificationCompat.Builder(getApplicationContext(), channelId)
//                    .setSmallIcon(R.drawable.icon)
//                    .setContent(contentView)
//                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND).setLargeIcon(BitmapFactory.decodeResource(getApplication().getResources(), R.mipmap.ic_launcher))
//                    .setAutoCancel(true).
//                            setContentIntent(pendingIntent)
//                    .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//            NotificationManager manager = (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);
//                manager.createNotificationChannel(channel);
//            }
//            manager.notify(0, builder.build());
            //showNotification("","");

        //}
    }


//    public static void createNotification(String title, String subTitle, Context context, int notificationsId) {
//
//        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notif);
//        contentView.setImageViewResource(R.id.image, R.drawable.icon);
//        contentView.setTextViewText(R.id.title, title);
//        contentView.setTextViewText(R.id.subTitle, subTitle);
//
//
//        Intent intent = new Intent(context, DashboardActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        String channelId = "Default";
//        NotificationCompat.Builder builder = new  NotificationCompat.Builder(context, channelId)
//                .setSmallIcon(R.drawable.icon)
//                .setContent(contentView)
//                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND).setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
//                .setAutoCancel(true).
//                        setContentIntent(pendingIntent)
//                .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);
//            manager.createNotificationChannel(channel);
//        }
//        manager.notify(0, builder.build());
//
//    }
//

    private void createGoogleApi() {
        //Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();
    }

    private boolean checkPermission() {
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;

        latitude = location.getLatitude();
        longitude=location.getLongitude();



        //Toast.makeText(getContext(),String.valueOf(latitude),Toast.LENGTH_LONG).show();
        //writeActualLocation(location);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.i(TAG, "onConnected()");
        getLastKnownLocation();
    }
    @Override
    public void onConnectionSuspended(int i) {
        //Log.w(TAG, "onConnectionSuspended()");
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Log.w(TAG, "onConnectionFailed()");
    }


    private void getLastKnownLocation() {
        //Log.d(TAG, "getLastKnownLocation()");
        if (checkPermission()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                //Log.i(TAG, "LasKnown location. " +                        "Long: " + lastLocation.getLongitude() +                        " | Lat: " + lastLocation.getLatitude());
                //writeLastLocation();
                startLocationUpdates();
            } else {
                //Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }
    }


    private void startLocationUpdates() {
        //Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (checkPermission())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

}
