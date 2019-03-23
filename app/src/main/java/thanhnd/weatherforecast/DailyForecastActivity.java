package thanhnd.weatherforecast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class DailyForecastActivity extends AppCompatActivity {

    private static final String api = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
    private static final String key = "&units=metric&cnt=7&appid=53fbf527d52d4d773e828243b90c1f8e";
    ListView list_view;
    ArrayList<ForecastItem> forecast_item_arraylist;
    ForecastAdapter forecast_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_weather_forecast);

        Mapping();

        final Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        GetDailyForecastData(city);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent extended_info_intent = new Intent(DailyForecastActivity.this, ExtendedInfoActivity.class);
                extended_info_intent.putExtra("city", intent.getStringExtra("city"));
                extended_info_intent.putExtra("position", position);
                startActivity(extended_info_intent);
            }
        });
    }

    private void GetDailyForecastData(String data) {
        String url = api + data + key;
        RequestQueue requestQueue = Volley.newRequestQueue(DailyForecastActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // get list
                            JSONArray jsonArrayList = jsonObject.getJSONArray("list");

                            // get each day data
                            for (int i = 0; i < jsonArrayList.length(); i++) {
                                JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);

                                // get date
                                String dt = jsonObjectList.getString("dt");
                                Date d = new Date(Long.valueOf(dt) * 1000L);
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM--dd");
                                String date = simpleDateFormat.format(d);

                                // get temp
                                JSONObject jsonObjectTemp = jsonObjectList.getJSONObject("temp");
                                String max = jsonObjectTemp.getString("max");
                                String min = jsonObjectTemp.getString("min");
                                String max_temp = String.valueOf(Double.valueOf(max).intValue()) + "°C";
                                String min_temp = String.valueOf(Double.valueOf(min).intValue()) + "°C";

                                // get weather icon
                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                String icon_id = jsonObjectWeather.getString("icon");
                                String icon = StringIconConverter.getStrIcon(DailyForecastActivity.this, icon_id);

                                forecast_item_arraylist.add(new ForecastItem(date, max_temp, min_temp, icon));
                            }
                            forecast_adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // code
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void Mapping() {
        // mapping
        list_view = findViewById(R.id.list_view);
        forecast_item_arraylist = new ArrayList<>();
        forecast_adapter = new ForecastAdapter(DailyForecastActivity.this, forecast_item_arraylist);
        list_view.setAdapter(forecast_adapter);
    }
}
