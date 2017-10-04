package mx.grailscoder.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.grailscoder.pogo.security.UserPogo
import mx.grailscoder.person.Person
import mx.grailscoder.presenter.CustomViewType
import mx.grailscoder.util.CommonRoutines

@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
//@GrailsCompileStatic
class UserApp implements Serializable{

	private static final long serialVersionUID = 1

	//PasswordEncoderService passwordEncoderService
	//transient bcryptService

	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	Person person
	Locale locale = new Locale("en", "US")
	//Company company
	String defaultView = CustomViewType.LANDING.viewName

	//@GrailsCompileStatic(TypeCheckingMode.SKIP)
	/*
	List<RoleApp> getAuthorities() {
		UserAppRoleApp.findAllByUserApp(this)*.roleApp
	}*/


	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	//@GrailsCompileStatic(TypeCheckingMode.SKIP)
	protected void encodePassword() {
		log.info "Password encoding occurring"
		//password = passwordEncoderService?passwordEncoderService.encodePassword(password):password
		log.info "Password encoded is: ${password}"
		//password = passwordEncoderService?passwordEncoderService.encodePassword(password):password
		//password = bcryptService?bcryptService.hashPassword(password):password
	}

	//static transients = ['passwordEncoderService']

	static constraints = {
		password blank: false, password: true
		username blank: false, unique: true
	}

	static mapping = {
		password column: '`password`'
		person lazy: false
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
