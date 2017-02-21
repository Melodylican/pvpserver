package com.dsky.netty.pvpser.redis;

import com.dsky.netty.pvpser.common.Keys;

public class SequenceRedis {
    private static final SequenceRedis instance = new SequenceRedis();

    public static SequenceRedis getInstance(){
        return instance;
    }

    /**
     * 获取下一个序列值
     *
     * @param key 序列名称
     * @return 序列值
     */
    public int getNext(String key) {
        return (int) RedisManager.hincrBy(Keys.REDIS_SEQUENCE, key, 1l);
    }
}
