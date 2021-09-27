package com.hoang.demoNetty.netty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hoang.demoNetty.dto.WsMessage;
import com.hoang.demoNetty.entity.StockRealTimeEntity;
import com.hoang.demoNetty.multithread.FakeDataThread;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class NettySocketServer implements WebSocketServer{

    private final Gson gson = new GsonBuilder().create();

    @Autowired
    private WebSocketHandler webSocketHandler;

    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final Map<String, ChannelGroup> channelGroupMap = new HashMap<>();
    private final CopyOnWriteArrayList<StockRealTimeEntity> data = new CopyOnWriteArrayList<>();

    @Value("${server.netty.port}")
    private int port;

    @Bean
    public void init() {
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup subGroup = new NioEventLoopGroup();

        new Thread(() -> {
            try {
                ServerBootstrap server = new ServerBootstrap();
                server.group(mainGroup, subGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new HttpServerCodec())
                                        .addLast(new ChunkedWriteHandler())
                                        .addLast(new HttpObjectAggregator(1024 * 64))
                                        .addLast(new WebSocketServerProtocolHandler("/test"))
                                        .addLast(webSocketHandler);
                            }
                        });
                ChannelFuture future = server.bind(port).sync();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mainGroup.shutdownGracefully();
                subGroup.shutdownGracefully();
            }
        }).start();
    }

    @Override
    public void broadcast(WsMessage message) {
        channelGroup.writeAndFlush(new TextWebSocketFrame(gson.toJson(message)));
    }

    @Override
    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    @Override
    public Map<String, ChannelGroup> getChannelGroupMap() {
        return channelGroupMap;
    }

    @Override
    public void stackData(StockRealTimeEntity data) {
        this.data.add(data);
    }

    @Override
    public CopyOnWriteArrayList<StockRealTimeEntity> getData() {
        return data;
    }

    @Bean
    public void autoBroadcast() {
        new Thread(() -> {
            try {
                while (true) {
                    broadcast(WsMessage.buildData(data));
                    data.clear();
                    Thread.sleep(50);
                    }
                }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}