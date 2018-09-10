package me.mile.kjb.storage.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

/**
 * SharedPreferences工具类
 * Created by wcy on 2015/11/28.
 */
public class Preferences {
    private static final String PLAY_POSITION = "play_position";
    private static final String PLAY_MODE = "play_mode";
    private static final String SPLASH_URL = "splash_url";
    private static final String NIGHT_MODE = "night_mode";
    private static final String SHARE_URL = "share_url";
    private static final String PLAY_INTERFACE_URL0 = "play_interface_url0";
    private static final String PLAY_INTERFACE_URL1 = "play_interface_url1";
    private static final String PLAY_INTERFACE_URL2 = "play_interface_url2";
    private static final String SEARCH_INTERFACE_URL0 = "search_interface_url0";
    private static final String SEARCH_INTERFACE_URL1 = "search_interface_url1";
    private static final String SEARCH_INTERFACE_URL2 = "search_interface_url2";
    private static final String LAST_WEBVIEW_URL = "last_webview_url";

    public static String getPlayInterfaceUrl0() {
        return getString(PLAY_INTERFACE_URL0, "http://014670.cn/jx/ty.php?url=");
    }

    public static void saveShareUrl(String s){ saveString(SHARE_URL,s);}

    public static String getShareUrl() {
        return getString(SHARE_URL, "");
    }

    public static void savePlayInterfaceUrl0(String s){ saveString(PLAY_INTERFACE_URL0,s);}

    public static String getPlayInterfaceUrl1() {
        return getString(PLAY_INTERFACE_URL1, "http://api.baiyug.cn/vip/index.php?url=");
    }

    public static void savePlayInterfaceUrl1(String s){ saveString(PLAY_INTERFACE_URL1,s);}

    public static String getPlayInterfaceUrl2() {
        return getString(PLAY_INTERFACE_URL2, "http://yun.mt2t.com/yun?url=");
    }

    public static void savePlayInterfaceUrl2(String s){ saveString(PLAY_INTERFACE_URL2,s);}

    public static String getSearchInterfaceUrl0() {
        return getString(SEARCH_INTERFACE_URL0, "http://caiji.000o.cc/");
    }

    public static void saveSearchInterfaceUrl0(String s){ saveString(SEARCH_INTERFACE_URL0,s);}

    public static String getSearchInterfaceUrl1() {
        return getString(SEARCH_INTERFACE_URL1, "http://www.zy131.com/");
    }

    public static void saveSearchInterfaceUrl1(String s){ saveString(SEARCH_INTERFACE_URL1,s);}

    public static String getSearchInterfaceUrl2() {
        return getString(SEARCH_INTERFACE_URL2, "http://okzyzy.cc/");
    }

    public static void saveSearchInterfaceUrl2(String s){ saveString(SEARCH_INTERFACE_URL2,s);}

    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static String getLastWebviewUrl(){return getString(LAST_WEBVIEW_URL,"file:///android_asset/index.html");}

    public static  void saveLastWebviewUrl(String s){saveString(LAST_WEBVIEW_URL,s);}



    public static int getPlayPosition() {
        return getInt(PLAY_POSITION, 0);
    }

    public static void savePlayPosition(int position) {
        saveInt(PLAY_POSITION, position);
    }

    public static int getPlayMode() {
        return getInt(PLAY_MODE, 0);
    }

    public static void savePlayMode(int mode) {
        saveInt(PLAY_MODE, mode);
    }

    public static String getSplashUrl() {
        return getString(SPLASH_URL, "");
    }

    public static void saveSplashUrl(String url) {
        saveString(SPLASH_URL, url);
    }

    public static boolean enableMobileNetworkPlay() {
        return getBoolean(sContext.getString(me.mile.kjb.R.string.setting_key_mobile_network_play), false);
    }

    public static void saveMobileNetworkPlay(boolean enable) {
        saveBoolean(sContext.getString(me.mile.kjb.R.string.setting_key_mobile_network_play), enable);
    }

    public static boolean enableMobileNetworkDownload() {
        return getBoolean(sContext.getString(me.mile.kjb.R.string.setting_key_mobile_network_download), false);
    }

    public static boolean isNightMode() {
        return getBoolean(NIGHT_MODE, false);
    }

    public static void saveNightMode(boolean on) {
        saveBoolean(NIGHT_MODE, on);
    }

    public static String getFilterSize() {
        return getString(sContext.getString(me.mile.kjb.R.string.setting_key_filter_size), "0");
    }

    public static void saveFilterSize(String value) {
        saveString(sContext.getString(me.mile.kjb.R.string.setting_key_filter_size), value);
    }

    public static String getFilterTime() {
        return getString(sContext.getString(me.mile.kjb.R.string.setting_key_filter_time), "0");
    }

    public static void saveFilterTime(String value) {
        saveString(sContext.getString(me.mile.kjb.R.string.setting_key_filter_time), value);
    }

    private static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    private static void saveBoolean(String key, boolean value) {
        getPreferences().edit().putBoolean(key, value).apply();
    }

    private static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    private static void saveInt(String key, int value) {
        getPreferences().edit().putInt(key, value).apply();
    }

    private static long getLong(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    private static void saveLong(String key, long value) {
        getPreferences().edit().putLong(key, value).apply();
    }

    private static String getString(String key, @Nullable String defValue) {
        return getPreferences().getString(key, defValue);
    }

    private static void saveString(String key, @Nullable String value) {
        getPreferences().edit().putString(key, value).apply();
    }
    public static void recieverSave(String key,String value){
        getPreferences().edit().putString(key, value).apply();
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }
}
