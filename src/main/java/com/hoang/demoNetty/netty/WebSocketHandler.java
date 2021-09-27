package com.hoang.demoNetty.netty;

import com.hoang.demoNetty.dto.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    WebSocketServer webSocketServer;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        webSocketServer.getChannelGroup().add(ctx.channel());
        addSubscriber(ctx.channel(), Collections.singletonList(Command.SUBSCRIBE_ALL.toString()));
        log.info("add ws: {}", ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("remove ws: {}", ctx.channel().id().asShortText());
    }

    private void addSubscriber(Channel channel, List<String> topics) {
        topics.forEach(topic -> {
            if (!webSocketServer.getChannelGroupMap().containsKey(topic)) {
                log.info("add topic {}", topic);
                webSocketServer.getChannelGroupMap().put(topic, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
            }
            webSocketServer.getChannelGroup().add(channel);
        });
    }

    private void removeSubscriber(Channel channel) {
        for (Map.Entry<String, ChannelGroup> entry : webSocketServer.getChannelGroupMap().entrySet()) {
            entry.getValue().remove(channel);
        }
    }
}
