package lt.nearspace.app.util;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.widget.TextView;

/**
 * Created by divonas on 14.10.8.
 */
public class GPSListener implements GpsStatus.Listener  {

    private static final String TAG = GPSListener.class.getSimpleName();
    private TextView gps;
    private LocationManager locationManager;

    public GPSListener() {
    }

    public GPSListener(LocationManager locationManager, TextView gps) {
        this.locationManager = locationManager;
        this.gps = gps;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        int satellites = 0;
        int satellitesInFix = 0;
        int timetofix = locationManager.getGpsStatus(null).getTimeToFirstFix();
        for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
            if(sat.usedInFix()) {
                satellitesInFix++;
            }
            satellites++;
        }
        gps.setText("Satelites fixed: " + satellitesInFix + "/" + String.valueOf(satellites));
    }
}
