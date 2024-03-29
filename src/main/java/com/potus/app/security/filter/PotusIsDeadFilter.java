package com.potus.app.security.filter;

import com.potus.app.potus.model.Potus;
import com.potus.app.potus.service.PotusRegistryService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.model.TrophyType;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserStatus;
import com.potus.app.user.service.TrophyService;
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


    public PotusIsDeadFilter(PotusRegistryService potusRegistryService, TrophyService trophyService){
        this.potusRegistryService = potusRegistryService;
        this.trophyService = trophyService;
    }

    private final PotusRegistryService potusRegistryService;
    private final TrophyService trophyService;

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {

        User user = getUser();
        Potus potus = user.getPotus();

        String path = request.getRequestURI();

        if(user.getStatus() == UserStatus.CONFIRMED && ! potus.isAlive()) {

            if(! potusRegistryService.existsByUserAndName(user, potus.getName())) {
                potusRegistryService.registryPotus(user);
                trophyService.updateTrophy(user, TrophyType.DEAD, 1);
            }

            response.sendError(HttpStatus.BAD_REQUEST.value(), POTUS_IS_DEAD);
            return;
        }

        filterChain.doFilter(request,response);
    }

}
