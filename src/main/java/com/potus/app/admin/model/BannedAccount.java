package com.potus.app.admin.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
public class BannedAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;


    @NotNull
    private String reason;

    @CreatedDate
    private Date date;

    public BannedAccount(String email, String reason, Date date){
        this.email = email;
        this.reason = reason;
        this.date = date;
    }
}
