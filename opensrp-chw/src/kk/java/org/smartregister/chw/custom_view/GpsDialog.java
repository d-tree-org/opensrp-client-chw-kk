package org.smartregister.chw.custom_view;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.vijay.jsonwizard.utils.PermissionUtils;
import com.vijay.jsonwizard.widgets.GpsFactory;

import org.smartregister.chw.R;

/**
 * Created by Kassim Sheghembe on 2023-08-22
 */

public class GpsDialog extends Dialog implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final double MIN_ACCURACY = 4d;
    private final View dataView;
    private final TextView latitudeTV, longitudeTV, altitudeTV, accuracyTV;
    private final Context context;
    private TextView dialogAccuracyTV;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    private GpsDialogCallback gpsDialogCallback;

    public GpsDialog(Context context, GpsDialogCallback gpsDialogCallback,View dataView, TextView latitudeTV, TextView longitudeTV, TextView altitudeTV, TextView accuracyTV) {
        super(context);
        this.context = context;
        this.dataView = dataView;
        this.latitudeTV = latitudeTV;
        this.longitudeTV = longitudeTV;
        this.altitudeTV = altitudeTV;
        this.accuracyTV = accuracyTV;
        this.gpsDialogCallback = gpsDialogCallback;
        init();
    }

    protected void init() {
        this.setContentView(R.layout.kk_gps_dialog);
        setTitle(R.string.loading_location);
        this.setCancelable(false);
        this.lastLocation = null;
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                disconnectGoogleApiClient();
            }
        });
        Button okButton = (Button) this.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndDismiss();
            }
        });
        Button cancelButton = (Button) this.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GpsDialog.this.dismiss();
            }
        });

        this.dialogAccuracyTV = (TextView) this.findViewById(R.id.accuracy);

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                initGoogleApiClient();
            }
        });
    }

    protected void saveAndDismiss() {
        updateLocationViews(lastLocation);
        if (gpsDialogCallback != null) {
            gpsDialogCallback.onLocationSelected(lastLocation);
        }
        GpsDialog.this.dismiss();
    }

    protected void initGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        googleApiClient.connect();
    }

    private void disconnectGoogleApiClient() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PermissionUtils.FINE_LOCATION_PERMISSION_REQUEST_CODE);
            GpsDialog.this.dismiss();
            return;
        }
        //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        //lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        locationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                lastLocation = locationResult.getLastLocation();

                if (lastLocation != null) {
                    dialogAccuracyTV.setText(String.format(context.getString(R.string.accuracy), String.valueOf(lastLocation.getAccuracy()) + " m"));
                }

                if (lastLocation != null && lastLocation.getAccuracy() <= MIN_ACCURACY) {
                    saveAndDismiss();
                }
            }

        }, null);
    }

    private void updateLocationViews(Location location) {
        if (location != null) {
            location.getProvider();

            // Format the text for the views
            String formattedAltitude = String.format("%.2f m", location.getAltitude());

            latitudeTV.setText(String.format(context.getString(R.string.latitude), location.getLatitude()));
            longitudeTV.setText(String.format(context.getString(R.string.longitude), location.getLongitude()));
            altitudeTV.setText(String.format(context.getString(R.string.altitude), formattedAltitude));
            accuracyTV.setText(String.format(context.getString(R.string.accuracy), location.getAccuracy() + " m"));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do nothing when the connection is suspended - This is bad and probably needs a review
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.dismiss();
        Toast.makeText(context, R.string.could_not_get_your_location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            dialogAccuracyTV.setText(String.format(context.getString(R.string.accuracy), String.valueOf(location.getAccuracy()) + " m"));
        }

        lastLocation = location;
        if (lastLocation != null && lastLocation.getAccuracy() <= MIN_ACCURACY) {
            saveAndDismiss();
        }
    }

    public View getDataView() {
        return dataView;
    }

    public TextView getLatitudeTV() {
        return latitudeTV;
    }

    public TextView getLongitudeTV() {
        return longitudeTV;
    }

    public TextView getAltitudeTV() {
        return altitudeTV;
    }

    public TextView getAccuracyTV() {
        return accuracyTV;
    }

    public TextView getDialogAccuracyTV() {
        return dialogAccuracyTV;
    }

    public void setDialogAccuracyTV(TextView dialogAccuracyTV) {
        this.dialogAccuracyTV = dialogAccuracyTV;
    }

    public interface GpsDialogCallback {
        void onLocationSelected(Location location);
    }

}

