package com.bigdo.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: NettyClient
 * @Description:
 * @Author: TangJW
 */
public class NettyClient {

    public static Channel channel;

    public boolean startNetty(String ip, int port) throws InterruptedException {
        System.out.println("长连接开始");
        if (start(ip, port)) {
            System.out.println("长连接成功");
//            ByteBuf buf = Unpooled.wrappedBuffer(("我是客户端，已连接成功，IP为" + Utils.getIpAddress()).getBytes(CharsetUtil.UTF_8));
//            channel.writeAndFlush(buf);
            return true;
        }
        return false;
    }

    public void clientSend(String msg) {
        channel.writeAndFlush(msg);
    }

    private Boolean start(String ip, int port) throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .group(eventLoopGroup)
                .remoteAddress(ip, port)
                .handler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
                    @Override
                    protected void initChannel(io.netty.channel.socket.SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
//                                .addLast(new IdleStateHandler(1, 1, 1, TimeUnit.SECONDS))
                                .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
                                .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
                                .addLast(new NettyClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(ip, port)).sync();
            if (future.isSuccess()) {
                channel = future.channel();
                System.out.println("连接服务器成功--—------");
                return true;
            } else {
                System.out.println("连接服务器失败-—-------");
                return false;
            }
        } catch (Exception e) {
            System.out.println("连接失败" + e.toString());
            //这里最好暂停一下。不然会基本属于毫秒时间内执行很多次。
            //造成重连失败
            TimeUnit.SECONDS.sleep(1);

            return false;
        }
    }
}
