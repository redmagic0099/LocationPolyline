package local.hal.st31.android.locationpolyline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LogData {
    private long _id;
    private String _start;
    private  String _goal;
    private  String _comment;
    private String[] _lat;
    private String[] _lng;
    private String _title;

    public long getId() {
        return _id;
    }
    public void setId(long id) {
        _id = id;
    }
    public  String getStart() {return _start;}
    public void setStart(String start) {_start = start;}
    public  String getGoal() {return _goal;}
    public void setGoal(String goal) {_goal = goal;}
    public  String getComment() {return _comment;
    }
    public void setComment(String comment) {_comment = comment;}
    public String[] getLat(){return _lat;}
    public void setLat(String lat) {
        String lat2 = lat.substring(1);
        Pattern pattern = Pattern.compile("]");
        Matcher matcher = pattern.matcher(lat2);
        String finalLat0 = matcher.replaceAll("");
        Pattern pattern2 = Pattern.compile("\"");
        Matcher matcher2 = pattern2.matcher(finalLat0);
        String finalLat = matcher2.replaceAll("");
        _lat = finalLat.split(",",0);
    }
    public String[] getLng(){return _lng;}
    public void setLng(String lng) {
        String lng2 = lng.substring(1);
        Pattern pattern = Pattern.compile("]");
        Matcher matcher = pattern.matcher(lng2);
        String finalLng0 = matcher.replaceAll("");
        Pattern pattern2 = Pattern.compile("\"");
        Matcher matcher2 = pattern2.matcher(finalLng0);
        String finalLng = matcher2.replaceAll("");
        _lng = finalLng.split(",",0);
    }
    public  String getTitle() {return _title;}
    public void setTitle(String title) {_title = title;}
}
