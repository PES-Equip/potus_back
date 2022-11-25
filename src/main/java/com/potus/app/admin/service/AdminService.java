package com.potus.app.admin.service;

import com.potus.app.admin.model.APIToken;
import com.potus.app.admin.repository.APITokenRepository;
import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.user.model.User;
import com.potus.app.user.repository.UserRepository;
import com.potus.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

import static com.potus.app.admin.utils.AdminExceptionMessages.*;
import static com.potus.app.admin.utils.AdminUtils.ABC;
import static com.potus.app.admin.utils.AdminUtils.APITOKEN_LEN;

@Service
public class AdminService {

    @Autowired
    APITokenRepository apiTokenRepository;

    @Autowired
    UserRepository userRepository;

    private static SecureRandom random = new SecureRandom();


    public boolean existsToken(String token){
        return apiTokenRepository.existsByToken(token);
    }


    private String generateToken(){
        StringBuilder sb = new StringBuilder(APITOKEN_LEN);
        for(int i = 0; i < APITOKEN_LEN; i++)
            sb.append(ABC.charAt(random.nextInt(ABC.length())));

        return sb.toString();
    }

    public boolean existsByName(String name){
        return apiTokenRepository.existsByName(name);
    }

    public APIToken createToken(String name){

        if(existsByName(name))
            throw new ResourceAlreadyExistsException(TOKEN_NAME_ALREADY_EXISTS);

        String newTokenValue = generateToken();

        while(existsToken(newTokenValue))
            newTokenValue = generateToken();

        APIToken token = new APIToken(newTokenValue, name);

        return apiTokenRepository.save(token);
    }

    public void deleteToken(APIToken token) {
        apiTokenRepository.delete(token);
    }

    public List<APIToken> getAllTokens() {
        return apiTokenRepository.findAll();
    }

    public APIToken findByName(String name) {
        return apiTokenRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException(TOKEN_DOES_NOT_EXISTS));
    }

    public User addAdmin(User user) {

        if (user.getAdmin()) {
            throw new ResourceAlreadyExistsException(USER_IS_ALREADY_ADMIN);
        }

        user.setAdmin(true);
        return userRepository.save(user);
    }

    public User deleteAdmin(User user) {

        if (!user.getAdmin()) {
            throw new ResourceAlreadyExistsException(USER_IS_NOT_AN_ADMIN);
        }

        user.setAdmin(false);
        return userRepository.save(user);
    }
}
