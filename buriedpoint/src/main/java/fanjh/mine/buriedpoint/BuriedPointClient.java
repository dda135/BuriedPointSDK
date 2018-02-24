package fanjh.mine.buriedpoint;

import android.app.Application;
import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import fanjh.mine.buriedpoint.core.BuriedPointCore;
import fanjh.mine.buriedpoint.core.BuriedPointCrashHandler;
import fanjh.mine.buriedpoint.core.Configuration;
import fanjh.mine.buriedpoint.core.DataModelFactory;
import fanjh.mine.buriedpoint.core.EventName;

/**
* @author fanjh
* @date 2018/2/7 9:27
* @description 埋点客户端入口
* @note
**/
public class BuriedPointClient {
    private Application application;
    private BuriedPointCore buriedPointCore;

    private static class Holder{
        static BuriedPointClient INSTANCE = new BuriedPointClient();
    }

    public static BuriedPointClient getInstance(){
        return Holder.INSTANCE;
    }

    private BuriedPointClient() {
    }

    public void setConfiguration(Configuration configuration){
        if(null == application){
            throw new IllegalArgumentException("使用前必须先关联应用！");
        }
        buriedPointCore.setConfiguration(configuration);
    }

    /**
     * 必须关联应用
     * @param application
     */
    public BuriedPointClient attachApplication(Application application){
        if(null != this.application){
            throw new IllegalArgumentException("不能多次关联应用！");
        }
        this.application = application;
        buriedPointCore = new BuriedPointCore(application);
        return this;
    }

    /**
     * 在Application中调用该方法来启动crash上报操作
     */
    public void reportCrash(){
        Thread.setDefaultUncaughtExceptionHandler(new BuriedPointCrashHandler());
    }

    public Context getApplicationContext(){
        if(null == application){
            throw new IllegalArgumentException("使用前必须先关联应用！");
        }
        return application.getApplicationContext();
    }

    public void setLocationMessage(String country,String province,String city){
        DataModelFactory.setLocationMessage(country, province, city);
    }

    public void login(int userId){
        DataModelFactory.setUserId(userId);
        HashMap<String,String> params = new HashMap<>(1);
        params.put("userId",userId+"");
        buriedPointCore.reportSoon(EventName.LOGIN,params);
    }

    public void loginOut(){
        int oldUserId = DataModelFactory.getUserId();
        DataModelFactory.setUserId(0);
        HashMap<String,String> params = new HashMap<>(1);
        params.put("userId",oldUserId+"");
        buriedPointCore.reportSoon(EventName.LOGINOUT,params);
    }

    public void register(String userId){
        HashMap<String,String> params = new HashMap<>(1);
        params.put("userId",userId);
        buriedPointCore.reportSoon(EventName.REGISTER,params);
    }

    /**
     * 启动App的时候手动调用
     * 说明一下为什么要单独调用
     * 1：可以在这里尝试进行上报缓存数据
     * 2：为了精确统计启动事件，一般的App的入口都是广告页，在广告页面调用即可
     * 单纯通过ActivityLifecycleCallbacks判断，会存在内存重启的场景并且过于复杂
     * 手动调用反而简单且统计准确
     */
    public void startApp(){
        buriedPointCore.startApp();
    }

    /**
     * 对于一些重要的事件，可以通过当前方法立刻尝试上报
     * @param eventName 事件名称
     * @param params 参数
     */
    public void reportSoon(String eventName,HashMap<String,String> params){
        buriedPointCore.reportSoon(eventName, params);
    }

    /**
     * 上报事件
     * @param eventName 事件名称
     * @param params 事件参数
     */
    public void report(String eventName,HashMap<String,String> params){
        buriedPointCore.report(eventName, params);
    }

    /**
     * 上报事件
     * @param eventName 事件名称
     */
    public void report(String eventName){
        buriedPointCore.report(eventName,null);
    }

}
