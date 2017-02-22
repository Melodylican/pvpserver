/**   
 * @文件名称: GateClient.java
 * @类路径: com.dsky.netty.serverTest
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月20日 下午3:16:38
 * @版本：V1.0   
 */
package com.dsky.netty.serverTest;


import com.dsky.netty.pvpser.common.ProtocolCode;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @类功能说明：
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月20日 下午3:16:38
 * @版本：V1.0
 */
public class PVPClient {
	static final String HOST = System.getProperty("host","127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port","9000"));
	
	
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new PVPClientInitializer());
			
			//创建连接
			Channel c = bootstrap.connect(HOST,PORT).sync().channel();
			
			//获取一个handle 来发送消息
			PVPClientHandler handle = c.pipeline().get(PVPClientHandler.class);

			SocketResponse resp = handle.sendRequest();
			c.close();
			
			System.out.println("Got reponse msg from Server : "+resp.getResponseMsg());
		} finally {
			group.shutdownGracefully();
		}
	}

}
