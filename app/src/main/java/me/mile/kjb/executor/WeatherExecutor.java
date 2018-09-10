package me.mile.kjb.executor;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;

import java.util.Calendar;

import me.mile.kjb.utils.binding.Bind;
import me.mile.kjb.utils.binding.ViewBinder;
import me.mile.kjb.application.AppCache;

/**
 * 更新天气
 * Created by wcy on 2016/1/17.
 * <p>
 * 天气现象表
 * <p>
 * 晴
 * 多云
 * 阴
 * 阵雨
 * 雷阵雨
 * 雷阵雨并伴有冰雹
 * 雨夹雪
 * 小雨
 * 中雨
 * 大雨
 * 暴雨
 * 大暴雨
 * 特大暴雨
 * 阵雪
 * 小雪
 * 中雪
 * 大雪
 * 暴雪
 * 雾
 * 冻雨
 * 沙尘暴
 * 小雨-中雨
 * 中雨-大雨
 * 大雨-暴雨
 * 暴雨-大暴雨
 * 大暴雨-特大暴雨
 * 小雪-中雪
 * 中雪-大雪
 * 大雪-暴雪
 * 浮尘
 * 扬沙
 * 强沙尘暴
 * 飑
 * 龙卷风
 * 弱高吹雪
 * 轻霾
 * 霾
 */
public class WeatherExecutor implements IExecutor, AMapLocalWeatherListener {
    private static final String TAG = "WeatherExecutor";
    private Context mContext;
    @Bind(me.mile.kjb.R.id.ll_weather)
    private LinearLayout llWeather;
    @Bind(me.mile.kjb.R.id.iv_weather_icon)
    private ImageView ivIcon;
    @Bind(me.mile.kjb.R.id.tv_weather_temp)
    private TextView tvTemp;
    @Bind(me.mile.kjb.R.id.tv_weather_city)
    private TextView tvCity;
    @Bind(me.mile.kjb.R.id.tv_weather_wind)
    private TextView tvWind;

    public WeatherExecutor(Context context, View navigationHeader) {
        mContext = context.getApplicationContext();
        ViewBinder.bind(this, navigationHeader);
    }

    @Override
    public void execute() {
        AMapLocalWeatherLive aMapLocalWeatherLive = AppCache.get().getAMapLocalWeatherLive();
        if (aMapLocalWeatherLive != null) {
            updateView(aMapLocalWeatherLive);
            release();
        } else {
            LocationManagerProxy mLocationManagerProxy = LocationManagerProxy.getInstance(mContext);
            mLocationManagerProxy.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_LIVE, this);
        }
    }

    @Override
    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
        if (aMapLocalWeatherLive != null && aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
            AppCache.get().setAMapLocalWeatherLive(aMapLocalWeatherLive);
            updateView(aMapLocalWeatherLive);
        } else {
            Log.e(TAG, "获取天气预报失败");
        }

        release();
    }

    @Override
    public void onWeatherForecaseSearched(AMapLocalWeatherForecast aMapLocalWeatherForecast) {
    }

    private void updateView(AMapLocalWeatherLive aMapLocalWeatherLive) {
        llWeather.setVisibility(View.VISIBLE);
        ivIcon.setImageResource(getWeatherIcon(aMapLocalWeatherLive.getWeather()));
        tvTemp.setText(mContext.getString(me.mile.kjb.R.string.weather_temp, aMapLocalWeatherLive.getTemperature()));
        tvCity.setText(aMapLocalWeatherLive.getCity());
        tvWind.setText(mContext.getString(me.mile.kjb.R.string.weather_wind, aMapLocalWeatherLive.getWindDir(),
                aMapLocalWeatherLive.getWindPower(), aMapLocalWeatherLive.getHumidity()));
    }

    private int getWeatherIcon(String weather) {
        if (TextUtils.isEmpty(weather)) {
            return me.mile.kjb.R.drawable.ic_weather_sunny;
        }

        if (weather.contains("-")) {
            weather = weather.substring(0, weather.indexOf("-"));
        }
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int resId;
        if (weather.contains("晴")) {
            if (hour >= 7 && hour < 19) {
                resId = me.mile.kjb.R.drawable.ic_weather_sunny;
            } else {
                resId = me.mile.kjb.R.drawable.ic_weather_sunny_night;
            }
        } else if (weather.contains("多云")) {
            if (hour >= 7 && hour < 19) {
                resId = me.mile.kjb.R.drawable.ic_weather_cloudy;
            } else {
                resId = me.mile.kjb.R.drawable.ic_weather_cloudy_night;
            }
        } else if (weather.contains("阴")) {
            resId = me.mile.kjb.R.drawable.ic_weather_overcast;
        } else if (weather.contains("雷阵雨")) {
            resId = me.mile.kjb.R.drawable.ic_weather_thunderstorm;
        } else if (weather.contains("雨夹雪")) {
            resId = me.mile.kjb.R.drawable.ic_weather_sleet;
        } else if (weather.contains("雨")) {
            resId = me.mile.kjb.R.drawable.ic_weather_rain;
        } else if (weather.contains("雪")) {
            resId = me.mile.kjb.R.drawable.ic_weather_snow;
        } else if (weather.contains("雾") || weather.contains("霾")) {
            resId = me.mile.kjb.R.drawable.ic_weather_foggy;
        } else if (weather.contains("风") || weather.contains("飑")) {
            resId = me.mile.kjb.R.drawable.ic_weather_typhoon;
        } else if (weather.contains("沙") || weather.contains("尘")) {
            resId = me.mile.kjb.R.drawable.ic_weather_sandstorm;
        } else {
            resId = me.mile.kjb.R.drawable.ic_weather_cloudy;
        }
        return resId;
    }

    private void release() {
        mContext = null;
        llWeather = null;
        ivIcon = null;
        tvTemp = null;
        tvCity = null;
        tvWind = null;
    }

    @Override
    public void onPrepare() {
    }

    @Override
    public void onExecuteSuccess(Object o) {
    }

    @Override
    public void onExecuteFail(Exception e) {
    }
}
