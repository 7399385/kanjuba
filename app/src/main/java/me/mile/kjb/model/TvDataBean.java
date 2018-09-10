package me.mile.kjb.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by sohnia on 17-12-20.
 */

@SuppressLint("ParcelCreator")
public class TvDataBean implements Parcelable,Serializable {

    private String picTvUrl;
    private String tvtitle;
    private String rawUrl;
    private String tvInfo;
    private String state;
    private String label;
    private List<ArrayList<String >> episodes = new ArrayList<ArrayList<String>>();


    public TvDataBean() {
    }

    public TvDataBean(String picTvUrl, String tvtitle, String rawUrl, String tvInfo, String state, String label) {
        this.picTvUrl = picTvUrl;
        this.tvtitle = tvtitle;
        this.rawUrl = rawUrl;
        this.tvInfo = tvInfo;
        this.state = state;
        this.label = label;
    }


    protected TvDataBean(Parcel in) {
        picTvUrl = in.readString();
        tvtitle = in.readString();
        rawUrl = in.readString();
        tvInfo = in.readString();
        state = in.readString();
        label = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(picTvUrl);
        dest.writeString(tvtitle);
        dest.writeString(rawUrl);
        dest.writeString(tvInfo);
        dest.writeString(state);
        dest.writeString(label);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TvDataBean> CREATOR = new Creator<TvDataBean>() {
        @Override
        public TvDataBean createFromParcel(Parcel in) {
            return new TvDataBean(in);
        }

        @Override
        public TvDataBean[] newArray(int size) {
            return new TvDataBean[size];
        }
    };

    public String getLabel() {
        return label;
    }

    public String getTvInfo() {
        return tvInfo;
    }

    public String getPicTvUrl() {
        return picTvUrl;
    }

    public String getState(){return state;}
    public void setPicTvUrl(String picTvUrl) {
        this.picTvUrl = picTvUrl;
    }

    public void addUrl(ArrayList<String> episode) {
        episodes.add(episode);
    }

    public List<ArrayList<String>> getEpisodes() {
        return episodes;
    }

    public String getTvtitle() {
        return tvtitle;
    }

    public void setRawUrl(String rawUrl) {
        this.rawUrl = rawUrl;
    }

    public String getRawUrl() {
        return rawUrl;
    }


    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "picTvUrl:"+ picTvUrl +"\n"+
                "tvtitle:" + tvtitle+"\n"+
                "rawUrl:" + rawUrl +"\n"+
                "tvInfo:" + tvInfo + "\n"+
                "state:" + state + "\n"+
                "label:"  + label +"\n"+
                "urls：" + episodes.toString();
    }

}
