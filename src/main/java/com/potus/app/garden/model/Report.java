package com.potus.app.garden.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.potus.app.garden.model.ChatMessage;
import com.potus.app.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Report {


    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    @JsonIncludeProperties(value = {"username", "id"})
    private User reporter;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private ChatMessage message;

    public Report(User reporter, Date date, ChatMessage message){
        this.reporter = reporter;
        this.date = date;
        this.message = message;
    }
}
