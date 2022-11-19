package com.potus.app.security.filter;

import com.potus.app.user.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.potus.app.admin.utils.AdminUtils.APITOKEN_TYPE;
import static com.potus.app.user.utils.UserExceptionMessages.USER_MUST_BE_ADMIN;
import static com.potus.app.user.utils.UserUtils.getUser;



public class AdminTokenFilter extends OncePerRequestFilter {

    private final RequestMatcher uriMatcher =
            new AntPathRequestMatcher("/api/admin/*", HttpMethod.GET.name());

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {
        User user = getUser();
        if (!user.getAdmin()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), USER_MUST_BE_ADMIN);
            return;
        }
        filterChain.doFilter(request,response);
    }

}


