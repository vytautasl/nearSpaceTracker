package lt.nearspace.app.dao;

import java.sql.SQLException;
import java.util.List;

import lt.nearspace.app.domain.Tracklog;

/**
 * Created by divonas on 14.6.21.
 */
public interface TracklogDao {
    List<Tracklog> getAll() throws SQLException;
    Integer saveTracklog(Tracklog tracklog) throws SQLException;

    Tracklog getTracklog(int id) throws SQLException;

    List<Tracklog> getAllUnsynced() throws SQLException;

    Tracklog getLastTracklog();
}
