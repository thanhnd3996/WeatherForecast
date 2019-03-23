package thanhnd.weatherforecast;

class ForecastItem {
    private String date;
    private String max_temp;
    private String min_temp;
    private String weather_icon;

    ForecastItem(String date, String max_temp, String min_temp, String weather_icon) {
        this.date = date;
        this.max_temp = max_temp;
        this.min_temp = min_temp;
        this.weather_icon = weather_icon;
    }


    String get_date() {
        return date;
    }

    String get_max_temp() {
        return max_temp;
    }

    String get_min_temp() {
        return min_temp;
    }

    String get_weather_icon() {
        return weather_icon;
    }
}
