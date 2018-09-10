package me.mile.kjb.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.mile.kjb.R;
import me.mile.kjb.model.TvDataBean;
import me.mile.kjb.storage.preference.Preferences;
import me.mile.kjb.utils.DesUtils;
import me.mile.kjb.utils.GlideLoadUtils;
import me.mile.kjb.utils.ToastUtils;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchResultActivity extends AppCompatActivity implements ISearchResultActivity {
    private GetTVTask getTVTask;
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
        loadResult();

    }
    private void loadResult(){
        getTVTask = new GetTVTask();
        getTVTask.execute(wd);
    }

    @Override
    public void addToStore(String filename, TvDataBean tvDataBean) {
        File file =new File(getFilesDir().getAbsolutePath()+"/"+filename+".dat");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(tvDataBean);
            objOut.flush();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"wirte failed!");
        }
    }
    @Override
    public void deletStore(String filename) {
        File file =new File(getFilesDir().getAbsolutePath()+"/"+filename+".dat");
        if (!file.exists()) {
            Log.i(TAG,"file doesn't exist！"+filename +".dat");
        } else {
            if (file.isFile()) file.delete();
            else Log.i(TAG," this is a dir！"+filename +".dat");
        }
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

    private class GetTVTask extends AsyncTask<String, Integer, ArrayList<TvDataBean>> {

        private String TAG = "GetTVTask";
        private ArrayList<TvDataBean> list =new ArrayList<TvDataBean>();
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute() called");
            llLoading.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected ArrayList<TvDataBean> doInBackground(String... params) {
            Log.i(TAG, "doInBackground(Params... params) called");
            OkHttpClient client = new OkHttpClient();
            FormBody fBody = new FormBody.Builder()
                    .add("wd", params[0]).build();
            Request request = new Request.Builder()
                    .url(urlInterface +"index.php?m=vod-search")
                    .post(fBody).build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Document document = Jsoup.parse(response.body().string());
                    Elements vb4 = document.getElementsByClass("xing_vb4");
                    List<Thread> threads = new ArrayList<Thread>();
                    for (Element vb : vb4) {
                        Elements link = vb.getElementsByTag("a");
                        final String rawUrl = urlInterface + link.attr("href");
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                try {
                                    String tvInfo;
                                    tvInfo = "";
                                    Document doc = Jsoup.connect(rawUrl).get();
                                    Elements title = doc.getElementsByClass("vodh");
                                    String tvtitle = title.select("h2").text();
                                    String state = title.select("span").text();
                                    String label = title.select("label").text();
                                    Elements elements = doc.getElementsByClass("vodplayinfo");
                                    tvInfo = elements.get(1).text();
                                    Element ul = elements.get(2).select("ul").last();
                                    Elements lis = ul.getElementsByTag("li");
                                    String picTvUrl = doc.getElementsByClass("lazy").attr("src");
                                    TvDataBean tvDataBean = new TvDataBean(picTvUrl, tvtitle, rawUrl, tvInfo, state, label);
                                    for (Element li : lis) {
                                        String[] splitStr = li.text().split("\\$",2);
                                        ArrayList<String> episode= new ArrayList<String>();
                                        episode.add(splitStr[0]);
                                        episode.add(splitStr[1]);
                                        tvDataBean.addUrl(episode);
                                    }
                                    list.add(tvDataBean);

                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
//                                  list.add(new TvDataBean("", "", "", "", "", ""));
                                }
                            }
                        };
                        thread.start();
                        threads.add(thread);
                    }
                    for (Thread thread : threads) {
                        thread.join();
                    }
                    return list;
                }
            } catch (Exception e) {
//                Log.e(TAG, e.getMessage());
            }
            return null;
        }
        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i(TAG, "onProgressUpdate(Progress... progresses) called");
//            progressBar.setProgress(progresses[0]);
//            textView.setText("loading..." + progresses[0] + "%");
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(ArrayList<TvDataBean> result) {
            Log.i(TAG, "onPostExecute(Result result) called");
            llLoading.setVisibility(View.GONE);
            //返回空值要处理
            if (result == null){
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

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            Log.i(TAG, "onCancelled() called");
        }
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
