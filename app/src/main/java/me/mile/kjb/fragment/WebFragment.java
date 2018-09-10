package me.mile.kjb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.mile.kjb.R;
import me.mile.kjb.activity.WebVideoActivity;
import me.mile.kjb.storage.preference.Preferences;
import me.mile.kjb.utils.ToastUtils;
import me.mile.kjb.view.X5WebView;

public class WebFragment extends Fragment {
    @BindView(R.id.webview)X5WebView webView;
    @BindView(R.id.ll_loading) LinearLayout llLoading;
    @BindView(R.id.FabPlus)FloatingActionButtonPlus floatingActionButtonPlus;
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,  Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_web, null);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        configWebView();
//        webView.loadUrl(Preferences.getLastWebviewUrl());
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void configWebView() {
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if(i<100){
                    webView.setVisibility(View.GONE);
                    llLoading.setVisibility(View.VISIBLE);
                }else {
                    webView.setVisibility(View.VISIBLE);
                    llLoading.setVisibility(View.GONE);
                }

            }
        });
       floatingActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
            @Override
            public void onItemClick(FabTagLayout tagView, int position) {
                switch (position) {
                    case 0:
                        webView.loadUrl("file:///android_asset/index.html");
                        break;
                    case 1:
                        webView.reload();
                        break;
                    case 2:
                        startActivity(webView.getUrl());
                        break;
                    case 3:
                        if(webView.canGoBack()) webView.goBack();
                        else ToastUtils.show("不能后退");
                        break;
                    case 4:
                        if(webView.canGoForward()) webView.goForward();
                        else ToastUtils.show("不能前进");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }
    private void startActivity(String s){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("episode",s);
        intent.putExtras(bundle);
        intent.setClass(getActivity(), WebVideoActivity.class);
        startActivity(intent);
    }
}