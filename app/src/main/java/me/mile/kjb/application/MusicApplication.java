package me.mile.kjb.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.guoxiaoxing.phoenix.core.listener.ImageLoader;
import com.guoxiaoxing.phoenix.picker.Phoenix;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;

import me.mile.kjb.receiver.NetWorkStateReceiver;
import me.mile.kjb.storage.db.DBManager;
import me.mile.kjb.service.PlayService;

/**
 * 自定义Application
 * Created by wcy on 2015/11/27.
 */
public class MusicApplication extends Application {
    private Message m;
    @Override
    public void onCreate() {
        super.onCreate();
        XGPushManager.registerPush(getApplicationContext(),
                new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object data, int flag) {
                        Log.w(Constants.LogTag, "+++ register push sucess. token:" + data + "flag" + flag);

                        m.obj = "+++ register push sucess. token:" + data;
                        m.sendToTarget();
                    }

                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        Log.w(Constants.LogTag,
                                "+++ register push fail. token:" + data
                                        + ", errCode:" + errCode + ",msg:"
                                        + msg);
                        m.obj = "+++ register push fail. token:" + data
                                + ", errCode:" + errCode + ",msg:" + msg;
                        m.sendToTarget();
                    }
                });
        XGPushManager.setTag(getApplicationContext(), "2.0.1");
        CrashReport.initCrashReport(getApplicationContext(), "a52f0c28d3", false);

        AppCache.get().init(this);
        ForegroundObserver.init(this);
        DBManager.get().init(this);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
        //添加网络监听
        NetWorkStateReceiver receiver = new NetWorkStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
        Phoenix.config()
                .imageLoader(new ImageLoader() {
                    @Override
                    public void loadImage(Context mContext, ImageView imageView
                            , String imagePath, int type) {
                        Glide.with(mContext)
                                .load(imagePath)
                                .into(imageView);
                    }
                });

        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
