package local.hal.st31.android.locationpolyline;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "locationlog.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CREATE TABLE logs (");
        stringBuffer.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        stringBuffer.append("start TEXT,");
        stringBuffer.append("goal TEXT,");
        stringBuffer.append("comment TEXT,");
        stringBuffer.append("jsonLat JSON,");
        stringBuffer.append("jsonLng JSON,");
        stringBuffer.append("title TEXT");
        stringBuffer.append(");");

        String sql = stringBuffer.toString();
        database.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
