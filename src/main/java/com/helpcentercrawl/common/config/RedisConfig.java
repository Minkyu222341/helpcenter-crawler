package com.helpcentercrawl.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisPort);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5))
                .shutdownTimeout(Duration.ofMillis(100))
                .build();

        return new LettuceConnectionFactory(redisConfiguration, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Redis 연결 테스트 및 재시도 로직
        checkAndRetryRedisConnection(redisTemplate);

        return redisTemplate;
    }

    private void checkAndRetryRedisConnection(RedisTemplate<String, String> redisTemplate) {
        // 최대 재시도 횟수
        int maxRetryAttempts = 3;
        // 재시도 대기 시간 (1초)
        long retryDelayMs = 1000;
        // 현재 시도 횟수
        int attempts = 0;
        // Redis 연결 상태
        boolean connected = false;

        // 1. 최대 재시도 횟수만큼 Redis 연결을 시도합니다.
        // 2. 연결이 성공하면 connected 변수를 true로 설정하고 루프를 종료합니다.
        // 3. 연결이 실패하면 attempts 변수를 증가시키고, 재시도 대기 시간만큼 대기합니다.
        // 4. 최대 재시도 횟수를 초과하면 에러 로그를 출력합니다.
        // 5. 연결이 성공하면 루프를 종료합니다.
        while (!connected && attempts < maxRetryAttempts) {
            try {
                attempts++;
                // Redis 연결 테스트
                Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().ping(); // ping 명령어로 연결 확인
                connected = true;
            } catch (Exception e) {
                if (attempts < maxRetryAttempts) {
                    log.warn("Redis 연결 실패 ({}번째 시도): {}", attempts, e.getMessage());
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    log.error("Redis 연결 최종 실패 (최대 시도 횟수 {} 초과): {}", maxRetryAttempts, e.getMessage());
                }
            }
        }
    }
}