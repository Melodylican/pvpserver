/**   
 * @文件名称: RoomDispatch.java
 * @类路径: com.dsky.netty.pvpser.dispatch
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月21日 上午10:29:08
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.dispatch;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dsky.netty.pvpser.common.Keys;
import com.dsky.netty.pvpser.common.ProtocolCode;
import com.dsky.netty.pvpser.model.Room;
import com.dsky.netty.pvpser.model.User;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketResponse;
import com.dsky.netty.pvpser.redis.RoomRedis;

/**
 * @类功能说明：处理与房间相关的请求
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月21日 上午10:29:08
 * @版本：V1.0
 */
public class RoomDispatch {
	 private static final Logger logger = Logger.getLogger(RoomDispatch.class);

	    /**
	     * 分发客户端请求
	     *
	     * @param ctx ctx
	     * @param request 请求指令
	     */
	    public static void dispatch(ChannelHandlerContext ctx, SocketRequest request) {
	        if (request.getNumber() == ProtocolCode.JOIN_ROOM) {
	            logger.debug("Protocol JOIN_ROOM message: " + request);
	            System.out.println("[Server] -- 客户端请求的服务方法为： [ joinRoom(ctx, request) ]");
	            joinRoom(ctx, request);
	        } else if (request.getNumber() == ProtocolCode.WAITTING_USER_JOIN_ROOM) {
	            logger.debug("Protocol WAITTING_USER_JOIN_ROOM message: " + request);
	            waittingUserJoinRoom(ctx, request);
	        } else if (request.getNumber() == ProtocolCode.EXIT_ROOM) {
	            logger.debug("Protocol EXIT_ROOM message: " + request);
	            exitRoom(ctx, request);
	        } else if (request.getNumber() == ProtocolCode.DESTROY_ROOM) {
	            logger.debug("Protocol DESTROY_ROOM message: " + request);
	            destroyRoom(ctx, request);
	        } else if (request.getNumber() == ProtocolCode.UPDATE_USER_DATA) {
	            logger.debug("Protocol UPDATE_USER_DATA message: " + request);
	            updateUserData(ctx, request);
	        } else {
	            logger.error("unknown protocol code: " + request.getNumber() + ", message: " + request);
	        }
	    }

	    /**
	     * 处理客户端发出的请求加入房间
	     *
	     * @param ctx ctx
	     * @param request 请求指令
	     */
	    public static void joinRoom(ChannelHandlerContext ctx, SocketRequest request) {
	    	SocketResponse.Builder response = null;
	        String jsonMsg = String.valueOf(request.getRequestMsg());
	        JSONObject jsonObj = null;
	        String roomId = "";
	        String userId = "";
	        String data = "";
	        
	        try{
	        	jsonObj =  com.alibaba.fastjson.JSON.parseObject(jsonMsg);
	        	if(jsonObj.containsKey("roomId") && jsonObj.containsKey("userId") && jsonObj.containsKey("data")) {
	        		roomId = jsonObj.getString("roomId");
	        		userId = jsonObj.getString("userId");
	        		data = jsonObj.getString("data");
	        	} else
	        		throw new JSONException("data not contain roomId field\n");
	        }catch(JSONException e) {
	        	response = SocketResponse.newBuilder();
	        	response.setNumber(ProtocolCode.ROOM_FAILURE);
	        	response.setResponseMsg("data format error not a json style！\n"+e.getMessage());
	        	System.out.println("[Server] -- 客户端请求加入房间失败： [ e.getMessage() ]");
	        	ctx.writeAndFlush(response);
	        }

	        Room room = RoomRedis.getInstance().getRoom(roomId);
	        User u = new User(userId,data);
	        if((room.getCurrentNumber() + 1) <= room.getNumbers()) {
	        	HashMap<String,User> map = (HashMap<String, User>) room.getMember();
	        	map.put(userId, u);
	        	room.setMember(map);
	        	room.setCurrentNumber(room.getCurrentNumber()+1);
	        	//TODO 继续完善用户加入房间后的处理逻辑
	        }
	        RoomRedis.getInstance().del(roomId);
	        RoomRedis.getInstance().add(roomId, room);
	
	    }

