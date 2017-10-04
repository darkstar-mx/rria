package mx.grailscoder.pogo.person

import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString
import groovy.util.logging.Log4j
import mx.grailscoder.person.Person
import mx.grailscoder.util.CommonRoutines

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya [ at ] outlook.com
 * @Date 3/28/2017

 *
 */
@GrailsCompileStatic
@Log4j
@ToString(includes='firstName,lastName', includeNames=false, includePackage=false)
class PersonPogo implements Serializable {

    Long id
    String firstName
    String lastName
    String title
    Boolean male
    String email
    String location
    String phone
    Boolean isCompany

    Object asType(Class clazz){
        log.info "asType:: Applying conversion to " + clazz.name
        if (clazz == Person) {
            try {
                return CommonRoutines.copyProperties(this, clazz)
            } catch (InstantiationException | IllegalAccessException ex){
                log.error "Error when converting class: ${ex.message}"
                throw ex
            }
        } else {
            throw new IllegalAccessException("No Person provided")
        }
    }

}
