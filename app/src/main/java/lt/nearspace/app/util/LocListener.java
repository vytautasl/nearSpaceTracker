package lt.nearspace.app.util;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lt.nearspace.app.NearSpaceApplication;

/**
 * Created by divonas on 14.6.24.
 */
public class LocListener implements LocationListener {

    private static final String TAG = LocListener.class.getSimpleName();
    TextView locText;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LocListener(TextView textView){
        locText = textView;
    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.d(TAG, "onLocationChanged, loc = " + loc);
        Toast.makeText(
                NearSpaceApplication.getContext(),
                "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                        + loc.getLongitude(), Toast.LENGTH_LONG
        ).show();
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v(TAG, latitude);
        String longitude = "Longitude: " + loc.getLongitude();
        Log.v(TAG, longitude);

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        /*List<Address> addresses;
        try {
            Geocoder gcd = new Geocoder(NearSpaceApplication.getContext(), Locale.getDefault());
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            cityName = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        String s = latitude + "\n" + longitude + "\n\nMy Current City is: "
                + cityName + ", time = " + sdf.format(Calendar.getInstance().getTime());
        locText.setText(s);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
