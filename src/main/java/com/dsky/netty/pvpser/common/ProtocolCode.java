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
	//客户端发起操作的命令代码：
	public static final int ROOM_MIN = 2000;

    public static final int JOIN_ROOM = 2000; //申请加入一个房间  //消息体中包含 自身相关信息json格式

    public static final int CREATE_ROOM = 2001;  // 创建房间，等待其他玩家加入 消息体中包含所创建的房间的信息如游戏时长及房间人数设定等json格式
    
    public static final int EXIT_ROOM = 2002;  // 退出房间 消息体为空即可
 
    public static final int DESTROY_ROOM = 2003; // 销毁一个房间 (只能房主发起，也代表解散当前房间) 消息体可以为空
    
    public static final int UPDATE_USER_DATA = 2004;  //更新自身数据 用户当前的状态(用户当前积分等)
    
    public static final int UPDATE_GAME_DATA = 2005;  //更新自身数据 用户当前的状态(用户当前最新的游戏数据(指令等))
    
    public static final int UPDATE_USER_STATUS = 2006;  // 更新自身状态：如准备完成
    
    public static final int GAME_OVER = 2007;  // 适用于客户端主动发起游戏结束的情况   
    
    public static final int ROOM_MAX = 2009;
    
    //服务端对命令做出响应的命令代码
    public static final int BROADCAST_JOIN_ROOM = 2101; //向房间内的用户广播某个用户加入房间成功 用扩展字段来代表加入房间的用户ID,消息体中为加入房间用户的具体信息 json格式表示
  
    public static final int BROADCAST_CREATE_ROOM = 2102; //通知玩家创建房间成功 消息体为空
  
    public static final int BROADCAST_EXIT_ROOM = 2103; //通知房间内所有玩家某个玩家退出房间    
    
    public static final int BROADCAST_DESTROY_ROOM = 2104; //通知房间内所有玩家房间解散 消息体可以为空  
    
    public static final int BROADCAST_UPDATE_USER_DATA = 2105;  //通知房间内所有玩家某个用户玩家信息更新
    
    public static final int BROADCAST_UPDATE_GAME_DATA = 2106;  //通知房间内所有玩家某个用户玩家信息更新  
    
    public static final int BROADCAST_UPDATE_USER_STATE = 2107;  //通知房间内所有玩家某个用户玩家信息更新  
    
    public static final int BROADCAST_GAME_OVER = 2108;  // 适用于服务端控制游戏结束的情况 
    
    public static final int BROADCAST_GAME_START = 2109;  // 适用于服务端控制游戏开始的情况 
    
    public static final int REQUEST_FAILURE = 2110;  // 返回给客户端发出的请求失败 请重新发送 用户原始请求的指令会保存到备用字段中    
    
    //系统及一些其它的指令
    public static final int SYSTEM_MIN = 9000;

    public static final int SYSTEM_GET_INFO = 9000;  // 获取系统配置信息

    public static final int SYSTEM_SEND_INFO = 9001;  // 返回系统配置信息，指令体为系统相关配置 如心跳间隔等

    public static final int SYSTEM_UPLOAD_HEARTBEAT = 9002; // 发送心跳，无指令体

    public static final int SYSTEM_SEND_HEARTBEAT = 9003; // 服务器端响应心跳

    public static final int SYSTEM_RELOAD_CONFIG = 9998; // 申请热加载配置（配置更新后可以通过该指令进行重新加载）

    public static final int SYSTEM_SEND_RELOAD_CONFIG = 9999; // 回复热加载配置

    public static final int SYSTEM_MAX = 9999;
    
    public static final int PLAYER_OFFLINE = 10000; //心跳检测用户下线
    
}
