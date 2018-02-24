package fanjh.mine.buriedpoint.core.cache;

import java.util.List;

/**
* @author fanjh
* @date 2018/2/7 15:20
* @description 存储抽象
* @note
**/
public interface IStorage {
    /**
     * 存储当前记录
     * @param json json串
     */
    void put(String json);

    /**
     * 获取指定条数最老的记录
     * @param limit 条数
     * @return 结果回调
     */
    List<String> get(int limit);

    /**
     * 删除指定的条数
     * @param limit
     */
    void delete(int limit);

    /**
     * 设置当前可用的最大缓存大小
     * @param maxCacheSize
     */
    void setMaxCacheSize(int maxCacheSize);

}
