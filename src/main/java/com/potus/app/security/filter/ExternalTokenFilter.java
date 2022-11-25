package com.potus.app.security.filter;

import com.potus.app.admin.service.AdminService;
import com.potus.app.config.InitialDataConfiguration;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.service.PotusRegistryService;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.potus.app.admin.utils.AdminUtils.APITOKEN_TYPE;
import static com.potus.app.potus.utils.PotusExceptionMessages.POTUS_IS_DEAD;
import static com.potus.app.user.utils.UserExceptionMessages.USER_MUST_CONFIRM_FIRST;
import static com.potus.app.user.utils.UserUtils.getUser;

public class ExternalTokenFilter extends OncePerRequestFilter {

    public ExternalTokenFilter(AdminService adminService){
        this.adminService = adminService;
    }

    private final AdminService adminService;

    Logger logger = LoggerFactory.getLogger(InitialDataConfiguration.class);

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(token == null || ! token.contains(APITOKEN_TYPE) || token.split(" ").length != 2){
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "token API Token must be defined");
            return;
        }

        if(! adminService.existsToken(token.split(" ")[1])){
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "API Token does not exists");
            return;
        }
        filterChain.doFilter(request,response);
    }
}
