package me.mile.kjb.application;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.mile.kjb.activity.MusicActivity;
import me.mile.kjb.constants.Extras;
import me.mile.kjb.model.Music;
import me.mile.kjb.receiver.StatusBarReceiver;
import me.mile.kjb.utils.FileUtils;
import me.mile.kjb.service.PlayService;
import me.mile.kjb.utils.CoverLoader;

/**
 * Created by wcy on 2017/4/18.
 */
public class Notifier {
    private static final int NOTIFICATION_ID = 0x111;
    private PlayService playService;
    private NotificationManager notificationManager;

    public static Notifier get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static Notifier instance = new Notifier();
    }

    private Notifier() {
    }

    public void init(PlayService playService) {
        this.playService = playService;
        notificationManager = (NotificationManager) playService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showPlay(Music music) {
        playService.startForeground(NOTIFICATION_ID, buildNotification(playService, music, true));
    }

    public void showPause(Music music) {
        playService.stopForeground(false);
        notificationManager.notify(NOTIFICATION_ID, buildNotification(playService, music, false));
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }

    private Notification buildNotification(Context context, Music music, boolean isPlaying) {
        Intent intent = new Intent(context, MusicActivity.class);
        intent.putExtra(Extras.EXTRA_NOTIFICATION, true);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(me.mile.kjb.R.drawable.ic_notification)
                .setCustomContentView(getRemoteViews(context, music, isPlaying));
        return builder.build();
    }

    private RemoteViews getRemoteViews(Context context, Music music, boolean isPlaying) {
        String title = music.getTitle();
        String subtitle = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), me.mile.kjb.R.layout.notification);
        if (cover != null) {
            remoteViews.setImageViewBitmap(me.mile.kjb.R.id.iv_icon, cover);
        } else {
            remoteViews.setImageViewResource(me.mile.kjb.R.id.iv_icon, me.mile.kjb.R.drawable.ic_launcher);
        }
        remoteViews.setTextViewText(me.mile.kjb.R.id.tv_title, title);
        remoteViews.setTextViewText(me.mile.kjb.R.id.tv_subtitle, subtitle);

        boolean isLightNotificationTheme = isLightNotificationTheme(playService);

        Intent playIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(me.mile.kjb.R.id.iv_play_pause, getPlayIconRes(isLightNotificationTheme, isPlaying));
        remoteViews.setOnClickPendingIntent(me.mile.kjb.R.id.iv_play_pause, playPendingIntent);

        Intent nextIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(me.mile.kjb.R.id.iv_next, getNextIconRes(isLightNotificationTheme));
        remoteViews.setOnClickPendingIntent(me.mile.kjb.R.id.iv_next, nextPendingIntent);

        return remoteViews;
    }

    private int getPlayIconRes(boolean isLightNotificationTheme, boolean isPlaying) {
        if (isPlaying) {
            return isLightNotificationTheme
                    ? me.mile.kjb.R.drawable.ic_status_bar_pause_dark_selector
                    : me.mile.kjb.R.drawable.ic_status_bar_pause_light_selector;
        } else {
            return isLightNotificationTheme
                    ? me.mile.kjb.R.drawable.ic_status_bar_play_dark_selector
                    : me.mile.kjb.R.drawable.ic_status_bar_play_light_selector;
        }
    }

    private int getNextIconRes(boolean isLightNotificationTheme) {
        return isLightNotificationTheme
                ? me.mile.kjb.R.drawable.ic_status_bar_next_dark_selector
                : me.mile.kjb.R.drawable.ic_status_bar_next_light_selector;
    }

    private boolean isLightNotificationTheme(Context context) {
        int notificationTextColor = getNotificationTextColor(context);
        return isSimilarColor(Color.BLACK, notificationTextColor);
    }

    private int getNotificationTextColor(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        RemoteViews remoteViews = notification.contentView;
        if (remoteViews == null) {
            return Color.BLACK;
        }
        int layoutId = remoteViews.getLayoutId();
        ViewGroup notificationLayout = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
        TextView title = notificationLayout.findViewById(android.R.id.title);
        if (title != null) {
            return title.getCurrentTextColor();
        } else {
            return findTextColor(notificationLayout);
        }
    }

    /**
     * 如果通过 android.R.id.title 无法获得 title ，
     * 则通过遍历 notification 布局找到 textSize 最大的 TextView ，应该就是 title 了。
     */
    private int findTextColor(ViewGroup notificationLayout) {
        List<TextView> textViewList = new ArrayList<>();
        findTextView(notificationLayout, textViewList);

        float maxTextSize = -1;
        TextView maxTextView = null;
        for (TextView textView : textViewList) {
            if (textView.getTextSize() > maxTextSize) {
                maxTextView = textView;
            }
        }

        if (maxTextView != null) {
            return maxTextView.getCurrentTextColor();
        }

        return Color.BLACK;
    }

    private void findTextView(View view, List<TextView> textViewList) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                findTextView(viewGroup.getChildAt(i), textViewList);
            }
        } else if (view instanceof TextView) {
            textViewList.add((TextView) view);
        }
    }

    private boolean isSimilarColor(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        return value < 180.0;
    }
}
