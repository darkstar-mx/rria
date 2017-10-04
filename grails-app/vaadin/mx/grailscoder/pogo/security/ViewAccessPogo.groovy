package mx.grailscoder.pogo.security

import grails.compiler.GrailsCompileStatic
import groovy.util.logging.Log4j
import mx.grailscoder.security.ViewAccess
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
class ViewAccessPogo implements Serializable {
    Long id
    String name
    String description
    String viewName

    Object asType(Class clazz){
        log.info "asType:: Applying conversion to " + clazz.name
        if (clazz == ViewAccess) {
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
