package me.mile.kjb.executor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import me.mile.kjb.activity.AboutActivity;
import me.mile.kjb.activity.MainActivity;
import me.mile.kjb.activity.MusicActivity;
import me.mile.kjb.activity.SettingActivity;
import me.mile.kjb.constants.Actions;
import me.mile.kjb.service.QuitTimer;
import me.mile.kjb.storage.preference.Preferences;
import me.mile.kjb.utils.ToastUtils;
import me.mile.kjb.service.PlayService;

/**
 * 导航菜单执行器
 */
public class NaviMenuExecutor {
    private MusicActivity activity;
    public NaviMenuExecutor(MusicActivity activity) {
        this.activity = activity;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case me.mile.kjb.R.id.action_setting:
                startActivity(SettingActivity.class);
                return true;
            case me.mile.kjb.R.id.action_night:
                nightMode();
                break;
            case me.mile.kjb.R.id.action_timer:
                timerDialog();
                return true;
            case me.mile.kjb.R.id.action_kanjuba:
                startActivity(MainActivity.class);
//                PlayService.startCommand(activity, Actions.ACTION_STOP);
                return true;
            case me.mile.kjb.R.id.action_about:
                startActivity(AboutActivity.class,false);
                return true;
        }
        return false;
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    private void nightMode() {
        Preferences.saveNightMode(!Preferences.isNightMode());
        activity.recreate();
    }
    private void startActivity(Class<?> cls,boolean iskanjuba) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("iskanjuba",iskanjuba);
        Intent intent = new Intent(activity, cls);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
    private void timerDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(me.mile.kjb.R.string.menu_timer)
                .setItems(activity.getResources().getStringArray(me.mile.kjb.R.array.timer_text), (dialog, which) -> {
                    int[] times = activity.getResources().getIntArray(me.mile.kjb.R.array.timer_int);
                    startTimer(times[which]);
                })
                .show();
    }

    private void startTimer(int minute) {
        QuitTimer.get().start(minute * 60 * 1000);
        if (minute > 0) {
            ToastUtils.show(activity.getString(me.mile.kjb.R.string.timer_set, String.valueOf(minute)));
        } else {
            ToastUtils.show(me.mile.kjb.R.string.timer_cancel);
        }
    }
}
