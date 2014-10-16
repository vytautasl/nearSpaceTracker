package lt.nearspace.app.network;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by divonas on 14.6.21.
 */
public class RESTClient {
    public static final String SERVER = "http://www.racelive.eu:8080/nearSpaceLive/";
    public static final String GET_TRACKLOGS = "api/track/get/limit/%d";
    public static final String POST_COORDINATES = "api/track/add/lat/%d/lng/%d/alt/%d/sent/%d";
    private static final String TAG = RESTClient.class.getSimpleName();


    public static String getTrackList(int limit) {
        return getResponse(SERVER + String.format(GET_TRACKLOGS, limit));
    }

    /*public static String getResponse(String uri) {
        RESTConnection connection = new RESTConnection(uri);
        String response = connection.getResponse();
        return response;
    }*/

    public static String getResponse(String uri) {
        HttpUriRequest request = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = client.execute(request);
            return new BasicResponseHandler().handleResponse(httpResponse);
        } catch (IOException e) {
            Log.e(TAG, "getting response", e);
        }
        return null;
    }

    /*public static void postCoordinates(int lat, int lon, int alt, int sent) throws IOException {
        URL url = new URL(SERVER + String.format(POST_COORDINATES, lat, lon, alt, sent));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");

        conn.connect();

        int responseCode = conn.getResponseCode();
        Log.d(TAG, "responseCode = " + responseCode);



        conn.disconnect();
    }*/

    public static boolean postCoordinates(long lat, long lon, long alt, long sent) {
        String URL = SERVER + String.format(POST_COORDINATES, lat, lon, alt, sent);
        Log.d(TAG, "postCoordinates " + lat + lon + alt + sent + " " + URL);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL);

        try {

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();
            if (199 < code && code < 300) {
                return true;
            }
        } catch (ClientProtocolException e) {
            Log.d(TAG, "ClientProtocolException ");
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException ");
            return false;
        }
        return false;
    }
}
