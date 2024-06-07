package com.simple.jvm;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import static java.lang.System.out;

/**
 * doc link: <a href="https://blog.csdn.net/qq_36434742/article/details/106854061">...</a>
 * <p>
 * 64 bits:
 * |-----------------------------------------------------------------------------------------------------------------|
 * |                                             Object Header(128bits)                                              |
 * |-----------------------------------------------------------------------------------------------------------------|
 * |                                   Mark Word(64bits)               |  Klass Word(64bits)    |      State         |
 * |-----------------------------------------------------------------------------------------------------------------|
 * | unused:25|identity_hashcode:31|unused:1|age:4|biase_lock:1|lock:2 | OOP to metadata object |      Nomal         |
 * |-----------------------------------------------------------------------------------------------------------------|
 * | thread:54|      epoch:2       |unused:1|age:4|biase_lock:1|lock:2 | OOP to metadata object |      Biased        |
 * |-----------------------------------------------------------------------------------------------------------------|
 * |                     ptr_to_lock_record:62                 |lock:2 | OOP to metadata object | Lightweight Locked |
 * |-----------------------------------------------------------------------------------------------------------------|
 * |                    ptr_to_heavyweight_monitor:62          |lock:2 | OOP to metadata object | Heavyweight Locked |
 * |-----------------------------------------------------------------------------------------------------------------|
 * |                                                           |lock:2 | OOP to metadata object |    Marked for GC   |
 * |-----------------------------------------------------------------------------------------------------------------|
 * ————————————————
 * <p>
 * vm option:
 * 解壓縮: -XX:-UseCompressedOops
 * <p>
 * 小端儲存顛倒: 第一個 byte 分代年龄、偏向锁信息、对象状态 (unused / age / biase_lock / lock)
 * <p>
 * 無鎖 001 / 偏向 101 / 000 輕量 / 010 重量 /
 */
public class JOLExample {

    public static void main(String[] args) throws InterruptedException {
//        objectHash();

//        biase_lock();

        heavy_lock();
    }

    public static void printObject() {
        SimplePojo pojo = new SimplePojo();

        // 打印vm
        out.println(VM.current().details());

        // 打印對象頭
        out.println(ClassLayout.parseInstance(pojo).toPrintable());
    }

    public static void objectHash() {
        SimplePojo pojo = new SimplePojo();

        //没有计算HashCode之前的对象头
        out.println("before hash");
        out.println(ClassLayout.parseInstance(pojo).toPrintable());

        //jvm计算HashCode
        out.println("jvm----------" + Integer.toHexString(pojo.hashCode()));

        //当计算完HashCode之后，我们可以查看对象头的信息变化
        out.println("after hash");
        out.println(ClassLayout.parseInstance(pojo).toPrintable());
    }

    /**
     * 偏向鎖延遲關閉:
     * jvm option: -XX:+UseBiasedLocking -XX:BiasedLockingStartupDelay=0
     */
    public static void biase_lock() throws InterruptedException {
//        Thread.sleep(5000);
        SimplePojo pojo = new SimplePojo();

        out.println("before lock");
        out.println(ClassLayout.parseInstance(pojo).toPrintable());
//        pojo.hashCode();

        synchronized (pojo) {
            out.println("locking thread id: " + Thread.currentThread().getId());
            out.println(ClassLayout.parseInstance(pojo).toPrintable());
        }

        out.println("after lock");
        out.println(ClassLayout.parseInstance(pojo).toPrintable());
    }

    public static void heavy_lock() throws InterruptedException {
        SimplePojo pojo = new SimplePojo();
        out.println("before lock");
        out.println(ClassLayout.parseInstance(pojo).toPrintable());

        Thread t1 = new Thread(()->{
            synchronized (pojo) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                out.println("t1 release");
            }
        });
        t1.start();

        Thread.sleep(1000);
        out.println("t1 lock ing");
        out.println(ClassLayout.parseInstance(pojo).toPrintable());

        synchronized (pojo) {
            out.println("main locking");
            out.println(ClassLayout.parseInstance(pojo).toPrintable());
        }

        out.println("after lock");
        out.println(ClassLayout.parseInstance(pojo).toPrintable());

        System.gc();
        out.println("after gc()");
        Thread.sleep(5000);
        out.println(ClassLayout.parseInstance(pojo).toPrintable());
    }
}
