package com.potus.app.security.filter;

import com.potus.app.potus.model.Potus;
import com.potus.app.potus.service.PotusService;
import com.potus.app.security.CustomSession;
import com.potus.app.user.model.Trophy;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserStatus;
import com.potus.app.user.service.TrophyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.potus.app.potus.utils.PotusExceptionMessages.POTUS_IS_DEAD;
import static com.potus.app.user.model.TrophyType.GARDEN_TIME;
import static com.potus.app.user.model.TrophyType.POTUS_TIME;
import static com.potus.app.user.utils.UserExceptionMessages.USER_MUST_CONFIRM_FIRST;
import static com.potus.app.user.utils.UserUtils.getUser;

public class PotusStatesFilter extends OncePerRequestFilter {

    private final PotusService potusService;

    private final TrophyService trophyService;

    private final RequestMatcher uriMatcherDELETE =
            new AntPathRequestMatcher("/api/user/profile", HttpMethod.DELETE.name());

    private final RequestMatcher uriMatcherPOST =
            new AntPathRequestMatcher("/api/user/profile", HttpMethod.POST.name());

    public PotusStatesFilter(PotusService potusService, TrophyService trophyService){
        this.potusService = potusService;
        this.trophyService = trophyService;
    }


    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {

        User user = getUser();
        Potus potus = user.getPotus();
        if(user.getStatus() == UserStatus.CONFIRMED) {
            if(potus.isAlive()) {
                potusService.updatePotusStats(potus);
                trophyService.updateTrophyDateBased(user, POTUS_TIME, potus.getCreatedDate());
            }
        }

        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return uriMatcherDELETE.matches(request)  || uriMatcherPOST.matches(request);
    }

}
