package com.potus.app.potus.service;

import com.potus.app.potus.model.Potus;
import com.potus.app.potus.repository.PotusRepository;
import com.potus.app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public class PotusService {

    @Autowired
    PotusRepository potusRepository;

    public Potus savePotus(Potus potus){
        return potusRepository.save(potus);
    }
}
