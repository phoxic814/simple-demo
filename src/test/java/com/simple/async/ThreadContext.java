package com.simple.async;

public class ThreadContext {

    private static final ThreadLocal<String> mainThread = new ThreadLocal<>();


    /**
     * 获取当前main thread執行id
     *
     * @return main thread執行id
     */
    public static String get() {
        String threadId = mainThread.get();
        return threadId == null ? "default" : threadId;
    }

    /**
     * 設定main thread執行id
     */
    public static void set(String threadId) {
        mainThread.set(threadId);
    }

    /**
     * 请求结束后，移除本地线程变量
     */
    public static void clear() {
        mainThread.remove();
    }
}
