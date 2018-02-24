package fanjh.mine.buriedpointsdk;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import fanjh.mine.buriedpoint.BuriedPointClient;
import fanjh.mine.buriedpoint.core.Configuration;

/**
 * Created by faker on 2018/2/8.
 */

public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        if(isMainProcess(this,getCurrentProcessName(this))) {
            BuriedPointClient.getInstance().
                    attachApplication(this).
                    setConfiguration(new Configuration.Builder().
                            serverUrl("http://10.1.93.196:8080/IM/report/report").
                            shouldReportWhenBackground(true).
                            useIndex(false).
                            build());
        }
        BuriedPointClient.getInstance().reportCrash();
    }

    public static String getCurrentProcessName(Context context) {

        try {
            int pid = android.os.Process.myPid();

            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);


            if (activityManager == null) {
                return null;
            }

            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
            if (runningAppProcessInfoList != null) {
                for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcessInfoList) {

                    if (appProcess != null) {
                        if (appProcess.pid == pid) {
                            return appProcess.processName;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static boolean isMainProcess(Context context, String mainProcessName) {
        if (TextUtils.isEmpty(mainProcessName)) {
            return true;
        }

        String currentProcess = getCurrentProcessName(context.getApplicationContext());
        if (TextUtils.isEmpty(currentProcess) || mainProcessName.equals(currentProcess)) {
            return true;
        }

        return false;
    }

}
