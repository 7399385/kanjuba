package me.mile.kjb.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.tencent.smtt.sdk.WebView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.mile.kjb.R;
import me.mile.kjb.storage.preference.Preferences;
import me.mile.kjb.utils.ToastUtils;
import me.mile.kjb.view.X5WebView;

public class WebVideoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    @BindView(R.id.webview) X5WebView webView;
    @BindView(R.id.ll_loading) LinearLayout llLoading;
    @BindView(R.id.fw_inteface_bottom_lv) ListView mListview;
    @BindView(R.id.activity_webvideo_fab)FloatingActionButton mfab;
    @BindView(R.id.LinearLayoutPopWindow)LinearLayout linearLayoutpw;
    private String url;
    private String tvInterface = Preferences.getPlayInterfaceUrl0();
    private String TAG = "WebVideoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle parms = getIntent().getExtras();
        url= parms.getString("episode");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setSystemBarTransparent();
        setContentView(R.layout.activity_web_video);
        ButterKnife.bind(this);
        banAdvertisement();
        configWebview();
        webView.loadUrl(tvInterface + url);
        initListView();
    }

    private void initListView() {
        List<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        for(int i = 0;i < 4;i ++) {
            if(i == 3){
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("btn_tv","取消");
                mylist.add(map);
                break;
            }
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("btn_tv", "接口"+String.valueOf(i+1));
            mylist.add(map);
        }
        //配置适配器
        SimpleAdapter adapter = new SimpleAdapter(this,
                mylist,//数据源
                R.layout.fw_interface_bottom_item,//显示布局
                new String[] {"btn_tv"}, //数据源的属性字段
                new int[] {R.id.btn_popup_option}); //布局里的控件id
        //添加并且显示
        mListview.setAdapter(adapter);
        mListview.setOnItemClickListener(this);
    }

    //去除QQ浏览器推广；
    private void banAdvertisement(){
        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ArrayList<View> outView = new ArrayList<View>();
                getWindow().getDecorView().findViewsWithText(outView,"QQ浏览器", View.FIND_VIEWS_WITH_TEXT);
                int size = outView.size();
                if(outView!=null&&outView.size()>0){
                    outView.get(0).setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        webView = null;
        super.onDestroy();
    }

    private void configWebview() {
        webView.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if (i == 100) {
                    webView.setVisibility(View.VISIBLE);
                    llLoading.setVisibility(View.GONE);
                }else {
                    webView.setVisibility(View.GONE);
                    llLoading.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void setSystemBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // LOLLIPOP解决方案
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // KITKAT解决方案
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    @OnClick(R.id.activity_webvideo_fab)
    public void openFwBottom(){linearLayoutpw.setVisibility(View.VISIBLE);}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
            case 0:
                tvInterface = Preferences.getPlayInterfaceUrl0();
                linearLayoutpw.setVisibility(View.GONE);
                webView.loadUrl(tvInterface + url);
                break;
            case 1:
                tvInterface = Preferences.getPlayInterfaceUrl1();
                linearLayoutpw.setVisibility(View.GONE);
                webView.loadUrl(tvInterface + url);
                break;
            case 2:
                tvInterface = Preferences.getPlayInterfaceUrl2();
                linearLayoutpw.setVisibility(View.GONE);
                webView.loadUrl(tvInterface+ url);
                break;
            case 3:
                linearLayoutpw.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(linearLayoutpw.getVisibility()== View.VISIBLE){
                linearLayoutpw.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
