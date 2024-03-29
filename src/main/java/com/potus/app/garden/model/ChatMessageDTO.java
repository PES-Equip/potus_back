package com.potus.app.garden.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ChatMessageDTO {

    private String id;
    private String senderName;
    private String receiverName;
    private String message;
    private String date;
    private MessageType status;
}