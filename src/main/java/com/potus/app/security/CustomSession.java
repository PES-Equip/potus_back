package com.potus.app.security;

import com.potus.app.user.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

public class CustomSession extends AbstractAuthenticationToken {

    private User user;
    private Jwt jwt;

    public CustomSession(User user, Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        this.jwt  = jwt;
        this.setAuthenticated(true);
    }

    public CustomSession() {
        super(null);
        this.setAuthenticated(false);
    }
    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}
