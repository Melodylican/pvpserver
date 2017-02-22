package com.dsky.netty.pvpser.common;

import com.dsky.netty.pvpser.utils.ProReaderUtil;


public class Config {

    // 心跳配置
    public static int HEARTBEAT_TIMEOUT = Integer.parseInt(ProReaderUtil.getInstance().getHeartbeat().get("heartbeat.timeout"));

    public static int HEARTBEAT_INTERVAL = Integer.parseInt(ProReaderUtil.getInstance().getHeartbeat().get("heartbeat.interval"));

    // 服务器配置
    public static int SERVER_PORT = Integer.parseInt(ProReaderUtil.getInstance().getNettyPro().get("netty.port"));

    // Redis 相关配置
    public static String REDIS_HOST = ProReaderUtil.getInstance().getRedisPro().get("redis.host");

    public static int REDIS_PORT = Integer.parseInt(ProReaderUtil.getInstance().getRedisPro().get("redis.port"));

    public static int REDIS_MAX_TOTAL = Integer.parseInt(ProReaderUtil.getInstance().getRedisPro().get("redis.maxTotal"));

    public static int REDIS_MAX_IDLE = Integer.parseInt(ProReaderUtil.getInstance().getRedisPro().get("redis.maxIdle"));

    public static int REDIS_MAX_WAIT_MILLIS = Integer.parseInt(ProReaderUtil.getInstance().getRedisPro().get("redis.timeOut"));
    
    public static String REDIS_PASS = ProReaderUtil.getInstance().getRedisPro().get("redis.pass");
/*
    // 游戏相关配置
    public static int PLAYER_DEGREE_DRIFT = Integer.parseInt(ProReaderUtil.getInstance().getRedisPro().get("player_degree_drift"));

    public static int PLAYER_TIMEOVER = Integer.parseInt(ProReaderUtil.get("player_timeover"));

    public static int LAYOUT_TIMEOVER = Integer.parseInt(ProReaderUtil.get("layout_timeover"));

    public static int ATTACK_TIMEOVER = Integer.parseInt(ProReaderUtil.get("attack_timeover"));

    public static int RANKING_MAX = Integer.parseInt(ProReaderUtil.get("ranking_max"));

    // 调试常量
    public static int CONSTANT_SCORE_A = Integer.parseInt(ProReaderUtil.get("constant_score_a"));

    public static int CONSTANT_SCORE_B = Integer.parseInt(ProReaderUtil.get("constant_score_b"));

    public static int CONSTANT_SCORE_C = Integer.parseInt(ProReaderUtil.get("constant_score_c"));

    public static int CONSTANT_SCORE_D = Integer.parseInt(ProReaderUtil.get("constant_score_d"));

    // 等级与积分的对应关系
    public static String DEGREE_POINTS = ProReaderUtil.get("degree_points");
    */
}
