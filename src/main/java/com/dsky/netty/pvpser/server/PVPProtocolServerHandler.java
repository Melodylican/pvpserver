/**   
 * @文件名称: GateProtocolServerHandler.java
 * @类路径: com.dsky.netty.server
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月20日 上午10:57:21
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.server;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.dsky.netty.pvpser.common.ProtocolCode;
import com.dsky.netty.pvpser.dispatch.RoomDispatch;
import com.dsky.netty.pvpser.dispatch.SystemDispatch;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @类功能说明：PVPSer逻辑处理类
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月20日 上午10:57:21
 * @版本：V1.0
 */
public class PVPProtocolServerHandler extends
		SimpleChannelInboundHandler<SocketRequest> {
	private static final Logger logger = Logger
			.getLogger(PVPProtocolServerHandler.class);

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//System.out.println("read complete ...");
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx,
			SocketRequest request) throws Exception {

		logger.debug("channel read: " + request.toString());
		//System.out.println("channel read: " + request.toString());
		// 按照协议规定分发客户端请求到相应的处理逻辑
		if (request.getNumber() >= ProtocolCode.ROOM_MIN
				&& request.getNumber() <= ProtocolCode.ROOM_MAX) {
			//System.out.println("[Server] -- 客户端请求的服务代码为： ["+ request.getNumber() + "]");
			RoomDispatch.dispatch(ctx, request);
		} else if (request.getNumber() >= ProtocolCode.SYSTEM_MIN
				&& request.getNumber() <= ProtocolCode.SYSTEM_MAX) {
			SystemDispatch.dispatch(ctx, request);
		} else {
			logger.error("unknown codec code: " + request.getNumber()
					+ ", message: " + request);
		}
	}

	// 通道激活时做出的响应写这里
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception // 当客户端连上服务器的时候会触发此函数
	{
		//System.out.println("client:" + ctx.channel().id() + " join server");
		//SocketManager.getDefaultStore().add("123", ctx);

		///*
		SocketRequest.Builder response = SocketRequest.newBuilder();
		response.setNumber(ProtocolCode.BROADCAST_CREATE_ROOM);
		response.setRequestMsg("4513212122");// 将对象数组转成json字符串
		response.setRoomId("1321321");
		response.setUserId("2321321");
		//System.out.println("[Server] -- 客户端请求加入房间成功");
		ctx.writeAndFlush(response.build());
		//*/
		//logger.info("clinet:" + ctx.channel().id() + " join server");
	}

	// 通道断开时做出的响应写这里
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception// 当客户端断开连接的时候触发函数
	{
		System.out.println("client:" + ctx.channel().id() + " leave server");
		//logger.info("client:" + ctx.channel().id() + " leave server");
		// User.onlineUser.remove(LoginDispatch.getInstance().user);
	}
}
