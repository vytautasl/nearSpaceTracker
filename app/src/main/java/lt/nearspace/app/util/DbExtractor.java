package lt.nearspace.app.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import lt.nearspace.app.NearSpaceApplication;
import lt.nearspace.app.daoImpl.DatabaseHelper;

/**
 * Created by Tadas on 2014.04.15.
 */
public class DbExtractor {
    private static final String DB_NAME = DatabaseHelper.DATABASE_NAME;
    private static final String BACKUP_DB_PATH = DatabaseHelper.DATABASE_NAME;
    private static final String TAG = DbExtractor.class.getSimpleName();
    private static String dbPath;

    public static void writeToSD() throws IOException {
        File sd = Environment.getExternalStorageDirectory();
        Context context = NearSpaceApplication.getContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            dbPath = context.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator;
        }
        else {
            dbPath = context.getFilesDir().getPath() + context.getPackageName() + "/databases/";
        }

        if (sd.canWrite()) {
            Log.d(TAG, "sd.canWrite()");
            String currentDBPath = DB_NAME;
            File currentDB = new File(dbPath, currentDBPath);
            File backupDB = new File(sd, BACKUP_DB_PATH);

            if (currentDB.exists()) {
                Log.d(TAG, "currentDB.exists()");
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            } else {
                Log.d(TAG, "currentDB does not exist");
            }
        }
    }
}
