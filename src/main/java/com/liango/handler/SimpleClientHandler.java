package com.liango.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ("ping".equals(msg.toString())) {
            ctx.channel().writeAndFlush("ping\r\n");
            return;
        }
        ctx.channel().attr(AttributeKey.valueOf("sssss")).set(msg);
//        ctx.channel().close();
    }

}