package mx.grailscoder.security

import grails.compiler.GrailsCompileStatic
import grails.transaction.Transactional

@GrailsCompileStatic
class RoleAppService {

    @Transactional(readOnly = true)
    List<ViewAccessRoleApp> getAllViewsInRoleList(List<RoleApp> roleAppList) {
        log.info "getAllViewsInRoleList:: Start"
        //return ViewAccessRoleApp.findAllByRoleAppInList(roleAppList)
        return ViewAccessRoleApp.getViews(roleAppList*.id)
    }

    @Transactional(readOnly = true)
    List<ViewAccessRoleApp> getAllViewsByRole(RoleApp roleApp) {
        log.info "getAllViewsByRole:: Start"
        return ViewAccessRoleApp.findAllByRoleApp(roleApp)
    }
}
