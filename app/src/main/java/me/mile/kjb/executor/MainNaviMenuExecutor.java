package me.mile.kjb.executor;

/**
 * Created by Administrator on 2018/2/11 0011.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import me.mile.kjb.R;
import me.mile.kjb.activity.AboutActivity;
import me.mile.kjb.activity.MainActivity;
import me.mile.kjb.activity.MusicActivity;
import me.mile.kjb.activity.SettingActivity;
import me.mile.kjb.constants.Actions;
import me.mile.kjb.service.PlayService;
import me.mile.kjb.service.QuitTimer;
import me.mile.kjb.storage.preference.Preferences;
import me.mile.kjb.utils.ToastUtils;

/**
 * 导航菜单执行器
 */
public class MainNaviMenuExecutor {
    private MainActivity activity;
    public MainNaviMenuExecutor(MainActivity activity) {
        this.activity = activity;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_music:
                startActivity(MusicActivity.class);
                return true;
            case R.id.action_setting:
                startActivity(SettingActivity.class);
                return true;
            case me.mile.kjb.R.id.action_about:
                startActivity(AboutActivity.class,true);
                return true;
        }
        return false;
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }
    private void startActivity(Class<?> cls,boolean iskanjuba) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("iskanjuba",iskanjuba);
        Intent intent = new Intent(activity, cls);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

}

