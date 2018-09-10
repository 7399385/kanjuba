package me.mile.kjb.executor;

import android.content.Context;
import android.content.Intent;

import me.mile.kjb.http.HttpCallback;
import me.mile.kjb.http.HttpClient;
import me.mile.kjb.utils.ToastUtils;
import me.mile.kjb.model.DownloadInfo;

/**
 * 分享在线歌曲
 * Created by hzwangchenyan on 2016/1/13.
 */
public abstract class ShareOnlineMusic implements IExecutor<Void> {
    private Context mContext;
    private String mTitle;
    private String mSongId;

    public ShareOnlineMusic(Context context, String title, String songId) {
        mContext = context;
        mTitle = title;
        mSongId = songId;
    }

    @Override
    public void execute() {
        onPrepare();
        share();
    }

    private void share() {
        // 获取歌曲播放链接
        HttpClient.getMusicDownloadInfo(mSongId, new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo response) {
                if (response == null) {
                    onFail(null);
                    return;
                }

                onExecuteSuccess(null);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(me.mile.kjb.R.string.share_music, mContext.getString(me.mile.kjb.R.string.app_name),
                        mTitle, response.getBitrate().getFile_link()));
                mContext.startActivity(Intent.createChooser(intent, mContext.getString(me.mile.kjb.R.string.share)));
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
                ToastUtils.show(me.mile.kjb.R.string.unable_to_share);
            }
        });
    }
}
