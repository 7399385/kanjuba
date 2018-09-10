package me.mile.kjb.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.mile.kjb.adapter.OnMoreClickListener;
import me.mile.kjb.adapter.SearchMusicAdapter;
import me.mile.kjb.http.HttpCallback;
import me.mile.kjb.http.HttpClient;
import me.mile.kjb.model.Music;
import me.mile.kjb.utils.FileUtils;
import me.mile.kjb.utils.ToastUtils;
import me.mile.kjb.utils.ViewUtils;
import me.mile.kjb.utils.binding.Bind;
import me.mile.kjb.enums.LoadStateEnum;
import me.mile.kjb.executor.DownloadSearchedMusic;
import me.mile.kjb.executor.PlaySearchedMusic;
import me.mile.kjb.executor.ShareOnlineMusic;
import me.mile.kjb.model.SearchMusic;
import me.mile.kjb.service.AudioPlayer;

public class SearchMusicActivity extends BaseActivity implements SearchView.OnQueryTextListener
        , AdapterView.OnItemClickListener, OnMoreClickListener {
    @Bind(me.mile.kjb.R.id.lv_search_music_list)
    private ListView lvSearchMusic;
    @Bind(me.mile.kjb.R.id.ll_loading)
    private LinearLayout llLoading;
    @Bind(me.mile.kjb.R.id.ll_load_fail)
    private LinearLayout llLoadFail;
    private List<SearchMusic.Song> searchMusicList = new ArrayList<>();
    private SearchMusicAdapter mAdapter = new SearchMusicAdapter(searchMusicList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.mile.kjb.R.layout.activity_search_music);
    }

    @Override
    protected void onServiceBound() {
        lvSearchMusic.setAdapter(mAdapter);
        TextView tvLoadFail = llLoadFail.findViewById(me.mile.kjb.R.id.tv_load_fail_text);
        tvLoadFail.setText(me.mile.kjb.R.string.search_empty);

        lvSearchMusic.setOnItemClickListener(this);
        mAdapter.setOnMoreClickListener(this);
    }

    @Override
    protected int getDarkTheme() {
        return me.mile.kjb.R.style.AppThemeDark_Search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(me.mile.kjb.R.menu.menu_search_music, menu);
        SearchView searchView = (SearchView) menu.findItem(me.mile.kjb.R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.onActionViewExpanded();
        searchView.setQueryHint(getString(me.mile.kjb.R.string.search_tips));
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        try {
            Field field = searchView.getClass().getDeclaredField("mGoButton");
            field.setAccessible(true);
            ImageView mGoButton = (ImageView) field.get(searchView);
            mGoButton.setImageResource(me.mile.kjb.R.drawable.ic_menu_search);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOADING);
        searchMusic(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void searchMusic(String keyword) {
        HttpClient.searchMusic(keyword, new HttpCallback<SearchMusic>() {
            @Override
            public void onSuccess(SearchMusic response) {
                if (response == null || response.getSong() == null) {
                    ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    return;
                }
                ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                searchMusicList.clear();
                searchMusicList.addAll(response.getSong());
                mAdapter.notifyDataSetChanged();
                lvSearchMusic.requestFocus();
                handler.post(() -> lvSearchMusic.setSelection(0));
            }

            @Override
            public void onFail(Exception e) {
                ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        new PlaySearchedMusic(this, searchMusicList.get(position)) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Music music) {
                cancelProgress();
                AudioPlayer.get().addAndPlay(music);
                ToastUtils.show("已添加到播放列表");
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
                ToastUtils.show(me.mile.kjb.R.string.unable_to_play);
            }
        }.execute();
    }

    @Override
    public void onMoreClick(int position) {
        final SearchMusic.Song song = searchMusicList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(song.getSongname());
        String path = FileUtils.getMusicDir() + FileUtils.getMp3FileName(song.getArtistname(), song.getSongname());
        File file = new File(path);
        int itemsId = file.exists() ? me.mile.kjb.R.array.search_music_dialog_no_download : me.mile.kjb.R.array.search_music_dialog;
        dialog.setItems(itemsId, (dialog1, which) -> {
            switch (which) {
                case 0:// 分享
                    share(song);
                    break;
                case 1:// 下载
                    download(song);
                    break;
            }
        });
        dialog.show();
    }

    private void share(SearchMusic.Song song) {
        new ShareOnlineMusic(this, song.getSongname(), song.getSongid()) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                cancelProgress();
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
            }
        }.execute();
    }

    private void download(final SearchMusic.Song song) {
        new DownloadSearchedMusic(this, song) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                cancelProgress();
                ToastUtils.show(getString(me.mile.kjb.R.string.now_download, song.getSongname()));
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
                ToastUtils.show(me.mile.kjb.R.string.unable_to_download);
            }
        }.execute();
    }
}
