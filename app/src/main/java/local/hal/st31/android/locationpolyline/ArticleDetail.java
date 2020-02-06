package local.hal.st31.android.locationpolyline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleDetail extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    Polyline polyline = null;
    GoogleMap gMap;
    private long _idNo;
    private String[] _lat;
    private String[] _lng;

    private TextView _tvProcess;
    private TextView _tvResult;
    private boolean _success = false;

    TextView tvTitle,tvStart,tvGoal,tvComment;

    List<LatLng> latLngList = new ArrayList<>();
    List<Double> listLat = new ArrayList<>();
    List<Double> listLng = new ArrayList<>();
    LatLng latLng = null;
    String[] arrayLat;
    String[] arrayLng;
    int red = 0, green = 140, blue = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        tvStart = findViewById(R.id.tvStart);
        tvGoal = findViewById(R.id.tvGoal);
        tvComment = findViewById(R.id.tvComment);
        tvTitle = findViewById(R.id.tvTitle);


        Intent intent = getIntent();
        ArrayList arrayList;
        arrayList = intent.getStringArrayListExtra("ArrayList");
        System.out.println(arrayList.size());
        System.out.println(arrayList.get(4));
        String latStr = (String) arrayList.get(4);
        String lat = latStr.substring(1);
        Pattern patternLat = Pattern.compile("]");
        Matcher matcherLat = patternLat.matcher(lat);
        String finalLat0 = matcherLat.replaceAll("");
        _lat = finalLat0.split(",",0);
        System.out.println(arrayList.get(5));
        String lngStr = (String) arrayList.get(5);
        String lng = lngStr.substring(1);
        Pattern pattern = Pattern.compile("]");
        Matcher matcher = pattern.matcher(lng);
        String finalLng = matcher.replaceAll("");
        _lng = finalLng.split(",",0);
        for (int i = 0; i < _lat.length; i++){
            listLat.add(Double.parseDouble(_lat[i]));
            listLng.add(Double.parseDouble(_lng[i]));
            latLng = new LatLng(Double.parseDouble(_lat[i]),Double.parseDouble(_lng[i]));
            latLngList.add(latLng);
        }

        System.out.println(latLngList);

        tvStart.setText(arrayList.get(2).toString());
        tvGoal.setText(arrayList.get(3).toString());
        tvComment.setText(arrayList.get(6).toString());
        tvTitle.setText(arrayList.get(1).toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuBrowse:
                Intent intent = new Intent(getApplicationContext(), ArticlesList.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
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
}
