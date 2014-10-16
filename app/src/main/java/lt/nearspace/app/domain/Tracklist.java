package lt.nearspace.app.domain;

import java.util.ArrayList;

/**
 * Created by divonas on 14.7.5.
 */
public class Tracklist {
    public Tracklist(){
    };
    ArrayList<Tracklog> tracklogList;

    public ArrayList<Tracklog> getTracklogList() {
        return tracklogList;
    }

    public void setTracklogList(ArrayList<Tracklog> tracklogList) {
        this.tracklogList = tracklogList;
    }
}
