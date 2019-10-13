
package com.evan.androiddemos.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private Dispatcher mDispatcher;

    public HttpServerInitializer(Dispatcher dispatcher) {
        super();

        mDispatcher = dispatcher;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new HttpResponseEncoder());
        ch.pipeline().addLast(new HttpRequestDecoder());
        ch.pipeline().addLast(new HttpServerHandler(mDispatcher));
    }
}
