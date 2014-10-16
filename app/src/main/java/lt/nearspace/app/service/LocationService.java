package lt.nearspace.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import lt.nearspace.app.dao.TracklogDao;
import lt.nearspace.app.daoImpl.TracklogDaoImpl;
import lt.nearspace.app.domain.Tracklog;
import lt.nearspace.app.network.RESTClient;
import lt.nearspace.app.receiver.ConnectivityChangeReceiver;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    private static final java.math.BigDecimal TEN_THOUSAND = new BigDecimal(10000);
    TracklogDao tracklogDao = TracklogDaoImpl.getDao();
    LocationManager mLocationManager;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG, "Service got created");
        Toast.makeText(this, "ServiceClass.onCreate()", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Location mLocation = getLastBestLocation(this);
        if (mLocation != null) {
            String longitude = "Longitude: " + mLocation.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + mLocation.getLatitude();
            Log.v(TAG, latitude);
            String altitude = "Altitude: " + mLocation.getAltitude();
            Toast.makeText(this, latitude + ", " + longitude + ", " + altitude, Toast.LENGTH_LONG).show();



            new PostTask().execute(mLocation);
        } else {
            Toast.makeText(this, "ServiceClass.onStart(). mLocation == null", Toast.LENGTH_LONG).show();
        }
    }

    public static Location getLastBestLocation(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        /*Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }*/
        return locationGPS;
    }

    class PostTask extends AsyncTask<Location, Void, Void>{

        @Override
        protected Void doInBackground(Location... params) {
            Location mLocation = params[0];
            BigDecimal latBigDec, lonBigDec, altBigDec;
            latBigDec = new BigDecimal(mLocation.getLatitude()).multiply(TEN_THOUSAND, new MathContext(0, RoundingMode.HALF_UP));
            lonBigDec = new BigDecimal(mLocation.getLongitude()).multiply(TEN_THOUSAND, new MathContext(0, RoundingMode.HALF_UP));
            altBigDec = new BigDecimal(mLocation.getAltitude()).setScale(0, BigDecimal.ROUND_HALF_UP);
            Log.d(TAG, "latBigDec = " + latBigDec + ", lonBigDec = " + lonBigDec + ", altBigDec = " + altBigDec);
            Tracklog tracklog = new Tracklog((new Date()).getTime(), lonBigDec.longValue()
                    , latBigDec.longValue(), altBigDec.longValue());
            try {
                boolean sent = false;
                if (ConnectivityChangeReceiver.isConnected(getApplicationContext()))                {
                    sent = RESTClient.postCoordinates(latBigDec.longValue(), lonBigDec.longValue(), altBigDec.longValue(), mLocation.getTime());
                    sendAllUnsynced();
                }

                if (!sent) {
                    tracklogDao.saveTracklog(tracklog);
                    Log.d(TAG, "Post failed. Tracklog saved");
                } else {
                    tracklog.setSynced(true);
                    tracklog.setSentTime((new Date()).getTime());
                    tracklogDao.saveTracklog(tracklog);
                    Log.d(TAG, "Post successful");
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error saving Tracklog", e);
            }

            return null;
        }
    }

    private void sendAllUnsynced() {
        List<Tracklog> tracklogList = null;
        try {
            tracklogList = tracklogDao.getAllUnsynced();
            Log.d(TAG, "sendAllUnsynced(). tracklogList = " + tracklogList + " tracklogList.size() = " + tracklogList.size());
            boolean sent;
            if (tracklogList != null && tracklogList.size() > 0) {
                for (Tracklog tracklog : tracklogList) {
                    sent = RESTClient.postCoordinates(tracklog.getLat(), tracklog.getLng(), tracklog.getAlt(), tracklog.getSentTime());
                    if (sent) {
                        tracklog.setSynced(true);
                        tracklogDao.saveTracklog(tracklog);
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error saving Tracklog", e);
        }
    }
}
