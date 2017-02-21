/**   
 * @文件名称: GateServerChannelInitializer.java
 * @类路径: com.dsky.netty.server
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月20日 上午10:40:12
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.server;


import com.dsky.netty.pvpser.protocode.PVPSerProtocol;
import com.dsky.netty.pvpser.utils.ProReaderUtil;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @类功能说明：
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月20日 上午10:40:12
 * @版本：V1.0
 */
public class PVPServerChannelInitializer extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
	    ChannelPipeline p = ch.pipeline();
	    p.addLast(new ProtobufVarint32FrameDecoder());
	    p.addLast(new ProtobufDecoder(PVPSerProtocol.SocketRequest.getDefaultInstance()));

	    p.addLast(new ProtobufVarint32LengthFieldPrepender());
	    p.addLast(new ProtobufEncoder());
	    ch.pipeline().addLast("readTimeOutHandler",
                new ReadTimeoutHandler(Integer.parseInt(
                        ProReaderUtil.getInstance().getNettyPro().get("heartBeatTimeOut"))));
	    p.addLast(new PVPProtocolServerHandler());
	  }

}
