package mx.grailscoder

import com.vaadin.server.VaadinSession
import grails.compiler.GrailsCompileStatic
import grails.transaction.Transactional
import groovy.transform.TypeCheckingMode
import mx.grailscoder.security.AuthManagerService
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
class CustomAuthService {

    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    Authentication login(String username, String password) {
        log.info "Attempting to login, username: " + username
        AuthManagerService authManagerService = new AuthManagerService()
        authManagerService.loadUserByUsername(username, false)
        println "AuthManagerService is: ${authManagerService?.dump()}"
        return null
        /*
        UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(username, password)
        AuthenticationManager authenticationManager = Grails.get(AuthManager)
        Authentication result = authenticationManager.authenticate(request)
        SecurityContextHolder.getContext().setAuthentication(result)
        return result*/


    }

    def logout(String username) {
        SecurityContextHolder.context.authentication = null
        VaadinSession.setCurrent(null)
    }

}
