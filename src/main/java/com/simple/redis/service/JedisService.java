package com.simple.redis.service;

import com.google.common.collect.Maps;
import com.simple.constant.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class JedisService {

    private final StringRedisTemplate redisTemplate;
    private final StringRedisTemplate slaveRedisTemplate;

    public <T> Map<String, T> mutiGet(String key, Class<T> type){
        redisTemplate.opsForValue().get("key");

        Map<String, T> returnMap = Maps.newHashMap();

        slaveRedisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                Cursor<Map.Entry<Object, Object>> cursor = operations.opsForHash().scan(key,
                        ScanOptions.scanOptions().match("*").count(1000).build());
                while (cursor.hasNext()) {
                    Map.Entry<Object, Object> entry = cursor.next();
                    returnMap.put(entry.getKey().toString(), JsonUtils.toObject(entry.getValue().toString(), type));
                }
                return null;
            }
        });

        return returnMap;
    }
}
