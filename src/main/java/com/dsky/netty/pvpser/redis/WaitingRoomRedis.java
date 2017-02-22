package com.dsky.netty.pvpser.redis;

import com.dsky.netty.pvpser.common.Config;
import com.dsky.netty.pvpser.common.Keys;

public class WaitingRoomRedis {
    private static final WaitingRoomRedis instance = new WaitingRoomRedis();

    public static WaitingRoomRedis getInstance() {
        return instance;
    }

    /**
     * 根据等级上下浮动找到等待的玩家
     * 有事务控制
     *
     * @param degree 等级
     * @return 房间ID
     */
    public String getRandomRoom(int degree) {
        //int min = degree - Config.PLAYER_DEGREE_DRIFT;
        //int max = degree + Config.PLAYER_DEGREE_DRIFT;

        //return RedisManager.zrandom(Keys.REDIS_PLAIN_GAME_WAITING_ROOM, min, max);
    	return "";
    }

    /**
     * 增加等待玩家的房间
     *
     * @param degree 等级
     * @param roomID 房间ID
     */
    public void add(int degree, String roomID) {
        RedisManager.zadd(Keys.REDIS_PLAIN_GAME_WAITING_ROOM, degree, roomID);
    }

    /**
     * 删除等待玩家的房间
     *
     * @param roomID 房间ID
     */
    public void del(String roomID) {
        RedisManager.zrem(Keys.REDIS_PLAIN_GAME_WAITING_ROOM, roomID);
    }
}
