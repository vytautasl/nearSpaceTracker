package lt.nearspace.app.daoImpl;

import android.content.SharedPreferences;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import lt.nearspace.app.NearSpaceApplication;
import lt.nearspace.app.dao.PictureDao;
import lt.nearspace.app.domain.Picture;

/**
 * Created by divonas on 14.6.21.
 */
public class PictureDaoImpl implements PictureDao {
    private static final String TAG = PictureDaoImpl.class.getSimpleName();
    private SharedPreferences mPreferences;

    private Dao<Picture, Integer> mDao;
    private PictureDaoImpl() {
        Log.d(TAG, "Creating PictureDao");
        try {
            mDao = new DatabaseHelper(NearSpaceApplication.getContext()).getDao(Picture.class);
        } catch (SQLException e) {
            Log.e("PictureDaoImpl", "Error creating dao", e);
        }
    }

    private static PictureDao mPictureDao;
    public static PictureDao getDao(/*Context context*/) {
        if(mPictureDao == null) {
            //mDao = new DatabaseHelper(LibertyCapsApplication.getContext()).getDao(Article.class);
            mPictureDao = new PictureDaoImpl(/*context*/);
        }
        return mPictureDao;
    }
    @Override
    public List<Picture> getAll() throws SQLException {
        return mDao.queryForAll();
    }

    @Override
    public Integer savePicture(Picture picture) throws SQLException {
        mDao.createOrUpdate(picture);
        return null;
    }

    @Override
    public Picture getPicture(int id) throws SQLException {
        return mDao.queryForId(id);
    }

    @Override
    public List<Picture> getAllUnsynced() throws SQLException {
        QueryBuilder<Picture, Integer> queryBuilder = mDao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq(Picture.SYNCED_COLUMN_NAME, false);
        return queryBuilder.query();
    }
}
