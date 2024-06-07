package com.simple.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnProperty(value = "redis.client", havingValue = "jedis")
public class JedisConfig {

    @Primary
    @Bean("masterRedisProperties")
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean(name = "readOnlyRedisProperties")
    @ConfigurationProperties(prefix = "spring.redis.readonly")
    public RedisProperties readOnlyRedisProperties() {
        return new RedisProperties();
    }

    @Primary
    @Bean("masterRedis")
    public RedisTemplate redisTemplate(@Qualifier("masterRedisProperties") RedisProperties redisProperties) {
        JedisConnectionFactory jedisConnectionFactory = buildJedisConnectionFactory(redisProperties);
        return new StringRedisTemplate(jedisConnectionFactory);
    }

    private JedisConnectionFactory buildJedisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisConfiguration = buildReadonlyRedisConfiguration(redisProperties);
        RedisProperties.Pool poolProperties = redisProperties.getJedis().getPool();
        JedisPoolConfig poolConfig = getPoolConfig(poolProperties);
        JedisClientConfiguration.JedisClientConfigurationBuilder builder = JedisClientConfiguration.builder();
        applyProperties(builder, redisProperties);
        builder.usePooling().poolConfig(poolConfig);
        JedisConnectionFactory factory = new JedisConnectionFactory(redisConfiguration, builder.build());
        factory.afterPropertiesSet();
        return factory;
    }
    private JedisPoolConfig getPoolConfig(RedisProperties.Pool properties) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        if (properties.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRuns().toMillis());
        }
        if (properties.getMaxWait() != null) {
            config.setMaxWaitMillis(properties.getMaxWait().toMillis());
        }

        return config;
    }

    private void applyProperties(
            JedisClientConfiguration.JedisClientConfigurationBuilder builder, RedisProperties readOnlyRedisProperties) {
        if (readOnlyRedisProperties.isSsl()) {
            builder.useSsl();
        }
        if (readOnlyRedisProperties.getTimeout() != null) {
            builder.readTimeout(readOnlyRedisProperties.getTimeout());
        }

        if (StringUtils.hasText(readOnlyRedisProperties.getClientName())) {
            builder.clientName(readOnlyRedisProperties.getClientName());
        }
    }

    private RedisStandaloneConfiguration buildReadonlyRedisConfiguration(RedisProperties redisProperties) {
        return createRedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort(), redisProperties);
    }

    private RedisStandaloneConfiguration createRedisStandaloneConfiguration(String host, int port, RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        return redisStandaloneConfiguration;
    }
}
