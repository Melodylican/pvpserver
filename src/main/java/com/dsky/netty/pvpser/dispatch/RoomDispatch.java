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

import com.dsky.netty.pvpser.common.ProtocolCode;
import com.dsky.netty.pvpser.model.Room;
import com.dsky.netty.pvpser.model.User;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketResponse;
import com.dsky.netty.pvpser.redis.RoomRedis;
import com.dsky.netty.pvpser.store.HeartbeatStore;

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
			//System.out.println("[Server] -- 客户端请求的服务方法为： [ joinRoom(ctx, request) ]");
			joinRoom(ctx, request);
		} else if (request.getNumber() == ProtocolCode.CREATE_ROOM) {
			logger.debug("Protocol WAITTING_USER_JOIN_ROOM message: " + request);
			//System.out.println("[Server] -- 客户端请求的服务方法为： [ waittingUserJoinRoom(ctx, request) ]");
			createRoom(ctx, request);
		} else if (request.getNumber() == ProtocolCode.EXIT_ROOM) {
			logger.debug("Protocol EXIT_ROOM message: " + request);
			exitRoom(ctx, request);
		} else if (request.getNumber() == ProtocolCode.DESTROY_ROOM) {
			logger.debug("Protocol DESTROY_ROOM message: " + request);
			destroyRoom(ctx, request);
		} else if (request.getNumber() == ProtocolCode.UPDATE_USER_DATA) {
			logger.debug("Protocol UPDATE_USER_DATA message: " + request);
			updateUserData(ctx, request);
		} else if (request.getNumber() == ProtocolCode.UPDATE_GAME_DATA) {
			logger.debug("Protocol UPDATE_GAME_DATA message: " + request);
			updateGameData(ctx, request);
		} else if (request.getNumber() == ProtocolCode.UPDATE_USER_STATUS) {
			logger.debug("Protocol UPDATE_USER_DATA message: " + request);
			updateUserStatus(ctx, request);
		} else {
			SocketResponse.Builder response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.REQUEST_FAILURE);
			response.setResponseMsg("{\"msg\":\"unknown protocol code: "+request.getNumber()+"}");
			response.setSequence(0);
			response.setUserId(request.getUserId());
			response.setRoomId(request.getRoomId());
			response.setReserve(request.getNumber()+"");
			if(request.getGateway() != null )
				response.setGateway(request.getGateway());
			//System.out.println("[Server] -- 客户端请求加入房间失败： [ "+ request.toString() + " ]");
			ctx.writeAndFlush(response);
			
			logger.warn("unknown protocol code: " + request.getNumber()
					+ ", message: " + request);
			return;
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
		String gamedata = "";
		String userdata = "";

		try {
			jsonObj = com.alibaba.fastjson.JSON.parseObject(jsonMsg);
			if (roomId != null && userId != null && jsonObj.containsKey("gamedata") && jsonObj.containsKey("userdata")) {
				gamedata = jsonObj.getString("gamedata");
				userdata = jsonObj.getString("userdata");
			} else
				throw new JSONException(
						"Request parameter incomplete or incorrect \n");
		} catch (JSONException e) {
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.REQUEST_FAILURE);
			response.setResponseMsg("data format error not a json style！\n"
					+ request.toString() + "  " + e.getMessage());
			response.setSequence(0);
			response.setUserId(userId);
			response.setRoomId(roomId);
			response.setReserve(request.getNumber()+"");
			if(request.getGateway() != null )
				response.setGateway(request.getGateway());
			//System.out.println("[Server] -- 客户端请求加入房间失败： [ "+ request.toString() + " ]");
			ctx.writeAndFlush(response);
			return;
		}

		Room room = RoomRedis.getInstance().getRoom(roomId);
		User u = new User(userId, gamedata, userdata);
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
			response.setNumber(ProtocolCode.BROADCAST_JOIN_ROOM);
			response.setResponseMsg(JSON.toJSONString(room.getMember().values()
					.toArray()));// 将对象数组转成json字符串
			response.setRoomId(room.getRoomId());
			response.setUserId(userId);
			//用于测试时间
			if(request.getReserve() != null) {
				response.setReserve(request.getReserve());
			}
			if(request.getGateway() != null )
				response.setGateway(request.getGateway());
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
	public static void createRoom(ChannelHandlerContext ctx,
			SocketRequest request) {
		SocketResponse.Builder response = null;
		String jsonMsg = String.valueOf(request.getRequestMsg());
		JSONObject jsonObj = null;
		String roomId = request.getRoomId();// 房间号
		String userId = request.getUserId();// 房主
		String gamedata = "";// 房主主要存放操作指令记录等
		String userdata = "";//房主信息主要存放用户其它一些信息 如积分等
		int roomCreatetime = 0;// 房间创建的时间 //TODO 是否需要房间创建的时间 需要根据具体需求
		String numbers = "";// 房间人数设定
		int time = 0;//一局游戏时长的设定 单位为秒

		try {
			jsonObj = com.alibaba.fastjson.JSON.parseObject(jsonMsg);
			
			if(jsonObj == null) {
				response = SocketResponse.newBuilder();
				response.setNumber(ProtocolCode.REQUEST_FAILURE);
				response.setResponseMsg("Incorrect parameter type ！\n");
				response.setRoomId(roomId);
				response.setUserId(userId);
				response.setReserve(request.getNumber()+"");
				return;
			}
			if (roomId != null && userId != null && jsonObj.containsKey("time")
					&& jsonObj.containsKey("roomCreatetime")
					&& jsonObj.containsKey("numbers")
					&& jsonObj.containsKey("userdata")
					&& jsonObj.containsKey("gamedata")) {
				gamedata = jsonObj.getString("gamedata");
				userdata = jsonObj.getString("userdata");
				time = jsonObj.getIntValue("time");
				roomCreatetime = jsonObj.getIntValue("roomCreatetime");
				numbers = jsonObj.getString("numbers");
				try {
					// 创建房间
					Room room = new Room();
					room.setCurrentNumber(1);
					room.setRoomId(roomId);
					room.setNumbers(Integer.parseInt(numbers));
					room.setRoomCreatetime(roomCreatetime);
					room.setRoomOwner(userId);
					room.setTime(time);
					User user = new User(userId, gamedata, userdata);
					Map<String, User> map = new HashMap<String, User>();
					map.put(userId, user);
					room.setMember(map);
					room.setRoomStatus(0);// 设置房间处于等待状态 //TODO 房间状态需要具体考虑
											// [0 匹配中 , 1 匹配成功状态 , 2 等待开始状态 , 3 游戏中 , 4 游戏结束 , 5 房间解散状态 ]
					// 将房间信息存入Redis中
					RoomRedis.getInstance().add(roomId, room); // 保存到Redis中
				} catch (NumberFormatException e) {
					String parameter = "[roomId : " + roomId + "],[numbers : "
							+ numbers + "],[userId : " + userId
							+ "],[roomCreatetime : " + roomCreatetime + "],[time : " + time + "]";
					response = SocketResponse.newBuilder();
					response.setNumber(ProtocolCode.REQUEST_FAILURE);
					response.setResponseMsg("Incorrect parameter type ！\n"
							+ parameter + e.getMessage());
					response.setRoomId(roomId);
					response.setUserId(userId);
					response.setReserve(request.getNumber()+"");
					if(request.getGateway() != null )
						response.setGateway(request.getGateway());
					//System.out.println("[Server] -- 客户端请求加入房间失败： ["+ e.getMessage() + "\n ]");
					ctx.writeAndFlush(response);
				}
			} else
				throw new JSONException("parameter is not complete\n");
		} catch (JSONException e) {
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.REQUEST_FAILURE);
			response.setResponseMsg("data format error not a json style！\n"
					+ jsonMsg + "\n" + e.getMessage());
			response.setRoomId(roomId);
			response.setUserId(userId);
			response.setReserve(request.getNumber()+"");
			//用于测试时间
			if(request.getReserve() != null)
				response.setReserve(request.getReserve());
			if(request.getGateway() != null )
				response.setGateway(request.getGateway());
			//System.out.println("[Server] -- 客户端请求加入房间失败： [" + jsonMsg + "\n"+ e.getMessage() + "\n ]");
			ctx.writeAndFlush(response);
			return;
		}
		response = SocketResponse.newBuilder();
		response.setNumber(ProtocolCode.BROADCAST_CREATE_ROOM);
		response.setResponseMsg("{\"msg\":\"Congratulations ! Room created successfully, waiting for players to join \"}");
		response.setRoomId(roomId); // TODO
		response.setUserId(userId);
		//用于测试时间延迟
		if(request.getReserve() != null)
			response.setReserve(request.getReserve());
		if(request.getGateway() != null )
			response.setGateway(request.getGateway());
		ctx.writeAndFlush(response);

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
					User exitUser = null;
					if(map.containsKey(userId)){
						exitUser = map.get(userId);
						map.remove(userId);
						room.setMember(map);
						room.setCurrentNumber(room.getCurrentNumber() - 1);
					}
					// TODO 继续完善用户离开房间后的处理逻辑 例如通知房间内其他玩家
					// 更新redis
					RoomRedis.getInstance().del(roomId);
					RoomRedis.getInstance().add(roomId, room);
					
					exitUser.setUserStatus(4); //4 为 退出房间状态
					map.put(userId, exitUser);

					response = SocketResponse.newBuilder();
					response.setNumber(ProtocolCode.BROADCAST_EXIT_ROOM);
					response.setResponseMsg(JSON.toJSONString(map
							.values().toArray()));// 将对象数组转成json字符串
					response.setRoomId(room.getRoomId());
					response.setUserId(userId);
					if(request.getGateway() != null )
						response.setGateway(request.getGateway());
					//System.out.println("[Server] -- 客户端请求离开房间成功");
					ctx.writeAndFlush(response);
				}

			} else
				throw new JSONException(
						"Request parameter incomplete or incorrect\n");
		} catch (JSONException e) {
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.REQUEST_FAILURE);
			response.setResponseMsg("data format error not a json style！\n"
					+ request.toString() + "\n" + e.getMessage());
			response.setRoomId(roomId); // TODO
			response.setUserId(userId);
			response.setReserve(request.getNumber()+"");
			if(request.getGateway() != null )
				response.setGateway(request.getGateway());
			//System.out.println("[Server] -- 客户端请求退出房间失败： [ " + e.getMessage()+ " ]");
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
	     	       
	    	        if(room != null ) {
	    	        	//HashMap<String,User> map = (HashMap<String, User>) room.getMember();
	    	        	if(room.getRoomId().equals(roomId)) {
	    	        		RoomRedis.getInstance().del(roomId);
	    	        	//TODO 继续完善房主撤销房间后的处理逻辑 例如通知房间内其他玩家房间解散
	    		            room.setRoomStatus(5); //5为房间销毁状态
	    		            room.setMember(null);
	    	            	response = SocketResponse.newBuilder();
	    	            	response.setNumber(ProtocolCode.BROADCAST_DESTROY_ROOM);//通知房间内所有玩家房间解散
	    	            	response.setResponseMsg(com.alibaba.fastjson.JSON.toJSONString(room));//将房间的状态返回给各个客户端
	    	            	response.setRoomId(roomId); //TODO
	    	            	response.setUserId(userId);
	    	    			if(request.getGateway() != null )
	    	    				response.setGateway(request.getGateway());
	    	            	//System.out.println("[Server] -- 客户端请求销毁房间成功");
	    	            	ctx.writeAndFlush(response);
	    	            	return ;
	    	        	} else {
	    	            	response = SocketResponse.newBuilder();
	    	            	response.setNumber(ProtocolCode.REQUEST_FAILURE);
	    	            	response.setResponseMsg(userId+" destroy room ["+room.getRoomId()+"] failure ,You have no right to destroy the room ！\n");
	    	            	response.setRoomId(roomId); //TODO
	    	            	response.setUserId(userId);
	    	            	response.setReserve(request.getNumber()+"");
	    	    			if(request.getGateway() != null )
	    	    				response.setGateway(request.getGateway());
	    	            	ctx.writeAndFlush(response);
	    	            	return ;
	    	        	}
	        	} else
	        		throw new JSONException("data not contain roomId field\n");
	    	    }
	        }catch(JSONException e) {
	        	response = SocketResponse.newBuilder();
	        	response.setNumber(ProtocolCode.REQUEST_FAILURE);
	        	response.setResponseMsg("data format error not a json style！\n"+jsonMsg+"\n"+e.getMessage());
	        	response.setRoomId(roomId); //TODO
	        	response.setUserId(userId);
	        	response.setReserve(request.getNumber()+"");
				if(request.getGateway() != null )
					response.setGateway(request.getGateway());
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
		String userdata = "";

		try {
			jsonObj = com.alibaba.fastjson.JSON.parseObject(jsonMsg);
			if (roomId != null  && userId != null
					&& jsonObj.containsKey("userdata")) {
				userdata = jsonObj.getString("userdata");
			} else
				throw new JSONException("parameter not complete \n" + jsonMsg);
		} catch (JSONException e) {
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.REQUEST_FAILURE);
			response.setResponseMsg("data format error not a json style！\n"
					+ jsonMsg + "\n" + e.getMessage());
			response.setRoomId(roomId); // TODO
			response.setUserId(userId);
			response.setReserve(request.getNumber()+"");
			if(request.getGateway() != null )
				response.setGateway(request.getGateway());
			ctx.writeAndFlush(response);
			return;
		}

		Room room = RoomRedis.getInstance().getRoom(roomId);

		if (room != null) {
			HashMap<String, User> map = (HashMap<String, User>) room
					.getMember();
			if (map.containsKey(userId)) {
				User user = map.get(userId);
				//user.setData(data);
				user.setUserData(userdata);
				map.remove(userId);
				map.put(userId, user);
				room.setMember(map);
				RoomRedis.getInstance().del(roomId);
				RoomRedis.getInstance().add(roomId, room);
				

				// TODO 返回整个房间的玩家的所有信息
				response = SocketResponse.newBuilder();
				response.setNumber(ProtocolCode.BROADCAST_UPDATE_USER_DATA);
				response.setResponseMsg(JSON.toJSONString(map
						.values().toArray()));// 将对象数组转成json字符串
				response.setRoomId(room.getRoomId());
				response.setUserId(userId);
				if(request.getGateway() != null )
					response.setGateway(request.getGateway());
				ctx.writeAndFlush(response);
			} else {
				response = SocketResponse.newBuilder();
				response.setNumber(ProtocolCode.REQUEST_FAILURE);
				response.setResponseMsg(userId + " update [" + room.getRoomId()
						+ "] failure ！\n");
				response.setRoomId(roomId); // TODO
				response.setUserId(userId);
				response.setReserve(request.getNumber()+"");
				if(request.getGateway() != null )
					response.setGateway(request.getGateway());
				ctx.writeAndFlush(response);
				return;
			}
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
	public static void updateGameData(ChannelHandlerContext ctx,
			SocketRequest request) {
		SocketResponse.Builder response = null;
		String jsonMsg = String.valueOf(request.getRequestMsg());
		JSONObject jsonObj = null;
		String roomId = request.getRoomId();
		//System.out.println("roomId  :  "+roomId);
		String userId = request.getUserId();
		String gamedata = "";

		try {
			jsonObj = com.alibaba.fastjson.JSON.parseObject(jsonMsg);
			if (roomId != null  && userId != null
					&& jsonObj.containsKey("gamedata")) {
				gamedata = jsonObj.getString("gamedata");
				//System.out.println("[Server] -- gamedata : "+gamedata);
			} else
				throw new JSONException("parameter not complete \n" + jsonMsg);
		} catch (JSONException e) {
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.REQUEST_FAILURE);
			response.setResponseMsg("data format error not a json style！\n"
					+ jsonMsg + "\n" + e.getMessage());
			response.setRoomId(roomId);
			response.setUserId(userId);
			response.setReserve(request.getNumber()+"");
			if(request.getGateway() != null )
				response.setGateway(request.getGateway());
			ctx.writeAndFlush(response);
			return;
		}

		Room room = RoomRedis.getInstance().getRoom(roomId);

		if (room != null) {
			HashMap<String, User> map = (HashMap<String, User>) room
					.getMember();
			if (map.containsKey(userId)) {
				User user = map.get(userId);
				//user.setData(data);
				user.setGameData(gamedata);
				map.remove(userId);
				map.put(userId, user);
				room.setMember(map);
				RoomRedis.getInstance().del(roomId);
				RoomRedis.getInstance().add(roomId, room);

				// TODO 返回整个房间的玩家的所有信息
				response = SocketResponse.newBuilder();
				response.setNumber(ProtocolCode.BROADCAST_UPDATE_GAME_DATA);
				response.setResponseMsg(JSON.toJSONString(map
						.values().toArray()));// 将对象数组转成json字符串
				response.setRoomId(room.getRoomId());
				response.setUserId(userId);
				if(request.getGateway() != null )
					response.setGateway(request.getGateway());
				ctx.writeAndFlush(response);
			} else {
				response = SocketResponse.newBuilder();
				response.setNumber(ProtocolCode.REQUEST_FAILURE);
				response.setResponseMsg(userId + " update [" + room.getRoomId()
						+ "] failure ！\n");
				response.setRoomId(roomId); // TODO
				response.setUserId(userId);
				response.setReserve(request.getNumber()+"");
				if(request.getGateway() != null )
					response.setGateway(request.getGateway());
				ctx.writeAndFlush(response);
				return;
			}
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
	public static void updateUserStatus(ChannelHandlerContext ctx,
			SocketRequest request) {
		SocketResponse.Builder response = null;
		String jsonMsg = String.valueOf(request.getRequestMsg());
		JSONObject jsonObj = null;
		String roomId = request.getRoomId();
		String userId = request.getUserId();
		int userstatus = 0;

		try {
			jsonObj = com.alibaba.fastjson.JSON.parseObject(jsonMsg);
			if (roomId != null  && userId != null
					&& jsonObj.containsKey("userstatus")) {
				userstatus = jsonObj.getIntValue("userstatus");
			} else
				throw new JSONException("parameter not complete \n" + jsonMsg);
		} catch (JSONException e) {
			response = SocketResponse.newBuilder();
			response.setNumber(ProtocolCode.REQUEST_FAILURE);
			response.setResponseMsg("data format error not a json style！\n"
					+ jsonMsg + "\n" + e.getMessage());
			response.setRoomId(roomId); // TODO
			response.setUserId(userId);
			response.setReserve(request.getNumber()+"");
			if(request.getGateway() != null )
				response.setGateway(request.getGateway());
			ctx.writeAndFlush(response);
			return;
		}

		Room room = RoomRedis.getInstance().getRoom(roomId);

		if (room != null) {
			HashMap<String, User> map = (HashMap<String, User>) room
					.getMember();
			if (map.containsKey(userId)) {
				User user = map.get(userId);
				user.setUserStatus(userstatus);
				map.remove(userId);
				map.put(userId, user);
				room.setMember(map);
				RoomRedis.getInstance().del(roomId);
				RoomRedis.getInstance().add(roomId, room);

				// TODO 返回整个房间的玩家的所有信息
				response = SocketResponse.newBuilder();
				response.setNumber(ProtocolCode.BROADCAST_UPDATE_USER_DATA);
				response.setResponseMsg(JSON.toJSONString(map
						.values().toArray()));// 将对象数组转成json字符串
				response.setRoomId(room.getRoomId());
				response.setUserId(userId);
				if(request.getGateway() != null )
					response.setGateway(request.getGateway());
				ctx.writeAndFlush(response);
				
				//TODO 接下来判断整个房间内的玩家是否都准备完成 如果都准备完成 则向所有玩家发送开始倒计时或者开始游戏命令
				//如果当前房间内人数已满 则开始判断是否所有玩家都准备完成
				if(room.getCurrentNumber() == room.getNumbers()) {
					for(User u:map.values()) {
						if(u.getUserStatus() != 1)
							return;
					}
					//所有玩家准备完成则发送开始命令并且启动游戏结束定时器
					//发送游戏开始
					
	            	SocketResponse.Builder startGame = SocketResponse.newBuilder();
	                response.setNumber(ProtocolCode.BROADCAST_GAME_START);
	                response.setRoomId(request.getRoomId());
	                response.setResponseMsg("Game Start!"); //测试
	                if(request.getGateway() != null )
	                	response.setGateway(request.getGateway());
	                ctx.writeAndFlush(response);
					
					//启动游戏结束定时器
					gameOverTimer(ctx,request,room.getTime());
				}
				
				
			} else {
				response = SocketResponse.newBuilder();
				response.setNumber(ProtocolCode.REQUEST_FAILURE);
				response.setResponseMsg(userId + " update [" + room.getRoomId()
						+ "] failure ！\n");
				response.setRoomId(roomId);
				response.setUserId(userId);
				response.setReserve(request.getNumber()+"");
				if(request.getGateway() != null )
					response.setGateway(request.getGateway());
				ctx.writeAndFlush(response);
				return;
			}
		}
	}
	
	
	
    /**
     * 心跳超时定时器
     *
     * @param sourceID 用户ID
     * @param roomID 房间ID
     */
    private static void gameOverTimer(final ChannelHandlerContext ctx, final SocketRequest request, int timelimit){
//    	/*
        Timer timer = new HashedWheelTimer();
        timer.newTimeout(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
            	
            	//通知房间内其他玩家 游戏结束了
            	SocketResponse.Builder response = SocketResponse.newBuilder();
                response.setNumber(ProtocolCode.BROADCAST_GAME_OVER);
                response.setRoomId(request.getRoomId());
                response.setResponseMsg("Game Over!"); //测试
                if(request.getGateway() != null )
                	response.setGateway(request.getGateway());
                //通知房间内的其他玩家 该玩家已掉线
                ctx.writeAndFlush(response);
                //向所有玩家发送游戏结束命令后 需要清空redis中该房间的数据 
                //TODO
                
            }
        }, timelimit, TimeUnit.SECONDS);

       HeartbeatStore.add(request.getRoomId() , timer);//保存当前定时器 用于处理游戏还未结束但所有玩家已经退出房间的情况
//        */
    }	
	
	/**
	 * 向房间内的玩家发送开始游戏指令
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
