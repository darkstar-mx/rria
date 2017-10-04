package mx.grailscoder.presenter.datasource.interfaces

import com.vaadin.data.provider.ListDataProvider
import mx.grailscoder.pogo.security.UserPogo
import mx.grailscoder.pogo.security.UserRolePogo
import mx.grailscoder.presenter.event.CustomEventBusResponse
import mx.grailscoder.pogo.security.RolePogo

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Nov 7, 2015

 *
 */
interface UserProvider {

    /**
     * @return A Collection of Users.
     */

    //DataSource<UserPogo> getAllUsersList()

    Collection<UserPogo> collectAllUsersList(final Integer offset, final Integer limit)

    ListDataProvider<UserPogo> collectAllUsersLdp(final Integer offset, final Integer limit)

    CustomEventBusResponse updateUser(final UserPogo userDetails)

    CustomEventBusResponse createUser(final UserPogo userDetails)

    CustomEventBusResponse createRolesForUser(final UserPogo userDetails, final Set<RolePogo> roles)

    CustomEventBusResponse createUserWithRoles(final UserPogo userDetails, final Set<RolePogo> roles)

    UserPogo getUserDetails(String username)

    /**
     * Counts the amount of users so that paging works
     * */
    Integer countUsers()

    /**
     * Retrieves all of the roles for given user
     * */
    List<UserRolePogo> getRolesForUser(final UserPogo user)

    Collection<RolePogo> getAllRoles(final Integer offset, final Integer limit)

    Integer countAllRoles()
}
