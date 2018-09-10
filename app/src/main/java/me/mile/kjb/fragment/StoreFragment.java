package me.mile.kjb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.tencent.smtt.sdk.TbsVideo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.mile.kjb.R;
import me.mile.kjb.activity.SearchResultActivity;
import me.mile.kjb.activity.WebVideoActivity;
import me.mile.kjb.model.TvDataBean;
import me.mile.kjb.utils.DesUtils;
import me.mile.kjb.utils.GlideLoadUtils;
import me.mile.kjb.utils.ToastUtils;

public class StoreFragment extends Fragment {
    private static final String TGA ="StoreFragment" ;
	@BindView(R.id.expanding_list_store)ExpandingList expandingList;
	@BindView(R.id.not_find_store)LinearLayout linearLayout;
	private List<TvDataBean> storeData;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		storeData = readData();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.fragment_store, null);
        ButterKnife.bind(this,view);
        if(storeData.size()!=0){
            for(TvDataBean tvDataBean:storeData){
                int[] color = new int[]{R.color.yellow,R.color.purple,R.color.pink,
                R.color.green,R.color.orange,R.color.blue};
                Random random = new Random();
                try {
                    addItem(tvDataBean, tvDataBean.getEpisodes(), color[random.nextInt(6)], R.mipmap.ic_ghost);
                }catch (Exception e){
                }
            }
        }
        isNullItem();
		return view;
	}
    private void isNullItem(){
        if(expandingList.getItemsCount()==0){
            expandingList.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }
	private List<TvDataBean> readData(){
        List<TvDataBean> dataBeans = new ArrayList<TvDataBean>();
        File file = new File(getActivity().getFilesDir().getAbsolutePath());
        File[] files = file.listFiles();
        for (File f:files){
            if(f.getName().endsWith(".dat")){
                try {
                    dataBeans.add((TvDataBean) readObjectFromFile(f));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dataBeans;
	}
    private void addItem(final TvDataBean tvDataBean, final List<ArrayList<String>> episodes, int colorRes, int iconRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = expandingList.createNewItem(R.layout.expanding_layout_store);

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            GlideLoadUtils.getInstance().glideLoad(StoreFragment.this,
                    tvDataBean.getPicTvUrl(),
                    R.mipmap.ic_ghost,
                    item,R.id.indicator_image);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            ((TextView) item.findViewById(R.id.tv_title)).setText(tvDataBean.getTvtitle());
            ((TextView) item.findViewById(R.id.tv_state)).setText(tvDataBean.getState());
            ((TextView) item.findViewById(R.id.tv_label)).setText(tvDataBean.getLabel());
            ((TextView) item.findViewById(R.id.tv_info)).setText(tvDataBean.getTvInfo());

            //We can create items in batch.
            item.createSubItems(episodes.size());
            for (int i = 0; i < item.getSubItemsCount(); i++) {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);
                //Let's set some values in
                configureSubItem(item, view,episodes.get(i).get(0),episodes.get(i).get(1));
            }
            //item加入收藏
            item.findViewById(R.id.delect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandingList.removeItem(item);
                    isNullItem();
                    try {
                        deletStore(new DesUtils().encrypt(tvDataBean.getTvtitle()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            //item直接播放第一集
            item.findViewById(R.id.item_play).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(episodes.get(0).get(1).endsWith(".m3u8")){
                        if(TbsVideo.canUseTbsPlayer(getActivity())) TbsVideo.openVideo(getActivity(),episodes.get(0).get(1));
                        else ToastUtils.show("抱歉，X5内核不可用，请重启app");
                    }else {
                        startActivity(episodes.get(0).get(1));
                    }
                }
            });
        }
    }
    private void configureSubItem(final ExpandingItem item, final View view, String subTitle, final String playUrl) {
        ((TextView) view.findViewById(R.id.sub_title)).setText(subTitle);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playUrl.endsWith(".m3u8")){
                    if(TbsVideo.canUseTbsPlayer(getActivity())) TbsVideo.openVideo(getActivity(),playUrl);
                    else ToastUtils.show("抱歉，X5内核不可用，请重启app");
                }else {
                    startActivity(playUrl);
                }
            }
        });
        view.findViewById(R.id.sub_item_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playUrl.endsWith(".m3u8")){
                    if(TbsVideo.canUseTbsPlayer(getActivity())) TbsVideo.openVideo(getActivity(),playUrl);
                    else ToastUtils.show("抱歉，X5内核不可用，请重启app");
                }else {
                    startActivity(playUrl);
                }
            }
        });
    }
    private void startActivity(String s){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("episode",s);
        intent.putExtras(bundle);
        intent.setClass(getActivity(), WebVideoActivity.class);
        startActivity(intent);
    }
    public Object readObjectFromFile(File file) {
        Object temp=null;
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn=new ObjectInputStream(in);
            temp=objIn.readObject();
            objIn.close();
        } catch (IOException e) {
            Log.i(TGA,"read object failed!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }
    public void deletStore(String filename) {
        File file =new File(getActivity().getFilesDir().getAbsolutePath()+"/"+filename+".dat");
        if (!file.exists()) {
            Log.i(TGA,"file doesn't exist！"+filename +".dat");
        } else {
            if (file.isFile()) file.delete();
            else Log.i(TGA," this is a dir！"+filename +".dat");
        }
    }
}
