package fanjh.mine.buriedpoint.core.cache;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import fanjh.mine.buriedpoint.BuriedPointClient;

/**
 * @author fanjh
 * @date 2018/2/7 15:24
 * @description 基于ACache的实现
 * @note
 **/
public class ACacheStorageImpl implements IStorage {
    private static final String CACHE_FILE = "buried_point";
    private static final String CACHE_KEY = "json";
    private ArrayList<String> jsons;
    private Context context;
    private int maxCacheSize;

    private static class Holder {
        static ACacheStorageImpl INSTANCE = new ACacheStorageImpl();
    }

    public static ACacheStorageImpl getInstance(){
        return Holder.INSTANCE;
    }

    private ACacheStorageImpl() {
        context = BuriedPointClient.getInstance().getApplicationContext();
    }

    @Override
    public void setMaxCacheSize(int maxCacheSize){
        this.maxCacheSize = maxCacheSize;
    }

    @Override
    public void put(final String json) {
        initJsons();
        jsons.add(json);
        getCache().put(CACHE_KEY, jsons);
    }

    @Override
    public List<String> get(final int limit) {
        initJsons();
        int size = jsons.size();
        ArrayList<String> results = new ArrayList<>();
        int count = (limit > size?size:limit);
        for (int i = 0; i < count; ++i) {
            results.add(jsons.get(i));
        }
        return results;
    }

    @Override
    public void delete(int limit) {
        initJsons();
        if(jsons.size() == 0){
            return;
        }
        int size = jsons.size();
        if(size <= limit){
            jsons.clear();
        }else{
            int count = size - limit;
            ArrayList<String> newItems = new ArrayList<>(count);
            for(int i = limit;limit < size;++i){
                newItems.add(jsons.get(i));
            }
            jsons = newItems;
        }
        getCache().put(CACHE_KEY,jsons);
    }

    private void initJsons(){
        if (null == jsons) {
            jsons = (ArrayList<String>) getCache().getAsObject(CACHE_KEY);
            if (null == jsons) {
                jsons = new ArrayList<>();
            }
        }
    }

    private ACache getCache(){
        return ACache.get(context, CACHE_FILE, maxCacheSize);
    }

}
