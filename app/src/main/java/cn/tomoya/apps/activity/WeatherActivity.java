package cn.tomoya.apps.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.tomoya.apps.R;
import cn.tomoya.apps.model.weather.CityData;
import cn.tomoya.apps.model.weather.Value;
import cn.tomoya.apps.model.weather.Weather;
import cn.tomoya.apps.model.weather.Weather3HoursDetailsInfo;
import cn.tomoya.apps.model.weather.WeekWeather;
import cn.tomoya.apps.util.DateUtil;
import cn.tomoya.apps.util.GsonUtil;
import cn.tomoya.apps.util.SharedPreferencesUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tomoya on 4/5/17.
 */

public class WeatherActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
    Toolbar.OnMenuItemClickListener {

  private Toolbar toolbar;
  private TextView weather;
  private TextView city;
  private TextView temp;
  private TextView sendibleTemp;
  private TextView pm25;
  private TextView quality;
  private TextView ziwaixian;
  private TextView wD;
  private TableLayout threeHourWeatherTableLayout;
  private TableLayout weathersTableLayout;
  private TextView refreshDate;
  private SwipeRefreshLayout refreshLayout;
  private String location;
  private boolean init;

  private List<CityData> citys = new ArrayList<>();
  private List<String> cityNames = new ArrayList<>();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_weather);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(getString(R.string.weather));
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        WeatherActivity.this.finish();
      }
    });
    toolbar.inflateMenu(R.menu.add);
    toolbar.setOnMenuItemClickListener(this);

    weather = (TextView) findViewById(R.id.weather);
    city = (TextView) findViewById(R.id.city);
    temp = (TextView) findViewById(R.id.temp);
    sendibleTemp = (TextView) findViewById(R.id.sendibleTemp);
    pm25 = (TextView) findViewById(R.id.pm25);
    quality = (TextView) findViewById(R.id.quality);
    ziwaixian = (TextView) findViewById(R.id.ziwaixian);
    wD = (TextView) findViewById(R.id.wD);
    refreshDate = (TextView) findViewById(R.id.refreshDate);
    threeHourWeatherTableLayout = (TableLayout) findViewById(R.id.threeHourWeatherTableLayout);
    weathersTableLayout = (TableLayout) findViewById(R.id.weathersTableLayout);

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
    refreshLayout.setOnRefreshListener(this);
    refreshLayout.setRefreshing(true);
    initData();
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_add:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.type_location);
        View view = LayoutInflater.from(this).inflate(R.layout.alert_choose_city, null);
        final AutoCompleteTextView cityNameTv = (AutoCompleteTextView) view.findViewById(R.id.cityName);
        cityNameTv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getCityName()));
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            String cityName = cityNameTv.getText().toString();
            if (getCityName().contains(cityName)) {
              SharedPreferencesUtil.saveLocation(cityName, WeatherActivity.this);
              refreshLayout.setRefreshing(true);
              initData();
            } else {
              Toast.makeText(WeatherActivity.this, R.string.msg_error_location, Toast.LENGTH_SHORT).show();
            }
          }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            //TODO
          }
        });
        builder.create().show();
        return true;
      default:
        return false;
    }
  }

  private void initData() {
    location = SharedPreferencesUtil.getLocation(this);
    if (location == null || "".equals(location)) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          refreshLayout.setRefreshing(false);
        }
      });
    } else {
      location = location.trim().split(",")[1];
      Request request = new Request.Builder().url("http://aider.meizu.com/app/weather/listWeather?cityIds=" + location).build();
      OkHttpClient client = new OkHttpClient();
      Call call = client.newCall(request);
      call.enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
          String body = response.body().string();
          Weather weatherInfo = GsonUtil.getInstance().fromJson(body, Weather.class);
          final Value value = weatherInfo.getValue().get(0);
          if (value.getCityid() > 0) {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                weather.setText(value.getRealtime().getWeather());
                city.setText(value.getCity());
                temp.setText(value.getRealtime().getTemp() + getString(R.string.temp));
                sendibleTemp.setText(getString(R.string.sendible_temp) + value.getRealtime().getSendibleTemp() + getString(R.string.temp));
                pm25.setText(getString(R.string.pm25) + value.getPm25().getPm25());
                quality.setText(getString(R.string.quality) + value.getPm25().getQuality());
                ziwaixian.setText(getString(R.string.ziwaixian) + value.getRealtime().getZiwaixian());
                wD.setText(value.getRealtime().getwD() + " " + value.getRealtime().getwS());
                refreshDate.setText(DateUtil.formatDateTime());

                if (init) {
                  threeHourWeatherTableLayout.removeViews(1, 9);
                  weathersTableLayout.removeViews(1, 5);
                }
                int i = 0;
                for (Weather3HoursDetailsInfo weather3HoursDetailsInfo : value.getWeatherDetailsInfo().getWeather3HoursDetailsInfos()) {
                  i++;
                  TextView tv1 = new TextView(getApplicationContext());
                  tv1.setTextColor(Color.WHITE);
                  tv1.setText(weather3HoursDetailsInfo.getStartTime().substring(10, 16));
                  TextView tv2 = new TextView(getApplicationContext());
                  tv2.setTextColor(Color.WHITE);
                  tv2.setText(weather3HoursDetailsInfo.getWeather());
                  TextView tv3 = new TextView(getApplicationContext());
                  tv3.setTextColor(Color.WHITE);
                  tv3.setText(weather3HoursDetailsInfo.getIsRainFall());
                  TextView tv4 = new TextView(getApplicationContext());
                  tv4.setTextColor(Color.WHITE);
                  tv4.setText(weather3HoursDetailsInfo.getHighestTemperature() + getString(R.string.temp));
                  TextView tv5 = new TextView(getApplicationContext());
                  tv5.setTextColor(Color.WHITE);
                  tv5.setText(weather3HoursDetailsInfo.getLowerestTemperature() + getString(R.string.temp));
                  TextView tv6 = new TextView(getApplicationContext());
                  tv6.setTextColor(Color.WHITE);
                  tv6.setText(weather3HoursDetailsInfo.getWd());
                  TextView tv7 = new TextView(getApplicationContext());
                  tv7.setTextColor(Color.WHITE);
                  tv7.setText(weather3HoursDetailsInfo.getWs());
                  TableRow tableRow1 = new TableRow(getApplicationContext());
                  tableRow1.addView(tv1, 0);
                  tableRow1.addView(tv2, 1);
                  tableRow1.addView(tv3, 2);
                  tableRow1.addView(tv4, 3);
                  tableRow1.addView(tv5, 4);
                  tableRow1.addView(tv6, 5);
                  tableRow1.addView(tv7, 6);
                  threeHourWeatherTableLayout.addView(tableRow1, i);
                }

                int j = 0;
                for (WeekWeather weekWeather : value.getWeathers()) {
                  j++;
                  TextView tv1 = new TextView(getApplicationContext());
                  tv1.setTextColor(Color.WHITE);
                  tv1.setText(weekWeather.getWeek());
                  TextView tv2 = new TextView(getApplicationContext());
                  tv2.setTextColor(Color.WHITE);
                  tv2.setText(weekWeather.getWeather());
                  TextView tv3 = new TextView(getApplicationContext());
                  tv3.setTextColor(Color.WHITE);
                  tv3.setText(weekWeather.getTemp_day_c());
                  TextView tv4 = new TextView(getApplicationContext());
                  tv4.setTextColor(Color.WHITE);
                  tv4.setText(weekWeather.getTemp_night_c());
                  TextView tv5 = new TextView(getApplicationContext());
                  tv5.setTextColor(Color.WHITE);
                  tv5.setText(weekWeather.getWd());
                  TextView tv6 = new TextView(getApplicationContext());
                  tv6.setTextColor(Color.WHITE);
                  tv6.setText(weekWeather.getWs());
                  TableRow tableRow2 = new TableRow(getApplicationContext());
                  tableRow2.addView(tv1, 0);
                  tableRow2.addView(tv2, 1);
                  tableRow2.addView(tv3, 2);
                  tableRow2.addView(tv4, 3);
                  tableRow2.addView(tv5, 4);
                  tableRow2.addView(tv6, 5);
                  weathersTableLayout.addView(tableRow2, j);
                }
                init = true;
                refreshLayout.setRefreshing(false);
              }
            });
          } else {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                refreshLayout.setRefreshing(false);
              }
            });
          }
        }
      });
    }
  }

  @Override
  public void onRefresh() {
    initData();
  }

  public List<String> getCityName() {
    if (cityNames.size() == 0) {
      for (CityData cityData : getCityInfo()) {
        cityNames.add(cityData.getD2() + ", " + cityData.getD1());
      }
    }
    return cityNames;
  }

  public List<CityData> getCityInfo() {
    if (citys.size() == 0) {
      try {
        InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open("china_city_id.json"));
        BufferedReader bufferedReader = new BufferedReader(
            inputStreamReader);
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
          stringBuilder.append(line);
        }
        bufferedReader.close();
        inputStreamReader.close();
        String json = stringBuilder.toString();
        citys = GsonUtil.getInstance().fromJson(json, new TypeToken<List<CityData>>() {
        }.getType());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return citys;
  }
}
