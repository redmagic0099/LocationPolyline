package local.hal.st31.android.locationpolyline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    //Initialize Variable
    GoogleMap gMap;
//    SeekBar seekWidth,seekRed,seekGreen,seekBlue;
    Button btDraw,btClear,btShow;
    LocationManager locationManager;

    Polyline polyline = null;
    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    List<Double> doublesLatitude = new ArrayList<>();
    List<Double> doublesLongitude = new ArrayList<>();
    List<String> list = new ArrayList<>();
    String txtSave = "";
    int btSave = 0;

    int red = 0, green = 0, blue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        gMap.setMyLocationEnabled(true);
//        gMap.getUiSettings().setCompassEnabled(true);
//        gMap.getUiSettings().setMyLocationButtonEnabled(true);

        btDraw = findViewById(R.id.bt_draw);
        btClear = findViewById(R.id.bt_clear);
        btShow = findViewById(R.id.bt_show);

        //Initialize SupportMapFragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);

        if (btSave == 0){
            btDraw.setText("開始");
        }else if(btSave == 1){

        }

        btDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSave == 0) {
                    btDraw.setText("保存");
                    btSave++;
                }
                else if (btSave == 1){
                    for (int i = 0; i < latLngList.size(); i++){
                        list.add(latLngList.get(i).toString());
                    }
                    JSONObject jsonObject = new JSONObject();
                    JSONArray jsonArrayLatitude = new JSONArray(doublesLatitude);
                    JSONArray jsonArrayLongitude = new JSONArray(doublesLongitude);
//                System.out.println(jsonArray);
                    try {
                        //テスト
                        jsonObject.put("name", "田原宏宣");
                        jsonObject.put("start", "新石切駅");
                        jsonObject.put("goal", "本町駅");
                        jsonObject.put("comment", "すごくいい");
                        jsonObject.put("lat", jsonArrayLatitude);
                        jsonObject.put("lng", jsonArrayLongitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(jsonObject);
                    Intent intent = new Intent(getApplicationContext(), SaveLog.class);
                    int j = 0;
                    String str = "";
                    for(j = 0; j < doublesLatitude.size(); j++){
                        str = String.valueOf(j);
                        intent.putExtra("lat" + str, doublesLatitude.get(j).toString());
                        intent.putExtra("lng" + str, doublesLongitude.get(j).toString());
                    }
                    btSave = 0;
                    btDraw.setText("開始");
                    intent.putExtra("size", String.valueOf(j));
                    startActivity(intent);
                }
            }
        });
        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clear Polyline on Map
                if (polyline != null) polyline.remove();
                for (Marker marker : markerList) marker.remove();
                list.clear();
                latLngList.clear();
                markerList.clear();
                doublesLatitude.clear();
                doublesLongitude.clear();
            }
        });

        btShow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), LogList.class);
                startActivity(intent);
            }
        });
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    1000);
        }
        else{
            locationStart();

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 50, this);

        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.actionbar,menu);
//
//        return true;
//    }

    private void locationStart() {
        Log.d("debug","locationStart()");

        // LocationManager インスタンス生成
        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled");
        } else {
            // GPSを設定するように促す
            Intent settingsIntent =
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 50, this);

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[]permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {

            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");

                locationStart();

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Create MarkerOptions
//                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                //Create Marker
//                Marker marker = gMap.addMarker(markerOptions);
                //Add Latlng and Marker
//                latLngList.add(latLng);
//                markerList.add(marker);
//                System.out.println(latLngList);
//                System.out.println(markerList);
//                System.out.println(latLng);
//                System.out.println(marker);
//                System.out.println(markerOptions);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if(btSave == 1){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            doublesLongitude.add(longitude);
            doublesLatitude.add(latitude);
            LatLng latLng = null;
            latLng = new LatLng(latitude,longitude);
            System.out.println(latLng);
            latLngList.add(latLng);
            System.out.println(latLngList);
            // 緯度の表示
//            TextView textView1 = (TextView) findViewById(R.id.text_view1);
//            String str1 = "Latitude:"+location.getLatitude();
//            textView1.setText(str1);
//
//            // 経度の表示
//            TextView textView2 = (TextView) findViewById(R.id.text_view2);
//            String str2 = "Longitude:"+location.getLongitude();
//            textView2.setText(str2);

            if (polyline != null) polyline.remove();
            //Create PolylineOptions
            PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngList).clickable(true);
            polyline = gMap.addPolyline(polylineOptions);
            //Set Polyline Color
            polyline.setColor(Color.rgb(red,green,blue));
            polyline.setWidth(7);
            //setWidth();
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setCompassEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);

        }

//        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
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

}