	    /**
	     * 处理客户端发送创建房间信息 等待其他玩家加入
	     *
	     * @param ctx ctx
	     * @param request 请求指令
	     */
	    public static void waittingUserJoinRoom(ChannelHandlerContext ctx, SocketRequest request) {

	        /*

	    	final String sourceID = String.valueOf(request.getSourceID());
	        final String targetID = String.valueOf(request.getTargetID());

	        String roomID = UserRedis.getInstance().get(sourceID, Keys.USER_ROOM_ID);

	        RoomRedis.getInstance().add(roomID, Keys.ROOM_LAYOUT_INFO_PREFIX + sourceID, JSON.toJSONString(request.getParamMap()));
	        String targetLayoutInfo = RoomRedis.getInstance().get(roomID, Keys.ROOM_LAYOUT_INFO_PREFIX + targetID);

	        if (targetLayoutInfo != null && !targetLayoutInfo.equals("")){
	            RoomRedis.getInstance().del(roomID, Keys.ROOM_LAYOUT_INFO_PREFIX + sourceID);
	            RoomRedis.getInstance().del(roomID, Keys.ROOM_LAYOUT_INFO_PREFIX + targetID);

	            SocketResponse response = new SocketResponse();
	            response.setNumber(ProtocolCode.BATTLE_SEND_LAYOUT_INFO);
	            response.setResult(0);
	            response.setValueMap(JSON.parseObject(targetLayoutInfo, new TypeReference<Map<String, String>>() {}));

	            ctx.writeAndFlush(response);
	            response.setValueMap(request.getParamMap());
	            SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);

	            sendAttackTimeStart(sourceID, targetID); // TODO 是否要发
	            timerAttackTimeOver(sourceID, targetID);
	        }

*/

	    }

	    /**
	     * 向双方发送开始攻击指令
	     *
	     * @param sourceID 用户ID
	     * @param targetID 对手ID
	     */
	    private static void sendAttackTimeStart(String sourceID, String targetID) {

	        /*
	    	SocketResponse response = new SocketResponse();

	        response.setNumber(ProtocolCode.BATTLE_SEND_ATTACK_START);
	        response.setResult(0);
	        response.setValueMap(null);

	        SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
	        SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
	        */
	    }

	    /**
	     * 处理客户端发出的退出房间请求
	     *
	     * @param ctx ctx
	     * @param request 请求指令
	     */
	    public static void exitRoom(ChannelHandlerContext ctx, SocketRequest request) {
	    	/*
	        String sourceID = String.valueOf(request.getSourceID());
	        String targetID = String.valueOf(request.getTargetID());
	        String roomID = UserRedis.getInstance().get(sourceID, Keys.USER_ROOM_ID);

	        RoomRedis.getInstance().add(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + sourceID, JSON.toJSONString(request.getParamMap()));
	        String targetAttackInfo = RoomRedis.getInstance().get(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + targetID);

	        if (targetAttackInfo != null && !targetAttackInfo.equals("")){
	            RoomRedis.getInstance().del(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + sourceID);
	            RoomRedis.getInstance().del(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + targetID);

	            SocketResponse response = new SocketResponse();
	            response.setNumber(ProtocolCode.BATTLE_SEND_ATTACK_INFO);
	            response.setResult(0);
	            response.setValueMap(JSON.parseObject(targetAttackInfo, new TypeReference<Map<String, String>>() {}));

	            ctx.writeAndFlush(response);

	            response.setValueMap(request.getParamMap());
	            SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
	        }
	        */
	    }

