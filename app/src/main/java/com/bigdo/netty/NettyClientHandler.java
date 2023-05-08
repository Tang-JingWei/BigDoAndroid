package com.bigdo.netty;

import android.os.Bundle;
import android.os.Message;
import com.bigdo.activity.MainActivity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: NettyClientHandler
 * @Description:
 * @Author: TangJW
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    //设置心跳时间   开始
    public static final int MIN_CLICK_DELAY_TIME = 1000 * 30;
    private long lastClickTime = 0;
    //设置心跳时间   结束
    //利用写空闲发送心跳检测消息


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                        lastClickTime = System.currentTimeMillis();
                        ByteBuf buf = Unpooled.wrappedBuffer("ping".getBytes(CharsetUtil.UTF_8));
                        ctx.writeAndFlush(buf);
                        System.out.println("向服务器发送 ping ");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object baseMsg) throws Exception {
        String msg = baseMsg.toString();
        System.out.println("接受服务端发送过来的消息 = " + msg);
        String[] split = msg.split(" ");

        Message message = new Message();
        message.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        message.setData(bundle);

        MainActivity.mClientHandler.sendMessage(message);
        ReferenceCountUtil.release(msg);
    }

    NettyClient nettyclient = new NettyClient();

    //这里是断线要进行的操作
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("重连了。—--------");
        //这里最好暂停一下。不然会基本属于毫秒时间内执行很多次。
        // 造成重连失败
//        TimeUnit.SECONDS.sleep(5);
//        nettyclient.startNetty();
    }


    //这里是出现异常的话要进行的操作
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("出现异常了.................");
        TimeUnit.SECONDS.sleep(3);
        //nettyClient.startNetty(context);
        cause.printStackTrace();
    }

}
