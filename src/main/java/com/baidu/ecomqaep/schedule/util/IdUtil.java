package com.baidu.ecomqaep.schedule.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class IdUtil {

    private static volatile AtomicReference<TimeCache> cache = new AtomicReference<IdUtil.TimeCache>();

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private final static NumberFormat numberFormat;

    /**
     * 每秒最大支持的并发数1000
     */
    static {
        cache.set(new TimeCache());
        String format = "000";
        numberFormat = new DecimalFormat(format);
        numberFormat.setMaximumIntegerDigits(format.length());
    }

    /**
     * 线程安全的，获取和时间有关的唯一ID CAS实现,多线程冲突时效率不降低，每秒714477个 渣实现，按秒取前缀使得对象创建时做的工作过多，导致和有锁效率差不多
     *
     * @return
     */
    public static String getUniqID() {
        // CAS
        for (;;) {
            TimeCache oldcache = cache.get();
            TimeCache newcache = new TimeCache(oldcache);
            if (cache.compareAndSet(oldcache, newcache))
                return newcache.toString();
        }
    }

    private final static class TimeCache {

        protected volatile String time = dateFormat.format(new Date());
        protected volatile Long id = 0L;

        public TimeCache() {
        }

        public TimeCache(TimeCache timeCache) {
            if (this.time.equals(timeCache.time)) {
                this.id = timeCache.id + 1;
            }
        }

        @Override
        public String toString() {
            return time + numberFormat.format(id);
        }

    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
