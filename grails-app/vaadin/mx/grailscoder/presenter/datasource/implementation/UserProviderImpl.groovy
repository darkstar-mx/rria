package mx.grailscoder.presenter.datasource.implementation

import com.vaadin.data.provider.ListDataProvider
import grails.compiler.GrailsCompileStatic
import groovy.util.logging.Log4j
import mx.grailscoder.error.BusinessException
import mx.grailscoder.pogo.security.RolePogo
import mx.grailscoder.pogo.security.UserPogo
import mx.grailscoder.pogo.security.UserRolePogo
import mx.grailscoder.presenter.datasource.interfaces.UserProvider
import mx.grailscoder.presenter.event.CustomEventBusResponse
import mx.grailscoder.security.RoleApp
import mx.grailscoder.security.UserApp
import mx.grailscoder.security.UserAppService
import mx.grailscoder.security.UserAppRoleApp
import mx.grailscoder.util.Grails

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Nov 7, 2015

 *
 */
@Log4j
@GrailsCompileStatic
class UserProviderImpl implements UserProvider {

    private final UserAppService userAppService = Grails.get(UserAppService)

    /**
     *
     * DS examples:
     * https://github.com/Artur-/griddesign/blob/master/src/main/java/org/vaadin/artur/griddesign/client/data/DataContainer.java
     * https://github.com/vaadin/vaadin/blob/master/uitest/src/com/vaadin/tests/widgetset/client/grid/GridCellFocusOnResetSizeWidget.java
     * https://dev.vaadin.com/review/gitweb?p=vaadin.git;a=blob;f=client/src/com/vaadin/client/ui/grid/datasources/ListDataSource.java
     * http://stackoverflow.com/questions/19521671/vaadin-display-a-list-in-table
     * https://morevaadin.com/content/table-dead-long-live-grid/
     *
     * https://github.com/vaadin/vaadin/blob/master/uitest/src/com/vaadin/tests/widgetset/client/grid/GridClientDataSourcesWidget.java
     *
     * */

    /*
    @Override
    public DataSource<UserPogo> getAllUsersList() {
        //println "UserDataImpl:getAllUsersList()"
        //return new AllUserDT<UserPogo>()
        return null
    }*/

    @Override
    public Collection<UserPogo> collectAllUsersList(final Integer offset, final Integer limit) {
        log.info "collectAllUsersList:: Start"
        log.info "collectAllUsersList:: offset:${offset}, limit:${limit}"
        return userAppService.getAllUsers(offset,limit).collect {it as UserPogo}
    }

    @Override
    public ListDataProvider<UserPogo> collectAllUsersLdp(final Integer offset, final Integer limit) {
        log.info "collectAllUsersLdp"
        return new ListDataProvider<UserPogo>(this.collectAllUsersList(offset,limit))
    }

    @Override
    CustomEventBusResponse updateUser(final UserPogo userDetails){
        log.info "updateUser:: Started"
        log.info "updateUser:: userDetails -> ${userDetails?.dump()}"
        try {
            Boolean exitStatus  = userAppService.updateUser(userDetails as UserApp)
            return new CustomEventBusResponse(isSuccessful: exitStatus, errorMessage: "Success" )

        } catch (RuntimeException/*|GroovyCastException*/ ex) {
            log.error "updateUser::" + ex.message
            return new CustomEventBusResponse(isSuccessful: false, errorMessage: ex.getMessage() )
        }
    }

    @Override
    CustomEventBusResponse createUser(final UserPogo userDetails) {
        log.info "createUser:: Started"
        log.info "createUser:: userDetails -> ${userDetails?.dump()}"
        try {
            Boolean exitStatus  = userAppService.createUser(userDetails as UserApp)?true:false
            return new CustomEventBusResponse(isSuccessful: exitStatus, errorMessage: "Success" )

        } catch (RuntimeException/*|GroovyCastException*/ ex) {
            log.error "createUser::" + ex.message
            return new CustomEventBusResponse(isSuccessful: false, errorMessage: ex.getMessage() )
        }
    }

    @Override
    CustomEventBusResponse createRolesForUser(final UserPogo userDetails, final Set<RolePogo> roles) {
        log.info "createRolesForUser:: Started"
        log.info "createRolesForUser:: userDetails -> ${userDetails?.dump()}"
        log.info "createRolesForUser:: roles -> ${roles?.dump()}"
        try {
            Boolean exitStatus  = !userAppService.createRolesForUser(userDetails as UserApp, roles.collect {it as RoleApp}).isEmpty()
            return new CustomEventBusResponse(isSuccessful: exitStatus, errorMessage: "Success" )

        } catch (RuntimeException/*|GroovyCastException*/ ex) {
            log.error "createRolesForUser::" + ex.message
            return new CustomEventBusResponse(isSuccessful: false, errorMessage: ex.getMessage() )
        }
    }

    @Override
    CustomEventBusResponse createUserWithRoles(final UserPogo userDetails, final Set<RolePogo> roles) {
        log.info "createUserWithRoles:: Started"
        log.info "createUserWithRoles:: userDetails -> ${userDetails.dump()}"
        log.info "createUserWithRoles:: roles -> ${roles.each {it.dump()}}"

        try {
            Boolean userExists = userAppService.getUserDetails(userDetails.username)?true:false
            if(!userExists) {

                Boolean exitStatus = userAppService.createUserAndRoles(userDetails as UserApp, roles.collect {
                    it as RoleApp
                })
                return new CustomEventBusResponse(isSuccessful: exitStatus, errorMessage: "Success")
            } else {
                return new CustomEventBusResponse(isSuccessful: false, errorMessage: "User creation failed: Username already exists")
            }

        } catch (BusinessException| GroovyRuntimeException ex) {
            log.error "createUserWithRoles:: exception -> " + ex.getStackTrace().toString()
            return new CustomEventBusResponse(isSuccessful: false, errorMessage: ex.getMessage() )
        }
    }

    @Override
    UserPogo getUserDetails(String username) {
        log.info "getUserDetails:: Started"
        return userAppService.findByUsername(username) as UserPogo
    }

    @Override
    List<UserRolePogo> getRolesForUser(final UserPogo user) {
        log.info "getRolesForUser:: Start"
        log.info "getRolesForUser:: user is -> ${user.dump()}"

        List<UserAppRoleApp> roles  = userAppService.findRolesbyUserApp(user as UserApp)
        log.info "getRolesForUser:: found info is -> ${roles.dump()}"
        return roles.collect {
            log.info "getRolesForUser:: item is -> ${it.dump()}"
            new UserRolePogo(userApp: (it.userApp as UserPogo), roleApp: (it.roleApp as RolePogo))
        }
    }

    @Override
    Collection<RolePogo> getAllRoles(final Integer offset, final Integer limit) {
        log.info "getAllRoles:: Start"
        return userAppService.findAllRoles(offset, limit).collect {it as RolePogo}
    }

    @Override
    Integer countAllRoles() {
        log.info "countAllRoles:: Start"
        return userAppService.countAllRoles()
    }

    @Override
    Integer countUsers(){
        log.info "countUsers:: Start"
        return userAppService.countUsers()
    }
}
