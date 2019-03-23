package thanhnd.weatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ExtendedInfoActivity extends AppCompatActivity {

    private static final String api = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
    private static final String key = "&units=metric&cnt=7&appid=53fbf527d52d4d773e828243b90c1f8e";
    TextView description_text_view;
    TextView night_temp_text_view;
    TextView morning_temp_text_view;
    TextView day_temp_text_view;
    TextView evening_temp_text_view;
    TextView wind_text_view;
    TextView rain_text_view;
    TextView cloud_text_view;
    TextView humidity_text_view;
    TextView pressure_text_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_extended_info);

        Mapping();

        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        int position = intent.getIntExtra("position", 0);
        getExtendedInfoActivity(city, position);
    }

    private void getExtendedInfoActivity(String data, final int position) {
        String url = api + data + key;
        RequestQueue requestQueue = Volley.newRequestQueue(ExtendedInfoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // get list
                            JSONArray jsonArrayList = jsonObject.getJSONArray("list");

                            // get data with position
                            JSONObject jsonObjectList = jsonArrayList.getJSONObject(position);

                            // get description
                            JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String description = jsonObjectWeather.getString("description");
                            description_text_view.setText(description);

                            // get temp: min, max, eve, morn
                            JSONObject jsonObjectTemp = jsonObjectList.getJSONObject("temp");
                            String day = jsonObjectTemp.getString("day");
                            String night = jsonObjectTemp.getString("night");
                            String eve = jsonObjectTemp.getString("eve");
                            String morn = jsonObjectTemp.getString("morn");
                            String day_temp = String.valueOf(Double.valueOf(day).intValue()) + "°C";
                            String night_temp = String.valueOf(Double.valueOf(night).intValue()) + "°C";
                            String eve_temp = String.valueOf(Double.valueOf(eve).intValue()) + "°C";
                            String morn_temp = String.valueOf(Double.valueOf(morn).intValue()) + "°C";
                            day_temp_text_view.setText(day_temp);
                            night_temp_text_view.setText(night_temp);
                            evening_temp_text_view.setText(eve_temp);
                            morning_temp_text_view.setText(morn_temp);

                            // get pressure, humidity, wind, cloud, rain
                            try {
                                String rains = jsonObjectList.getString("rain");
                                String rain = "Rain: " + rains + " mm";
                                rain_text_view.setText(rain);
                            } catch (JSONException e) {
                                String rains = "0";
                                String rain = "Rain: " + rains + " mm";
                                rain_text_view.setText(rain);
                            }
                            String speed = jsonObjectList.getString("speed");
                            String wind = "Wind: " + speed + " m/s";
                            wind_text_view.setText(wind);
                            String clouds = jsonObjectList.getString("clouds");
                            String cloud = "Cloud: " + clouds + " %";
                            cloud_text_view.setText(cloud);
                            String pressure = jsonObjectList.getString("pressure");
                            String press = "Pressure: " + pressure + " hPa";
                            pressure_text_view.setText(press);
                            String humidity = jsonObjectList.getString("humidity");
                            String humid = "Humidity: " + humidity + " %";
                            humidity_text_view.setText(humid);

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
        description_text_view = findViewById(R.id.description_text_view);
        night_temp_text_view = findViewById(R.id.night_temp_text_view);
        morning_temp_text_view = findViewById(R.id.morning_temp_text_view);
        day_temp_text_view = findViewById(R.id.day_temp_text_view);
        evening_temp_text_view = findViewById(R.id.evening_temp_text_view);
        wind_text_view = findViewById(R.id.wind_text_view);
        rain_text_view = findViewById(R.id.rain_text_view);
        cloud_text_view = findViewById(R.id.cloud_text_view);
        humidity_text_view = findViewById(R.id.humidity_text_view);
        pressure_text_view = findViewById(R.id.pressure_text_view);

    }
}
