package me.mile.kjb.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;

import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import me.mile.kjb.storage.preference.Preferences;

public class X5WebView extends WebView {
	private String TAG = "SHOULDINTERCEPTREQUEST";
	private String webpage = null;
	@SuppressLint("SetJavaScriptEnabled")
	private final String UA= "Mozilla/5.0 (iPhone; CPU iPhone OS 9_2_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13D15 Safari/601.1";

	public X5WebView(final Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
        this.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//屏蔽调起
				if(url.contains("iqiyi:")||url.contains("tenvideo2:")||url.contains("item-apps:")||url.contains("youku:")||url.contains("letv:")||url.contains("pptv")||url.contains("sohu:")) Log.d("视频app尝试调起","已阻止");
				else if(url.contains("alipays:")||url.contains("wtloginmqq")){
					Intent intent;
					try {
						intent = Intent.parseUri(url,
								Intent.URI_INTENT_SCHEME);
						intent.addCategory(Intent.CATEGORY_BROWSABLE);
						intent.setComponent(null);
						arg0.startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else view.loadUrl(url);
				return true;
			}

		});
		this.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {
			@Override
			public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
                                       JsResult arg3) {
				return super.onJsConfirm(arg0, arg1, arg2, arg3);
			}


			@Override
			public boolean onJsAlert(WebView arg0, String arg1, String arg2,
                                     JsResult arg3) {
				/**
				 * 这里写入你自定义的window alert
				 */
				return super.onJsAlert(null, arg1, arg2, arg3);
			}
		});
		this.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String args0, String args1, String args2,
                                        String args3, long args4) {
				new AlertDialog.Builder(arg0)
						.setTitle("是否下载？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										Toast.makeText(
												arg0,
												"下载功能未开启",
												Toast.LENGTH_SHORT).show();
									}
								})
						.setNegativeButton("算了",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										Toast.makeText(
												arg0,
												"算了，不下了",
												Toast.LENGTH_SHORT).show();
									}
								})
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {

									@Override
									public void onCancel(DialogInterface dialog) {
										// TODO Auto-generated method stub
										Toast.makeText(
												arg0,
												"取消下载",
												Toast.LENGTH_SHORT).show();
									}
								}).show();
			}
		});
		initWebViewSettings();
		this.getView().setClickable(true);

	}

	private void initWebViewSettings() {
		WebSettings webSetting = this.getSettings();
		webSetting.setUserAgent(UA);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setAllowFileAccess(true);
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webSetting.setSupportZoom(true);
		webSetting.setBuiltInZoomControls(true);
		webSetting.setUseWideViewPort(true);
		webSetting.setSupportMultipleWindows(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setAppCacheEnabled(true);
		 webSetting.setDatabaseEnabled(true);
		webSetting.setDomStorageEnabled(true);
		webSetting.setGeolocationEnabled(true);
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
		//webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
		webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		//webSetting.setBlockNetworkImage(true);//组织网络图片数据。

//		this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
		// settings 的设计
	}

	private void fullScreen(Activity activity) {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	/*@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean ret = super.drawChild(canvas, child, drawingTime);
		canvas.save();
		Paint paint = new Paint();
		paint.setColor(0x7fff0000);
		paint.setTextSize(24.f);
		paint.setAntiAlias(true);
		if (getX5WebViewExtension() != null) {
			canvas.drawText(this.getContext().getPackageName() + "-pid:"
					+ android.os.Process.myPid(), 10, 50, paint);
			canvas.drawText(
					"X5  Core:" + QbSdk.getTbsVersion(this.getContext()), 10,
					100, paint);
		} else {
			canvas.drawText(this.getContext().getPackageName() + "-pid:"
					+ android.os.Process.myPid(), 10, 50, paint);
			canvas.drawText("Sys Core", 10, 100, paint);
		}
		canvas.drawText(Build.MANUFACTURER, 10, 150, paint);
		canvas.drawText(Build.MODEL, 10, 200, paint);
		canvas.restore();
		return ret;
	}*/

	public X5WebView(Context arg0) {
		super(arg0);
		setBackgroundColor(255);//设置背景色
		getBackground().setAlpha(0);//设置填充透明度（布局中一定要设置background，不然getbackground会是null）
	}

	@Override
	public void destroy() {
		Preferences.saveLastWebviewUrl(this.getUrl());
		super.destroy();
	}
}
