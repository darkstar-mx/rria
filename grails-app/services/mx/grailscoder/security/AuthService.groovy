package mx.grailscoder.security

import com.vaadin.server.VaadinService
import com.vaadin.server.VaadinSession
import grails.compiler.GrailsCompileStatic
import grails.transaction.Transactional
import groovy.transform.TypeCheckingMode
import mx.grailscoder.util.Grails
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date 15/08/2015

 *
 */
@GrailsCompileStatic
@Transactional
class AuthService {

    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    Authentication login(String username, String password) {
        log.info "Attempting to login, username: " + username
        UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(username, password)
        AuthenticationManager authenticationManager = Grails.get(CustomAuthenticationProvider)
        Authentication result = authenticationManager.authenticate(request)

        // Reinitialize the session to protect against session fixation attacks. This does not work
        // with websocket communication.
        VaadinService.reinitializeSession(VaadinService.getCurrentRequest());

        SecurityContextHolder.getContext().setAuthentication(result)
        return result
    }

    def logout(String username) {
        SecurityContextHolder.context.authentication = null
        VaadinSession.setCurrent(null)
    }

}
