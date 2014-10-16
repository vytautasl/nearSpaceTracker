package lt.nearspace.app.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;


public class RESTConnection {
    private HttpURLConnection mConnection;

    public RESTConnection(String uri) {
        mConnection = establishConnection(uri, RESTConfig.DEFAULT_METHOD, RESTConfig.DEFAULT_CONTENT_TYPE, RESTConfig.DEFAULT_ACCEPT);
    }

    public RESTConnection(String uri, String method) {
        mConnection = establishConnection(uri, method, RESTConfig.DEFAULT_CONTENT_TYPE, RESTConfig.DEFAULT_ACCEPT);
    }

    public RESTConnection(String uri, String method, String contentType) {
        mConnection = establishConnection(uri, method, contentType, RESTConfig.DEFAULT_ACCEPT);
    }

    public RESTConnection(String uri, String method, String contentType, String accept) {
        mConnection = establishConnection(uri, method, contentType, accept);
    }

    private HttpURLConnection establishConnection(String uri, String method, String contentType, String accept) {
        HttpURLConnection connection = null;
        try {
            if (uri.startsWith("https")) {
                connection = getHttpsConnection(uri);
            } else {
                connection = getHttpConnection(uri);
            }
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Accept", accept);
            //connection.setRequestProperty("Host", "lc");
            connection.setAllowUserInteraction(true);
            connection.setUseCaches(false);
            //connection.setDoOutput(true);
            connection.setDoInput(true);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public String getResponse() {
        try {
            return convertStreamToString(mConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection getHttpConnection(String uri) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(RESTConfig.DEFAULT_HOST + uri).openConnection();
        conn.setConnectTimeout(RESTConfig.REQUEST_TIMEOUT);
        conn.setReadTimeout(RESTConfig.READ_TIMEOUT);
        return conn;
    }

    public boolean setHttpConnectionParams(String request) {
        OutputStream os;
        try {
            os = mConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(request);
            writer.close();
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private HttpURLConnection getHttpsConnection(String uri) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, null, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(RESTConfig.DEFAULT_HOST + uri).openConnection();
        urlConnection.setConnectTimeout(RESTConfig.REQUEST_TIMEOUT);
        urlConnection.setReadTimeout(RESTConfig.READ_TIMEOUT);
        return urlConnection;
    }

    private String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
}
