package mx.grailscoder.security

import grails.compiler.GrailsCompileStatic
import grails.transaction.Transactional
import mx.grailscoder.error.BusinessException

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date 15/08/2015

 *
 */

@GrailsCompileStatic
class UserAppService {

    PasswordEncoderService passwordEncoderService

    //@GrailsCompileStatic(TypeCheckingMode.SKIP)
    @Transactional(readOnly=true)
    UserApp findByUsername(final String username) {
        log.info "findByUsername:: username: " + username
        UserApp user = UserApp.findByUsername(username)
        log.info "findByUsername:: bean found -> " + user.dump()
        return user
    }

    @Transactional(readOnly = true)
    UserApp getUserDetails(String username) {
        log.info "getUserDetails::Getting user details for: " + username
        if(!username){
            return null
        }
        return UserApp.findByUsername(username)
    }

    @Transactional(readOnly = true)
    UserApp getUserDetailsByUserId(Long userId) {
        log.info "getUserDetailsByUserId::Getting user details for id: " + userId
        if(!userId){
            return null
        }

        return UserApp.get(userId)
        //return UserApp.findById(userId)
    }

    @Transactional(readOnly = true)
    Collection<UserApp> getAllUsers(Integer offset, Integer limit, String sortField = "username", String orderType = "asc") {
        log.info "getAllUsers::Retrieving all users "
        UserApp.findAll([max: limit, sort: sortField, order: orderType, offset: offset])
    }

    @Transactional(rollbackFor = RuntimeException)
    Boolean updateUser(final UserApp userDetails) {
        log.info "updateUser:: Start"
        log.info "updateUser::Updating user: " + userDetails.dump()

        Boolean result      = false
        UserApp userApp     = this.getUserDetailsByUserId(userDetails.id)

        if(!userApp) {
            throw new BusinessException("Update failed: Username does not exist")
        }

        // copy obtained properties
        userApp.person.firstName    = userDetails.person.firstName
        userApp.person.lastName     = userDetails.person.lastName
        userApp.person.title        = userDetails.person.title
        userApp.person.male         = userDetails.person.male
        userApp.person.email        = userDetails.person.email
        userApp.person.phone        = userDetails.person.phone
        userApp.locale              = userDetails.locale

        if(userDetails?.defaultView) {
            userApp.defaultView = userDetails.defaultView
        }

        if(userDetails?.password) {
            userApp.password = passwordEncoderService.encodePassword(userDetails.password)
        }

        if(!userApp.person.validate()){
            String errors = userApp.person.errors.allErrors.collect {it?.arguments[0]}.join(", ")
            throw new BusinessException("Update failed, rejected fields: ${errors}")
        }

        if(!userApp.validate()){
            String errors = userApp.errors.allErrors.collect {it?.arguments[0]}.join(", ")
            throw new BusinessException("Update failed, rejected fields: ${errors}")
        }

        log.info "updateUser::saving person"

        Boolean personSaveResult = userApp.person.save(failOnError: true)?:false
        if (!personSaveResult){
            throw new BusinessException("updateUser: Error saving personal details")
        }
        log.info "updateUser::person result -> ${personSaveResult}"

        // Perform user data saving
        log.info "updateUser::saving user"

        result = userApp.save(failOnError: true)?:false
        log.info "updateUser::saving user result is: ${result.dump()}"

        return result
    }

    @Transactional(rollbackFor = RuntimeException)
    UserApp createUser(final UserApp userDetails) {
        log.info "createUser:: Start"
        log.info "createUser::Creating user: " + userDetails.dump()

        UserApp result      = null

        /*
        if(this.getUserDetails(userDetails.username)) {
            throw new RuntimeException("User creation failed: Username already exists")
        }*/

        if(userDetails.password) {
            userDetails.password = passwordEncoderService.encodePassword(userDetails.password)
        }

        if(!userDetails.person.validate()){
            String errors = userDetails.person.errors.allErrors.collect {it?.arguments[0]}.join(", ")
            throw new GroovyRuntimeException("User creation failed, rejected fields: ${errors}")
        }

        if(!userDetails.validate()){
            String errors = userDetails.errors.allErrors.collect {it?.arguments[0]}.join(", ")
            throw new GroovyRuntimeException("User creation failed, rejected fields: ${errors}")
        }

        // Perform user data saving
        log.info "createUser::saving user"

        result = userDetails.save(failOnError: true)
        log.info "createUser::saving user result is: ${result.dump()}"

        return result
    }

    @Transactional(rollbackFor = RuntimeException)
    List<UserAppRoleApp> createRolesForUser(final UserApp userDetails, List<RoleApp> roles) {
        log.info "createRolesForUser:: Start"

        List<UserAppRoleApp> result      = []

        /*
        if(!this.getUserDetails(userDetails.username)) {
            throw new BusinessException("Role creation failed: Username does not exist")
        }*/

        if(roles?.isEmpty()){
            throw new BusinessException("Role creation failed: No role was assigned to the user")
        }

        // Perform user data saving
        log.info "createRolesForUser::allocating roles for user"
        roles.each {
            result << UserAppRoleApp.create(userDetails, it)
        }
        log.info "createRolesForUser::saving user result is: ${result?"Successful":"Not Successful"}"

        return result
    }

    @Transactional(rollbackFor = RuntimeException)
    Boolean createUserAndRoles (final UserApp userDetails, List<RoleApp> roles){
        log.info "createUserAndRoles:: Start"

        Boolean result      = false

        if(!userDetails) {
            throw new GroovyRuntimeException("User creation failed: User details must not be empty")
        }

        if(roles?.isEmpty()){
            throw new GroovyRuntimeException("User creation failed: No role was assigned to the user")
        }

        log.info "createUserAndRoles::saving user"
        UserApp createdUser = this.createUser(userDetails)
        if (createdUser){
            result = !this.createRolesForUser(createdUser,roles).isEmpty()
        }

        log.info "createUserAndRoles::operation is: ${result?"Successful":"Not Successful"}"
        return result
    }

    @Transactional(readOnly = true)
    List<UserAppRoleApp> findRolesbyUserApp(UserApp user) {
        log.info "findRolesbyUserApp:: Start"
        //return UserAppRoleApp.findAllByUserApp(user)
        //return UserAppRoleApp.getRoles(user)
        return UserAppRoleApp.getRoles(user.id)

    }

    @Transactional(readOnly = true)
    List<RoleApp> findAllRoles(Integer offset, Integer limit, String sortField = "authority", String orderType = "asc") {
        log.info "findAllRoles:: Start"
        log.info "findAllRoles:: return is -> ${RoleApp.findAll([max: limit, sort: sortField, order: orderType, offset: offset]).dump()}"
        return RoleApp.findAll([max: limit, sort: sortField, order: orderType, offset: offset])
    }

    @Transactional(readOnly = true)
    List<UserAppRoleApp> findRolesbyUserId(Long id) {
        log.info "findRolesbyUserId:: Start"
        log.info "findRolesbyUserId:: id is -> ${id}"
        return UserAppRoleApp.getRoles(id)
        //return UserAppRoleApp.findAllById(id)

    }

    @Transactional(readOnly = true)
    Integer countUsers(){
        log.info "countUsers:: Start"
        UserApp.count()
    }

    @Transactional(readOnly = true)
    Integer countAllRoles(){
        log.info "countAllRoles:: Start"
        RoleApp.count()
    }

}
