package com.potus.app.garden.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;

import javax.persistence.*;

import com.potus.app.user.model.User;
import org.springframework.data.annotation.CreatedDate;

import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class ChatMessage {

    @Id
    private String id;

    @NotNull
    @ManyToOne
    @JsonIncludeProperties(value = {"username", "id"})
    private User sender;

    @CreatedDate
    @NotNull
    private Date date;

    @NotNull
    private String room;

    @NotNull
    private MessageType status;

    @NotNull
    private String message;



}
