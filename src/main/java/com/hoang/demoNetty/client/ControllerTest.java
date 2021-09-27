package com.hoang.demoNetty.client;

import com.hoang.demoNetty.dto.WsMessage;
import com.hoang.demoNetty.netty.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("test")
public class ControllerTest {

    @Autowired
    WebSocketServer webSocketServer;

    @PostMapping("ws")
    public String test(@RequestBody String message) {
        webSocketServer.broadcast(WsMessage.buildData(message));
        log.info("message {}", message);
        return message;
    }
}
