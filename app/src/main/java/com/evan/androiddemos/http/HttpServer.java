
package com.evan.androiddemos.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer {
    private int mPort;
    private Dispatcher mDispatcher;
    private NioEventLoopGroup mBossGroup;
    private NioEventLoopGroup mWorkerGroup;
    private ChannelFuture mChannelFuture;

    public HttpServer(int port, Dispatcher dispatcher) {
        if (dispatcher == null) {
            throw new IllegalArgumentException("dispatcher is null");
        }
        mPort = port;
        mDispatcher = dispatcher;
    }

    public void start() throws Exception {
        mBossGroup = new NioEventLoopGroup();
        mWorkerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(mBossGroup, mWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer(mDispatcher))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        mChannelFuture = b.bind(mPort).sync();
    }

    public void stop() {
        try {
            mChannelFuture.sync().channel().close().sync();
            mWorkerGroup.shutdownGracefully();
            mBossGroup.shutdownGracefully();
        } catch (Exception e) {
        }
    }
}
