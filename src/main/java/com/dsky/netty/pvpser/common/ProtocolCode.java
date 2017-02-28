package com.dsky.netty.pvpser.common;

/**
 * @类功能说明： 协议编码
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月21日 下午1:56:40
 * @版本：V1.0
 */
public class ProtocolCode {
	public static final int ROOM_MIN = 2000;

    public static final int JOIN_ROOM = 2000; //申请加入一个房间  //消息体中包含 自身用户ID以及加入的房间号

    public static final int WAITTING_USER_JOIN_ROOM = 2001;  // 创建房间，等待其他玩家加入 消息体中包含用户自身ID以及所创建的房间的信息

    public static final int EXIT_ROOM = 2002;  // 退出房间  消息体中包含自身用户ID以及退出的房间号

    public static final int DESTROY_ROOM = 2003; // 销毁一个房间  消息体中包含自身用户ID以及销毁的房间号

    public static final int UPDATE_USER_DATA = 2004;  //更新自身数据 消息体中包含自身用户ID以及房间号和游戏数据

    public static final int UPDATE_STATE = 2005;  // 更新房间内自身状态：如准备完成，游戏进行中，掉线等
    
    public static final int ROOM_SUCCESS = 2008;  // 返回给客户端发出的请求 成功消息
    
    public static final int ROOM_FAILURE = 2009;  // 返回给客户端发出的请求失败 重新发送
    
    public static final int ROOM_MAX = 2009;

    public static final int SYSTEM_MIN = 9000;

    public static final int SYSTEM_GET_INFO = 9000;  // 获取系统配置信息

    public static final int SYSTEM_SEND_INFO = 9001;  // 返回系统配置信息，指令体为系统相关配置 如心跳间隔等

    public static final int SYSTEM_UPLOAD_HEARTBEAT = 9002; // 发送心跳，无指令体

    public static final int SYSTEM_SEND_HEARTBEAT = 9003; // 服务器端响应心跳

    public static final int SYSTEM_RELOAD_CONFIG = 9998; // 申请热加载配置（配置更新后可以通过该指令进行重新加载）

    public static final int SYSTEM_SEND_RELOAD_CONFIG = 9999; // 回复热加载配置

    public static final int SYSTEM_MAX = 9999;
    
    public static final int PLAYER_OFFLINE = 10000; //用户下线
    
    public static final int UNICAST = 10001;//单播 只返回给特定的用户
    
    public static final int BROADCAST = 10002;//广播 广播给该房间的内的所有用户
   
    public static final int ROOM_DESTROY_BROADCAST = 10003;//广播 广播给该房间的内的所有用户房间解散
}
