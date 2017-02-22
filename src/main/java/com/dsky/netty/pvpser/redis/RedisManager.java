package com.dsky.netty.pvpser.redis;

import redis.clients.jedis.*;

import java.util.*;

import com.dsky.netty.pvpser.common.Config;
import com.dsky.netty.pvpser.model.Room;
import com.dsky.netty.pvpser.utils.SerializeUtil;

public class RedisManager {
    private static JedisPool pool;

    static {
        if (pool == null) {

            // 建立连接池配置参数
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Config.REDIS_MAX_TOTAL);
            config.setMaxWaitMillis(Config.REDIS_MAX_WAIT_MILLIS);
            config.setMaxIdle(Config.REDIS_MAX_IDLE);
            // 创建连接池
            pool = new JedisPool(config, Config.REDIS_HOST, Config.REDIS_PORT);
            
        }
    }

    public static long incrBy(String key, long increment){
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        long value = client.incrBy(key, increment);
        pool.returnResource(client);

        return value;
    }

    public static void hset(String key, String field, String value){
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        client.hset(key, field, value);
        pool.returnResource(client);
    }
    
    /**
     * 
     * 方法功能说明：将房间信息写入redis
     * 创建：2017年2月21日 by chris.li 
     * 修改：日期 by 修改者
     * 修改内容：
     * @参数： @param key
     * @参数： @param room    
     * @return void   
     * @throws
     */
    public static void set(String key ,Room room) {
    	Jedis client = pool.getResource();
    	client.auth(Config.REDIS_PASS);
    	client.set(key.getBytes(),  SerializeUtil.serialize(room));
    	pool.returnResource(client);
    }
    
    /**
     * 
     * 方法功能说明：从redis中获取Room
     * 创建：2017年2月21日 by chris.li 
     * 修改：日期 by 修改者
     * 修改内容：
     * @参数： @param key
     * @参数： @return    
     * @return Room   
     * @throws
     */
    public static Room get(String key) {
    	Jedis client = pool.getResource();
    	client.auth(Config.REDIS_PASS);
    	Room room = (Room)SerializeUtil.unserialize(client.get(key.getBytes()));
    	pool.returnResource(client);
		return room;
    }
    
    public static void hset(byte[] key, byte[] field, byte[] bytes){
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        client.hset(key, field, bytes);
        pool.returnResource(client);
    }

    public static String hget(String key, String field){
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        String value = client.hget(key, field);
        pool.returnResource(client);

        return value;
    }

    public static Map<String, String> hgetall(String key){
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        Map<String, String> valueMap = client.hgetAll(key);
        pool.returnResource(client);

        return valueMap;
    }

    public static long hincrBy(String key, String field, long increment){
        Jedis client = pool.getResource();
        long value = client.hincrBy(key, field, increment);
        pool.returnResource(client);

        return value;
    }

    public static boolean hexists(String key, String field){
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        boolean flag = client.hexists(key, field);
        pool.returnResource(client);

        return flag;
    }

    public static Set<String> sunion(List<String> keys) {
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        Set<String> valueSet = client.sunion((String[])keys.toArray());
        pool.returnResource(client);

        return valueSet;
    }

    public static void sadd(String key, String member){
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        client.sadd(key, member);
        pool.returnResource(client);
    }

    public static void srem(String key, String member){
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        client.srem(key, member);
        pool.returnResource(client);
    }

    public static void hdel(String key, String field){
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        client.hdel(key, field);
        pool.returnResource(client);
    }
    
    /**
     * 
     * 方法功能说明： 用于删除Redis中已存的房间信息
     * 创建：2017年2月21日 by chris.li 
     * 修改：日期 by 修改者
     * 修改内容：
     * @参数： @param key    
     * @return void   
     * @throws
     */
    public static void del(String key){
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        client.del(key);
        pool.returnResource(client);
    }

    public static void zadd(String key, long score, String member) {
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        client.zadd(key, score, member);
        pool.returnResource(client);
    }

    public static void zincrBy(String key, String member, int increment) {
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        client.zincrby(key, increment, member);
        pool.returnResource(client);
    }

    public static long zrevrank(String key, String member) {
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        long rank = client.zrevrank(key, member);
        pool.returnResource(client);

        return rank + 1l;
    }

    public static long zscore(String key, String memebr) {
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        long score = Math.round(client.zscore(key, memebr));
        pool.returnResource(client);

        return score;
    }

    public static Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        Set<Tuple> set = client.zrevrangeWithScores(key, -- start, -- stop);
        pool.returnResource(client);

        return set;
    }

    public static Set<String> zrangeByScore(String key, int min, int max) {
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        Set<String> set = client.zrangeByScore(key, min, max);
        pool.returnResource(client);

        return set;
    }

    public static void zrem(String key, String member) {
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);
        client.zrem(key, member);
        pool.returnResource(client);
    }

    public static String zrandom(String key, int min, int max) {
        Jedis client = pool.getResource();
        client.auth(Config.REDIS_PASS);

        Transaction trans = client.multi();

        Response<Set<String>> response = trans.zrangeByScore(key, min, max);
        Set<String> members = response.get();
        String member = null;
        if (!members.isEmpty()) {
            member = new ArrayList<String>(members).get(new Random().nextInt(members.size()));
            trans.zrem(key, member);
        }

        trans.exec();
        pool.returnResource(client);

        return member;
    }
}
