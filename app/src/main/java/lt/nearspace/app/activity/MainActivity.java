package lt.nearspace.app.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lt.nearspace.app.NearSpaceApplication;
import lt.nearspace.app.R;
import lt.nearspace.app.dao.PictureDao;
import lt.nearspace.app.daoImpl.PictureDaoImpl;
import lt.nearspace.app.domain.Picture;
import lt.nearspace.app.service.LocationService;
import lt.nearspace.app.util.CameraPreview;
import lt.nearspace.app.util.DbExtractor;
import lt.nearspace.app.util.EmailSender;
import lt.nearspace.app.util.GPSListener;
import lt.nearspace.app.util.LocListener;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final long INTERVAL_PICTURE_TAKEN = 120000;
    private static final int THIRTY_SECONDS = 30;
    private static final int MILLISECONDS_IN_SECOND = 1000;
    private TextView output;
    private TextView gps;
    private TextView location;
    LocationManager mLocationManager;
    private ImageView mImageView;
    private SurfaceView mSurfaceView;
    private Handler uIHandler;
    private Handler cameraHandler;

    private PictureDao pictureDao = PictureDaoImpl.getDao();
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken");
            new SaveAndSendPicture(data).execute();
//            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//            if (pictureFile == null){
//                Log.d(TAG, "Error creating media file, check storage permissions: pictureFile == null");
//                       // e.getMessage());          nera prasmes deti e.get.. jei nera try catch.
//                return;
//            }
//
//            try {
//                FileOutputStream fos = new FileOutputStream(pictureFile);
//                fos.write(data);
//                fos.close();
//                Log.d(TAG, "onPictureTaken. File saved to: " + pictureFile.getAbsolutePath());
//                pictureDao.savePicture(new Picture(pictureFile.getAbsolutePath()));
//                new EmailSender(pictureFile.getAbsolutePath(), MainActivity.this).send();
//            } catch (FileNotFoundException e) {
//                Log.d(TAG, "File not found: " + e.getMessage());
//            } catch (IOException e) {
//                Log.d(TAG, "Error accessing file: " + e.getMessage());
//            }  catch (SQLException e) {
//                Log.d(TAG, "Error accessing picture path to database: " + e.getMessage());
//            }
        }
    };
    private Camera mCamera;
    private static boolean activityVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        output = (TextView) findViewById(R.id.output);
        gps = (TextView) findViewById(R.id.gps);
        location = (TextView) findViewById(R.id.location);
        mImageView = (ImageView) findViewById(R.id.image_view);
        //mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        //Log.d(TAG, "Picture taken = " + (new SpaceSnap(this)).takePicture());

        mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        GPSListener gpsListener = new GPSListener(mLocationManager, gps);
        mLocationManager.addGpsStatusListener(gpsListener);
        LocationListener locationListener = new LocListener(location);
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 30 * MILLISECONDS_IN_SECOND, 0.5f, locationListener);


        Location mLocation = LocationService.getLastBestLocation(this);
        if (mLocation != null) {
            String longitude = "Longitude: " + mLocation.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + mLocation.getLatitude();
            Log.v(TAG, latitude);
            String altitude = "Altitude: " + mLocation.getAltitude();
            location.setText(latitude + ", " + longitude + ", " + altitude);
        }


        //Log.d(TAG, "gps location = "+ mLocationManager.getGpsStatus());
        Log.d(TAG, "Application context = " + NearSpaceApplication.getContext());


        Intent intent = new Intent(this, LocationService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()
                , THIRTY_SECONDS * MILLISECONDS_IN_SECOND, pintent);

        //dispatchTakePictureIntent();

        /*AlarmManager alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        alarmManager.setInexactRepeating();*/

        //new GetTrackList().execute(10);


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        activityVisible = true;
        mCamera = getCameraInstance();
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> supportedSizes = params.getSupportedPictureSizes();
            for (Camera.Size size: supportedSizes) {
                Log.d(TAG, "Size width = " + size.width + ", height = " + size.height);
            }
            Camera.Size maxSize = supportedSizes.get(0);
            params.setPictureSize(maxSize.width, maxSize.height);
            mCamera.setParameters(params);
            // Create our Preview view and set it as the content of our activity.
            CameraPreview mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);

            new Thread(new CameraRunnable(mCamera, mPicture)).start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityVisible = false;
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_extract_db) {
            Log.d(TAG, "extracting db");
            try {
                DbExtractor.writeToSD();
            } catch (IOException e) {
                Log.e(TAG, "Extracting DB error.", e);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "NearSpace");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("NearSpace", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    class CameraTask extends AsyncTask<Handler, Void, Void> {
        Camera camera;
        Camera.PictureCallback callback;
        public CameraTask(Camera camera, Camera.PictureCallback callback) {
            this.camera = camera;
            this.callback = callback;
        }
        @Override
        protected Void doInBackground(Handler... handlers) {
            while (true) {
                try {
                    Thread.sleep(INTERVAL_PICTURE_TAKEN);
                    if (activityVisible) {
                        Log.d(TAG, "CameraTask. Post runnable");
                        handlers[0].post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "CameraTask. Take picture");
                                camera.startPreview();
                                camera.takePicture(null, null, callback);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e(TAG, "CameraTask. Exception while taking picture", e);
                }
            }
        }
    }

    class CameraRunnable implements Runnable {
        Camera camera;
        Camera.PictureCallback callback;
        public CameraRunnable(Camera camera, Camera.PictureCallback callback) {
            this.camera = camera;
            this.callback = callback;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(INTERVAL_PICTURE_TAKEN);
                    if (activityVisible) {
                        Log.d(TAG, "CameraTask. Take picture");
                        camera.startPreview();
                        camera.takePicture(null, null, callback);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e(TAG, "CameraTask. Exception while taking picture", e);
                }
            }
        }
    }

    class SaveAndSendPicture extends AsyncTask<Void, Void, Void> {
        private byte[] data;
        public SaveAndSendPicture(byte[] data) {
            this.data = data;
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "SaveAndSendPicture. doInBackground");
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: pictureFile == null");
                // e.getMessage());          nera prasmes deti e.get.. jei nera try catch.
                return null;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Log.d(TAG, "onPictureTaken. File saved to: " + pictureFile.getAbsolutePath());
                pictureDao.savePicture(new Picture(pictureFile.getAbsolutePath()));
                new EmailSender(pictureFile.getAbsolutePath(), MainActivity.this).send();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }  catch (SQLException e) {
                Log.d(TAG, "Error accessing picture path to database: " + e.getMessage());
            }
            return null;
        }
    }
}
