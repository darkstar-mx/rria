package mx.grailscoder.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya@outlook.com
 * @Date 3/6/2017

 *
 */
class AuthManager extends User{

    private static final long serialVersionUID = 1
    final Locale locale
    final id

    AuthManager(String username,
                String password,
                boolean enabled,
                boolean accountNonExpired,
                boolean credentialsNonExpired,
                boolean accountNonLocked,
                Collection<GrantedAuthority> authorities,
                long id,
                Locale locale){
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities)
        this.id     = id
        this.locale = locale
    }

}
