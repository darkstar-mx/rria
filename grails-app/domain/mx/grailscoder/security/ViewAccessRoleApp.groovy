package mx.grailscoder.security

import grails.gorm.DetachedCriteria
import groovy.transform.ToString
import org.apache.commons.lang.builder.HashCodeBuilder

@ToString(cache=true, includeNames=true, includePackage=false)
class ViewAccessRoleApp implements Serializable {

	private static final long serialVersionUID = 1

	ViewAccess viewAccess
	RoleApp roleApp

	@Override
	boolean equals(other) {
		if (other instanceof ViewAccessRoleApp) {
			other.roleAppId == roleApp?.id && other.viewAccessId == viewAccess?.id
		}
	}

	@Override
	int hashCode() {
		def builder = new HashCodeBuilder()
		if (viewAccess) builder.append(viewAccess.id)
		if (roleApp) builder.append(roleApp.id)
		builder.toHashCode()
	}

	static List<ViewAccessRoleApp> getViews(List<Long> roleIds){
		criteriaFor(roleIds).list()
	}

	static ViewAccessRoleApp get(long viewAccessId, long roleAppId) {
		criteriaFor(viewAccessId, roleAppId).get()
	}

	static boolean exists(long viewAccessId, long roleAppId) {
		criteriaFor(viewAccessId, roleAppId).count()
	}

	private static DetachedCriteria criteriaFor(long viewAccessId, long roleAppId) {
		ViewAccessRoleApp.where {
			viewAccess == ViewAccess.load(viewAccessId) &&
			roleApp == RoleApp.load(roleAppId)
		}
	}

	private static DetachedCriteria criteriaFor(List<Long> roleAppIds) {
		ViewAccessRoleApp.where {
			roleApp {
				inList("id", roleAppIds)
			}
		}
	}

	static ViewAccessRoleApp create(ViewAccess viewAccess, RoleApp roleApp) {
		def instance = new ViewAccessRoleApp(viewAccess: viewAccess, roleApp: roleApp)
		instance.save()
		instance
	}

	static boolean remove(ViewAccess rg, RoleApp r) {
		if (rg != null && r != null) {
			ViewAccessRoleApp.where { viewAccess == rg && roleApp == r }.deleteAll()
		}
	}

	static int removeAll(RoleApp r) {
		r == null ? 0 : ViewAccessRoleApp.where { roleApp == r }.deleteAll()
	}

	static int removeAll(ViewAccess rg) {
		rg == null ? 0 : ViewAccessRoleApp.where { viewAccess == rg }.deleteAll()
	}

	static constraints = {
		roleApp validator: { RoleApp r, ViewAccessRoleApp rg ->
			if (rg.viewAccess?.id) {
				ViewAccessRoleApp.withNewSession {
					if (ViewAccessRoleApp.exists(rg.viewAccess.id, r.id)) {
						return ['roleGroup.exists']
					}
				}
			}
		}
	}

	static mapping = {
		id composite: ['viewAccess', 'roleApp']
		roleApp lazy: false
		viewAccess lazy: false
		version false
	}
}
