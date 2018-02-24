package fanjh.mine.buriedpoint.core;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Map;

import fanjh.mine.buriedpoint.BuriedPointClient;

/**
* @author fanjh
* @date 2018/2/7 10:57
* @description 工具类
* @note
**/
public class Utils {
    public static final String NETWORK_TYPE_WIFI = "wifi";
    public static final String NETWORK_TYPE_2G = "2G";
    public static final String NETWORK_TYPE_3G = "3G";
    public static final String NETWORK_TYPE_4G = "4G";
    public static final String NETWORK_TYPE_NO_PERMISSION = "no_permission";
    public static final String NETWORK_TYPE_UNKNOW = "unknow";

    public static String getVersionName(){
        Context mContext = BuriedPointClient.getInstance().getApplicationContext();
        final PackageManager manager = mContext.getPackageManager();
        try {
            final PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "unkown";
    }

    public static String getChannel(){
        Context mContext = BuriedPointClient.getInstance().getApplicationContext();
        PackageManager pm = mContext.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = pm.getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            return bundle.getString("buried_point_channel","unConfiguration");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
       return "unConfiguration";
    }

    public static String getModel(){
        return Build.MODEL;
    }

    public static String getManufacture(){
        return Build.MANUFACTURER;
    }

    public static String getSDKVersion(){
        return Build.VERSION.RELEASE;
    }

    public static String getResolution(){
        Context context = BuriedPointClient.getInstance().getApplicationContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels + "x" + displayMetrics.widthPixels;
    }

    public static String getNetworkType() {
        Context context = BuriedPointClient.getInstance().getApplicationContext();

        // 检测权限
        if (!checkHasPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)) {
            return NETWORK_TYPE_NO_PERMISSION;
        }

        // Wifi
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Network []networks = manager.getAllNetworks();
                for(Network network:networks){
                    NetworkInfo networkInfo = manager.getNetworkInfo(network);
                    if(ConnectivityManager.TYPE_WIFI == networkInfo.getType() && networkInfo.isConnectedOrConnecting()){
                        return NETWORK_TYPE_WIFI;
                    }
                }
            }else {
                NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                    return NETWORK_TYPE_WIFI;
                }
            }
        }

        // Mobile network
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context
                .TELEPHONY_SERVICE);

        if(null == telephonyManager){
            return NETWORK_TYPE_UNKNOW;
        }

        int networkType = telephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_TYPE_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_TYPE_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_TYPE_4G;
        }

        // disconnected to the internet
        return NETWORK_TYPE_UNKNOW;
    }

    /**
     * 当前网络是否连接
     * @return true表示连接
     */
    private boolean isNetworkConnected() {
        Context context = BuriedPointClient.getInstance().getApplicationContext();
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null != connMgr) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
        return false;
    }

    /**
     * 检测权限
     *
     * @param context    Context
     * @param permission 权限名称
     * @return true:已允许该权限; false:没有允许该权限
     */
    public static boolean checkHasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static String getOperator(){
        Context context = BuriedPointClient.getInstance().getApplicationContext();
        if (checkHasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context
                        .TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    String operatorString = telephonyManager.getSubscriberId();
                    if (!TextUtils.isEmpty(operatorString)) {
                        switch (operatorString){
                            case "46000":
                            case "46002":
                            case "46007":
                            case "46008":
                                return "中国移动";
                            case "46001":
                            case "46006":
                            case "46009":
                                return "中国联通";
                            case "46003":
                            case "46005":
                            case "46011":
                                return "中国电信";
                            default:
                                return "其他";
                        }
                    }else{
                        return "其他";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "其他";
    }

}
