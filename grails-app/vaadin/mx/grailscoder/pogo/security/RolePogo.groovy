package mx.grailscoder.pogo.security

import grails.compiler.GrailsCompileStatic
import groovy.util.logging.Log4j
import mx.grailscoder.security.RoleApp
import mx.grailscoder.util.CommonRoutines

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Nov 7, 2015
 *
 */
@GrailsCompileStatic
@Log4j
class RolePogo implements Serializable {
    Long id
    String authority

    String toString() {
        authority
    }

    Object asType(Class clazz){
        log.info "asType:: Applying conversion to " + clazz.name
        if (clazz == RoleApp) {
            try {
                return CommonRoutines.copyProperties(this, clazz)
            } catch (InstantiationException | IllegalAccessException ex){
                log.error ex.message
            }
        } else {
            throw new IllegalAccessException("No RoleApp provided")
        }
    }

}
