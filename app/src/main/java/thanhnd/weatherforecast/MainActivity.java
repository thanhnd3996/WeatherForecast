package thanhnd.weatherforecast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    public static final String api = "https://api.openweathermap.org/data/2.5/weather?";
    public static final String key = "&units=metric&appid=b7d637e499d5ba4e70839884eaadee16";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    ImageButton detect_location_img_btn;
    EditText search_edit_text;
    Button search_btn, daily_forecast_btn;
    TextView city_name_text_view;
    TextView country_code_text_view;
    TextView temperature_text_view;
    TextView description_text_view;
    TextView date_time_text_view;
    TextView wind_speed_text_view;
    TextView humidity_text_view;
    TextView pressure_text_view;
    TextView cloudiness_text_view;
    TextView weather_icon_text_view;
    TextView wind_icon_text_view;
    TextView humidity_icon_text_view;
    TextView pressure_icon_text_view;
    TextView cloudiness_icon_text_view;
    String icon_wind, icon_humidity, icon_pressure, icon_cloudiness;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);

        Mapping();

        // Show default city weather data
        String default_url = api + "q=London" + key;
        GetCurrentWeatherData(default_url);

        // detect current location and get weather data
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        detect_location_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCoordinates();
            }
        });

        // search city and get its weather data
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = search_edit_text.getText().toString();
                String url = api + "q=" + city + key;
                GetCurrentWeatherData(url);
            }
        });

        // extended daily forecast
        daily_forecast_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent daily_forecast_intent = new Intent(MainActivity.this, DailyForecastActivity.class);
                daily_forecast_intent.putExtra("city", search_edit_text.getText().toString());
                startActivity(daily_forecast_intent);
            }
        });
    }

    // get coordinates of current location and get weather data
    private void getCoordinates() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to access the feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_ACCESS_COARSE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String lat = String.valueOf(latitude);
                    String lon = String.valueOf(longitude);
                    String url = api + "lat=" + lat + "&lon=" + lon + key;
                    GetCurrentWeatherData(url);
                }
            });
        }
    }


    private void GetCurrentWeatherData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // get city name
                            String city = jsonObject.getString("name");
                            city_name_text_view.setText(city);
                            search_edit_text.setText(city);

                            // set country code
                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                            String country_code = jsonObjectSys.getString("country");
                            country_code_text_view.setText(country_code);

                            // get day
                            String date_time = jsonObject.getString("dt");
                            Long l = Long.valueOf(date_time);
                            Date date = new Date(l * 1000L);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, yyyy/MM/dd HH:mm:ss");
                            String day = "Last update: " + simpleDateFormat.format(date);
                            date_time_text_view.setText(day);

                            // get weather
                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            // set description
                            String description = jsonObjectWeather.getString("description");
                            description_text_view.setText(description);
                            // set weather icon
                            String icon_id = jsonObjectWeather.getString("icon");
                            String icon = StringIconConverter.getStrIcon(MainActivity.this, icon_id);
                            weather_icon_text_view.setText(icon);

                            // get main
                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            // set temperature
                            String temperature = jsonObjectMain.getString("temp");
                            String temp = String.valueOf(Double.valueOf(temperature).intValue()) + "°";
                            temperature_text_view.setText(temp);
                            // set humidity
                            String humidity = jsonObjectMain.getString("humidity");
                            String hum = "Humidity: " + humidity + " %";
                            humidity_text_view.setText(hum);
                            // set pressure
                            String pressure = jsonObjectMain.getString("pressure");
                            String press = "Pressure: " + pressure + " hPa";
                            pressure_text_view.setText(press);

                            // get wind
                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            //set wind speed
                            String wind_speed = jsonObjectWind.getString("speed");
                            String ws = "Wind: " + wind_speed + " m/s";
                            wind_speed_text_view.setText(ws);

                            // get cloudiness
                            JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
                            // set cloudiness
                            String cloudiness = jsonObjectClouds.getString("all");
                            String cloud = "Cloudiness: " + cloudiness + " %";
                            cloudiness_text_view.setText(cloud);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //code
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void Mapping() {
        // create typefaces from Asset
        Typeface weatherFontIcon = Typeface.createFromAsset(this.getAssets(),
                "fonts/weathericons-regular-webfont.ttf");
        Typeface robotoThin = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Thin.ttf");
        Typeface robotoLight = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Light.ttf");

        // create icon string
        icon_wind = getString(R.string.icon_wind);
        icon_humidity = getString(R.string.icon_humidity);
        icon_pressure = getString(R.string.icon_pressure);
        icon_cloudiness = getString(R.string.icon_cloudiness);

        // mapping text view
        detect_location_img_btn = findViewById(R.id.detect_location_img_btn);
        search_edit_text = findViewById(R.id.search_edit_text);
        search_btn = findViewById(R.id.search_btn);
        daily_forecast_btn = findViewById(R.id.daily_forecast_btn);
        city_name_text_view = findViewById(R.id.city_name_text_view);
        country_code_text_view = findViewById(R.id.country_code_text_view);
        temperature_text_view = findViewById(R.id.temperature_text_view);
        description_text_view = findViewById(R.id.description_text_view);
        date_time_text_view = findViewById(R.id.date_time_text_view);
        wind_speed_text_view = findViewById(R.id.wind_speed_text_view);
        humidity_text_view = findViewById(R.id.humidity_text_view);
        pressure_text_view = findViewById(R.id.pressure_text_view);
        cloudiness_text_view = findViewById(R.id.cloudiness_text_view);

        // mapping icon text view
        weather_icon_text_view = findViewById(R.id.weather_icon_text_view);
        pressure_icon_text_view = findViewById(R.id.pressure_icon_text_view);
        humidity_icon_text_view = findViewById(R.id.humidity_icon_text_view);
        wind_icon_text_view = findViewById(R.id.wind_icon_text_view);
        cloudiness_icon_text_view = findViewById(R.id.cloudiness_icon_text_view);

        // set type face for text view
        temperature_text_view.setTypeface(robotoThin);
        wind_speed_text_view.setTypeface(robotoLight);
        humidity_text_view.setTypeface(robotoLight);
        pressure_text_view.setTypeface(robotoLight);
        cloudiness_text_view.setTypeface(robotoLight);

        // set type face for icon text view
        weather_icon_text_view.setTypeface(weatherFontIcon);
        wind_icon_text_view.setTypeface(weatherFontIcon);
        humidity_icon_text_view.setTypeface(weatherFontIcon);
        pressure_icon_text_view.setTypeface(weatherFontIcon);
        cloudiness_icon_text_view.setTypeface(weatherFontIcon);
        wind_icon_text_view.setText(icon_wind);
        humidity_icon_text_view.setText(icon_humidity);
        pressure_icon_text_view.setText(icon_pressure);
        cloudiness_icon_text_view.setText(icon_cloudiness);
    }
}