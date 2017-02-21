/**   
 * @文件名称: GateClientInitializer.java
 * @类路径: com.dsky.netty.serverTest
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月20日 下午3:25:58
 * @版本：V1.0   
 */
package com.dsky.netty.serverTest;


import com.dsky.netty.pvpser.protocode.PVPSerProtocol;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @类功能说明：
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月20日 下午3:25:58
 * @版本：V1.0
 */
public class PVPClientInitializer extends ChannelInitializer<SocketChannel>{


	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		
		p.addLast(new ProtobufVarint32FrameDecoder());
		p.addLast(new ProtobufDecoder(PVPSerProtocol.SocketResponse.getDefaultInstance()));
		
		p.addLast(new ProtobufVarint32LengthFieldPrepender());
		p.addLast(new ProtobufEncoder());
		
		p.addLast(new PVPClientHandler());
		
	}

}
