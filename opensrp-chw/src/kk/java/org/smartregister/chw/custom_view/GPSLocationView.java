package org.smartregister.chw.custom_view;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.chw.R;

/**
 * Created by Kassim Sheghembe on 2023-08-21
 */

public class GPSLocationView extends LinearLayout implements GpsDialog.GpsDialogCallback {

    GpsDialog gpsDialog;
    private Location location;

    public GPSLocationView(Context context) {
        this(context, null);
    }

    public GPSLocationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.gps_location_view, this, false);
        addView(view);

        Button button = view.findViewById(R.id.gps_location_button);
        TextView latitudeTextView = view.findViewById(R.id.latitude);
        TextView longitudeTextView = view.findViewById(R.id.longitude);
        TextView altitudeTextView = view.findViewById(R.id.altitude);
        TextView accuracyTextView = view.findViewById(R.id.accuracy);

        latitudeTextView.setText(String.format( context.getString(R.string.latitude), ""));
        longitudeTextView.setText(String.format( context.getString(R.string.longitude), ""));
        altitudeTextView.setText(String.format( context.getString(R.string.altitude), ""));
        accuracyTextView.setText(String.format( context.getString(R.string.accuracy), ""));

        gpsDialog = new GpsDialog(context, this, view, latitudeTextView, longitudeTextView, altitudeTextView, accuracyTextView);

        button.setOnClickListener(v -> gpsDialog.show());
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void onLocationSelected(Location location) {
        setLocation(location);
    }
}
