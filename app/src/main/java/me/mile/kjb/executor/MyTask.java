package me.mile.kjb.executor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.tencent.smtt.sdk.TbsVideo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.mile.kjb.R;
import me.mile.kjb.activity.ISearchResultActivity;
import me.mile.kjb.activity.WebVideoActivity;
import me.mile.kjb.model.TvDataBean;
import me.mile.kjb.utils.DesUtils;
import me.mile.kjb.utils.GlideLoadUtils;
import me.mile.kjb.utils.ToastUtils;

public class MyTask extends AsyncTask<String, Integer, ArrayList<TvDataBean>> implements ISearchResultActivity {

    private String TAG = "GetTVTask";
    private LinearLayout llLoading;
    private LinearLayout linearLayout;
    private String urlInterface;
    private ExpandingList mExpandingList;
    private Context context;


    private ArrayList<TvDataBean> list =new ArrayList<TvDataBean>();

    public MyTask(LinearLayout llLoading, LinearLayout linearLayout,
                  String urlInterface, ExpandingList mExpandingList,
                  Context context) {
        this.llLoading = llLoading;
        this.linearLayout = linearLayout;
        this.urlInterface = urlInterface;
        this.mExpandingList = mExpandingList;
        this.context = context;
    }

    //onPreExecute方法用于在执行后台任务前做一些UI操作
    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute() called");
        linearLayout.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);

    }

    //doInBackground方法内部执行后台任务,不可在此方法内修改UI
    @Override
    protected ArrayList<TvDataBean> doInBackground(String... params) {
        Log.i(TAG, "doInBackground(Params... params) called");
        Connection.Response res = null;
        Map<String, String> datas = new HashMap<>();
        datas.put("wd",params[0]);
        datas.put("submit","search");
        try {
            res = Jsoup.connect(urlInterface + "index.php?m=vod-search")
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.2")
                    .header("Referer", urlInterface + "index.php?m=vod-search")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36")
                    .header("Connection", "keep-alive")
                    .timeout(6000)
                    .maxBodySize(0)
                    .method(Connection.Method.POST)
                    .data(datas)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = res.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements vb4 = document.getElementsByClass("xing_vb4");
        List<Thread> threads = new ArrayList<Thread>();
        for (Element vb : vb4) {
            Elements link = vb.getElementsByTag("a");
            final String rawUrl = urlInterface + link.attr("href");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        String tvInfo;
                        tvInfo = "";
                        Document doc = Jsoup.connect(rawUrl).get();
                        Elements title = doc.getElementsByClass("vodh");
                        String tvtitle = title.select("h2").text();
                        String state = title.select("span").text();
                        String label = title.select("label").text();
                        Elements elements = doc.getElementsByClass("vodplayinfo");
                        try {
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

                        }catch (Exception e){
                            e.printStackTrace();
                        }
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
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return list;
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

    //onCancelled方法用于在取消执行中的任务时更改UI
    @Override
    protected void onCancelled() {
        Log.i(TAG, "onCancelled() called");
    }
    private void addItem(final TvDataBean tvDataBean, final List<ArrayList<String>> episodes, int colorRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            GlideLoadUtils.getInstance().glideLoad((Activity) context,
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
                        if(TbsVideo.canUseTbsPlayer(context)) TbsVideo.openVideo(context,episodes.get(0).get(1));
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
                    if(TbsVideo.canUseTbsPlayer(context)) TbsVideo.openVideo(context,playUrl);
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
                    if(TbsVideo.canUseTbsPlayer(context)) TbsVideo.openVideo(context,playUrl);
                    else ToastUtils.show("抱歉，TbsVideo不可用，请重启app");
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
        intent.setClass(context, WebVideoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void addToStore(String filename, TvDataBean tvDataBean) {

    }

    @Override
    public void deletStore(String filename) {

    }
}