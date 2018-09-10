package me.mile.kjb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.mile.kjb.http.HttpCallback;
import me.mile.kjb.http.HttpClient;
import me.mile.kjb.R;
import me.mile.kjb.model.OnlineMusic;
import me.mile.kjb.model.OnlineMusicList;
import me.mile.kjb.model.SheetInfo;
import me.mile.kjb.utils.binding.Bind;
import me.mile.kjb.utils.binding.ViewBinder;

/**
 * 歌单列表适配器
 * Created by wcy on 2015/12/19.
 */
public class SheetAdapter extends BaseAdapter {
    private static final int TYPE_PROFILE = 0;
    private static final int TYPE_MUSIC_LIST = 1;
    private Context mContext;
    private List<SheetInfo> mData;

    public SheetAdapter(List<SheetInfo> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_MUSIC_LIST;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getType().equals("#")) {
            return TYPE_PROFILE;
        } else {
            return TYPE_MUSIC_LIST;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mContext = parent.getContext();
        ViewHolderProfile holderProfile;
        ViewHolderMusicList holderMusicList;
        SheetInfo sheetInfo = mData.get(position);
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_PROFILE:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_holder_sheet_profile, parent, false);
                    holderProfile = new ViewHolderProfile(convertView);
                    convertView.setTag(holderProfile);
                } else {
                    holderProfile = (ViewHolderProfile) convertView.getTag();
                }
                holderProfile.tvProfile.setText(sheetInfo.getTitle());
                break;
            case TYPE_MUSIC_LIST:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_holder_sheet, parent, false);
                    holderMusicList = new ViewHolderMusicList(convertView);
                    convertView.setTag(holderMusicList);
                } else {
                    holderMusicList = (ViewHolderMusicList) convertView.getTag();
                }
                getMusicListInfo(sheetInfo, holderMusicList);
                holderMusicList.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
                break;
        }
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != mData.size() - 1;
    }

    private void getMusicListInfo(final SheetInfo sheetInfo, final ViewHolderMusicList holderMusicList) {
        if (sheetInfo.getCoverUrl() == null) {
            holderMusicList.tvMusic1.setTag(sheetInfo.getTitle());
            holderMusicList.ivCover.setImageResource(R.drawable.default_cover);
            holderMusicList.tvMusic1.setText("1.加载中…");
            holderMusicList.tvMusic2.setText("2.加载中…");
            holderMusicList.tvMusic3.setText("3.加载中…");
            HttpClient.getSongListInfo(sheetInfo.getType(), 3, 0, new HttpCallback<OnlineMusicList>() {
                @Override
                public void onSuccess(OnlineMusicList response) {
                    if (response == null || response.getSong_list() == null) {
                        return;
                    }
                    if (!sheetInfo.getTitle().equals(holderMusicList.tvMusic1.getTag())) {
                        return;
                    }
                    parse(response, sheetInfo);
                    setData(sheetInfo, holderMusicList);
                }

                @Override
                public void onFail(Exception e) {
                }
            });
        } else {
            holderMusicList.tvMusic1.setTag(null);
            setData(sheetInfo, holderMusicList);
        }
    }

    private void parse(OnlineMusicList response, SheetInfo sheetInfo) {
        List<OnlineMusic> onlineMusics = response.getSong_list();
        sheetInfo.setCoverUrl(response.getBillboard().getPic_s260());
        if (onlineMusics.size() >= 1) {
            sheetInfo.setMusic1(mContext.getString(R.string.song_list_item_title_1,
                    onlineMusics.get(0).getTitle(), onlineMusics.get(0).getArtist_name()));
        } else {
            sheetInfo.setMusic1("");
        }
        if (onlineMusics.size() >= 2) {
            sheetInfo.setMusic2(mContext.getString(R.string.song_list_item_title_2,
                    onlineMusics.get(1).getTitle(), onlineMusics.get(1).getArtist_name()));
        } else {
            sheetInfo.setMusic2("");
        }
        if (onlineMusics.size() >= 3) {
            sheetInfo.setMusic3(mContext.getString(R.string.song_list_item_title_3,
                    onlineMusics.get(2).getTitle(), onlineMusics.get(2).getArtist_name()));
        } else {
            sheetInfo.setMusic3("");
        }
    }

    private void setData(SheetInfo sheetInfo, ViewHolderMusicList holderMusicList) {
        holderMusicList.tvMusic1.setText(sheetInfo.getMusic1());
        holderMusicList.tvMusic2.setText(sheetInfo.getMusic2());
        holderMusicList.tvMusic3.setText(sheetInfo.getMusic3());
        Glide.with(mContext)
                .load(sheetInfo.getCoverUrl())
                .placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover)
                .into(holderMusicList.ivCover);
    }

    private static class ViewHolderProfile {
        @Bind(R.id.tv_profile)
        private TextView tvProfile;

        public ViewHolderProfile(View view) {
            ViewBinder.bind(this, view);
        }
    }

    private static class ViewHolderMusicList {
        @Bind(R.id.iv_cover)
        private ImageView ivCover;
        @Bind(R.id.tv_music_1)
        private TextView tvMusic1;
        @Bind(R.id.tv_music_2)
        private TextView tvMusic2;
        @Bind(R.id.tv_music_3)
        private TextView tvMusic3;
        @Bind(R.id.v_divider)
        private View vDivider;

        public ViewHolderMusicList(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
