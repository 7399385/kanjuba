package me.mile.kjb.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.mile.kjb.R;


/**
 * Created by Administrator on 2017/7/31.
 */

public class DisclaimerView {
    public DisclaimerView(final Context context, final SharedPreferences setting){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        String disclaimer = "用户须知：本APP仅限用于学习和研究目的，且不得用于商业或者非法用途。否则，一切后果请用户自负。本APP信息均收集于网络,广告也非本人提供！在此特别注意，" + "版权争议与本APP无关。您必须在下载后的24个小时之内，从您的手机中彻底卸载本APP。";
        AlertDialogView normalDialog = new AlertDialogView(context).builder();
        normalDialog.setCancelable(false);
        normalDialog.setTitle("免责声明");
        normalDialog.setMsg(disclaimer);
        normalDialog.setPositiveButton("同意", new View.OnClickListener() {
                    @Override
                        public void onClick(View v) {
                            //...To-do
                            setting.edit().putBoolean("FIRST", false).commit();
                        }
                    });
            normalDialog.setNegativeButton("不同意",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //...To-do
                           System.exit(0);
                        }
                    });
            // 显示
            normalDialog.show();
        }
    public class AlertDialogView {
        private Context context;
        private Dialog dialog;
        private LinearLayout lLayout_bg;
        private TextView txt_title;
        private TextView txt_msg;
        private Button btn_neg;
        private Button btn_pos;
        private ImageView img_line;
        private Display display;
        private boolean showTitle = false;
        private boolean showMsg = false;
        private boolean showPosBtn = false;
        private boolean showNegBtn = false;

        public AlertDialogView(Context context) {
            this.context = context;
            WindowManager windowManager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            display = windowManager.getDefaultDisplay();
        }

        public AlertDialogView builder() {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.view_alertdialog, null);

            lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
            txt_title = (TextView) view.findViewById(R.id.txt_title);
            txt_title.setVisibility(View.GONE);
            txt_msg = (TextView) view.findViewById(R.id.txt_msg);
            txt_msg.setVisibility(View.GONE);
            btn_neg = (Button) view.findViewById(R.id.btn_neg);
            btn_neg.setVisibility(View.GONE);
            btn_pos = (Button) view.findViewById(R.id.btn_pos);
            btn_pos.setVisibility(View.GONE);
            img_line = (ImageView) view.findViewById(R.id.img_line);
            img_line.setVisibility(View.GONE);

            dialog = new Dialog(context, R.style.AlertDialogStyle);
            dialog.setContentView(view);

            lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                    .getWidth() * 0.80), WindowManager.LayoutParams.WRAP_CONTENT));

            return this;
        }

        public AlertDialogView setTitle(String title) {
            showTitle = true;
            if ("".equals(title)) {
                txt_title.setText("标题");
            } else {
                txt_title.setText(title);
            }
            return this;
        }

        public AlertDialogView setMsg(String msg) {
            showMsg = true;
            if ("".equals(msg)) {
                txt_msg.setText("内容");
            } else {
                txt_msg.setText(msg);
            }
            return this;
        }

        public AlertDialogView setCancelable(boolean cancel) {
            dialog.setCancelable(cancel);
            return this;
        }

        public AlertDialogView setPositiveButton(String text,
                                                                     final View.OnClickListener listener) {
            showPosBtn = true;
            if ("".equals(text)) {
                btn_pos.setText("确定");
            } else {
                btn_pos.setText(text);
            }
            btn_pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                    dialog.dismiss();
                }
            });
            return this;
        }

        public AlertDialogView setNegativeButton(String text,
                                                                     final View.OnClickListener listener) {
            showNegBtn = true;
            if ("".equals(text)) {
                btn_neg.setText("取消");
            } else {
                btn_neg.setText(text);
            }
            btn_neg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                    dialog.dismiss();
                }
            });
            return this;
        }

        private void setLayout() {
            if (!showTitle && !showMsg) {
                txt_title.setText("提示");
                txt_title.setVisibility(View.VISIBLE);
            }

            if (showTitle) {
                txt_title.setVisibility(View.VISIBLE);
            }

            if (showMsg) {
                txt_msg.setVisibility(View.VISIBLE);
            }

            if (!showPosBtn && !showNegBtn) {
                btn_pos.setText("确定");
                btn_pos.setVisibility(View.VISIBLE);
//          btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
                btn_pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            if (showPosBtn && showNegBtn) {
                btn_pos.setVisibility(View.VISIBLE);
//          btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
                btn_neg.setVisibility(View.VISIBLE);
//          btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
                img_line.setVisibility(View.VISIBLE);
            }

            if (showPosBtn && !showNegBtn) {
                btn_pos.setVisibility(View.VISIBLE);
//          btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            }

            if (!showPosBtn && showNegBtn) {
                btn_neg.setVisibility(View.VISIBLE);
//          btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
            }
        }

        public void show() {
            setLayout();
            dialog.show();
        }
    }
}
