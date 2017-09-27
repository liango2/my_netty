package com.liango.server;

import com.liango.constant.Constants;
import com.liango.factory.ZookeeperFactory;
import com.liango.handler.SimpleServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.net.InetAddress;


/**
 * @author liango
 * @version 1.0
 * @since 2017-09-27 21:43
 */
public class MyNettyServer {
    public static void main(String[] args) {
        // 创建一个服务器启动引擎
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // 指定group（就是指定两个group）,
        // 一个group（即线程组）用来监听端口的accept事件，
        // 另外一个线程组用来监听通道的read/write等等事件
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        //  1. 绑定两个线程组来处理客户端的accept和read/write事件；
        ChannelFuture channelFuture = null;
        try {
            channelFuture = serverBootstrap.group(parentGroup, childGroup)
                    .option(ChannelOption.SO_BACKLOG, 128)   //排队
                    .childOption(ChannelOption.SO_KEEPALIVE, false) // 心跳
                    //  2. 绑定服务端通道: NioServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    //  3. 给读写事件的线程通道绑定handler去真正处理读写
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                      .addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()))
                                    .addLast(new StringDecoder())
                                    .addLast(new SimpleServerHandler())
                                    .addLast(new StringEncoder());
                        }
                    })
                    //  4. 监听端口
                    .bind(8080).sync();

            // 获取ip
            String hostAddress = InetAddress.getLocalHost().getHostAddress();

            // 服务器注册到zk里面去
            CuratorFramework curatorFramework = ZookeeperFactory.create();
            curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(Constants.SERVER_PATH + hostAddress);

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();

            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
