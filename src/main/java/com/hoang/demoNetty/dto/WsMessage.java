package com.hoang.demoNetty.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WsMessage {

    private String command;
    private Object data;

    public WsMessage(String command, Object data) {
        this.command = command;
        this.data = data;
    }

    public static WsMessage buildData(Object data) {
        return new WsMessage(Command.SUBSCRIBE_ALL.name(), data);
    }
}
