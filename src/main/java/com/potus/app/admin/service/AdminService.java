package com.potus.app.admin.service;

import com.potus.app.admin.model.APIToken;
import com.potus.app.admin.repository.APITokenRepository;
import com.potus.app.exception.ResourceAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

import static com.potus.app.admin.utils.AdminExceptionMessages.TOKEN_NAME_ALREADY_EXISTS;
import static com.potus.app.admin.utils.AdminUtils.ABC;
import static com.potus.app.admin.utils.AdminUtils.APITOKEN_LEN;

@Service
public class AdminService {

    @Autowired
    APITokenRepository apiTokenRepository;

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

    public APIToken createToken(String name){

        if(apiTokenRepository.existsByName(name))
            throw new ResourceAlreadyExistsException(TOKEN_NAME_ALREADY_EXISTS);

        String newTokenValue = generateToken();

        while(existsToken(newTokenValue))
            newTokenValue = generateToken();

        APIToken token = new APIToken(newTokenValue, name);

        return apiTokenRepository.save(token);
    }

    public List<APIToken> getAllTokens() {
        return apiTokenRepository.findAll();
    }
}
