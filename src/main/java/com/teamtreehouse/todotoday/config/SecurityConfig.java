package com.teamtreehouse.todotoday.config;

import com.teamtreehouse.todotoday.service.UserService;
import com.teamtreehouse.todotoday.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.data.repository.query.spi.Function;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Map;

/**
 * Created by kpfromer on 3/24/17.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    /*

    BCrypt is a hashing function that is based on the Blowfish cipher.
    It is designed to allow the ability to be iteratively applied a specified number of times (referred to as the cost parameter).
    One advantage of this hash is that, as processors get faster and servers have the ability to process more requests concurrently,
    the number of iterations can be increased so that a "brute-force" attack can be slowed to the point of detection before the attack is successful.

    https://en.wikipedia.org/wiki/Bcrypt
    https://en.wikipedia.org/wiki/Blowfish_(cipher)
    BCryptPasswordEncoder

    */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**");//Ignore the security checks for all the static assets
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        * This method is apart of the Authorization process
        * authorizeRequests() will insure that all request must be authorized at this point
        * .anyRequest().hasRole("USER") any request must have the role USER
        *
        * Why is it not ROLE_USER, just USER? This is because the hasRole method will prepend "ROLE_"
        *
        * NOTE: (because future Kyle might forget!)
        * ROLE_USER is a general user
        *
        * If you want to allow for access to multiple pages look at
        * https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#authorize-requests
        * */

        http
                .authorizeRequests()
                    .antMatchers("/test").permitAll()
                    .anyRequest().hasRole("USER")
                .and()
                    .formLogin()
                        .loginPage("/login")
                        .permitAll()// allow anyone on this page
                        .successHandler(loginSuccessHandler())
                        .failureHandler(loginFailureHandler())
                .and()//Logout : https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#jc-logout
                    .logout()//logout url default is /logout; also the request type to logout must be a post request!
                        .permitAll()// allow anyone on this page
                        .logoutSuccessUrl("/login")//you can use a handler if you want
                .and()
                    .csrf();//adds CSRF Protection for post requests
    }

    public AuthenticationSuccessHandler loginSuccessHandler(){
        /*
        * This current setup redirects the user to the home page
        * but if you wanted you can add curly brackets and have functionality to redirect to other places (maybe a project page)
        * or even better create a class that implements AuthenticationSuccessHandler with addition functionality
        * */

        return (request, response, authentication) -> response.sendRedirect("/");
    }

    public AuthenticationFailureHandler loginFailureHandler(){
        /*
        * Look at the layout.html and login.html for info about how the custom flash message works to display info
        * Look at the LoginController to see how to remove the flash message after being used
        * */
        return (request, response, exception) -> {
            request.getSession().setAttribute("flash", new FlashMessage("Incorrect username and/or password. Please try again.", FlashMessage.Status.FAILURE));
            response.sendRedirect("/login");
        };
    }

    @Bean
    public EvaluationContextExtension securityExtension(){
        return new EvaluationContextExtensionSupport() {
            @Override
            public String getExtensionId() {
                return "security";
            }

            @Override
            public Object getRootObject() {
                /*
                * This will get the security expression root object, exposing all the details
                * */
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                return new SecurityExpressionRoot(authentication) {};
            }
        };
    }
}
