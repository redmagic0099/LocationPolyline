package local.hal.st31.android.locationpolyline;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataAccess {

    public static Cursor findAll(SQLiteDatabase db) {
        String sql = "SELECT * FROM logs";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public static long insert(SQLiteDatabase db, String start, String goal, String comment, JSONArray jsonArrayLatitude, JSONArray jsonArrayLongitude, String title) {
//        String jsonLat = "json('{\"lat\":"+jsonArrayLatitude+"}')";
//        String jsonLng = "json('{\"lng\":"+jsonArrayLongitude+"}')";
        String jsonLat = ""+jsonArrayLatitude;
        String jsonLng = ""+jsonArrayLongitude;
        System.out.println(jsonLat);
        System.out.println(jsonLng);
        System.out.println(comment);
        String sql = "INSERT INTO logs (start, goal, comment, jsonLat, jsonLng, title) VALUES (?,?,?,?,?,?)";

        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, start);
        stmt.bindString(2, goal);
        stmt.bindString(3, comment);
        stmt.bindString(4,jsonLat);
        stmt.bindString(5,jsonLng);
        stmt.bindString(6, title);
        System.out.println(sql);
        long result = stmt.executeInsert();
        return result;
    }

    public static LogData findByPK(SQLiteDatabase db, long id){

        String sql = "SELECT _id, start, goal, comment, jsonLat, jsonLng, title FROM logs WHERE _id = " + id;
        Cursor cursor = db.rawQuery(sql,null);
        LogData result = null;
        if(cursor.moveToFirst()) {
            int idxStart = cursor.getColumnIndex("start");
            int idxGoal = cursor.getColumnIndex("goal");
            int idxComment = cursor.getColumnIndex("comment");
            int idxLat = cursor.getColumnIndex("jsonLat");
            int idxLng = cursor.getColumnIndex("jsonLng");
            int idxTitle = cursor.getColumnIndex("title");


            String start = cursor.getString(idxStart);
            String goal = cursor.getString(idxGoal);
            String comment = cursor.getString(idxComment);
            String lat = cursor.getString(idxLat);
            String lng = cursor.getString(idxLng);
            String title = cursor.getString(idxTitle);

//            length = lat.length();
//            lat2 = lat.substring(1);
//            Pattern pattern = Pattern.compile(str1);
//            Matcher matcher = pattern.matcher(lat2);
//            finalLat = matcher.replaceAll("");

            result = new LogData();
            result.setStart(start);
            result.setGoal(goal);
            result.setComment(comment);
            result.setLat(lat);
            result.setLng(lng);
            result.setTitle(title);
        }
//        System.out.println(length);
//        System.out.println(finalLat);
        return result;
    }
}
