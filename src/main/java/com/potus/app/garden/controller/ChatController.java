package com.potus.app.garden.controller;


import com.potus.app.garden.model.ChatMessageDTO;
import com.potus.app.garden.model.GardenMember;
import com.potus.app.garden.service.GardenService;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.UUID;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private GardenService gardenService;

    @MessageMapping("/message/{garden}")
    @SendTo("/chatroom/{garden}")
    public ChatMessageDTO receiveMessage(@DestinationVariable String garden, @Payload ChatMessageDTO message){
        message.setId(UUID.randomUUID().toString());
        message.setDate(new Date().toString());

        User user = userService.findByUsername(message.getSenderName());
        gardenService.saveChatMessage(message, user, garden);
        return message;
    }

    /*
    @MessageMapping("/private-message")
    public ChatMessage recMessage(@Payload ChatMessage message){
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        return message;
    }
    */



}
