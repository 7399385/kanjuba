package me.mile.kjb.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import me.mile.kjb.adapter.OnMoreClickListener;
import me.mile.kjb.adapter.PlaylistAdapter;
import me.mile.kjb.model.Music;
import me.mile.kjb.service.AudioPlayer;
import me.mile.kjb.service.OnPlayerEventListener;
import me.mile.kjb.utils.binding.Bind;

/**
 * 播放列表
 */
public class PlaylistActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnMoreClickListener, OnPlayerEventListener {
    @Bind(me.mile.kjb.R.id.lv_playlist)
    private ListView lvPlaylist;

    private PlaylistAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.mile.kjb.R.layout.activity_playlist);
    }

    @Override
    protected void onServiceBound() {
        adapter = new PlaylistAdapter(AudioPlayer.get().getMusicList());
        adapter.setPosition(AudioPlayer.get().getPlayPosition());
        adapter.setOnMoreClickListener(this);
        lvPlaylist.setAdapter(adapter);
        lvPlaylist.setOnItemClickListener(this);
        AudioPlayer.get().addOnPlayEventListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AudioPlayer.get().play(position);
    }

    @Override
    public void onMoreClick(int position) {
        String[] items = new String[]{"移除"};
        Music music = AudioPlayer.get().getMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(music.getTitle());
        dialog.setItems(items, (dialog1, which) -> {
            if (AudioPlayer.get().getPlayPosition() == position) {
                AudioPlayer.get().stopPlayer();
            }
            AudioPlayer.get().delete(position);
            adapter.notifyDataSetChanged();
            AudioPlayer.get().next();
        });
        dialog.show();
    }

    @Override
    public void onChange(Music music) {
        adapter.setPosition(AudioPlayer.get().getPlayPosition());
    }

    @Override
    public void onPlayerStart() {
    }

    @Override
    public void onPlayerPause() {
    }

    @Override
    public void onPublish(int progress) {
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }

    @Override
    protected void onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(this);
        super.onDestroy();
    }
}
