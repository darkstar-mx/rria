package mx.grailscoder.security

import grails.compiler.GrailsCompileStatic
import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya@outlook.com
 * @Date 3/7/2017
 *
 */
@Service
@GrailsCompileStatic
@Log4j
class CustomAuthenticationProvider implements AuthenticationManager{

    @Autowired
    private AuthManagerService authManagerService

    @Override
    Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info "CustomAuthenticationProvider::authenticate -> Called"
        log.info "CustomAuthenticationProvider::authentication -> ${authentication?.dump()}"

        String username = authentication.name
        String password = authentication.credentials?.toString()?.trim()

        if (!username) {
            throw new BadCredentialsException("Username must not be empty")
        }

        UserDetails user = authManagerService.loadUserByUsername(username)

        log.info "CustomAuthenticationProvider::user obtained -> ${user.dump()}"

        if(!user || !user.username.equalsIgnoreCase(username)){
            throw new BadCredentialsException("Username not found.")
        } else {
            log.info "Username validation passed."
        }

        if(!authManagerService.passwordMatches(user.password,password)){
            throw new BadCredentialsException("Wrong password.")
        } else {
            log.info "Password validation passed."
        }

        log.info "CustomAuthenticationProvider::authenticate -> Passed"

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        log.info "CustomAuthenticationProvider::authorities are -> ${authorities.dump()}"

        return new UsernamePasswordAuthenticationToken(username, password, authorities)

    }
/*
    @Override
    boolean supports(Class<?> authentication) {
        log.info "CustomAuthenticationProvider::supports -> Called"
        log.info "CustomAuthenticationProvider::authentication_bean -> ${authentication?.dump()}"
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
*/
}
