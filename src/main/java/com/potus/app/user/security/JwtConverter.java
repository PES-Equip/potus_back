package com.potus.app.user.security;

import com.potus.app.user.exception.ResourceNotFoundException;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import com.sun.istack.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final UserService userService;


    public JwtConverter(UserService userService){
        this.userService = userService;
    }

    @Override
    public AbstractAuthenticationToken convert(@NotNull final Jwt jwt){
        String email = jwt.getClaim("email");
        User user = new User( email, null);
        try {
            user = userService.findByEmail(email);
        }
        catch (ResourceNotFoundException ex) {
            userService.saveUser(user);
        }
        Collection<? extends GrantedAuthority> authorities = translateAuthorities(jwt);
        return new CustomSession(user, jwt, authorities);
    }

    // Translate from your jwt as seen fit
    private static Collection<? extends GrantedAuthority> translateAuthorities(final Jwt jwt) {
        Collection<String> userRoles = jwt.getClaimAsStringList("roles");
        if (userRoles != null)
            return userRoles
                    .stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toSet());
        return Collections.emptySet();
    }
}