package mx.grailscoder.pogo.security

import grails.compiler.GrailsCompileStatic

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya [ at ] outlook.com
 * @Date 3/14/2017

 *
 */
@GrailsCompileStatic
class UserRolePogo implements Serializable{
    Long id
    UserPogo userApp
    RolePogo roleApp
}
