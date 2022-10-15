package com.potus.app.user.service;


import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.model.User;
import com.potus.app.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.potus.app.user.utils.UserExceptionMessages.*;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PotusService potusService;

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

    public User createPotus(User user) {
        Potus potus = potusService.createPotus();
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

}
