package com.example.j2n.utils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setValue(String key, Object value, long timeout, TimeUnit unit) {
        validateSetValueRequest(key, value, timeout, unit);
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object getValue(String key) {
        validateRedisKey(key);
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteKey(String key) {
        validateRedisKey(key);
        redisTemplate.delete(key);
    }

    public void deleteKeys(List<String> keys) {
        redisTemplate.delete(keys);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void addSet(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public Set<Object> getSet(String key) {
        validateRedisKey(key);
        return redisTemplate.opsForSet().members(key);
    }

    public void removeSet(String key, Object value) {
        validateRedisKey(key);
        redisTemplate.opsForSet().remove(key, value);
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        validateExpireRequest(key, timeout, unit);
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    public void validateSetValueRequest(String key, Object value, long timeout, TimeUnit unit) {
        if (key == null) {
            log.error("Redis Key is null");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Redis Key"));
        }
        if (value == null) {
            log.error("Redis Value is null");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Redis Value"));
        }
        if (timeout < 0 || timeout == 0) {
            log.error("Redis Timeout is null");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Redis Timeout"));
        }
        if (unit == null) {
            log.error("Redis Time is null");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Redis Time"));
        }
    }

    public void validateRedisKey(String key) {
        if (key == null || !redisTemplate.hasKey(key)) {
            throw new DataNotFoundException(BaseMessageEnum.NOT_FOUND.withArgs(key));
        }
    }

    public void validateExpireRequest(String key, long timeout, TimeUnit unit) {
        if (key == null) {
            log.error("Redis Key is null");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Redis Key"));
        }
        if (timeout < 0 || timeout == 0) {
            log.error("Redis Timeout is null");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Redis Timeout"));
        }
        if (unit == null) {
            log.error("Redis Time is null");
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Redis Time"));
        }
    }
}
