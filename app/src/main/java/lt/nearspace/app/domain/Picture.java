package lt.nearspace.app.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;

/**
 * Created by divonas on 14.8.11.
 */
@DatabaseTable(tableName = "Picture")
public class Picture {
    public static final String SYNCED_COLUMN_NAME = "synced";
    private static final boolean DEFAULT_SYNCED_VALUE = false;
    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true, unique = true)
    int id;

    @DatabaseField
    String path;

    @DatabaseField
    long time;

    @DatabaseField(columnName = SYNCED_COLUMN_NAME)
    private boolean synced;

    public Picture() {
    }

    public Picture(String path) {
        this.path = path;
        this.time = Calendar.getInstance().getTimeInMillis();
        this.synced = DEFAULT_SYNCED_VALUE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
