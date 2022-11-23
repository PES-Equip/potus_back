package com.potus.app;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.potus.app.config.ChatAuthorizer;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.garden.model.GardenMember;
import com.potus.app.security.CustomSession;
import com.potus.app.security.JwtConverter;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.potus.app.airquality.utils.AirQualityUtils.*;
import static com.potus.app.airquality.utils.AirQualityUtils.API_CODE_PARAM;


@SpringBootApplication
@EnableScheduling
public class AppApplication {

	@Autowired
	UserService userService;
	@Bean
	public SocketIOServer socketIOServer() {
		Configuration config = new Configuration();
		config.setHostname("localhost");
		config.setPort(9092);
		config.setAuthorizationListener(new ChatAuthorizer(userService));
		return new SocketIOServer(config);
	}

	public static void main(String[] args) { SpringApplication.run(AppApplication.class, args); }
}

