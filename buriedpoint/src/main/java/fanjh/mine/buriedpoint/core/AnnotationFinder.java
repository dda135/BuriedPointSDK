package fanjh.mine.buriedpoint.core;

import android.app.Activity;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import fanjh.mine.buriedpointannotation.*;
import fanjh.mine.buriedpointannotation.Const;

/**
* @author fanjh
* @date 2018/2/9 18:05
* @description 注解处理
* @note
**/
public class AnnotationFinder {
    private IBuriedPointApt iBuriedPointApt;
    private boolean hasApt = true;
    private String className;
    private Class an;

    public AnnotationFinder(String className,Class an) {
        this.className = className;
        this.an = an;
    }

    /**
     * 编译期已经生成指定的索引
     * 当前通过索引来获取参数
     * @param activity 当前活动
     * @return 指定的参数
     */
    private HashMap<String,String> getAptParams(Activity activity){
        String methodName = iBuriedPointApt.getMethod(activity.getClass().getCanonicalName());
        if(null == methodName){
            return null;
        }
        Method method = null;
        try {
            method = activity.getClass().getMethod(methodName);
            return (HashMap<String, String>) method.invoke(activity);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从缓存中获取，这个对应编译期生成的类
     * @param activity 当前活动
     * @return 指定的参数
     */
    private HashMap<String,String> getParamsFromIndex(Activity activity){
        //当前编译期没有生成对应的类
        if(!hasApt){
            return null;
        }
        //尝试直接使用之前已经反射出指定的辅助类
        if(null != iBuriedPointApt){
            return getAptParams(activity);
        }
        try {
            //通过之前定义的规则来反射指定的类
            Class cls = Class.forName(Const.PACKAGE_NAME + "." + className);
            iBuriedPointApt = (IBuriedPointApt) cls.newInstance();
            return getAptParams(activity);
            //出现任何的异常都不允许再使用索引了
        } catch (ClassNotFoundException e) {
            hasApt = false;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            hasApt = false;
            e.printStackTrace();
        } catch (InstantiationException e) {
            hasApt = false;
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过反射来获取参数
     * @param activity 当前活动
     * @param c 注解
     * @return 指定的参数
     */
    private HashMap<String,String> getParamsFromReflect(Activity activity,Class c){
        HashMap<String,String> params = new HashMap<>();
        //获取当前类中定义的所有方法
        Method[] methods = activity.getClass().getDeclaredMethods();
        for(Method method:methods){
            //尝试从当前方法获取指定的注解
            Annotation annotation = method.getAnnotation(c);
            if(null != annotation){
                try {
                    params = (HashMap<String, String>) method.invoke(activity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return params;
    }

    public HashMap<String,String> getParams(Activity activity,boolean useIndex){
        //是否使用索引，实际上就是缓存
        if(useIndex){
            return getParamsFromIndex(activity);
        }else{
            return getParamsFromReflect(activity,an);
        }
    }

}
