package mx.grailscoder.presenter.datasource.implementation

import grails.compiler.GrailsCompileStatic
import groovy.util.logging.Log4j
import mx.grailscoder.pogo.security.UserRolePogo
import mx.grailscoder.presenter.datasource.interfaces.RoleProvider
import mx.grailscoder.pogo.security.RolePogo
import mx.grailscoder.pogo.security.RoleViewPogo
import mx.grailscoder.pogo.security.ViewAccessPogo
import mx.grailscoder.security.RoleApp
import mx.grailscoder.security.RoleAppService
import mx.grailscoder.security.UserAppService
import mx.grailscoder.security.ViewAccessRoleApp
import mx.grailscoder.util.Grails

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya [ at ] outlook.com
 * @Date 3/14/2017

 *
 */
@Log4j
@GrailsCompileStatic
class RoleProviderImpl implements RoleProvider{

    private final RoleAppService roleAppService = Grails.get(RoleAppService)
    private final UserAppService userAppService = Grails.get(UserAppService)

    @Override
    List<RoleViewPogo> getAllViewsByUserAndRoleList(final List<UserRolePogo> userRolePogos) {
        log.info "getAllViewsByUserAndRoleList"
        List<ViewAccessRoleApp> result = roleAppService.getAllViewsInRoleList(userRolePogos.collect {it.roleApp as RoleApp}.unique())
        log.info "getAllViewsByUserAndRoleList: retrieved information -> ${result?.dump()}"
        return result.collect {
            new RoleViewPogo(roleApp: (it.roleApp as RolePogo), viewAccess: (it.viewAccess as ViewAccessPogo))
        }
    }

    @Override
    List<RoleViewPogo> getAllViewsByRoleList(List<RolePogo> rolePogos) {
        log.info "getAllViewsByRoleList"
        List<ViewAccessRoleApp> result = roleAppService.getAllViewsInRoleList(rolePogos.collect {it as RoleApp}.unique())
        log.info "getAllViewsByRoleList: retrieved information -> ${result?.dump()}"
        return result.collect {
            new RoleViewPogo(roleApp: (it.roleApp as RolePogo), viewAccess: (it.viewAccess as ViewAccessPogo))
        }
    }

    @Override
    List<RoleViewPogo> getAllViewsByRole(RolePogo role) {
        log.info "getAllViewsByRole"
        List<ViewAccessRoleApp> result = roleAppService.getAllViewsByRole(role as RoleApp)
        return result.collect {
            new RoleViewPogo(roleApp: (it.roleApp as RolePogo), viewAccess: (it.viewAccess as ViewAccessPogo))
        }
    }

}
