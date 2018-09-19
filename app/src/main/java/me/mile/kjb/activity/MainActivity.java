package me.mile.kjb.activity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.guoxiaoxing.phoenix.core.model.MediaEntity;
import com.guoxiaoxing.phoenix.picker.Phoenix;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;
import com.tencent.smtt.sdk.TbsVideo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.mile.kjb.R;
import me.mile.kjb.executor.MainNaviMenuExecutor;
import me.mile.kjb.fragment.HomeFragment;
import me.mile.kjb.fragment.StoreFragment;
import me.mile.kjb.fragment.WebFragment;
import me.mile.kjb.storage.preference.Preferences;
import me.mile.kjb.utils.DeviceUtils;
import me.mile.kjb.utils.ToastUtils;
import me.mile.kjb.view.AlertDialogView;
import me.mile.kjb.view.DisclaimerView;

public class MainActivity extends BaseActivity implements IMainActivity, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    // 定义Fragment页面
    private WebFragment webFragment;
    private StoreFragment storeFragment;
    private HomeFragment homeFragment;
    @BindView(R.id.txt_webview) TextView bdyTv;
    @BindView(R.id.txt_home) TextView hmTv;
    @BindView(R.id.txt_store) TextView stTv;
    // 定义布局对象
    @BindView(R.id.layout_store) FrameLayout stFl;
    @BindView(R.id.layout_home) FrameLayout hmFl;
    @BindView(R.id.layout_webview) FrameLayout webFl;
    @BindView(R.id.bt_webview) ImageView webBt;
    @BindView(R.id.bt_home) ImageView hmBt;
    @BindView(R.id.bt_store) ImageView stBt;
    @BindView(R.id.toolbar_title_tv)TextView toolbarTv;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.iv_menu)ImageView imageViewMenu;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    private int REQUEST_CODE = 0x000111;
    private  MainNaviMenuExecutor naviMenuExecutor;

    @OnClick(R.id.layout_store)
    public void clickStoreBtn() {
        // 得到Fragment事务管理器
        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        // 替换当前的页面
        fragmentTransaction.replace(R.id.frame_content, storeFragment);
        // 事务管理提交
        fragmentTransaction.commit();
        // 改变选中状态
        hmFl.setSelected(false);
        hmBt.setSelected(false);
        setPinkDim(hmTv);

        webFl.setSelected(false);
        webBt.setSelected(false);
        setPinkDim(bdyTv);

        stFl.setSelected(true);
        stBt.setSelected(true);
        setPink(stTv);
        toolbarTv.setText(R.string.store);
    }
    @OnClick(R.id.layout_webview)
    public void clickWebviewBtn() {
        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        // 替换当前的页面
        fragmentTransaction.replace(R.id.frame_content,webFragment);
        // 事务管理提交
        fragmentTransaction.commit();
        // 改变选中状态
        hmFl.setSelected(false);
        hmBt.setSelected(false);
        setPinkDim(hmTv);

        webFl.setSelected(true);
        webBt.setSelected(true);
        setPink(bdyTv);

        stFl.setSelected(false);
        stBt.setSelected(false);
        setPinkDim(stTv);
        toolbarTv.setText(R.string.yun_searcher);
    }
    @OnClick(R.id.layout_home)
    public void clickHomeBtn() {
        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        // 替换当前的页面
        fragmentTransaction.replace(R.id.frame_content, homeFragment);
        // 事务管理提交
        fragmentTransaction.commit();
        // 改变选中状态
        hmFl.setSelected(true);
        hmBt.setSelected(true);
        setPink(hmTv);

        webFl.setSelected(false);
        webBt.setSelected(false);
        setPinkDim(bdyTv);

        stFl.setSelected(false);
        stBt.setSelected(false);
        setPinkDim(stTv);
        toolbarTv.setText(R.string.main);
    }
    @OnClick(R.id.iv_menu)
    public void drawerOpener(){drawerLayout.openDrawer(GravityCompat.START);}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Preferences.isNightMode()) {
            setTheme(R.style.MainTheme);
        }
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fixToolbar();
        initView();
        handler.postDelayed(() -> checkFirst(),2000);
//        Log.i("info", DeviceUtils.getDeviceToken(this));

    }
    private void fixToolbar(){
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        int statusBarHeight = getStatusBarHeight(this);
        params.height += statusBarHeight;
        Log.i("wxf","1:"+params.height);
        Log.i("wxf","2:"+statusBarHeight);
        toolbar.setLayoutParams(params);
//        2.设置paddingTop，以达到状态栏不遮挡toolbar的内容。
        toolbar.setPadding(toolbar.getPaddingLeft(),
                toolbar.getPaddingTop() + getStatusBarHeight(this),
                toolbar.getPaddingRight(),
                toolbar.getPaddingBottom());
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private int getStatusBarHeight(Context context) {
        // 反射手机运行的类：android.R.dimen.status_bar_height.
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            String heightStr = clazz.getField("status_bar_height").get(object).toString();
            int height = Integer.parseInt(heightStr);
            //dp--->px 因为padding的单位是px
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public void initView() {
        homeFragment = new HomeFragment();
        webFragment = new WebFragment();
        storeFragment = new StoreFragment();
        clickHomeBtn();
        navigationView.setNavigationItemSelectedListener(this);
        naviMenuExecutor = new MainNaviMenuExecutor(this);
    }


    @Override
    public void checkFirst() {
        SharedPreferences setting = this.getSharedPreferences("isFirst", 0);
        Boolean user_first = setting.getBoolean("FIRST", true);
        if (user_first) {//第一次
            new DisclaimerView(MainActivity.this, setting);
        } else {
            //Toast.makeText(MainActivity.this, "不是第一次", Toast.LENGTH_LONG).show();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //返回的数据
            List<MediaEntity> result = Phoenix.result(data);
            if (result.size()== 1) {
                String filepath = result.get(0).getLocalPath();
                if(TbsVideo.canUseTbsPlayer(MainActivity.this)) TbsVideo.openVideo(MainActivity.this,filepath);
                else ToastUtils.show("Tbs不可使用");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
                return true;
            }else {
                new AlertDialogView(this).builder().setMsg("确定要退出吗？")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                startActivity(intent);
                            }
                        }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                    }
                }).show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    public void setPinkDim(TextView tv){
        tv.setTextColor(getResources().getColor(R.color.colorAccentDim));
    }

    public void setPink(TextView tv){
        tv.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawers();
        new Handler().postDelayed(() -> item.setChecked(false), 500);
        return naviMenuExecutor.onNavigationItemSelected(item);
    }
}
