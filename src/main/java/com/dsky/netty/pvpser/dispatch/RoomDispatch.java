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
	 * @param ctx
	 *            ctx
	 * @param request
	 *            请求指令
	 */
	public static void dispatch(ChannelHandlerContext ctx, SocketRequest request) {
		if (request.getNumber() == ProtocolCode.JOIN_ROOM) {
			logger.debug("Protocol JOIN_ROOM message: " + request);
			System.out
					.println("[Server] -- 客户端请求的服务方法为： [ joinRoom(ctx, request) ]");
			joinRoom(ctx, request);
		} else if (request.getNumber() == ProtocolCode.WAITTING_USER_JOIN_ROOM) {
			logger.debug("Protocol WAITTING_USER_JOIN_ROOM message: " + request);
			System.out
					.println("[Server] -- 客户端请求的服务方法为： [ waittingUserJoinRoom(ctx, request) ]");
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
			logger.error("unknown protocol code: " + request.getNumber()
					+ ", message: " + request);
		}
	}

	/**
	 * 处理客户端发出加入房间请求
	 * 
	 * @param ctx
	 *            ctx
	 * @param request
	 *            请求指令
	 */
	public static void joinRoom(ChannelHandlerContext ctx, SocketRequest request) {
		SocketResponse.Builder response = null;
		String jsonMsg = String.valueOf(request.getRequestMsg());
		JSONObject jsonObj = null;
		String roomId = request.getRoomId();
		String userId = request.getUserId();
		String data = "";

		try {
			jsonObj = com.alibaba.fastjson.JSON.parseObject(jsonMsg);
			if (roomId != null && userId != null && jsonObj.containsKey("data")) {
				data = jsonObj.getString("data");
			} else
				throw new JSONException(
						"Request parameter incomplete or incorrect \n");
		} catch (JSONException e) {
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.ROOM_FAILURE);
			response.setResponseMsg("data format error not a json style！\n"
					+ request.toString() + "  " + e.getMessage());
			response.setSequence(0);
			System.out.println("[Server] -- 客户端请求加入房间失败： [ "
					+ request.toString() + " ]");
			ctx.writeAndFlush(response);
			return;
		}

		Room room = RoomRedis.getInstance().getRoom(roomId);
		User u = new User(userId, data);
		if (room != null
				&& ((room.getCurrentNumber() + 1) <= room.getNumbers())) {
			// 满足加入条件 加入房间 更新房间当前人数
			HashMap<String, User> map = (HashMap<String, User>) room
					.getMember();
			map.put(userId, u);
			room.setMember(map);
			room.setCurrentNumber(room.getCurrentNumber() + 1);
			// 先同步数据到redis //TODO 此处需要修改 确保健壮性
			RoomRedis.getInstance().del(roomId);
			RoomRedis.getInstance().add(roomId, room);
			// TODO 继续完善用户加入房间后的处理逻辑 例如通知房间内其他玩家
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.BROADCAST);
			response.setResponseMsg(JSON.toJSONString(room.getMember().values()
					.toArray()));// 将对象数组转成json字符串
			response.setRoomId(room.getRoomId());
			System.out.println("[Server] -- 客户端请求加入房间成功");
			ctx.writeAndFlush(response);
		}
	}

	/**
	 * 处理客户端发送创建房间信息 等待其他玩家加入
	 * 
	 * @param ctx
	 *            ctx
	 * @param request
	 *            请求指令
	 */
	public static void waittingUserJoinRoom(ChannelHandlerContext ctx,
			SocketRequest request) {
		SocketResponse.Builder response = null;
		String jsonMsg = String.valueOf(request.getRequestMsg());
		JSONObject jsonObj = null;
		String roomId = request.getRoomId();// 房间号
		String userId = request.getUserId();// 房主
		String data = "";// 房主信息
		String roomCreatetime = "";// 房间创建的时间 //TODO 是否需要房间创建的时间 需要根据具体需求
		String numbers = "";// 房间人数设定

		try {
			jsonObj = com.alibaba.fastjson.JSON.parseObject(jsonMsg);
			if (roomId != null && userId != null && jsonObj.containsKey("data")
					&& jsonObj.containsKey("roomCreatetime")
					&& jsonObj.containsKey("numbers")) {
				data = jsonObj.getString("data");
				roomCreatetime = jsonObj.getString("roomCreatetime");
				numbers = jsonObj.getString("numbers");
				try {
					// 创建房间
					Room room = new Room();
					room.setCurrentNumber(1);
					room.setRoomId(roomId);
					room.setNumbers(Integer.parseInt(numbers));
					room.setRoomCreatetime(Integer.parseInt(roomCreatetime));
					room.setRoomOwner(userId);
					User user = new User(userId, data);
					Map<String, User> map = new HashMap<String, User>();
					map.put(userId, user);
					room.setMember(map);
					room.setRoomStatus(1);// 设置房间处于等待状态 //TODO 房间状态需要具体考虑
											// 到底房间应该设定为哪些状态
					// 将房间信息存入Redis中
					RoomRedis.getInstance().add(roomId, room); // 保存到Redis中
				} catch (NumberFormatException e) {
					String parameter = "[roomId : " + roomId + "],[numbers : "
							+ numbers + "],[userId : " + userId
							+ "],[roomCreatetime : " + roomCreatetime + "]";
					response = SocketResponse.newBuilder();
					response.setNumber(ProtocolCode.ROOM_FAILURE);
					response.setResponseMsg("Incorrect parameter type ！\n"
							+ parameter + e.getMessage());
					response.setRoomId(roomId);
					response.setUserId(userId);
					System.out.println("[Server] -- 客户端请求加入房间失败： ["
							+ e.getMessage() + "\n ]");
					ctx.writeAndFlush(response);
				}
			} else
				throw new JSONException("parameter is not complete\n");
		} catch (JSONException e) {
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.ROOM_FAILURE);
			response.setResponseMsg("data format error not a json style！\n"
					+ jsonMsg + "\n" + e.getMessage());
			response.setRoomId(roomId);
			response.setUserId(userId);
			System.out.println("[Server] -- 客户端请求加入房间失败： [" + jsonMsg + "\n"
					+ e.getMessage() + "\n ]");
			ctx.writeAndFlush(response);
			return;
		}
		// TODO 下面两句为 测试语句 正式环境中注意删除
		Room room1 = RoomRedis.getInstance().getRoom(roomId);
		System.out.println("[Server]  -- 获取的房间信息是： " + room1.getCurrentNumber()
				+ room1.getMember().get("123456") + " .....");

		response = SocketResponse.newBuilder();
		response.setNumber(ProtocolCode.ROOM_SUCCESS);
		response.setResponseMsg("{\"msg\":\"Congratulations ! Room created successfully, waiting for players to join \"}");
		response.setRoomId(roomId); // TODO
		response.setUserId(userId);
		System.out.println("[Server] -- 客户端请求创建房间成功");
		ctx.writeAndFlush(response);
		System.out.println("返回信息成功 。。。");

		/*
		 * sendAttackTimeStart(sourceID, targetID); // TODO 是否要发
		 * timerAttackTimeOver(sourceID, targetID);
		 */

	}


	/**
	 * 处理客户端发出的退出房间请求
	 * 
	 * @param ctx
	 *            ctx
	 * @param request
	 *            请求指令
	 */
	public static void exitRoom(ChannelHandlerContext ctx, SocketRequest request) {
		SocketResponse.Builder response = null;
//		String jsonMsg = String.valueOf(request.getRequestMsg());
//		JSONObject jsonObj = null;
		String roomId = request.getRoomId();
		String userId = request.getUserId();

		try {
//			jsonObj = com.alibaba.fastjson.JSON.parseObject(jsonMsg);
			if (roomId != null && userId != null) {
				Room room = RoomRedis.getInstance().getRoom(roomId);

				if (room != null) {
					HashMap<String, User> map = (HashMap<String, User>) room
							.getMember();
					map.containsKey(userId);
					map.remove(userId);
					room.setMember(map);
					room.setCurrentNumber(room.getCurrentNumber() - 1);
					// TODO 继续完善用户离开房间后的处理逻辑 例如通知房间内其他玩家
					// 更新redis
					RoomRedis.getInstance().del(roomId);
					RoomRedis.getInstance().add(roomId, room);

					response = SocketResponse.newBuilder();
					response.setNumber(ProtocolCode.BROADCAST);
					response.setResponseMsg(JSON.toJSONString(room.getMember()
							.values().toArray()));// 将对象数组转成json字符串
					response.setRoomId(room.getRoomId());
					System.out.println("[Server] -- 客户端请求离开房间成功");
					ctx.writeAndFlush(response);
				}

			} else
				throw new JSONException(
						"Request parameter incomplete or incorrect\n");
		} catch (JSONException e) {
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.ROOM_FAILURE);
			response.setResponseMsg("data format error not a json style！\n"
					+ request.toString() + "\n" + e.getMessage());
			response.setRoomId(roomId); // TODO
			response.setUserId(userId);
			System.out.println("[Server] -- 客户端请求退出房间失败： [ " + e.getMessage()
					+ " ]");
			ctx.writeAndFlush(response);
			return;
		}

	}

	/**
	 * 处理客户房主发出的销毁房间请求
	 * 
	 * @param ctx
	 *            ctx
	 * @param request
	 *            请求指令
	 */
	public static void destroyRoom(ChannelHandlerContext ctx, SocketRequest request) {
	    	SocketResponse.Builder response = null;
	        String jsonMsg = String.valueOf(request.getRequestMsg());
	        JSONObject jsonObj = null;
	        String roomId = request.getRoomId();
	        String userId = request.getUserId();
	        
	        try{
	        	jsonObj =  com.alibaba.fastjson.JSON.parseObject(jsonMsg);
	        	if(roomId != null && userId != null && jsonObj.containsKey("data")) {
	        		//获取用户请求销毁的房间的信息
	                Room room = RoomRedis.getInstance().getRoom(roomId);
	     	       
	    	        if(room!=null ) {
	    	        	//HashMap<String,User> map = (HashMap<String, User>) room.getMember();
	    	        	if(room.getRoomId().equals(roomId)) {
	    	        		RoomRedis.getInstance().del(roomId);
	    	        	//TODO 继续完善房主撤销房间后的处理逻辑 例如通知房间内其他玩家房间解散
	    		            
	    	            	response = SocketResponse.newBuilder();
	    	            	response.setNumber(ProtocolCode.ROOM_DESTROY_BROADCAST);//通知房间内所有玩家房间解散
	    	            	response.setResponseMsg("{\"msg\":\"Room dissolution\"}");
	    	            	response.setRoomId(roomId); //TODO
	    	            	System.out.println("[Server] -- 客户端请求销毁房间成功");
	    	            	ctx.writeAndFlush(response);
	    	            	return ;
	    	        	} else {
	    	            	response = SocketResponse.newBuilder();
	    	            	response.setNumber(ProtocolCode.ROOM_FAILURE);
	    	            	response.setResponseMsg(userId+" destroy room ["+room.getRoomId()+"] failure ,You have no right to destroy the room ！\n");
	    	            	response.setRoomId(roomId); //TODO
	    	            	response.setUserId(userId);
	    	            	System.out.println("[Server] -- 客户端请求销毁房间失败");
	    	            	ctx.writeAndFlush(response);
	    	            	return ;
	    	        	}
	        	} else
	        		throw new JSONException("data not contain roomId field\n");
	    	    }
	        }catch(JSONException e) {
	        	response = SocketResponse.newBuilder();
	        	response.setNumber(ProtocolCode.ROOM_FAILURE);
	        	response.setResponseMsg("data format error not a json style！\n"+jsonMsg+"\n"+e.getMessage());
	        	response.setRoomId(roomId); //TODO
	        	response.setUserId(userId);
	        	System.out.println("[Server] -- 客户端请求销毁房间失败： [ "+e.getMessage()+" ]");
	        	ctx.writeAndFlush(response);
	        	return ;
	        }
	    }

	/**
	 * 处理客户端发出更新游戏信息请求
	 * 
	 * @param ctx
	 *            ctx
	 * @param request
	 *            请求指令
	 */
	public static void updateUserData(ChannelHandlerContext ctx,
			SocketRequest request) {
		SocketResponse.Builder response = null;
		String jsonMsg = String.valueOf(request.getRequestMsg());
		JSONObject jsonObj = null;
		String roomId = request.getRoomId();
		String userId = request.getUserId();
		String data = "";

		try {
			jsonObj = com.alibaba.fastjson.JSON.parseObject(jsonMsg);
			if (roomId != null  && userId != null
					&& jsonObj.containsKey("data")) {
				data = jsonObj.getString("data");
			} else
				throw new JSONException("parameter not complete \n" + jsonMsg);
		} catch (JSONException e) {
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.ROOM_FAILURE);
			response.setResponseMsg("data format error not a json style！\n"
					+ jsonMsg + "\n" + e.getMessage());
			response.setRoomId(roomId); // TODO
			response.setUserId(userId);
			System.out.println("[Server] -- 客户端请求销毁房间失败： [ " + e.getMessage()
					+ " ]");
			ctx.writeAndFlush(response);
			return;
		}

		Room room = RoomRedis.getInstance().getRoom(roomId);

		if (room != null) {
			HashMap<String, User> map = (HashMap<String, User>) room
					.getMember();
			if (map.containsKey(userId)) {
				User user = map.get(userId);
				user.setData(data);
				map.remove(userId);
				map.put(userId, user);
				room.setMember(map);
				RoomRedis.getInstance().del(roomId);
				RoomRedis.getInstance().add(roomId, room);
			} else {
				response = SocketResponse.newBuilder();
				response.setNumber(ProtocolCode.ROOM_FAILURE);
				response.setResponseMsg(userId + " update [" + room.getRoomId()
						+ "] failure ！\n");
				response.setRoomId(roomId); // TODO
				response.setUserId(userId);
				System.out.println("[Server] -- 客户端更新房间成功");
				ctx.writeAndFlush(response);
				return;
			}
		}

		// TODO 返回整个房间的玩家的所有信息
		response = SocketResponse.newBuilder();
		response.setNumber(ProtocolCode.BROADCAST);
		response.setResponseMsg(JSON.toJSONString(room.getMember()
				.values().toArray()));// 将对象数组转成json字符串
		response.setRoomId(room.getRoomId());
		System.out.println("[Server] -- 客户端请求更新游戏数据成功");
		ctx.writeAndFlush(response);
	}

	/**
	 * 向房间内的玩家发送开始攻击指令
	 * 
	 * @param sourceID
	 *            用户ID
	 * @param targetID
	 *            对手ID
	 */
	private static void sendAttackTimeStart(String sourceID, String targetID) {

		/*
		 * SocketResponse response = new SocketResponse();
		 * 
		 * response.setNumber(ProtocolCode.BATTLE_SEND_ATTACK_START);
		 * response.setResult(0); response.setValueMap(null);
		 * 
		 * SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
		 * SocketManager
		 * .getDefaultStore().get(targetID).writeAndFlush(response);
		 */
	}

	/**
	 * 布局时间到定时器
	 * 
	 * @param sourceID
	 *            用户ID
	 * @param targetID
	 *            对手ID
	 */
	private static void timerLayoutTimeOver(final String sourceID,
			final String targetID) {
		/*
		 * // 启动布局时间定时器 Timer timer = new HashedWheelTimer();
		 * timer.newTimeout(new TimerTask() {
		 * 
		 * @Override public void run(Timeout timeout) throws Exception {
		 * SocketResponse response = new SocketResponse();
		 * response.setNumber(ProtocolCode.BATTLE_SEND_LAYOUT_TIME_OVER);
		 * response.setResult(0); response.setValueMap(null);
		 * 
		 * SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
		 * SocketManager
		 * .getDefaultStore().get(targetID).writeAndFlush(response); } },
		 * Config.LAYOUT_TIMEOVER, TimeUnit.SECONDS);
		 */
	}

	/**
	 * 攻击时间结束定时器
	 * 
	 * @param sourceID
	 *            用户ID
	 * @param targetID
	 *            对手ID
	 */
	private static void timerAttackTimeOver(final String sourceID,
			final String targetID) {
		/*
		 * // 启动攻击时间定时器 Timer timer = new HashedWheelTimer();
		 * timer.newTimeout(new TimerTask() {
		 * 
		 * @Override public void run(Timeout timeout) throws Exception {
		 * SocketResponse response = new SocketResponse();
		 * response.setNumber(ProtocolCode.BATTLE_SEND_ATTACK_TIME_OVER);
		 * response.setResult(0); response.setValueMap(null);
		 * 
		 * SocketManager.getDefaultStore().get(sourceID).writeAndFlush(response);
		 * SocketManager
		 * .getDefaultStore().get(targetID).writeAndFlush(response); } },
		 * Config.ATTACK_TIMEOVER, TimeUnit.SECONDS);
		 */
	}

}
