package com.potus.app.security.filter;

import com.potus.app.admin.model.BannedAccount;
import com.potus.app.admin.service.AdminService;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserStatus;
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

import static com.potus.app.user.utils.UserExceptionMessages.USER_MUST_CONFIRM_FIRST;
import static com.potus.app.user.utils.UserUtils.getUser;


public class BannedUserFilter extends OncePerRequestFilter {

    private final RequestMatcher uriMatcher =
            new AntPathRequestMatcher("/api/external/airquality/regions", HttpMethod.GET.name());

    private final RequestMatcher uriMatcher2 =
            new AntPathRequestMatcher("/api/external/airquality/region", HttpMethod.GET.name());
    public BannedUserFilter(AdminService adminService){
        this.adminService = adminService;
    }

    private final AdminService adminService;
    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {

        User user = getUser();
        if(adminService.emailIsBanned(user.getEmail())){
            response.sendError(HttpStatus.BAD_REQUEST.value(), "ACCOUNT IS BANNED");
            return;
        }

        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return uriMatcher.matches(request) || uriMatcher2.matches(request);
    }
}
