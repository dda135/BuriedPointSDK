package fanjh.mine.buriedpoint.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import fanjh.mine.buriedpoint.core.cache.ACacheStorageImpl;
import fanjh.mine.buriedpoint.core.cache.IStorage;
import fanjh.mine.buriedpoint.pb.ReportEntry;
import fanjh.mine.buriedpointannotation.*;
import fanjh.mine.buriedpointannotation.Const;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author fanjh
 * @date 2018/2/7 9:54
 * @description 埋点实现类
 * @note
 **/
public class BuriedPointCore {
    private static final int MSG_REPORT = 1;
    private static final int MSG_PUT = 2;
    private static final String EXTRA_EVENT_NAME = "event_name";
    private static final String EXTRA_PARAMS = "params";
    private Configuration configuration;
    private IStorage storage;
    private OkHttpClient client = new OkHttpClient.Builder().build();
    private int count;
    private Handler threadHandler;
    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REPORT:
                    int limit = configuration.onceReportMaxSize;
                    List<String> datas = storage.get(limit);
                    JSONArray jsonArray = new JSONArray(datas);
                    try {
                        ReportEntry.Report.Builder builder = ReportEntry.Report.newBuilder();
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            builder.addArrays(DataModelFactory.getPbCommonModel(new JSONObject(jsonArray.getString(i))));
                        }
                        Request request = new Request.Builder().
                                url(configuration.serverUrl).
                                post(RequestBody.create(MediaType.parse("application/application/octet-stream; charset=utf-8"),
                                        builder.build().toByteArray())).
                                build();
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            storage.delete(limit);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_PUT:
                    Bundle bundle = msg.getData();
                    String eventName = bundle.getString(EXTRA_EVENT_NAME);
                    HashMap<String, String> params = (HashMap<String, String>) bundle.getSerializable(EXTRA_PARAMS);
                    JSONObject jsonObject = DataModelFactory.getCommonModel(eventName, params);
                    storage.put(jsonObject.toString());
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private Context context;
    private Activity currentActivity;
    private long lastActiveTime;
    private AnnotationFinder pageShowFinder;
    private AnnotationFinder appBackgroundFinder;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        storage.setMaxCacheSize(configuration.maxCacheSize);
    }

    public BuriedPointCore(Application application) {
        storage = ACacheStorageImpl.getInstance();
        HandlerThread thread = new HandlerThread(getClass().getCanonicalName());
        thread.start();
        threadHandler = new Handler(thread.getLooper(), callback);
        pageShowFinder = new AnnotationFinder(Const.PAGE_SHOW_CLASSNAME,PageShow.class);
        appBackgroundFinder = new AnnotationFinder(Const.APP_BACKGROUND_CLASSNAME,PageBackground.class);
        application.registerActivityLifecycleCallbacks(callbacks);
        context = application.getApplicationContext();
    }

    private Application.ActivityLifecycleCallbacks callbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            count++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            saveAppShowPage(activity);
            if (count == 0) {
                lastActiveTime = SystemClock.elapsedRealtime();
                if (configuration.shouldReportWhenForeground) {
                    reportSoon();
                }
            }
            currentActivity = activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            count--;
            if (count == 0) {
                saveAppToBackground();
                if (configuration.shouldReportWhenBackground) {
                    reportSoon();
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };



    private void saveAppShowPage(Activity activity) {
        HashMap<String, String> params = pageShowFinder.getParams(activity,configuration.useIndex);
        if(null == params){
            params = new HashMap<>();
        }
        params.put("$showActivity", activity.getClass().getCanonicalName());
        saveReport(EventName.APP_PAGE_SHOW, params);
    }

    private void saveAppToBackground() {
        HashMap<String, String> params = null;
        if (null != currentActivity) {
             params = appBackgroundFinder.getParams(currentActivity,configuration.useIndex);
            if(null == params){
                params = new HashMap<>();
            }
            params.put("$currentActivity", currentActivity.getClass().getCanonicalName());
            params.put("$activeTime", (SystemClock.elapsedRealtime() - lastActiveTime) + "");
        }
        saveReport(EventName.APP_BACKGROUND, params);
    }

    private Message getPutMessage(String eventName, HashMap<String, String> params) {
        Message message = Message.obtain();
        message.what = MSG_PUT;
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_EVENT_NAME, eventName);
        bundle.putSerializable(EXTRA_PARAMS, params);
        message.setData(bundle);
        return message;
    }

    private void saveReport(String eventName, HashMap<String, String> params) {
        threadHandler.sendMessage(getPutMessage(eventName, params));
    }

    public void report(final String eventName, final HashMap<String, String> params) {
        saveReport(eventName, params);
        if (null == configuration.serverUrl) {
            return;
        }
        report();
    }

    private void report() {
        if (null == configuration.serverUrl) {
            return;
        }
        if (!threadHandler.hasMessages(MSG_REPORT)) {
            Message message = Message.obtain();
            message.what = MSG_REPORT;
            threadHandler.sendMessageDelayed(message, configuration.reportInterval);
        }
    }

    public void reportSoon(String eventName, HashMap<String, String> params) {
        saveReport(eventName, params);
        if (null == configuration.serverUrl) {
            return;
        }
        reportSoon();
    }

    public void reportSoon() {
        if (null == configuration.serverUrl) {
            return;
        }
        threadHandler.removeMessages(MSG_REPORT);
        Message message = Message.obtain();
        message.what = MSG_REPORT;
        threadHandler.sendMessage(message);
    }

    public void startApp() {
        saveReport(EventName.APP_START, null);
        reportSoon();
    }

}
