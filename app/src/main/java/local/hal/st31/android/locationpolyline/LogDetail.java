package local.hal.st31.android.locationpolyline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LogDetail extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    Polyline polyline = null;
    GoogleMap gMap;

    List<Double> doublesLatitude = new ArrayList<>();
    List<Double> doublesLongitude = new ArrayList<>();

    private DatabaseHelper _helper;

    private static final String POST_URL = "";

    private long _idNo;

    private TextView _tvProcess;
    private TextView _tvResult;
    private boolean _success = false;

    TextView tvTitle,tvStart,tvGoal,tvComment;

    List<LatLng> latLngList = new ArrayList<>();
    List<Double> listLat = new ArrayList<>();
    List<Double> listLng = new ArrayList<>();
    String[] arrayLat;
    String[] arrayLng;
    int red = 0, green = 140, blue = 255;

    Button btDraw;

    LocationManager locationManager;
    LatLng latLng = null;

    String start,goal,title,comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_detail);

        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        tvStart = findViewById(R.id.tvStart);
        tvGoal = findViewById(R.id.tvGoal);
        tvComment = findViewById(R.id.tvComment);
        tvTitle = findViewById(R.id.tvTitle);
        _helper = new DatabaseHelper(getApplicationContext());

        Intent intent = getIntent();
        _idNo = intent.getLongExtra("idNo",0);
        SQLiteDatabase db = _helper.getWritableDatabase();
        LogData LogData = DataAccess.findByPK(db,_idNo);
        tvStart.setText(LogData.getStart());
        tvGoal.setText(LogData.getGoal());
        tvComment.setText(LogData.getComment());
        tvTitle.setText(LogData.getTitle());
        arrayLat = LogData.getLat();
        arrayLng = LogData.getLng();
        for (int i = 0; i < arrayLat.length; i++){
            listLat.add(Double.parseDouble(arrayLat[i]));
            listLng.add(Double.parseDouble(arrayLng[i]));
            latLng = new LatLng(Double.parseDouble(arrayLat[i]),Double.parseDouble(arrayLng[i]));
            latLngList.add(latLng);
        }
        System.out.println(latLng);
        System.out.println(latLngList);
        if (polyline != null) polyline.remove();

        System.out.println(latLngList);
    }

    private void locationStart() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));

        //        if (polyline != null) polyline.remove();
        //Create PolylineOptions
        PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngList).clickable(true);
        polyline = gMap.addPolyline(polylineOptions);
        //Set Polyline Color
        polyline.setColor(Color.rgb(red,green,blue));
        polyline.setWidth(7);
    }

}
