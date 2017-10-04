package mx.grailscoder.security

import grails.transaction.Transactional
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

//import org.springframework.stereotype.Service

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya@outlook.com
 * @Date 3/6/2017

 *
 */
class AuthManagerService implements UserDetailsService {

    /**
     * Some Spring Security classes (e.g. RoleHierarchyVoter) expect at least
     * one role, so we give a user with no granted roles this one which gets
     * past that restriction but doesn't grant anything.
     */
    static final List NO_ROLES = [new SimpleGrantedAuthority('ROLE_NO_ROLES')]

    def passwordEncoderService

    @Override
    @Transactional(readOnly=true, noRollbackFor=[IllegalArgumentException, BadCredentialsException])
    UserDetails loadUserByUsername(String username) throws BadCredentialsException {
        log.info "loadUserByUsername:: Started"
        UserApp user = UserApp.findByUsername(username)

        if (!user) throw new BadCredentialsException("Username not found")

        log.info "Obtained user is: ${user?.dump()}"
        //log.info "Authorities are: ${user?.authorities.dump()}"

        // or if you are using role groups:


        List<UserAppRoleApp> userRoles = UserAppRoleApp.findAllByUserApp(user)
        List<RoleApp> roles = userRoles*.roleApp.unique()

        def authorities = roles.collect {
            new SimpleGrantedAuthority(it.authority)
        }

        return new AuthManager(user.username, user.password, user.enabled,
                !user.accountExpired, !user.passwordExpired,
                !user.accountLocked, authorities ?: NO_ROLES, user.id, user.locale)
    }

    Boolean passwordMatches(String encPassword, String rawPassword){
        log.info "passwordMatches:: Started"
        if (!encPassword || !rawPassword){
            return false
        }
        return passwordEncoderService.isPasswordValid(encPassword,rawPassword)

    }

}
