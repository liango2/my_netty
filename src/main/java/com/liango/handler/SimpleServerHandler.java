package com.liango.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

/**
 * @author liango
 * @version 1.0
 * @since 2017-09-27 22:08
 *
 * 4,7
 */
public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取客户端通道的数据
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            System.out.println("读取客户端通道的数据: " + ((ByteBuf) msg).toString(Charset.defaultCharset()));
        }

        // 告诉客户端，我已经读到了你的数据了
        ctx.channel().writeAndFlush("client你好，我是server,我已经读到了你的数据了is ok\r\n");
//        ctx.channel().close();
    }
}
