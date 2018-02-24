package fanjh.mine.buriedpoint.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import fanjh.mine.buriedpoint.BuriedPointClient;
import fanjh.mine.buriedpoint.pb.ReportEntry;

/**
* @author fanjh
* @date 2018/2/7 10:13
* @description 数据模型
* @note
**/
public class DataModelFactory {
    private static final String LIB = "lib";
    private static final String USER = "user";
    private static final String COMMON = "common";
    private static final String SP_FILE = "data_model";
    private static final String USER_ID = "user_id";
    private static final String COUNTRY = "country";
    private static final String PROVINCE = "province";
    private static final String CITY = "city";

    public static JSONObject getCommonModel(String eventName,Map<String,String> params){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(LIB,getLibModel());
            jsonObject.put(USER,getUserModel());
            if(null != params) {
                jsonObject.put(COMMON, new JSONObject(params).toString());
            }
            jsonObject.put(Const.EVENT_NAME,eventName);
            jsonObject.put(Const.TIME,System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static ReportEntry.Report.Entry getPbCommonModel(JSONObject object) throws Exception{
        String common = object.optString("common");
        ReportEntry.Report.Entry.Builder builder = ReportEntry.Report.Entry.newBuilder();
        builder.setLib(object.getString("lib")).
                setUser(object.getString("user")).
                setEventname(object.getString("$event_name")).
                setTime(object.getLong("$time"));
        if(!TextUtils.isEmpty(common)){
            builder.setCommon(common);
        }
        return builder.build();
    }

    public static String getLibModel(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.APP_VERSION,Utils.getVersionName());
            jsonObject.put(Const.CHANNEL,Utils.getChannel());
            jsonObject.put(Const.MANUFACTURE,Utils.getManufacture());
            jsonObject.put(Const.MODEL,Utils.getModel());
            jsonObject.put(Const.PLATFORM,Const.PLATFORM_ANDROID);
            jsonObject.put(Const.OS,Utils.getSDKVersion());
            jsonObject.put(Const.RESOLUTION,Utils.getResolution());
            jsonObject.put(Const.NETWORK_TYPE,Utils.getNetworkType());
            jsonObject.put(Const.OPERATOR,Utils.getOperator());
            jsonObject.put(Const.DEVICE_ID,DeviceIdGenerator.generator());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getUserModel(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.USER_ID,getUserId());
            jsonObject.put(Const.COUNTRY,getString(COUNTRY));
            jsonObject.put(Const.CITY,getString(CITY));
            jsonObject.put(Const.PROVINCE,getString(PROVINCE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static boolean setUserId(int userId){
        Context context = BuriedPointClient.getInstance().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.edit().putInt(USER_ID,userId).commit();
    }

    public static int getUserId(){
        Context context = BuriedPointClient.getInstance().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(USER_ID,0);
    }

    public static void setLocationMessage(String country,String province,String city){
        Context context = BuriedPointClient.getInstance().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
        sharedPreferences.edit().
                putString(COUNTRY,country).
                putString(PROVINCE,province).
                putString(CITY,city).
                apply();
    }

    private static String getString(String key){
        Context context = BuriedPointClient.getInstance().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

}
