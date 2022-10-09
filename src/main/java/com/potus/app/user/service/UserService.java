package com.potus.app.user.service;


import com.potus.app.user.exception.ResourceNotFoundException;
import com.potus.app.user.model.User;
import com.potus.app.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User findById(String id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow( () ->
                new ResourceNotFoundException("User", id));
    }

    public User findByUsername(String username) throws ResourceNotFoundException {
        return  userRepository.findByUsername(username).orElseThrow( () ->
                new ResourceNotFoundException("User", username));
    }

    public User findByEmail(String email) throws ResourceNotFoundException {
        return  userRepository.findByEmail(email).orElseThrow( () ->
                new ResourceNotFoundException("User", email));
    }


    public boolean exists(String id) {
        Optional<User> user = userRepository.findById(id);
        return user.isPresent();
    }

    public User saveUser(User newUser){
        return userRepository.save(newUser);
    }

}
