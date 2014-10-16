package lt.nearspace.app.util;

import android.content.Context;
import android.util.Log;

import lt.nearspace.app.dao.TracklogDao;
import lt.nearspace.app.daoImpl.TracklogDaoImpl;
import lt.nearspace.app.domain.Tracklog;

/**
 * Created by divonas on 14.8.16.
 */
public class EmailSender {
    private static final String RECIPIENT_EMAIL = "VLesciauskas@gmail.com";
//    private static final String RECIPIENT_EMAIL = "tadas.tabulevicius@gmail.com";
    String absolutePath;
    Context context;
    private String TAG = EmailSender.class.getSimpleName();

    public EmailSender(String absolutePath, Context context) {
        this.absolutePath = absolutePath;
        this.context = context;
    }

    public void send() {
        Log.d(TAG, "send");
        sendEmail();
        /*new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.d(TAG, "Async send");
                sendEmail();
                return null;
            }
        }.execute();*/
    }

    public void sendEmail() {
        TracklogDao tracklogDao = TracklogDaoImpl.getDao();
        Tracklog tracklog = tracklogDao.getLastTracklog();

        try {
            GMailSender sender = new GMailSender("nearspace2014@gmail.com", "space2014");
            sender.addAttachment(absolutePath, tracklog != null
                    ? "" + tracklog.getLat() + "_" + tracklog.getLng() + "_" + tracklog.getAlt()
                    : "attachment subject");
            sender.sendMail("This is Subject",
                    "This is Body",
                    "nearspace2014@gmail.com",
                    RECIPIENT_EMAIL);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }


}
