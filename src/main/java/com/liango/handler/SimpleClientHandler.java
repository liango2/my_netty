package com.liango.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ("ping".equals(msg.toString())) {
            System.out.println("客户端接收到了 = ping 并且 客户端 回发了一个同样的 ping");
            ctx.channel().writeAndFlush("ping\r\n");
            return;
        }else{
            System.out.println("客户端接收到了 = " + msg.toString());
        }
        ctx.channel().attr(AttributeKey.valueOf("sssss")).set(msg);
//        ctx.channel().close();
    }

}