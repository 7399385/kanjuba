package me.mile.kjb.executor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.mile.kjb.activity.PlaylistActivity;
import me.mile.kjb.model.Music;
import me.mile.kjb.service.AudioPlayer;
import me.mile.kjb.service.OnPlayerEventListener;
import me.mile.kjb.utils.CoverLoader;
import me.mile.kjb.utils.binding.Bind;
import me.mile.kjb.utils.binding.ViewBinder;

/**
 * Created by hzwangchenyan on 2018/1/26.
 */
public class ControlPanel implements View.OnClickListener, OnPlayerEventListener {
    @Bind(me.mile.kjb.R.id.iv_play_bar_cover)
    private ImageView ivPlayBarCover;
    @Bind(me.mile.kjb.R.id.tv_play_bar_title)
    private TextView tvPlayBarTitle;
    @Bind(me.mile.kjb.R.id.tv_play_bar_artist)
    private TextView tvPlayBarArtist;
    @Bind(me.mile.kjb.R.id.iv_play_bar_play)
    private ImageView ivPlayBarPlay;
    @Bind(me.mile.kjb.R.id.iv_play_bar_next)
    private ImageView ivPlayBarNext;
    @Bind(me.mile.kjb.R.id.v_play_bar_playlist)
    private ImageView vPlayBarPlaylist;
    @Bind(me.mile.kjb.R.id.pb_play_bar)
    private ProgressBar mProgressBar;

    public ControlPanel(View view) {
        ViewBinder.bind(this, view);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        vPlayBarPlaylist.setOnClickListener(this);
        onChange(AudioPlayer.get().getPlayMusic());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case me.mile.kjb.R.id.iv_play_bar_play:
                AudioPlayer.get().playPause();
                break;
            case me.mile.kjb.R.id.iv_play_bar_next:
                AudioPlayer.get().next();
                break;
            case me.mile.kjb.R.id.v_play_bar_playlist:
                Context context = vPlayBarPlaylist.getContext();
                Intent intent = new Intent(context, PlaylistActivity.class);
                context.startActivity(intent);
                break;
        }
    }

    @Override
    public void onChange(Music music) {
        if (music == null) {
            return;
        }
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);
        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing());
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress((int) AudioPlayer.get().getAudioPosition());
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
    }

    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }
}
