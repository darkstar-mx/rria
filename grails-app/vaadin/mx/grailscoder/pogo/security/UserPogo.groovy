package mx.grailscoder.pogo.security

import groovy.transform.ToString
import groovy.util.logging.Log4j
import mx.grailscoder.pogo.person.PersonPogo
import mx.grailscoder.security.UserApp
import mx.grailscoder.util.CommonRoutines

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Nov 7, 2015

 *
 */
@Log4j
@ToString(includes='username', includeNames=true, includePackage=false)
class UserPogo implements Serializable {

    Long id
    String username
    String password
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    PersonPogo person
    Locale locale
    //CompanyWrapper company
    String defaultView
    //Collection<RoleWrapper> roles


    Object asType(Class clazz) {
        log.info "asType:: Applying conversion to " + clazz.name
        if (clazz == UserApp) {
            try {
                return CommonRoutines.copyProperties(this, clazz)
            } catch (InstantiationException | IllegalAccessException ex){
                log.error ex.message
            }
        } else {
            super.asType(clazz)
        }
    }

}
