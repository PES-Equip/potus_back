package com.potus.app.garden.service;


import com.potus.app.exception.ConflictException;
import com.potus.app.exception.ForbiddenException;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.garden.model.Garden;
import com.potus.app.garden.model.GardenMember;
import com.potus.app.garden.model.GardenRole;
import com.potus.app.garden.repository.GardenMemberRepository;
import com.potus.app.garden.repository.GardenRepository;
import com.potus.app.garden.utils.GardenExceptionMessages;
import com.potus.app.garden.utils.GardenUtils;
import com.potus.app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static com.potus.app.garden.utils.GardenExceptionMessages.*;

@Service
public class GardenService {

    @Autowired
    GardenRepository gardenRepository;

    @Autowired
    GardenMemberRepository gardenMemberRepository;

    public List<Garden> getAll(){
        return gardenRepository.findAll();
    }

    public boolean existsByName(String name) {
        return gardenRepository.existsByName(name);
    }

    @Transactional
    public GardenMember createGarden(User user, String name) {

        Garden garden = new Garden(name);
        GardenMember gardenMember = new GardenMember(garden, user, GardenRole.OWNER);
        garden.setMembers(Collections.singleton(gardenMember));
        saveFullGarden(garden);
        return gardenMember;
    }

    private void saveFullGarden(Garden garden) {
        gardenMemberRepository.saveAll(garden.getMembers());
        gardenRepository.save(garden);
    }

    public Garden findByName(String name){
        return gardenRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException(GARDEN_DOES_NOT_EXISTS));
    }

    @Transactional
    public void removeUser(GardenMember member){
        if(member.getRole().equals(GardenRole.OWNER))
            throw  new ConflictException(GARDEN_OWNER_CAN_NOT_EXIT);

        Garden garden = member.getGarden();
        gardenMemberRepository.delete(member);
        garden.getMembers().remove(member);
        gardenRepository.save(garden);
    }

    public void removeUser(GardenMember action, GardenMember userRemove) {
        if(action.getRole().compareTo(userRemove.getRole()) < 1)
            throw new ForbiddenException();

        removeUser(userRemove);
    }


    public void deleteGarden(Garden garden) {
        gardenRepository.delete(garden);
    }

    public GardenMember findByUser(User user) {
        return gardenMemberRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException(USER_HAS_NOT_GARDEN));
    }

    public Garden editDescription(Garden garden, String description) {
        garden.setDescription(description);
        return gardenRepository.save(garden);
    }

    public GardenMember addUser(Garden garden, User user) {

        if(garden.getMembers().size() == GardenUtils.GARDEN_MAX_SIZE)
            throw new ConflictException(GARDEN_MAX_SIZE);

        if(user.getGarden() != null)
            throw new ConflictException(USER_HAS_GARDEN);

        GardenMember gardenMember = new GardenMember(garden, user, GardenRole.NORMAL);
        garden.getMembers().add(gardenMember);
        garden.setMembers(garden.getMembers());
        saveFullGarden(garden);
        return gardenMember;
    }

}
