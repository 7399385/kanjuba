package me.mile.kjb.executor;

import android.os.AsyncTask;
import android.util.Log;
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

import me.mile.kjb.model.TvDataBean;


public class MyTask extends AsyncTask<String, Integer, ArrayList<TvDataBean>> {

    private String TAG = "GetTVTask";
    private String urlInterface;

    private ArrayList<TvDataBean> list =new ArrayList<TvDataBean>();

    public MyTask(String urlInterface) {

        this.urlInterface = urlInterface;
    }

    //onPreExecute方法用于在执行后台任务前做一些UI操作
    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute() called");


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

                        }catch (Exception e){
                            e.printStackTrace();
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
    }

    //onCancelled方法用于在取消执行中的任务时更改UI
    @Override
    protected void onCancelled() {
        Log.i(TAG, "onCancelled() called");
    }
}