package com.potus.app.user.service;


import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.meetings.model.Meeting;
import com.potus.app.meetings.service.MeetingsService;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.PotusModifier;
import com.potus.app.potus.service.PotusService;
import com.potus.app.potus.utils.ModifierUtils;
import com.potus.app.user.model.User;
import com.potus.app.user.repository.UserRepository;
import com.potus.app.user.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.potus.app.user.utils.UserExceptionMessages.*;

@Service
public class UserService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    PotusService potusService;

    @Autowired
    MeetingsService meetingsService;

    Logger logger = LoggerFactory.getLogger(UserService.class);


    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow( () ->
                new ResourceNotFoundException(userNotFound(id)));
    }

    public User findByUsername(String username) throws ResourceNotFoundException {
        return  userRepository.findByUsername(username).orElseThrow( () ->
                new ResourceNotFoundException(userNotFound(username)));
    }

    public User findByEmail(String email) throws ResourceNotFoundException {
        return  userRepository.findByEmail(email).orElseThrow( () ->
                new ResourceNotFoundException(userNotFound(email)));
    }


    public boolean exists(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.isPresent();
    }

    public User setUsername(User user, String username) throws ResourceAlreadyExistsException{
        try{
            User userExist = findByUsername(username);
            throw new ResourceAlreadyExistsException(USERNAME_ALREADY_TAKEN);
        } catch (ResourceNotFoundException ignored) {}

        user.setUsername(username);
        return saveUser(user);
    }

    public User createPotus(User user, String name) {
        Potus potus = potusService.createPotus(name);
        user.setPotus(potus);
        return saveUser(user);
    }

    public User addCurrency(User user, Integer amount){
        user.setCurrency(user.getCurrency() + amount);
        return saveUser(user);
    }

    public User saveUser(User newUser){
        return userRepository.save(newUser);
    }


    public void addAdmins() {
        List<String> adminMails = UserUtils.adminUsers();

        for(String mail : adminMails) {
            try {
                User user = findByEmail(mail);

                if (!user.getAdmin()){
                    user.setAdmin(Boolean.TRUE);
                    saveUser(user);
                    logger.info("Admin user with email "+mail+" is now an admin.");
                }
            }
            catch (ResourceNotFoundException ignore){
                logger.info("Admin user with email "+mail+" haven't been created yet");
            }
        }
    }

    public User checkAdmin(User user) {
        List<String> adminMails = UserUtils.adminUsers();
        if (adminMails.contains(user.getEmail())) {
            user.setAdmin(Boolean.TRUE);
        }
        return user;
    }


    @Transactional
    public User newPotus(User user, String name) {
        Potus potus = potusService.createPotus(name);
        user.setPotus(potus);
        return user;
    }


    /**
     * DELETES POTUS - REGISTRY - USER
     * @param user
     */
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void upgradeModifier(User user, PotusModifier selectedModifier) {

        Double price = ModifierUtils.getCurrentPrice(selectedModifier.getModifier().getPrice(),selectedModifier.getLevel());

        if(user.getCurrency() < price)
            throw new ResourceAlreadyExistsException(USER_HAS_NOT_ENOUGH_CURRENCY);

        potusService.upgradeModifier(selectedModifier);
        user.setCurrency((int) (user.getCurrency() - price));
    }

    public void addMeeting(User user, Long meetingId) {
        Meeting meeting = meetingsService.getMeetingById(meetingId);

        Set<Meeting> meetings = user.getMeetings();

        for(Meeting meetingAux : meetings) {
            if(Objects.equals(meetingAux.getId(), meetingId)) throw new ResourceAlreadyExistsException(USER_ALREADY_HAS_ADDED_MEETING);
        }

        user.addMeeting(meeting);
        saveUser(user);
    }

    public void deleteMeeting(User user, Long meetingId) {
        for(Meeting meeting : user.getMeetings()) {
            if(Objects.equals(meeting.getId(), meetingId)) {
                user.deleteMeeting(meeting);
                saveUser(user);
                return;
            }
        }
        throw new ResourceNotFoundException(USER_DOES_NOT_HAVE_MEETING);
    }

}
