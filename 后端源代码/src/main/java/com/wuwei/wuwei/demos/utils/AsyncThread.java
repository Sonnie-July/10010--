package com.wuwei.wuwei.demos.utils;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class AsyncThread {
    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 100;
    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = 150;

    /**
     * 任务队列最大长度
     */
    private static final int QUEUE_CAPACITY = 100;
    /**
     * 线程池中空闲线程等待工作的超时时间
     */
    private static final Long KEEP_ALIVE_TIME = 2L;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy());


    public static void submitTask(Runnable task) throws ExecutionException, InterruptedException, TimeoutException {
        Future<String> submit = executor.submit(() -> {
                    try {
                        task.run();
                    } catch (Exception e) {
                        System.out.println(e.toString());
                        throw new RuntimeException("线程任务失败");
                    }
                    return "ok";
                }
        );

//        String res = submit.get(1L, TimeUnit.SECONDS);
    }

}
