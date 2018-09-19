package me.mile.kjb.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.tencent.smtt.sdk.TbsVideo;
import com.zaaach.toprightmenu.MenuItem;
import com.zaaach.toprightmenu.TopRightMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.mile.kjb.R;
import me.mile.kjb.executor.MyTask;
import me.mile.kjb.model.TvDataBean;
import me.mile.kjb.storage.preference.Preferences;
import me.mile.kjb.utils.DesUtils;
import me.mile.kjb.utils.GlideLoadUtils;
import me.mile.kjb.utils.ToastUtils;

public class SearchResultActivity extends AppCompatActivity  implements ISearchResultActivity{
    private MyTask getTVTask;
    private final String TAG = "SearchResultActivity";
    private String wd;
    @BindView(R.id.iv_change_interface) ImageView imageView;
    @BindView(R.id.expanding_list_main) ExpandingList mExpandingList;
    @BindView(R.id.toolbar_title_tv) TextView mToolbartv;
    @BindView(R.id.ll_loading) LinearLayout llLoading;
    @BindView(R.id.not_find_404) LinearLayout linearLayout;
    private TopRightMenu mTopRightMenu;
    private String urlInterface = Preferences.getSearchInterfaceUrl0();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSystemBarTransparent();
        Bundle bundle = getIntent().getExtras();
        wd= bundle.getString("wd");
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        mToolbartv.setText(wd);
        llLoading.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
        loadResult();

    }
    private void loadResult(){
        getTVTask = new MyTask( urlInterface){
            @Override
            protected void onCancelled() {
                super.onCancelled();
                linearLayout.setVisibility(View.GONE);
                llLoading.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(ArrayList<TvDataBean> result) {
                super.onPostExecute(result);
                llLoading.setVisibility(View.GONE);
                //返回空值要处理
                Log.d(TAG,result.toString());
                if ( null == result){
                    mExpandingList.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }else {
                    Log.i(TAG,result.toString());
                    int [] color = new int[]{R.color.blue,R.color.green,R.color.pink,
                            R.color.purple, R.color.yellow, R.color.orange,};
                    Random random = new Random();
                    for (TvDataBean tvDataBean:result){
                        addItem(tvDataBean,tvDataBean.getEpisodes(),color[random.nextInt(6)]);
                    }
                    if(mExpandingList.getItemsCount() == 0){
                        mExpandingList.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        getTVTask.execute(wd);
    }
    private void configureSubItem(final ExpandingItem item, final View view, String subTitle, final String playUrl) {
        ((TextView) view.findViewById(R.id.sub_title)).setText(subTitle);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playUrl.endsWith(".m3u8")){
                    if(TbsVideo.canUseTbsPlayer(SearchResultActivity.this)) TbsVideo.openVideo(SearchResultActivity.this,playUrl);
                    else ToastUtils.show("抱歉，X5内核不可用，请重启app");
                }else {
                    startActivity(playUrl);
                }
//                Bundle bundle = new Bundle();
//                bundle.putString("episode",playUrl);
//                startActivity(WebVideoActivity.class,bundle);
            }
        });
        view.findViewById(R.id.sub_item_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playUrl.endsWith(".m3u8")){
                    if(TbsVideo.canUseTbsPlayer(SearchResultActivity.this)) TbsVideo.openVideo(SearchResultActivity.this,playUrl);
                    else ToastUtils.show("抱歉，TbsVideo不可用，请重启app");
                }else {
                    startActivity(playUrl);
                }
            }
        });
    }
    private void addItem(final TvDataBean tvDataBean, final List<ArrayList<String>> episodes, int colorRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            GlideLoadUtils.getInstance().glideLoad(SearchResultActivity.this,
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
            item.findViewById(R.id.add_to_like).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.isSelected())
                    {
                        //取消收藏
                        try {
                            deletStore(new DesUtils().encrypt(tvDataBean.getTvtitle()));
                            v.setSelected(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else {
                        //添加收藏
                        try {
                            addToStore(new DesUtils().encrypt(tvDataBean.getTvtitle()),tvDataBean);
                            v.setSelected(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtils.show("未知错误，添加收藏失败");
                        }
                    }

                }
            });
            //item直接播放第一集
            item.findViewById(R.id.item_play).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(episodes.get(0).get(1).endsWith(".m3u8")){
                        if(TbsVideo.canUseTbsPlayer(SearchResultActivity.this)) TbsVideo.openVideo(SearchResultActivity.this,episodes.get(0).get(1));
                        else ToastUtils.show("抱歉，X5内核不可用，请重启app");
                    }else {
                        startActivity(episodes.get(0).get(1));
                    }
//                    Bundle bundle = new Bundle();
//                    bundle.putString("episode",episodes.get(0).get(1));
//                    startActivity(WebVideoActivity.class,bundle);
                }
            });
        }
    }

    @Override
    public void addToStore(String filename, TvDataBean tvDataBean) {

    }

    @Override
    public void deletStore(String filename) {

    }
    @OnClick(R.id.iv_change_interface)
    public void changeInterface(){
        mTopRightMenu = new TopRightMenu(SearchResultActivity.this);
//添加菜单项
        List<MenuItem> menuItems = new ArrayList<>();
        boolean add = menuItems.add(new MenuItem(R.mipmap.ic_ghost, getString(R.string.default_search_i)));
        menuItems.add(new MenuItem(R.mipmap.ic_ghost,getString(R.string.search_i_131)));
        menuItems.add(new MenuItem(R.mipmap.ic_ghost,getString(R.string.search_i_qq)));

        mTopRightMenu
                .setHeight(440)     //默认高度480
                .setWidth(320)      //默认宽度wrap_content
                .showIcon(false)     //显示菜单图标，默认为true
                .dimBackground(true)        //背景变暗，默认为true
                .needAnimationStyle(true)   //显示动画，默认为true
                .setAnimationStyle(R.style.TRM_ANIM_STYLE)
                .addMenuList(menuItems)
                .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(int position) {
                       switch (position){
                           case 0:
                               mExpandingList.removeAllViews();
                               urlInterface = Preferences.getSearchInterfaceUrl0();
                               loadResult();
                               break;
                           case 1:
                               mExpandingList.removeAllViews();
                               urlInterface = Preferences.getSearchInterfaceUrl1();
                               loadResult();
                               break;
                           case 2:
                               mExpandingList.removeAllViews();
                               urlInterface = Preferences.getSearchInterfaceUrl2();
                               loadResult();
                               break;
                           default:
                               break;
                       }
                    }
                })
                .showAsDropDown(imageView,-225, 26);	//带偏移量
//      		.showAsDropDown(moreBtn)
    }
    private void startActivity(String s){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("episode",s);
        intent.putExtras(bundle);
        intent.setClass(SearchResultActivity.this, WebVideoActivity.class);
        startActivity(intent);
    }
    private void setSystemBarTransparent() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);//calculateStatusColor(Color.WHITE, (int) alphaValue)
        }
    }

}
