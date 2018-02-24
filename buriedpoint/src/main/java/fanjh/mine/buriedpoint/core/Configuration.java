package fanjh.mine.buriedpoint.core;

/**
* @author fanjh
* @date 2018/2/7 9:30
* @description 配置
* @note
**/
public class Configuration {
    /**
     * 当前是否应该在启动App的时候上报数据
     */
    boolean shouldReportWhenLauncherApp;
    /**
     * 普通上报数据时的延迟时间/ms
     */
    long reportInterval;
    /**
     * 最大缓存大小/B
     */
    int maxCacheSize;
    /**
     * 一次性上报数据的最大条数
     */
    int onceReportMaxSize;
    /**
     * 是否应该打印日志
     */
    boolean shouldLog;
    /**
     * 是否应该在进入后台的时候上报数据
     */
    boolean shouldReportWhenBackground;
    /**
     * 是否应该在进入前台的时候上报数据
     */
    boolean shouldReportWhenForeground;
    /**
     * 需要上报数据的服务端连接
     */
    String serverUrl;
    /**
     * 是否使用索引
     */
    boolean useIndex;

    private Configuration(Builder builder) {
        shouldReportWhenLauncherApp = builder.shouldReportWhenLauncherApp;
        reportInterval = builder.reportInterval;
        maxCacheSize = builder.maxCacheSize == 0?50 * 1024 * 1024:builder.maxCacheSize;
        onceReportMaxSize = builder.onceReportMaxSize == 0?100:builder.onceReportMaxSize;
        shouldLog = builder.shouldLog;
        shouldReportWhenBackground = builder.shouldReportWhenBackground;
        shouldReportWhenForeground = builder.shouldReportWhenForeground;
        serverUrl = builder.serverUrl;
        useIndex = builder.useIndex;
    }


    public static final class Builder {
        private boolean shouldReportWhenLauncherApp;
        private long reportInterval;
        private int maxCacheSize;
        private int onceReportMaxSize;
        private boolean shouldLog;
        private boolean shouldReportWhenBackground;
        private boolean shouldReportWhenForeground;
        private String serverUrl;
        private boolean useIndex;

        public Builder() {
        }

        public Builder useIndex(boolean val) {
            useIndex = val;
            return this;
        }

        public Builder shouldReportWhenLauncherApp(boolean val) {
            shouldReportWhenLauncherApp = val;
            return this;
        }

        public Builder reportInterval(long val) {
            reportInterval = val;
            return this;
        }

        public Builder maxCacheSize(int val) {
            maxCacheSize = val;
            return this;
        }

        public Builder onceReportMaxSize(int val) {
            onceReportMaxSize = val;
            return this;
        }

        public Builder shouldLog(boolean val) {
            shouldLog = val;
            return this;
        }

        public Builder shouldReportWhenBackground(boolean val) {
            shouldReportWhenBackground = val;
            return this;
        }

        public Builder shouldReportWhenForeground(boolean val) {
            shouldReportWhenForeground = val;
            return this;
        }

        public Builder serverUrl(String val) {
            serverUrl = val;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }
}
