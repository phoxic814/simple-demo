package com.simple.redis;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.nio.charset.StandardCharsets;

/**
 * 基於 redis bitmap 數據結構實現 bloomFilter
 *<p>
 * Redis的 bitmap 支持2^32大小，内存最大512MB，误判率万分之一 ，差不多可以放下2亿左右的数据，性能高，空间占用率极小，省去了大量无效的数据库连接。
 * 可用于实现用户签到、用户日活、在线状态、布隆过滤器等。
 */
public class RedisBitMapBloomFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisBitMapBloomFilter.class);

    /**
     * 公共key前缀
     */
    private static final String BF_KEY_PREFIX = "bf:";

    /**
     * jedis连接池
     */
    private final JedisPool jedisPool;

    /**
     * bit数组长度
     */
    private final long bitmapLength;

    /**
     * Hash 函数个数
     */
    private final int hashFunctionNum;

    /**
     * redis Key
     */
    private String key;

    /**
     * 失效时间
     */
    private int expireSecond;


    /**
     * 构造 布隆过滤器
     *
     * @param key            redis key
     * @param expireSecond   过期时间（秒）
     * @param maxElementSize 预估元素数量
     * @param fpp            可接受的最大误差率：示例0.01
     * @param jedisPool      Jedis连接池
     */
    public RedisBitMapBloomFilter(String key, int expireSecond, int maxElementSize, double fpp, JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.key = key;
        this.expireSecond = expireSecond;
        //计算bit数组长度
        bitmapLength = (int) (-maxElementSize * Math.log(fpp) / (Math.log(2) * Math.log(2)));
        //计算hash函数个数
        hashFunctionNum = Math.max(1, (int) Math.round((double) bitmapLength / maxElementSize * Math.log(2)));
    }

    /**
     * 构造 布隆过滤器
     *
     * @param maxElementSize 预估元素数量
     * @param fpp            可接受的最大误差率：示例0.01
     * @param jedisPool      Jedis连接池
     */
    public RedisBitMapBloomFilter(int maxElementSize, double fpp, JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        //计算bit数组长度
        bitmapLength = (int) (-maxElementSize * Math.log(fpp) / (Math.log(2) * Math.log(2)));
        //计算hash函数个数
        hashFunctionNum = Math.max(1, (int) Math.round((double) bitmapLength / maxElementSize * Math.log(2)));
    }

    /**
     * 插入元素
     *
     * @param element 元素值
     */
    public void put(Object element) throws Exception {
        this.put(this.key, element, this.expireSecond);
    }

    /**
     * 检查元素在集合中是否（可能）存在
     *
     * @param element 元素值
     */
    public boolean contains(Object element) {
        return this.contains(this.key, element);
    }

    /**
     * 插入元素
     *
     * @param key          原始Redis键，会自动加上'bf:'前缀
     * @param element      元素值，默认 toString后存储
     * @param expireSecond 过期时间（秒）
     */
    public void put(String key, Object element, int expireSecond) throws Exception {
        if (key == null || element == null) {
            throw new Exception("key or element  is not null");
        }

        Jedis jedis = null;
        String redisBitKey = BF_KEY_PREFIX.concat(key);
        try {
            //获得连接
            jedis = jedisPool.getResource();
            //获得redis管道
            Pipeline pipeline = jedis.pipelined();
            for (long index : getBitIndices(element.toString())) {
                pipeline.setbit(redisBitKey, index, true);
            }
            pipeline.syncAndReturnAll();
            //刷新过期时间
            jedis.expire(redisBitKey, expireSecond);
        } catch (JedisException ex) {
            LOGGER.error("[ RedisBloomFilter ] >> put exception >>  messages:{}", ex.getMessage(), ex);
        } finally {
            if (jedis != null) {
                if (jedis.isConnected()) {
                    jedis.close();
                }
            }
        }
    }

    /**
     * 检查元素在集合中是否（可能）存在
     *
     * @param key     原始Redis键，会自动加上'bf:'前缀
     * @param element 元素值
     */
    public boolean contains(String key, Object element) {
        if (key == null || element == null) {
            throw new RuntimeException("键值均不能为空");
        }
        boolean result;
        Jedis jedis = null;
        String redisBitKey = BF_KEY_PREFIX.concat(key);
        try {
            //获得连接
            jedis = jedisPool.getResource();
            //获得redis管道
            Pipeline pipeline = jedis.pipelined();
            for (long index : getBitIndices(element.toString())) {
                pipeline.getbit(redisBitKey, index);
            }
            result = !pipeline.syncAndReturnAll().contains(false);
        } catch (JedisException ex) {
            LOGGER.error("[ RedisBloomFilter ] >> contains exception >>  messages:{}", ex.getMessage(), ex);
            throw new RuntimeException("檢查錯誤");
        } finally {
            if (jedis != null) {
                if (jedis.isConnected()) {
                    jedis.close();
                }
            }
        }
        return result;
    }

    /**
     * 计算一个元素值哈希后映射到Bitmap的哪些bit上
     *
     * @param element 元素值
     * @return bit下标的数组
     */
    private long[] getBitIndices(String element) {
        long hash64 = Hashing.murmur3_128()
                .hashObject(element, Funnels.stringFunnel(StandardCharsets.UTF_8))
                .asLong();
        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);

        long[] offset = new long[hashFunctionNum];
        for (int i = 1; i <= hashFunctionNum; i++) {
            int nextHash = hash1 + i * hash2;
            if (nextHash < 0) {
                nextHash = ~nextHash;
            }
            offset[i - 1] = nextHash % bitmapLength;
        }

        return offset;
    }
}
