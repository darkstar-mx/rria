package mx.grailscoder.security

import grails.gorm.DetachedCriteria
import groovy.transform.ToString
import mx.grailscoder.pogo.security.UserPogo
import mx.grailscoder.util.CommonRoutines
import org.apache.commons.lang.builder.HashCodeBuilder

@ToString(cache=true, includeNames=true, includePackage=false)
class UserAppRoleApp implements Serializable {

	private static final long serialVersionUID = 1

	UserApp userApp
	RoleApp roleApp

	@Override
	boolean equals(other) {
		if (other instanceof UserAppRoleApp) {
			other.userAppId == userApp?.id && other.roleAppId == roleApp?.id
		}
	}

	@Override
	int hashCode() {
		def builder = new HashCodeBuilder()
		if (userApp) builder.append(userApp.id)
		if (roleApp) builder.append(roleApp.id)
		builder.toHashCode()
	}


	static List<UserAppRoleApp> getRoles(long userAppId) {
		List<UserAppRoleApp> result = criteriaFor(userAppId).list()
		// trick to avoid LazyLoading exception
		//println "getRoles:: size is -> ${result.size()}"
		//println "getRoles:: content is -> ${result.dump()}"
		return result
	}

	static UserAppRoleApp get(long userAppId, long roleAppId) {
		criteriaFor(userAppId, roleAppId).get()
	}

	static boolean exists(long userAppId, long roleAppId) {
		criteriaFor(userAppId, roleAppId).count()
	}

	private static DetachedCriteria criteriaFor(long userAppId, long roleAppId) {
		UserAppRoleApp.where {
			userApp == UserApp.load(userAppId) &&
			roleApp == RoleApp.load(roleAppId)
		}
	}

	private static DetachedCriteria criteriaFor(long userAppId) {
		UserAppRoleApp.where {
			userApp == UserApp.load(userAppId)
		}
	}

	static UserAppRoleApp create(UserApp userApp, RoleApp roleApp) {
		def instance = new UserAppRoleApp(userApp: userApp, roleApp: roleApp)
		instance.save()
		instance
	}

	static boolean remove(UserApp u, RoleApp r) {
		if (u != null && r != null) {
			UserAppRoleApp.where { userApp == u && roleApp == r }.deleteAll()
		}
	}

	static int removeAll(UserApp u) {
		u == null ? 0 : UserAppRoleApp.where { userApp == u }.deleteAll()
	}

	static int removeAll(RoleApp r) {
		r == null ? 0 : UserAppRoleApp.where { roleApp == r }.deleteAll()
	}

	static List<UserAppRoleApp> getRoles(UserApp u) {
		u == null ? [] : UserAppRoleApp.where { userApp == u }.list()
	}

	/*
	static List<UserAppRoleApp> getRoles(Long userId) {
		userId == null ? [] : UserAppRoleApp.where { userApp.id == userId }.list()
	}*/

	static constraints = {
		roleApp validator: { RoleApp r, UserAppRoleApp ur ->
			if (ur.userApp?.id) {
				UserAppRoleApp.withNewSession {
					if (UserAppRoleApp.exists(ur.userApp.id, r.id)) {
						return ['userRole.exists']
					}
				}
			}
		}
	}

	static mapping = {
		id composite: ['userApp', 'roleApp']
		userApp lazy: false
		roleApp lazy: false
		version false
	}

	Object asType(Class clazz) {
		if (clazz == UserPogo) {
			try {
				def userPogo = CommonRoutines.copyProperties(this, clazz)
				return userPogo
			}
			catch (InstantiationException | IllegalAccessException ex) {
				log.error ex.message
			}

		} else {
			super.asType(clazz)
		}
	}

}
