package lt.nearspace.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by divonas on 14.6.21.
 */
public class NearSpaceApplication extends Application {
    public static final String TAG = NearSpaceApplication.class.getSimpleName();

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        context = getApplicationContext();
    }
}
