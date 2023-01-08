package com.potus.app.garden.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import com.potus.app.user.model.User;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ChatMessage {

    @Id
    private String id;

    @NotNull

    private User sender;

    @CreatedDate
    @NotNull
    private Date date;

    @NotNull
    private String room;

    @NotNull
    private MessageType status;



}