	    /**
	     * 处理客户房主发出的销毁房间请求
	     *
	     * @param ctx ctx
	     * @param request 请求指令
	     */
	    public static void destroyRoom(ChannelHandlerContext ctx, SocketRequest request) {
	    	/*
	        String sourceID = String.valueOf(request.getSourceID());
	        String targetID = String.valueOf(request.getTargetID());
	        String roomID = UserRedis.getInstance().get(sourceID, Keys.USER_ROOM_ID);

	        RoomRedis.getInstance().add(roomID, Keys.ROOM_ATTACK_RESULT_PREFIX + sourceID, JSON.toJSONString(request.getParamMap()));
	        String targetAttackResult = RoomRedis.getInstance().get(roomID, Keys.ROOM_ATTACK_RESULT_PREFIX + targetID);

	        if (targetAttackResult != null && !targetAttackResult.equals("")){
	            RoomRedis.getInstance().del(roomID, Keys.ROOM_ATTACK_RESULT_PREFIX + sourceID);
	            RoomRedis.getInstance().del(roomID, Keys.ROOM_ATTACK_RESULT_PREFIX + targetID);

	            // 增加回合数
	            RoomRedis.getInstance().add(roomID, Keys.ROOM_ROUND, 1);

	            Map<String, String> targetParamMap = JSON.parseObject(targetAttackResult, new TypeReference<Map<String, String>>() {});
	            int sourceRemain = Integer.parseInt(targetParamMap.get(Keys.ROOM_ATTACK_RESULT_PLAIN_REMAIN));
	            int targetRemain = Integer.parseInt(request.getValue(Keys.ROOM_ATTACK_RESULT_PLAIN_REMAIN));

	            if (sourceRemain != 0 && targetRemain != 0){
	                sendAttackTimeStart(sourceID, targetID);
	                timerAttackTimeOver(sourceID, targetID);
	            } else {
	                sendBattleResult(roomID, sourceID, targetID);
	            }
	        }
	        */
	    }
	    
	    /**
	     * 处理客户端发出更新游戏信息请求
	     *
	     * @param ctx ctx
	     * @param request 请求指令
	     */
	    public static void updateUserData(ChannelHandlerContext ctx, SocketRequest request) {
	    	/*
	        String sourceID = String.valueOf(request.getSourceID());
	        String targetID = String.valueOf(request.getTargetID());
	        String roomID = UserRedis.getInstance().get(sourceID, Keys.USER_ROOM_ID);

	        RoomRedis.getInstance().add(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + sourceID, JSON.toJSONString(request.getParamMap()));
	        String targetAttackInfo = RoomRedis.getInstance().get(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + targetID);

	        if (targetAttackInfo != null && !targetAttackInfo.equals("")){
	            RoomRedis.getInstance().del(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + sourceID);
	            RoomRedis.getInstance().del(roomID, Keys.ROOM_ATTACK_INFO_PREFIX + targetID);

	            SocketResponse response = new SocketResponse();
	            response.setNumber(ProtocolCode.BATTLE_SEND_ATTACK_INFO);
	            response.setResult(0);
	            response.setValueMap(JSON.parseObject(targetAttackInfo, new TypeReference<Map<String, String>>() {}));

	            ctx.writeAndFlush(response);

	            response.setValueMap(request.getParamMap());
	            SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
	        }
	        */
	    }


	    /**
	     * 布局时间到定时器
	     *
	     * @param sourceID 用户ID
	     * @param targetID 对手ID
	     */
	    private static void timerLayoutTimeOver(final String sourceID, final String targetID) {
	    	/*
	        // 启动布局时间定时器
	        Timer timer = new HashedWheelTimer();
	        timer.newTimeout(new TimerTask() {
	            @Override
	            public void run(Timeout timeout) throws Exception {
	                SocketResponse response = new SocketResponse();
	                response.setNumber(ProtocolCode.BATTLE_SEND_LAYOUT_TIME_OVER);
	                response.setResult(0);
	                response.setValueMap(null);

	                SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
	                SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
	            }
	        }, Config.LAYOUT_TIMEOVER, TimeUnit.SECONDS);
	        */
	    }

	    /**
	     * 攻击时间结束定时器
	     *
	     * @param sourceID 用户ID
	     * @param targetID 对手ID
	     */
	    private static void timerAttackTimeOver(final String sourceID, final String targetID) {
	    	/*
	        // 启动攻击时间定时器
	        Timer timer = new HashedWheelTimer();
	        timer.newTimeout(new TimerTask() {
	            @Override
	            public void run(Timeout timeout) throws Exception {
	                SocketResponse response = new SocketResponse();
	                response.setNumber(ProtocolCode.BATTLE_SEND_ATTACK_TIME_OVER);
	                response.setResult(0);
	                response.setValueMap(null);

	                SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
	                SocketManager.getDefaultStore().get(targetID).writeAndFlush(response);
	            }
	        }, Config.ATTACK_TIMEOVER, TimeUnit.SECONDS);
	        */
	    }

}
