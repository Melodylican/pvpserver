package com.dsky.netty.pvpser.redis;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.dsky.netty.pvpser.common.Config;
import com.dsky.netty.pvpser.common.Keys;
import com.dsky.netty.pvpser.model.Room;


public class RoomRedis {
    private static final RoomRedis instance = new RoomRedis();

    public static RoomRedis getInstance() {
        return instance;
    }

    /**
     * 获取房间对象
     *
     * @param roomID 房间ID
     * @param key 字段
     * @return 字段值
     */
    public Room getRoom(String roomID) {
        return RedisManager.get(Keys.REDIS_GAME_ROOM_PREFIX + roomID);
    }

    /**
     * 增加房间属性值
     *
     * @param roomID 房间ID
     * @param field 字段
     * @param value 字段值
     */
    public void add(String roomID, String field, String value) {
        RedisManager.hset(Keys.REDIS_GAME_ROOM_PREFIX + roomID, field, value);
    }

    /**
     * 字段增加增量
     *
     * @param roomID 房间ID
     * @param field 字段
     * @param increment 增量
     */
    public void add(String roomID, String field, int increment) {
        RedisManager.hincrBy(Keys.REDIS_GAME_ROOM_PREFIX + roomID, field, increment);
    }
    
    /**
     * 
     *
     * @param roomID 房间ID
     * @param field 字段
     * @param increment 增量
     */
    /**
     * 
     * 方法功能说明：增加房间
     * 创建：2017年2月21日 by chris.li 
     * 修改：日期 by 修改者
     * 修改内容：
     * @参数： @param roomID
     * @参数： @param room    
     * @return void   
     * @throws
     */
    public void add(String roomID, Room room) {
        RedisManager.set(Keys.REDIS_GAME_ROOM_PREFIX+roomID, room);
    }

    /**
     * 删除字段
     *
     * @param roomID 房间ID
     * @param field 字段
     */
    public void del(String roomID, String field) {
        RedisManager.hdel(Keys.REDIS_GAME_ROOM_PREFIX + roomID, field);
    }
    
    /**
     * 删除房间
     *
     * @param roomID 房间ID
     * @param field 字段
     */
    public void del(String roomID) {
        RedisManager.del(Keys.REDIS_GAME_ROOM_PREFIX + roomID);
    }

  

    /**
     * 等待对手，超时后替换为AI
     *
     * @param userID 用户ID
     * @param roomID 房间ID
     * @param degree AI等级
     */
    private void timerWaitingPlayerTimeover(String userID, final String roomID, final String degree){
        Timer timer = new HashedWheelTimer();
/*
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                // 超时后，从等待队列中移除，设置AI为对方玩家
                WaitingRoomRedis.getInstance().del(roomID);

                // 设置对手数据为AI
                RoomRedis.getInstance().add(roomID, Keys.ROOM_TARGET_ID, "0");
                RoomRedis.getInstance().add(roomID, Keys.ROOM_TARGET_DEGREE, degree);
            }
        }, Config.PLAYER_TIMEOVER, TimeUnit.SECONDS);

        WaitingRoomStore.add(userID, timer);
        */
    }
}
