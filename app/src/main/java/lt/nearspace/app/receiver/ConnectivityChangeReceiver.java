package lt.nearspace.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.SQLException;
import java.util.List;

import lt.nearspace.app.dao.TracklogDao;
import lt.nearspace.app.daoImpl.TracklogDaoImpl;
import lt.nearspace.app.domain.Tracklog;
import lt.nearspace.app.network.RESTClient;

/**
 * Created by divonas on 14.7.5.
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {
    private static final String TAG = ConnectivityChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean connected = isConnected(context);
        if (connected) {
            Log.d(TAG, "connected");
            //sendUnsynced();
        } else {
            Log.i(TAG, "not connected");
        }
    }

    private void sendUnsynced() {
        TracklogDao tracklogDao = TracklogDaoImpl.getDao();
        List<Tracklog> tracklogList = null;
        Location mLocation;
        try {
            tracklogList = tracklogDao.getAllUnsynced();
        } catch (SQLException e) {
            Log.e(TAG, "Error getting unsynced tracklogs");
        }
        for (Tracklog tracklog: tracklogList) {
            new PostTask().execute(tracklog);
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();//getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
    }

    class PostTask extends AsyncTask<Tracklog, Void, Void> {

        @Override
        protected Void doInBackground(Tracklog... params) {
            Tracklog tracklog = params[0];

            try {
                boolean sent = RESTClient.postCoordinates(tracklog.getLat(), tracklog.getLng(), tracklog.getAlt(), tracklog.getSentTime());

                if (sent) {
                    tracklog.setSynced(true);
                    TracklogDao tracklogDao = TracklogDaoImpl.getDao();
                    tracklogDao.saveTracklog(tracklog);
                    Log.d(TAG, "Post successfull");
                } else {
                    Log.d(TAG, "Post failed");
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error saving Tracklog", e);
            }

            return null;
        }
    }
}
