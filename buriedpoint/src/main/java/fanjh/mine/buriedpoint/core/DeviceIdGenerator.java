package fanjh.mine.buriedpoint.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.UUID;

import fanjh.mine.buriedpoint.BuriedPointClient;

/**
 * @author fanjh
 * @date 2018/2/7 13:54
 * @description 设备标识生成器
 * @note
 **/
public class DeviceIdGenerator {
    private static final String SP_FILE = "device";
    private static final String VALUE_UUID = "uuid";

    public static String generator() {
        String androidId = getAndroidId();
        if(null == androidId){
            Context context = BuriedPointClient.getInstance().getApplicationContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
            String uuid = sharedPreferences.getString(VALUE_UUID,null);
            if(null == uuid) {
                uuid = "u" + UUID.randomUUID().toString();
                sharedPreferences.edit().putString(VALUE_UUID, uuid).apply();
            }
            return uuid;
        }else{
            return "a" + androidId;
        }
    }

    public static String getAndroidId() {
        Context context = BuriedPointClient.getInstance().getApplicationContext();
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        switch (androidId) {
            case "9774d56d682e549c":
            case "0123456789abcdef":
                return null;
            default:
                return androidId;
        }
    }

}
