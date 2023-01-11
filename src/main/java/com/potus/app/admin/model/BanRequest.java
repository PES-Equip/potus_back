package com.potus.app.admin.model;

import com.potus.app.garden.model.Report;
import com.potus.app.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter @Setter
public class BanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    private List<Report> reports;

    @ManyToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public BanRequest(User user){
        this.user = user;
    }


}
