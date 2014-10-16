package lt.nearspace.app.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;

import lt.nearspace.app.dao.TracklogDao;
import lt.nearspace.app.daoImpl.TracklogDaoImpl;
import lt.nearspace.app.domain.Tracklog;
import lt.nearspace.app.receiver.ConnectivityChangeReceiver;

public class PictureService extends Service {
    private static final String TAG = PictureService.class.getSimpleName();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    TracklogDao tracklogDao = TracklogDaoImpl.getDao();

    public PictureService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Toast.makeText(this, "PictureServiceClass.onCreate()", Toast.LENGTH_LONG).show();
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
        new PostPictureTask().execute();
    }


    class PostPictureTask extends AsyncTask<Location, Void, Void>{

        @Override
        protected Void doInBackground(Location... params) {
            try {
                boolean sent = false;
                if (ConnectivityChangeReceiver.isConnected(getApplicationContext()))                {
                }

                if (!sent) {
                    tracklogDao.saveTracklog(new Tracklog());
                    Log.d(TAG, "Post failed. Tracklog saved");
                } else {
                    Log.d(TAG, "Post successful");
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error saving Tracklog", e);
            }

            return null;
        }
    }

}
