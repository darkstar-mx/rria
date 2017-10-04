package mx.grailscoder.security

import com.vaadin.navigator.View
import grails.compiler.GrailsCompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date 15/08/2015

 *
 */
@GrailsCompileStatic
class ViewSecurity {

    private static Map<Class<? extends View>, List<String>> views = [:]

    static add(Class<? extends View> view, List<String> roles) {
        List<String> viewItemList = views[view]
        if (viewItemList) {
            //println "ViewSecurity::Add -> Adding item to existing list: " + viewItemList.dump()
            roles?.each { String roleItem ->
                viewItemList << roleItem
            }
            views[view] = viewItemList
        } else {
            //println "ViewSecurity::Add -> Creating list: " + viewItemList.dump()
            views.put(view, roles)
        }

    }

    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    static boolean isViewAccessible(View view) {
        List<String> roles = views.get(view.class)
        if (!roles) {
            // if userRoles is null, the access is forbidden (secured)
            //println "ViewSecurity::isViewAccessible -> No roles found for views"
            return false
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        //Authentication authentication = (Authentication) VaadinSession.getCurrent().getAttribute("SPRING_SECURITY_CONTEXT")
        if (!authentication) {
            //throw new InternalAuthenticationServiceException('No authentication found in the context.')
            //println "ViewSecurity::isViewAccessible -> No authentication found in the context"
            return false
        }
        List<GrantedAuthority> authorities = authentication.getAuthorities()
        /**
         * TODO: Redefine this algorithm so it can be statically compiled with the @GrailsCompileStatic instruction
         * */
        for (String role : roles) {
            boolean isRoleAssigned = role in authorities*.getAuthority()
            if (isRoleAssigned) {
                //println "ViewSecurity::isViewAccessible -> Role found in authorities, returning true"
                return true
            }
        }
        //println "ViewSecurity::isViewAccessible -> Default behaviour, false is returned"
        return false
    }
}
