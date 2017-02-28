package com.dsky.netty.pvpser.dispatch;


import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import org.apache.log4j.Logger;

import com.dsky.netty.pvpser.common.Config;
import com.dsky.netty.pvpser.common.ConfigUtil;
import com.dsky.netty.pvpser.common.Keys;
import com.dsky.netty.pvpser.common.ProtocolCode;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketResponse;
import com.dsky.netty.pvpser.server.SocketManager;
import com.dsky.netty.pvpser.store.HeartbeatStore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class SystemDispatch {
    private static final Logger logger = Logger.getLogger(SystemDispatch.class);
    
    /**
     * 分发客户端请求
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void dispatch(ChannelHandlerContext ctx, SocketRequest request){
        if (request.getNumber() == ProtocolCode.SYSTEM_GET_INFO){
            logger.debug("Protocol SYSTEM_GET_INFO message: " + request);
            getSystemInfo(ctx , request);
        } else if (request.getNumber() == ProtocolCode.SYSTEM_UPLOAD_HEARTBEAT) {
            logger.debug("Protocol SYSTEM_UPLOAD_HEARTBEAT message: " + request);
            System.out.println("调用发送心跳方法");
            heartBeat(ctx, request);
        } else if (request.getNumber() == ProtocolCode.SYSTEM_RELOAD_CONFIG){
            logger.debug("Protocol SYSTEM_RELOAD_CONFIG message: " + request);
            reloadConfig(ctx , request);
        } else {
            logger.error("unknown codec code: " + request.getNumber() + ", message: " + request);
        }
    }

    /**
     * 处理获取系统配置信息 获取服务端配置的心跳间隔
     *
     * @param ctx ctx
     */
    public static void getSystemInfo(ChannelHandlerContext ctx , SocketRequest request){
    	//TODO
        Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put(Keys.HEARTBEAT_INTERVAL, String.valueOf(Config.HEARTBEAT_INTERVAL));

        SocketResponse.Builder response = SocketResponse.newBuilder();
        response.setNumber(ProtocolCode.SYSTEM_SEND_INFO);
        response.setRoomId(request.getRoomId());
        response.setUserId(request.getUserId());
        response.setResponseMsg("{\"heartbeat_interval\" : "+ProtocolCode.SYSTEM_SEND_INFO+" }");

        ctx.writeAndFlush(response);
    }

    /**
     * 处理心跳请求
     *
     * @param ctx ctx
     * @param request 请求指令
     */
    public static void heartBeat(ChannelHandlerContext ctx, final SocketRequest request) {
        String userID = request.getUserId();//request 中获取userId
        String roomId = request.getRoomId();

        //收到用户的心跳数据 首先停止用户的超时计时器，并且将其删除
        Timer timer= HeartbeatStore.get(userID);
        if(null != timer) {
        	timer.stop();
        	HeartbeatStore.remove(userID);
        }
        
        //向客户端返回心跳包确认数据
        SocketResponse.Builder response = SocketResponse.newBuilder();
        response.setNumber(ProtocolCode.SYSTEM_SEND_HEARTBEAT);
        response.setSequence(0);
        response.setResponseMsg("[Server] -- 服务端收到心跳确认");
        ctx.writeAndFlush(response);

        //TODO
        //从新为该用户设定一个超时定时器
        timerSendHeartbeat(userID, roomId);
    }

    /**
     * 心跳超时定时器
     *
     * @param sourceID 用户ID
     * @param roomID 房间ID
     */
    private static void timerSendHeartbeat(final String userID, final String roomID){
//    	/*
        Timer timer = new HashedWheelTimer();
        timer.newTimeout(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
            	System.out.println(userID+"  掉线了 。。。");
            	/*
                SocketManager.getDefaultStore().get(sourceID).fireChannelInactive();
                SocketManager.getDefaultStore().get(sourceID).close();
                SocketManager.getDefaultStore().remove(sourceID);
            	 */
            	//通知房间内其他玩家 某个玩家掉线了
            	SocketResponse.Builder response = SocketResponse.newBuilder();
                response.setNumber(ProtocolCode.PLAYER_OFFLINE);
                response.setUserId(userID);
                response.setRoomId(roomID);
                response.setResponseMsg(userID+" 掉线了"); //测试
                //通知房间内的其他玩家 该玩家已掉线
                SocketManager.getDefaultStore().get("123").writeAndFlush(response);
                
            }
        }, Config.HEARTBEAT_TIMEOUT, TimeUnit.SECONDS);

        HeartbeatStore.add(userID, timer);
//        */
    }

    /**
     * 重新加载配置文件
     */
    public static void reloadConfig(ChannelHandlerContext ctx , SocketRequest request) {
    	
        ConfigUtil.init();
        SocketResponse.Builder response = SocketResponse.newBuilder();
        response.setNumber(ProtocolCode.SYSTEM_SEND_RELOAD_CONFIG);
        response.setUserId(request.getUserId());
        response.setRoomId(request.getRoomId());
        response.setResponseMsg("{\"system_status\":"+Keys.SYSTEM_STATUS_OK+"}");
        ctx.writeAndFlush(response);
        
    }
}
