package lt.nearspace.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by divonas on 14.6.21.
 */
@DatabaseTable(tableName = "Tracklog")
public class Tracklog {

    public static final String SYNCED_COLUMN_NAME = "synced";
    public static final String RECEIVED_TIME_COLUMN_NAME = "receivedTime";
    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true, unique = true)
    private Integer id;

    @DatabaseField
    private long sentTime;

    @DatabaseField(columnName = RECEIVED_TIME_COLUMN_NAME)
    private long receivedTime;

    @DatabaseField
    private long lng;

    @DatabaseField
    private long lat;

    @DatabaseField
    private long alt;

    @JsonIgnore
    @DatabaseField(columnName = SYNCED_COLUMN_NAME)
    private boolean synced;

    public Tracklog() {
    }

    public Tracklog(Integer id, long sentTime, long receivedTime, long lng, long lat, long alt) {
        this.id = id;
        this.sentTime = sentTime;
        this.receivedTime = receivedTime;
        this.lng = lng;
        this.lat = lat;
        this.alt = alt;
        this.synced = true;
    }

    public Tracklog(long sentTime, long lng, long lat, long alt) {
        this.sentTime = sentTime;
        this.lng = lng;
        this.lat = lat;
        this.alt = alt;
        this.synced = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public long getLng() {
        return lng;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getAlt() {
        return alt;
    }

    public void setAlt(long alt) {
        this.alt = alt;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
