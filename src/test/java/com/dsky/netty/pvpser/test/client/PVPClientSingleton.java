/**   
 * @文件名称: PVPClientSingleton.java
 * @类路径: com.dsky.netty.pvpser.test.client
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月27日 下午2:30:06
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.test.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.dsky.netty.pvpser.common.ProtocolCode;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest.Builder;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketResponse;

/**
 * @类功能说明： 单例类 （双重校验锁）
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月27日 下午2:30:06
 * @版本：V1.0
 */
public class PVPClientSingleton {
	private volatile static PVPClientSingleton singleton;
	private PVPClientSingleton(){}
	public static PVPClientSingleton getSingleton() {
		if(singleton == null) {
			System.out.println("test ..");
			synchronized(PVPClientSingleton.class) {
				if(singleton == null) {
					System.out.println("test 1");
					singleton = new PVPClientSingleton();
				}
			}
		}
		return singleton;
	}
	
	static final String HOST = System.getProperty("host","127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port","9000"));

	
	public static SocketResponse send(SocketRequest socketRequest) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new PVPClientInitializer());
			
			//创建连接
			Channel c = bootstrap.connect(HOST,PORT).sync().channel();
			
			//获取一个handle 来发送消息
			PVPClientHandler handle = c.pipeline().get(PVPClientHandler.class);
			
			System.out.println("调用了sendRequest 方法 ...");

			// 发送请求
			c.writeAndFlush(socketRequest);
			System.out.println("[client] -- 发送的请求信息体是： "+socketRequest.getRequestMsg());

			while(true) {
				SocketResponse resp = handle.sendRequest();
				
				if(resp.getResponseMsg() == null )
					return null;
				else {
					System.out.println("Got reponse msg from Server : "+resp.getResponseMsg());
					c.close();
					return resp;
				}
			}
		} finally {
			
			group.shutdownGracefully();
		}
	}

}
