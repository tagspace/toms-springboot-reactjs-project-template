package com.mywebsite.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                if(accessDeniedException.getClass() == InvalidCsrfTokenException.class) {
                    throw new RuntimeException("Invalid CSRF token");
                }
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()//starts chain for restricting access
                    .antMatchers("/", "/css/**", "/js/**", "/img/**")//maps requests at these paths
                    .permitAll()//urls are allowed by anyone
                .anyRequest()//maps any request
                    .authenticated()
                .and()
                    .exceptionHandling()
                        .accessDeniedHandler(accessDeniedHandler())//handles insuffient heroku permissions
                .and()
                    .formLogin()
                        .loginPage("/login")
                .and()
                    .logout().logoutUrl("/logout").logoutSuccessUrl("/");//note: this is a POST request
    }
}
