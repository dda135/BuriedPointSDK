package fanjh.mine.buriedpoint.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import fanjh.mine.buriedpoint.BuriedPointClient;

/**
* @author fanjh
* @date 2018/2/8 14:59
* @description 用于上报异常
* @note
**/
public class BuriedPointCrashHandler implements Thread.UncaughtExceptionHandler{
    private Thread.UncaughtExceptionHandler base;

    public BuriedPointCrashHandler() {
        base = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        HashMap<String,String> params = new HashMap<>();
        params.put("reason",result);
        BuriedPointClient.getInstance().reportSoon(EventName.CRASH,params);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if(null != base){
            base.uncaughtException(t,e);
        }
    }
}
