package me.mile.kjb.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by huagnshuyuan on 2017/3/16.
 */
public class DeviceUtils {


    /**
     * 2 * 获取版本号 3 * @return 当前应用的版本号 4
     */
    public static int getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            int versioncode = info.versionCode;
            return versioncode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void parseJson(String jsondata) {
        try {
            //consume an optional byte order mark (BOM) if it exists
            if (jsondata != null && jsondata.startsWith("\ufeff")) {
                jsondata = jsondata.substring(1);
            }
            JSONArray jsonArray = new JSONArray(jsondata);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                UpdateInformation.appname = jsonObject.getString("appname");
                UpdateInformation.versionCode = jsonObject.getInt("VersionCode");
                UpdateInformation.lastForce = jsonObject.getInt("LastForce");
                UpdateInformation.updateurl = jsonObject.getString("updateurl");
                UpdateInformation.upgradeinfo = jsonObject.getString("upgradeinfo");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("json", "解析失败");
        }
    }

    public static String getDeviceToken(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return tm.getDeviceId();
        }else return null;
    }
    // if (VERSION.SDK_INT > 16) {
    // Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
    // final RenderScript rs = RenderScript.create(context);
    // final Allocation input = Allocation.createFromBitmap(rs, sentBitmap,
    // Allocation.MipmapControl.MIPMAP_NONE,
    // Allocation.USAGE_SCRIPT);
    // final Allocation output = Allocation.createTyped(rs, input.getType());
    // final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs,
    // Element.U8_4(rs));
    // script.setRadius(radius /* e.g. 3.f */);
    // script.setInput(input);
    // script.forEach(output);
    // output.copyTo(bitmap);
    // return bitmap;
    // }
}
