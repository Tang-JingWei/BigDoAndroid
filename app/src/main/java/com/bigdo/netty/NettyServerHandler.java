package com.bigdo.netty;

import android.os.Bundle;
import android.os.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @ClassName: NettyServerHandler
 * @Description:
 * @Author: TangJW
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static ChannelHandlerContext ctx1;
    private static ChannelHandlerContext ctx2;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.err.println("link " + ctx.channel() + " established......");
        if (ctx1 == null) {
            ctx1 = ctx;
            Message msg = new Message();
            msg.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("msg", "客户端1已连接");
            msg.setData(bundle);
            //MainActivity.mMainHandler.sendMessage(msg);
        } else {
            ctx2 = ctx;
            Message msg = new Message();
            msg.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("msg", "客户端2已连接");
            msg.setData(bundle);
            //DataDisplay.mMainHandler.sendMessage(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (ctx == ctx1) {
            Message msg = new Message();
            msg.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("msg", "客户端1已断开");
            msg.setData(bundle);
            //DataDisplay.mMainHandler.sendMessage(msg);
            ctx1 = null;
        } else if (ctx == ctx2) {
            Message msg = new Message();
            msg.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("msg", "客户端2已断开");
            msg.setData(bundle);
            //DataDisplay.mMainHandler.sendMessage(msg);
            ctx2 = null;
        }
    }

    //这里是从客户端过来的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String rec = msg.toString();
        System.out.println("接受客户端发送过来的消息  收到 = " + rec);
        String[] split = rec.split(" ");
        if ("c".equals(split[0])) {
            Message message = new Message();
            message.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("msg", rec);
            message.setData(bundle);
           // DataDisplay.mMainHandler.sendMessage(message);
        } else {
            Message message = new Message();
            message.what = 0;
            Bundle bundle = new Bundle();
            bundle.putString("msg", rec);
            message.setData(bundle);
           // DataDisplay.mMainHandler.sendMessage(message);
        }

        if (ctx == ctx1 && ctx2 != null) {
            ctx2.writeAndFlush(msg);
        } else if (ctx == ctx2) {
            ctx1.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("我是服务器出现异常了!!!");
    }

    public void sendMessage(String msg) {
        ctx1.writeAndFlush(msg);
    }
}
