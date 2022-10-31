package com.potus.app.potus.service;

import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.PotusRegistry;
import com.potus.app.potus.repository.PotusRegistryRepository;
import com.potus.app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PotusRegistryService {

    @Autowired
    PotusRegistryRepository potusRegistryRepository;

    public boolean existsByUserAndName(User user, String name){
        System.out.println( potusRegistryRepository.existsByUserAndName(user, name));
        return potusRegistryRepository.existsByUserAndName(user, name);
    }

    public PotusRegistry registryPotus(User user){
        Potus potus = user.getPotus();

        PotusRegistry potusRegistry = new PotusRegistry(potus.getName(), user, potus.getCreatedDate(), new Date());
        return potusRegistryRepository.save(potusRegistry);
    }

    public List<PotusRegistry> findByUser(User user) {
        return potusRegistryRepository.findByUser(user);
    }
}
