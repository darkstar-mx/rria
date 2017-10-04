package mx.grailscoder.presenter.datasource.interfaces

import mx.grailscoder.pogo.security.RolePogo
import mx.grailscoder.pogo.security.RoleViewPogo
import mx.grailscoder.pogo.security.UserRolePogo

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya [ at ] outlook.com
 * @Date 3/14/2017

 *
 */
interface RoleProvider {

    /**
     * Retrieves all user views
     * @return List<ViewAccessPogo>
     * */
    List<RoleViewPogo> getAllViewsByUserAndRoleList(final List<UserRolePogo> userRolePogos)

    /**
     * Retrieves all user views
     * @return List<ViewAccessPogo>
     * */
    List<RoleViewPogo> getAllViewsByRoleList(final List<RolePogo> rolePogos)

    /**
     * Retrieves the views that belong only to a single role
     * @return List<ViewAccessPogo>
     * */
    List<RoleViewPogo> getAllViewsByRole(final RolePogo role)

}