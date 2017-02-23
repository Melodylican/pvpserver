/**   
 * @文件名称: GateServer.java
 * @类路径: com.dsky.netty.server
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月20日 上午10:22:35
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import org.apache.log4j.Logger;

import com.dsky.netty.pvpser.common.Config;
import com.dsky.netty.pvpser.utils.ProReaderUtil;

/**
 * @类功能说明：PVPserver 启动类
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月20日 上午10:22:35
 * @版本：V1.0
 */
/**
*
*                 _oo0oo_
                 o8888888o
                 88" . "88
                 (| -_- |)
                 0\  =  /0
               ___/`---'\___
             .' \\|     |-- '.
            / \\|||  :  |||-- \
           / _||||| -:- |||||- \
          |   | \\\  -  --/ |   |
          | \_|  ''\---/''  |_/ |
          \  .-\__  '-'  ___/-. /
        ___'. .'  /--.--\  `. .'___
     ."" '<  `.___\_<|>_/___.' >' "".
    | | :  `- \`.;`\ _ /`;.`/ - ` : | |
    \  \ `_.   \_ __\ /__ _/   .-` /  /
=====`-.____`.___ \_____/___.-`___.-'=====
                  `=---='
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
             	佛祖保佑    永无BUG
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*
*
* 负责游戏PVP 路由服务器的初始化，基础资源的加载，服务器进程的启动
*
*/
public class PVPServer {
	
	private static Logger logger = Logger.getLogger(PVPServer.class);
	
	/**
     * 监听端口号
     */
    private int PORT;

    public PVPServer() {
        //this.PORT = Integer.parseInt(ProReaderUtil.getInstance().getNettyPro().get("netty.port"));
        this.PORT = Config.SERVER_PORT;
        System.out.println(this.PORT);
    }

    /**
     * 初始化服务器设置
     *
     * @throws Exception
     */
    public void run() throws Exception {
    	 EventLoopGroup serverGroup = new NioEventLoopGroup(1);
	     EventLoopGroup workerGroup = new NioEventLoopGroup();
	    
	     try {
	    	 ServerBootstrap bootStrap = new ServerBootstrap();
	         bootStrap.group(serverGroup, workerGroup)
	         .channel(NioServerSocketChannel.class)
	         .handler(new LoggingHandler(LogLevel.INFO))
//	         /*
	         .childOption(ChannelOption.TCP_NODELAY, true)
             .childOption(ChannelOption.SO_KEEPALIVE, true)
             .childOption(ChannelOption.SO_REUSEADDR, true) //重用地址
             .childOption(ChannelOption.SO_RCVBUF, 1048576)
             .childOption(ChannelOption.SO_SNDBUF, 1048576)
             .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false))  // heap buf 's better
//           */
	         .childHandler(new PVPServerChannelInitializer());
	      
	        // Bind to port 
	        bootStrap.bind(PORT).sync().channel().closeFuture().sync();
	    } finally {
	    	serverGroup.shutdownGracefully();
	        workerGroup.shutdownGracefully();
	    }
    }

    public static void main(String[] args) throws Exception {
    	new PVPServer().run();
	}
}
