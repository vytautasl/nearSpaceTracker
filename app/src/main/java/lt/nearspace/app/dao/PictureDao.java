package lt.nearspace.app.dao;

import java.sql.SQLException;
import java.util.List;

import lt.nearspace.app.domain.Picture;

/**
 * Created by divonas on 14.6.21.
 */
public interface PictureDao {
    List<Picture> getAll() throws SQLException;

    Integer savePicture(Picture picture) throws SQLException;

    Picture getPicture(int id) throws SQLException;

    List<Picture> getAllUnsynced() throws SQLException;
}
