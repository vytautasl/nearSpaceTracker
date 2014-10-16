package lt.nearspace.app.util;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class JSON {
    private static final String TAG = JSON.class.getSimpleName();
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ObjectMapper mapper;
    private static TypeFactory factory;

    private JSON() {
    }

    private static ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //mapper.setDateFormat(df);
            factory = mapper.getTypeFactory();
        }
        return mapper;
    }

    public static String fromObject(Object o) {
        try {
            return getMapper().writeValueAsString(o);
        } catch (IOException e) {
            Log.e(TAG, "IOException serializing to json", e);
        }
        return null;
    }

    public static <T> T toObject(InputStream is, Class<T> o) {
        try {
            return getMapper().readValue(is, o);
        } catch (IOException e) {
            Log.e(TAG, "IOException deserializing from json", e);
        }
        return null;
    }

    public static <T> T toObject(String json, Class<T> o) {
        try {
            return getMapper().readValue(json, o);
        } catch (IOException e) {
            Log.e(TAG, "IOException parsing from json", e);
        }
        return null;
    }

    public static <T> T toCollection(String json, Class<? extends Collection> list, Class types) {
        try {
            return getMapper().readValue(json, factory.constructCollectionType(list, types));
        } catch (IOException e) {
            Log.e(TAG, "IOException parsing from json", e);
        }
        return null;
    }

    public static <T> T toCollection(InputStream is, Class<? extends Collection> list, Class types) throws Exception {
        return getMapper().readValue(is, factory.constructCollectionType(list, types));
    }
}


