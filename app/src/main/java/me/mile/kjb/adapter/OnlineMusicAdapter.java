package me.mile.kjb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.mile.kjb.model.OnlineMusic;
import me.mile.kjb.utils.FileUtils;
import me.mile.kjb.utils.binding.Bind;
import me.mile.kjb.utils.binding.ViewBinder;

/**
 * 在线音乐列表适配器
 * Created by wcy on 2015/12/22.
 */
public class OnlineMusicAdapter extends BaseAdapter {
    private List<OnlineMusic> mData;
    private OnMoreClickListener mListener;

    public OnlineMusicAdapter(List<OnlineMusic> data) {
        this.mData = data;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(me.mile.kjb.R.layout.view_holder_music, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OnlineMusic onlineMusic = mData.get(position);
        Glide.with(parent.getContext())
                .load(onlineMusic.getPic_small())
                .placeholder(me.mile.kjb.R.drawable.default_cover)
                .error(me.mile.kjb.R.drawable.default_cover)
                .into(holder.ivCover);
        holder.tvTitle.setText(onlineMusic.getTitle());
        String artist = FileUtils.getArtistAndAlbum(onlineMusic.getArtist_name(), onlineMusic.getAlbum_title());
        holder.tvArtist.setText(artist);
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMoreClick(position);
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != mData.size() - 1;
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        mListener = listener;
    }

    private static class ViewHolder {
        @Bind(me.mile.kjb.R.id.iv_cover)
        private ImageView ivCover;
        @Bind(me.mile.kjb.R.id.tv_title)
        private TextView tvTitle;
        @Bind(me.mile.kjb.R.id.tv_artist)
        private TextView tvArtist;
        @Bind(me.mile.kjb.R.id.iv_more)
        private ImageView ivMore;
        @Bind(me.mile.kjb.R.id.v_divider)
        private View vDivider;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
