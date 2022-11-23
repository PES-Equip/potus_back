package com.potus.app.config;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class ChatAuthorizer implements AuthorizationListener {

    UserService userService;

    public ChatAuthorizer(UserService userService){
        this.userService = userService;
    }


    @Override
    public boolean isAuthorized(HandshakeData handshakeData) {

        System.out.println(handshakeData.getAddress());
        System.out.println(handshakeData.getHttpHeaders().toString());
        String token = handshakeData.getSingleUrlParam("token");
        System.out.println(token);

        String uri = "https://oauth2.googleapis.com/tokeninfo?id_token="+ token;
        RestTemplate restTemplate = new RestTemplate();

        try {
            Object result = restTemplate.getForObject(uri, Object.class);
            Map<String,String> parsedResult = (Map<String, String>) result;

            String email = parsedResult.get("email");
            User user = userService.findByEmail(email);
            return true;
					/*
					GardenMember member = user.getGarden();
					if(member != null){
						System.out.println(Objects.equals(member.getGarden().getName(), "tet"));
					}
					else{
						System.out.println("BADDDD");
					}
					System.out.println(user.getStatus());
					System.out.println(token);
					System.out.println(uri);
					System.out.println(result);

					 */
        }
        catch (HttpClientErrorException exception){
            System.out.println(exception.getLocalizedMessage());
            return false;
        }
        catch (ResourceNotFoundException exception){
            System.out.println("YEPA");
            return false;
        }
    }
}
