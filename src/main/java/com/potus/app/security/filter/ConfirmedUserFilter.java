package com.potus.app.security.filter;

import com.potus.app.security.CustomSession;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.potus.app.user.utils.UserExceptionMessages.USER_MUST_CONFIRM_FIRST;
import static com.potus.app.user.utils.UserUtils.getUser;


public class ConfirmedUserFilter extends OncePerRequestFilter {

    private final RequestMatcher uriMatcher =
            new AntPathRequestMatcher("/api/user/profile", HttpMethod.GET.name());

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {

        //CustomSession session = (CustomSession) request.getUserPrincipal();
        User user = getUser();

        if(user.getStatus() == UserStatus.NEW){
            response.sendError(HttpStatus.BAD_REQUEST.value(), USER_MUST_CONFIRM_FIRST);
            return;
        }

        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return uriMatcher.matches(request);
    }

}
