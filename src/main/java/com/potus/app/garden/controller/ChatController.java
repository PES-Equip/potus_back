package com.potus.app.garden.controller;


import com.potus.app.garden.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.UUID;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/message/{garden}")
    @SendTo("/chatroom/{garden}")
    public ChatMessage receiveMessage(@DestinationVariable String garden, @Payload ChatMessage message){
        message.setId(UUID.randomUUID().toString());
        message.setDate(new Date().toString());
        return message;
    }

    @MessageMapping("/private-message")
    public ChatMessage recMessage(@Payload ChatMessage message){
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        return message;
    }


}
