package com.potus.app.security.filter;

import com.potus.app.potus.model.Potus;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.potus.app.potus.utils.PotusExceptionMessages.POTUS_IS_DEAD;
import static com.potus.app.user.utils.UserUtils.getUser;

public class PotusIsDeadFilter extends OncePerRequestFilter {


    public PotusIsDeadFilter(){}


    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {

        User user = getUser();
        Potus potus = user.getPotus();

        if (user.getStatus() == UserStatus.CONFIRMED && ! potus.isAlive()) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), POTUS_IS_DEAD);
            return;
        }

        filterChain.doFilter(request,response);
    }

}