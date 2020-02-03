package local.hal.st31.android.locationpolyline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SaveLog<btJson> extends AppCompatActivity {
    private DatabaseHelper _helper;
    List<String> strLatitudeList = new ArrayList<>();
    List<String> strLongitudeList = new ArrayList<>();
    JSONArray jsonArrayLatitude;
    JSONArray jsonArrayLongitude;
    private Object btJson;
    String title,start,goal,comment;
    EditText etTitle,etStart,etGoal,etComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String strSize = intent.getStringExtra("size");
        int size = Integer.parseInt(strSize);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_log);

        etTitle = findViewById(R.id.etTitle);
        etStart = findViewById(R.id.etStart);
        etGoal = findViewById(R.id.etGoal);
        etComment = findViewById(R.id.etComment);

        String strLongitude;
        String strLatitude;
        for (int i = 0; i < size; i++){
            String key = String.valueOf(i);
            strLatitude = intent.getStringExtra("lat" + key);
            strLongitude = intent.getStringExtra("lng" + key);
            strLatitudeList.add(strLatitude);
            strLongitudeList.add(strLongitude);
        }
        btJson = findViewById(R.id.bt_json);
        jsonArrayLatitude  = new JSONArray(strLatitudeList);
        jsonArrayLongitude  = new JSONArray(strLongitudeList);
    }


    public void onJsonButtonClick(View view) {

        title = etTitle.getText().toString();
        start = etStart.getText().toString();
        goal = etGoal.getText().toString();
        comment = etComment.getText().toString();
        System.out.println(jsonArrayLatitude);
        System.out.println(jsonArrayLongitude);
        _helper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = _helper.getWritableDatabase();
        DataAccess.insert(db, start,goal,comment,jsonArrayLatitude,jsonArrayLongitude,title);
        finish();
    }
}
