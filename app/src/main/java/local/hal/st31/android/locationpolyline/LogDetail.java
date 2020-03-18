package local.hal.st31.android.locationpolyline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogDetail extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

//    private static final String API_URL = "http://10.0.2.2:8000"; // https://developer.android.com/studio/run/emulator-networking.html
    public static final String PREFS_NAME = "MyApp_Settings"; // sharedPreference
    private static final String TOKEN_KEY = "jwt"; // sharedPreference内にJWTを保存するときに使用
    private static Context context; // sharedPreferenceにデータを格納するときに使用
    private  static final String METHOD = "POST";
    private List<Map<String, String>> _list;


    Polyline polyline = null;
    GoogleMap gMap;

    List<Double> doublesLatitude = new ArrayList<>();
    List<Double> doublesLongitude = new ArrayList<>();

    private DatabaseHelper _helper;

//    private static final String POST_URL = "http://10.0.2.2:8080/lp_api/";
    private static final String POST_URL = "http://3.112.113.58:8080/lp_api/";
//    private static final String POST_URL = "http://3.112.113.58/lp_api/";

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
    String postData="";
    Button btDraw;

    LocationManager locationManager;
    LatLng latLng = null;

    String btStart,btGoal,btTitle,btComment;

    public static Context getAppContext() {
        return LogDetail.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar,menu);

        return super.onCreateOptionsMenu(menu);
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



    private List<Map<String, String>> createList() {
        List<Map<String, String>> list = new ArrayList<>();
        btStart = (String) tvStart.getText();
        btGoal = (String) tvGoal.getText();
        btTitle = (String) tvTitle.getText();
        btComment = (String) tvComment.getText();
        JSONObject createArticleParam = new JSONObject();
        try {
            createArticleParam.put("title", btTitle);
            createArticleParam.put("start", btStart);
            createArticleParam.put("goal", btGoal);
            createArticleParam.put("lat", listLat);
            createArticleParam.put("lng", listLng);
            createArticleParam.put("comment", btComment);
        } catch (JSONException ex) {
            Log.e("Json構築失敗", ex.toString());
        }

        list.add(makeMap("POST", createArticleParam.toString()));
//        System.out.println(list.toString());
        return list;
    }

    public void onBtShareClick(View view) {

        _list = createList();
        String jsonList = _list.toString();
        Map<String, String> item = _list.get(0);
        String requestJson = item.get("json");
        for (int i = 0; i < _list.size(); i++){
//            System.out.println(_list.get(i));
            System.out.println(_list.size());
//            System.out.println(jsonList);
        }
//        System.out.println(requestJson);
//        new PostArticleReceiver().execute(API_URL, "POST", requestJson);
        TextView tvProcess = findViewById(R.id.tvProcess);
        TextView tvResult = findViewById(R.id.tvResult);
        PostAccess access = new PostAccess(tvProcess, tvResult);
        access.execute(POST_URL, METHOD,requestJson);
    }

    private Map<String, String> makeMap(String method, String jsonStr) {
        Map<String, String> map = new HashMap<>();
        map.put("method", method);
        map.put("json", jsonStr);
//        System.out.println(map);
        return map;
    }

    private class PostAccess extends AsyncTask<String, String, String> {
        /**
         * ログに記載する文字列
         */
        private static final String DEBUG_TAG = "PostAccess";
        /**
         * 各種メッセージを表示するための文字列作品
         */
        private TextView _tvProcess;
        /**
         * 通信結果をメッセージを表示するための文字列部品
         */
        private TextView _tvResult;
        /**
         * サーバーが通信したかどうかのフラグ；
         * 成功した場合はtrue 失敗 false
         */
        private boolean _success = false;

        /**
         * コンストラクタ
         *
         * @param tvProcess 各種メッセージを表示するために画面部品。
         * @param tvResult  通信結果メッセージを表示するための文字列部品
         */
        public PostAccess(TextView tvProcess, TextView tvResult) {
            _tvProcess = tvProcess;
            _tvResult = tvResult;
        }

        @Override
        protected String doInBackground(String... params) {

            String accessUrl = params[0];
            String accessMethod = params[1];
            String accessJson = params[2];
            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";
            System.out.println(accessJson);
            try {
                publishProgress(getString(R.string.msg_send_before));
                URL url = new URL(accessUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(accessJson.getBytes());
                os.flush();
                os.close();
                int status = con.getResponseCode();
                if (status < 200 && status >= 300) {
                    throw new IOException("ステータスコード：" + status);
                }
                publishProgress(getString(R.string.msg_send_after));
                is = con.getInputStream();

                result = is2String(is);

                _success = true;


            } catch (SocketTimeoutException ex) {
                publishProgress(getString(R.string.msg_err_timeout));
                Log.e(DEBUG_TAG, "URL変換失敗", ex);

            } catch (MalformedURLException ex) {
                publishProgress(getString(R.string.msg_err_send));
                Log.e(DEBUG_TAG, "URL変換失敗", ex);

            } catch (IOException ex) {
                publishProgress(getString(R.string.msg_err_send));
                Log.e(DEBUG_TAG, "通信失敗:", ex);

            } finally {
                if (con != null) {
                    con.disconnect();
                }
                try {
                    if (is != null) {

                        is.close();
                    }
                } catch (IOException ex) {
                    publishProgress(getString(R.string.msg_err_parse));
                    Log.e(DEBUG_TAG, "InputStream解析失敗：", ex);
                }


            }

            return result;
        }


        /**
         * InputStreamをオブジェクトに変換するメソッド
         *
         * @param is 変換対象のInputStream オブジェクト
         * @return 変換された文字列
         * @throws IOException 変換に失敗したときの発生。
         */
        private String is2String(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();

            char[] b = new char[1024];
            int line;
            while (0 <= (line = reader.read(b))) {
                sb.append(b, 0, line);

            }
            return sb.toString();
        }
    }

    private class PostArticleReceiver extends BaseInfoReceiver {
        private static final String DEBUG_TAG = "PostArticleReceiver";

        @Override
        protected String doInBackground(String... params) {
            String uri = params[0];
            String method = params[1];
            String jsonStr = params[2]; // リクエストで送るJSON
            System.out.println(jsonStr);
            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try {
                URL url = new URL(uri);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-Type", "application/json; utf-8"); // 追記
                con.setRequestProperty("Accept", "application/json");
                // Authorizationヘッダーにトークンを設定する
                // まず、SharedPreferenceからトークンを取り出す
                SharedPreferences sharedPref = LogDetail.getAppContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String token = sharedPref.getString(TOKEN_KEY, "");
                con.setRequestProperty("Authorization", "Bearer " + token);
                con.setRequestMethod(method);
                con.setDoOutput(true);
                // サーバーへ送るJSONをセットする
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(jsonStr);
                wr.flush();
                wr.close();

                con.connect();
                is = con.getInputStream();

                result = super.is2String(is);
            } catch (MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            } catch (IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
                    }
                }
            }
            return result;
        }
    }

    private class BaseInfoReceiver extends AsyncTask<String, Void, String> {
        private static final String DEBUG_TAG = "BaseInfoReceiver";

        @Override
        protected String doInBackground(String... params) {
            String uri = params[0];
            String method = params[1];

            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try {
                URL url = new URL(uri);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-Type", "application/json; utf-8"); // 追記
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod(method);
                con.connect();
                is = con.getInputStream();

                result = is2String(is);
            } catch (MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            } catch (IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            ApiResponseDialog dialog = new ApiResponseDialog();
            Bundle extras = new Bundle();
            extras.putString("response", result);
            dialog.setArguments(extras);
            FragmentManager manager = getSupportFragmentManager();
            dialog.show(manager, "ApiResponseDialog");
        }

        private String is2String(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            char[] b = new char[1024];
            int line;
            while (0 <= (line = reader.read(b))) {
                sb.append(b, 0, line);
            }
            return sb.toString();
        }
    }

}

