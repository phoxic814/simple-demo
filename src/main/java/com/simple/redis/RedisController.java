package com.simple.redis;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * redis 16384 slot
 */
@RestController
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("redis/send")
    public void send(@RequestParam String key) {
        redisTemplate.opsForValue().set(key, key);
    }

    @GetMapping("redis/google-bloomFilter/test")
    public void bloomFilter(){
        //创建一个插入对象为100万，误报率为0.01%的布隆过滤器
        BloomFilter<Integer> bloomFilter = BloomFilter
                .create(Funnels.integerFunnel(), 1000000, 0.01);

        //插入数据 0 ~ 100w
        for (int i = 0; i < 1000000; i++) {
            bloomFilter.put(i);
        }

        int count = 0;
        //测试误判
        for (int i = 1000000; i < 2000000; i++) {
            if (bloomFilter.mightContain(i)) {
                //累加误判次数
                count++;
            }
        }
        System.out.println("总共的误判数:" + count);
        System.out.println("误判率：" + new BigDecimal(count).divide(BigDecimal.valueOf(1000000)));
    }
}
