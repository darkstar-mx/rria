package mx.grailscoder.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.grailscoder.pogo.security.ViewAccessPogo
import mx.grailscoder.util.CommonRoutines

@EqualsAndHashCode(includes='name')
@ToString(includes='viewName', includeNames=true, includePackage=false)
class ViewAccess implements Serializable {

	private static final long serialVersionUID = 1

	String viewName
	String description

	static constraints = {
		viewName blank: false, nullable: false, unique: true
		description blank: true, nullable: true
	}

	static mapping = {
		cache true
	}

	Object asType(Class clazz) {
		if (clazz == ViewAccessPogo) {
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
