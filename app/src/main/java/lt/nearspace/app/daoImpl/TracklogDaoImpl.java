package lt.nearspace.app.daoImpl;

import android.content.SharedPreferences;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import lt.nearspace.app.NearSpaceApplication;
import lt.nearspace.app.dao.TracklogDao;
import lt.nearspace.app.domain.Tracklog;

/**
 * Created by divonas on 14.6.21.
 */
public class TracklogDaoImpl implements TracklogDao {
    private static final String TAG = TracklogDaoImpl.class.getSimpleName();
    private SharedPreferences mPreferences;

    private Dao<Tracklog, Integer> mDao;
    private TracklogDaoImpl() {
        Log.d(TAG, "Creating ArticleDao");
        try {
            mDao = new DatabaseHelper(NearSpaceApplication.getContext()).getDao(Tracklog.class);
        } catch (SQLException e) {
            Log.e("articleDaoImpl", "Error creating dao", e);
        }
    }

    private static TracklogDao mTracklogDao;
    public static TracklogDao getDao(/*Context context*/) {
        if(mTracklogDao == null) {
            //mDao = new DatabaseHelper(LibertyCapsApplication.getContext()).getDao(Article.class);
            mTracklogDao = new TracklogDaoImpl(/*context*/);
        }
        return mTracklogDao;
    }
    @Override
    public List<Tracklog> getAll() throws SQLException {
        return mDao.queryForAll();
    }

    @Override
    public Integer saveTracklog(Tracklog tracklog) throws SQLException {
        mDao.createOrUpdate(tracklog);
        return null;
    }

    @Override
    public Tracklog getTracklog(int id) throws SQLException {
        return mDao.queryForId(id);
    }

    @Override
    public List<Tracklog> getAllUnsynced() throws SQLException {
        QueryBuilder<Tracklog, Integer> queryBuilder = mDao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq(Tracklog.SYNCED_COLUMN_NAME, false);
        return queryBuilder.query();
    }

    @Override
    public Tracklog getLastTracklog() {
        QueryBuilder<Tracklog, Integer> queryBuilder = mDao.queryBuilder();
        queryBuilder.orderBy(Tracklog.RECEIVED_TIME_COLUMN_NAME, false);
        try {
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.d(TAG, "unable to retrieve getLastTracklog");
            return null;
        }
    }
}
