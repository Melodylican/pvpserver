package com.dsky.netty.pvpser.common;


public class Keys {

    // 统一使用的字段分隔符
    public static final String DELIMITER = "_";

    // Redis 游戏房间相关字段
    public static final String REDIS_GAME_ROOM_PREFIX = "room_information_";

    public static final String ROOM_ROUND = "round";

    public static final String ROOM_SOURCE_ID = "source_id";

    public static final String ROOM_TARGET_ID = "target_id";

    public static final String ROOM_SOURCE_DEGREE = "source_degree";

    public static final String ROOM_TARGET_DEGREE = "target_degree";

    public static final String ROOM_LAYOUT_INFO_PREFIX = "layout_info_";

    public static final String ROOM_ATTACK_INFO_PREFIX = "attack_info_";

    public static final String ROOM_ATTACK_RESULT_PREFIX = "attack_result_";

    public static final String ROOM_ATTACK_RESULT_PLAIN_REMAIN = "plain_remain";

    public static final String ROOM_ATTACK_RESULT_AI_PLAIN_REMAIN = "ai_plain_remain";

    public static final String ROOM_RESULT_FUEL_TOTAL = "fuel_total";

    public static final String ROOM_RESULT_NOT_INJURED_TOTAL = "not_injured_total";

    // 等待对手的roomID有序集合
    public static final String REDIS_PLAIN_GAME_WAITING_ROOM = "plain_game_waiting_room";

    public static final String SYSTEM_STATUS = "status";

    public static final String SYSTEM_STATUS_OK = "ok";

    public static final String HEARTBEAT_INTERVAL = "heartbeat_interval";
}
