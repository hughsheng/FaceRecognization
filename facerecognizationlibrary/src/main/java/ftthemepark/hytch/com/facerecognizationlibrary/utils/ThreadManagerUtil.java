package ftthemepark.hytch.com.facerecognizationlibrary.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiekang on 2018/1/11.
 * 线程管理工具
 */

public class ThreadManagerUtil {
    private static ExecutorService fixThreadPool;
    private static ExecutorService singleThreadPool;


    public static ExecutorService getFixThreadPool() {
        if (fixThreadPool == null) {
            fixThreadPool = Executors.newFixedThreadPool(10);
        }
        return fixThreadPool;
    }

    public static ExecutorService getSingleThreadPool() {
        if (singleThreadPool == null) {
            singleThreadPool = Executors.newSingleThreadExecutor();
        }
        return singleThreadPool;
    }
}
