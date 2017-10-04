package rria

import mx.grailscoder.person.Person
import mx.grailscoder.presenter.CustomViewType
import mx.grailscoder.security.RoleApp
import mx.grailscoder.security.UserApp
import mx.grailscoder.security.UserAppRoleApp
import mx.grailscoder.security.ViewAccess
import mx.grailscoder.security.ViewAccessRoleApp

import javax.servlet.ServletContext

import static mx.grailscoder.util.Grails.i18n

class BootStrap {

    //@Autowired
    def passwordEncoderService

    def userAppService
    //private final UserAppService userAppService = Grails.get(UserAppService)

    def init = { ServletContext ctx ->
        try {
            print("i18n is: ")
            println(i18n("mx.grailscoder.view.security.userview.title", Locale.ENGLISH))
            /*********************************************************************************************************************
             *********************************************************************************************************************
             **                                  User and Role creation by using Domain classes
             *********************************************************************************************************************
             *********************************************************************************************************************
             */

            ViewAccess landingViewAccess    = new ViewAccess(viewName: CustomViewType.LANDING.viewName, description: "Invoice view access").save()
            ViewAccess pricingViewAccess    = new ViewAccess(viewName: CustomViewType.PRICING.viewName, description: "Pricing view access").save()
            ViewAccess errorViewAccess      = new ViewAccess(viewName: CustomViewType.ERROR.viewName, description: "Error view").save()
            ViewAccess userViewAccess       = new ViewAccess(viewName: CustomViewType.USER.viewName, description: "User View with secured content for User role", name: "user-view").save()

            RoleApp adminRoleApp    = new RoleApp(authority: 'ROLE_ADMIN').save()
            //RoleApp userRoleApp     = new RoleApp(authority: 'ROLE_USER').save()
            //RoleApp errorRoleApp  = new RoleApp(authority: 'ROLE_ERROR').save()


            Person person = new Person(firstName: "Armando", lastName: "Montoya", title: "Mr.", male: true, email: "somemail@mail.com", isCompany: false, phone: "555-561-8877")
            UserApp me = new UserApp(username:'admin', password:passwordEncoderService.encodePassword('admin'), defaultView: CustomViewType.USER.viewName, locale: new Locale("es", "MX"), person: person).save()
            //UserApp me = new UserApp(username:'admin', password:'admin', defaultView: CustomViewType.USER.viewName, locale: new Locale("es", "MX"), person: person).save()
            //UserApp me = new UserApp(username:'admin', password: 'admin', defaultView: CustomViewType.LANDING.viewName, locale: new Locale("es", "MX"), person: person)
            UserAppRoleApp.create(me, adminRoleApp)

            /*
            (1..10).each {
                Person personItem = new Person(firstName: "Admin"+it, lastName: "LastName"+it, title: "Mr.", male: true, email: "my.mail_${it}@outlook.com", isCompany: false).save()
                UserApp userApp = new UserApp(username:'admin'+it, password:passwordEncoderService.encodePassword('admin'+it), defaultView: CustomViewType.LANDING.viewName, locale: new Locale("es", "MX"), person: personItem).save()
                UserAppRoleApp.create(userApp, userRoleApp)
            }*/

            ViewAccessRoleApp.create(landingViewAccess, adminRoleApp)
            ViewAccessRoleApp.create(userViewAccess, adminRoleApp)
            ViewAccessRoleApp.create(pricingViewAccess, adminRoleApp)
            //ViewAccessRoleApp.create(errorViewAccess, errorRoleApp)


            UserAppRoleApp.withSession {
                it.flush()
                it.clear()
            }
            ViewAccessRoleApp.withSession {
                it.flush()
                it.clear()
            }
            /**
             * Testing updateUser
             *
             * */
            /*
            UserProvider userProvider = new UserProviderImpl()
            try {
                me.username = "armando"
                me.save()
                def result = userProvider.updateUser(me as UserPogo)
                println result.dump()
            } catch (RuntimeException ex) {
                println ex.getMessage()
            }*/

            //def roles = []
            //roles << adminRoleApp
            //def success = userAppService.createUserAndRoles(me, roles)

            //println "User creation result: ${success?"Correct":"Wrong"}"

        }
        catch (Exception ex){
            println "Error on Bootstrap: ${ex.getMessage()}"
        }
    }
    def destroy = {
    }
}