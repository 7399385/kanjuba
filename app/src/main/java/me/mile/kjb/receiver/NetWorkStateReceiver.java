package me.mile.kjb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import me.mile.kjb.R;



public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //System.out.println("网络状态发生变化");
        //检测API是不是小于21，因为到了API21之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
               // Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
               // APPAplication.isconnect = true;
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
               // Toast.makeText(context, "WIFI已连接", Toast.LENGTH_SHORT).show();
               // APPAplication.isconnect = true;
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Toast.makeText(context, R.string.caution_flow, Toast.LENGTH_SHORT).show();
               // APPAplication.isconnect = true;
            } else {
                //Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
                //APPAplication.isconnect = false;
            }
        }else {
            switch (GetNetype(context)) {
                case -1:
                    Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    Toast.makeText(context, R.string.no_wifi_network, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    private int GetNetype(Context context)
    {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo==null)
        {
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType== ConnectivityManager.TYPE_MOBILE)
        {
            netType = 0;
        }
        else if(nType== ConnectivityManager.TYPE_WIFI)
        {
            netType = 1;
        }
        return netType;
    }
}
