package mx.grailscoder.person

import groovy.transform.ToString
import mx.grailscoder.pogo.person.PersonPogo
import mx.grailscoder.util.CommonRoutines

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Oct 28, 2015

 *
 */
@ToString(includes='firstName,lastName', includeNames=true, includePackage=false)
class Person {

    String firstName
    String lastName
    String title
    Boolean male
    String email
    String location
    String phone
    Boolean isCompany
    //String rfc

    static constraints = {
        firstName blank: false, nullable: false
        lastName blank: true, nullable: true
        title blank: true, nullable: true
        email blank: true, nullable: true
        location blank: true, nullable: true
        phone blank: true, nullable: true
        isCompany blank: false, nullable: false
    }

    Object asType(Class clazz) {
        if (clazz == PersonPogo) {
            try {
                def personWrapper = CommonRoutines.copyProperties(this, clazz)
                return personWrapper
            }
            catch (InstantiationException | IllegalAccessException ex) {
                log.error ex.message
            }
        } else {
            super.asType(clazz)
        }
    }


}
