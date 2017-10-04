package mx.grailscoder.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.grailscoder.pogo.security.RolePogo
import mx.grailscoder.util.CommonRoutines

@EqualsAndHashCode(includes='authority')
@ToString(includes='authority', includeNames=true, includePackage=false)
class RoleApp implements Serializable {

	private static final long serialVersionUID = 1

	String authority

	static constraints = {
		authority blank: false, unique: true
	}

	static mapping = {
		cache true
	}

	Object asType(Class clazz) {
		if (clazz == RolePogo) {
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
