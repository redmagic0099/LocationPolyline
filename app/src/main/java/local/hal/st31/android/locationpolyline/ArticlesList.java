package local.hal.st31.android.locationpolyline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticlesList extends AppCompatActivity {

//    private static final String POST_URL = "http://10.0.2.2:8080/lp_api/";
    private static final String POST_URL = "http://3.112.113.58:8080/lp_api/";
//    private static final String POST_URL = "http://3.112.113.58/lp_api/";
    private List<Map<String, String>> arrayList = new ArrayList<>();
    private String[] list = {"id", "title", "start", "goal", "lat", "lng", "comment"};
    ListView _listView;
    SimpleAdapter adapter;
    String[] form = {"title", "comment" };

    int[] to = {android.R.id.text1, android.R.id.text2,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WeatherInfoReceiver weatherInfoReceiver = new WeatherInfoReceiver();
        weatherInfoReceiver.execute(POST_URL);
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
    protected void onResume() {
        super.onResume();
//        PostAccess access = new PostAccess();
//        access.execute(POST_URL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArrayList itemList = new ArrayList<String>();

            for (int i=0; i < list.length; i++){

                itemList.add(arrayList.get(position).get(list[i]));
            }
            System.out.println(itemList);
            Intent intent = new Intent(getApplicationContext(), ArticleDetail.class);

            intent.putStringArrayListExtra("ArrayList",itemList);

            startActivity(intent);
        }
    }

    private class WeatherInfoReceiver extends AsyncTask<String, Void, String> {
        /**
         * ログに記載するタグ用の文字列。
         */
        private static final String DEBUG_TAG = "WeatherInfoReceiver";

        @Override
        protected String doInBackground(String... params) {
            String urlStr = params[0];

            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try{
                URL url = new URL(urlStr);

                System.out.println(url);

                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                is = con.getInputStream();

                result = is2String(is);
                System.out.println(result);
            }
            catch (MalformedURLException ex) {
                Log.e(DEBUG_TAG, "URL変換失敗", ex);
            }
            catch (IOException ex) {
                Log.e(DEBUG_TAG, "通信失敗", ex);
            }
            finally {
                if (con != null) {
                    con.disconnect();
                }

                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException ex) {
                        Log.e(DEBUG_TAG, "InputStatment解放失敗", ex);
                    }
                }
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            String title = "";
            String text = "";
            String dateLabel = "";
            String telop = "";
            try {
                System.out.println(result);
//                JSONObject rootJSON = new JSONObject(result);
//                System.out.println(rootJSON);
                JSONArray jsonArray = new JSONArray(result);
                System.out.println(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject loop = jsonArray.getJSONObject(i);
                            Map<String, String> map = new HashMap<>();

                            for (int j = 0; j < list.length; j++) {
                                String columns = loop.getString(list[j]);
                                map.put(list[j], columns);
                            }
                            arrayList.add(map);
                        }
                    _listView = findViewById(R.id.lvSiteList);
                    adapter = new SimpleAdapter(getApplicationContext(), arrayList, android.R.layout.simple_list_item_2, form, to);

                    _listView.setAdapter(adapter);
                    _listView.setOnItemClickListener(new ListItemClickListener());
            }
            catch (JSONException ex) {
                Log.e(DEBUG_TAG, "JSON解析失敗", ex);
            }


        }

    }
    /**
     * InputStreamオブジェクトを文字列に変換するメソッド。文字コードはUTF-8。
     *
     * @param is 返還対象のInputStreamオブジェクト
     * @return 変換された文字列。
     * @throws IOException　変換に失敗した時に発生。
     */
    private String is2String(InputStream is) throws IOException{
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
