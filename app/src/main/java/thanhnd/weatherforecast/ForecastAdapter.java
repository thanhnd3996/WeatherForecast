package thanhnd.weatherforecast;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ForecastAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ForecastItem> forecast_items_array_list;

    ForecastAdapter(Context context, ArrayList<ForecastItem> arrayList) {
        this.context = context;
        this.forecast_items_array_list = arrayList;
    }

    @Override
    public int getCount() {
        return forecast_items_array_list.size();
    }

    @Override
    public Object getItem(int position) {
        return forecast_items_array_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.forecast_item, null);

        ForecastItem forecast_item = forecast_items_array_list.get(position);

        TextView date_text_view = convertView.findViewById(R.id.date_text_view);
        date_text_view.setText(forecast_item.get_date());
        TextView min_temp_text_view = convertView.findViewById(R.id.min_temp_text_view);
        min_temp_text_view.setText(forecast_item.get_min_temp());
        TextView max_temp_text_view = convertView.findViewById(R.id.max_temp_text_view);
        max_temp_text_view.setText(forecast_item.get_max_temp());
        TextView weather_icon_text_view = convertView.findViewById(R.id.weather_icon_text_view);
        Typeface weatherFontIcon = Typeface.createFromAsset(context.getAssets(),
                "fonts/weathericons-regular-webfont.ttf");
        weather_icon_text_view.setTypeface(weatherFontIcon);
        weather_icon_text_view.setText(forecast_item.get_weather_icon());
        return convertView;
    }
}
