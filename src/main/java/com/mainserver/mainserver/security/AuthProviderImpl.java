package com.mainserver.mainserver.security;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class AuthProviderImpl implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        RestTemplate restTemplate = new RestTemplate();
            String ans = restTemplate.getForObject("http://localhost:8080/admin/checkout?username={username}&password={password}",
                    String.class, authentication.getName(), authentication.getCredentials());
            System.out.println(ans);
            if (ans.equals("Ok")) return new UsernamePasswordAuthenticationToken(authentication.getName(),
                    authentication.getCredentials(), Collections.emptyList());
            else if (ans.equals("password")) throw new UsernameNotFoundException("Wrong password");
            else if (ans.equals("name")) throw new UsernameNotFoundException("Admins with this name not found");
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
