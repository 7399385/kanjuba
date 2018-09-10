package me.mile.kjb.utils;

/**
 * Created by Administrator on 2017/8/18.
 */

public class UpdateInformation {
    private static boolean isUpdate = false;
    public static String appname = "看剧吧" ;
    public static int versionCode;
    public static int lastForce ;// 之前强制升级版本
    public static String updateurl = "http://114.215.89.174/data/style/apk/kajubav1.9.apk" ;// 升级包获取地址
    public static String upgradeinfo = "V1.9版本更新啦,你想不想要试一下哈!";// 升级信息
    public static String downloadDir = "kanjuba";// 下载目录
}
