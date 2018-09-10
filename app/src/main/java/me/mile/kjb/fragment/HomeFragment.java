package me.mile.kjb.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guoxiaoxing.phoenix.core.PhoenixOption;
import com.guoxiaoxing.phoenix.core.model.MediaEntity;
import com.guoxiaoxing.phoenix.core.model.MimeType;
import com.guoxiaoxing.phoenix.picker.Phoenix;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.mile.kjb.R;
import me.mile.kjb.activity.SearchResultActivity;

public class HomeFragment extends Fragment {
	private ArrayList<String> filePaths = new ArrayList<>();
    private int REQUEST_CODE = 0x000111;
    @BindView(R.id.tiet_search) TextInputEditText textInputEditText;
    @BindView(R.id.acbt_search) AppCompatButton acbt_search;

    @BindView(R.id.button_from_local) AppCompatButton bt_from_local;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}
    @OnClick(R.id.acbt_search)
    public void openUrlVideo(){
	    String wd = textInputEditText.getText().toString();
	    if(!wd.equals("")) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("wd",wd);
			intent.putExtras(bundle);
			intent.setClass(getActivity(), SearchResultActivity.class);
			startActivity(intent);
        }
        //未写完
    }
    @OnClick(R.id.button_from_local)
    public void openLocalVideo(){
        //打开文件选择器
		Phoenix.with()
				.theme(Color.parseColor("#F44336"))// 主题
				.fileType(MimeType.ofVideo())//显示的文件类型图片、视频、图片和视频
				.maxPickNumber(1)// 最大选择数量
				.minPickNumber(0)// 最小选择数量
				.spanCount(4)// 每行显示个数
				.enablePreview(true)// 是否开启预览
				.enableCamera(false)// 是否开启拍照
				.enableAnimation(true)// 选择界面图片点击效果
				.enableCompress(false)// 是否开启压缩
//				.compressPictureFilterSize(1024)//多少kb以下的图片不压缩
//				.compressVideoFilterSize(2018)//多少kb以下的视频不压缩
				.thumbnailHeight(160)// 选择界面图片高度
				.thumbnailWidth(160)// 选择界面图片宽度
				.enableClickSound(false)// 是否开启点击声音
				.pickedMediaList(new ArrayList<MediaEntity>())// 已选图片数据
				.videoFilterTime(0)//显示多少秒以内的视频
				//如果是在Activity里使用就传Activity，如果是在Fragment里使用就传Fragment
				.start(getActivity(), PhoenixOption.TYPE_PICK_MEDIA, REQUEST_CODE);

    }
//    @OnClick(R.id.open_alipay)
//            public void openalipay(){
//                ClipboardManager cmb = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//                cmb.setText("qVbi3r66J8");
//                try {
//			PackageManager packageManager
//					= getActivity().getApplicationContext().getPackageManager();
//			Intent intent = packageManager.
//					getLaunchIntentForPackage("com.eg.android.AlipayGphone");
//			startActivity(intent);
//		}catch (Exception e) {
//			String url = "https://ds.alipay.com/?from=mobileweb";
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.setData(Uri.parse(url));
//			startActivity(intent);
//		}
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this,view);
		return view;
	}

}
