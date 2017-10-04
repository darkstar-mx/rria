package mx.grailscoder.security

import grails.compiler.GrailsCompileStatic
import grails.transaction.Transactional
import grails.util.Holders
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Transactional
@GrailsCompileStatic
class PasswordEncoderService {

    private static Integer LOG_ROUNDS = Integer.parseInt(Holders.grailsApplication.metadata['grails.plugin.springsecurity.password.bcrypt.logrounds'].toString())

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(LOG_ROUNDS)

    String encodePassword(String password) {
        //passwordEncoder = new BCryptPasswordEncoder(logrounds)
        return passwordEncoder.encode(password)
    }

    Boolean isPasswordValid(String encryptedPassword, String rawPass){
        //passwordEncoder = new BCryptPasswordEncoder(logrounds)
        return passwordEncoder.matches(rawPass, encryptedPassword)
    }
}
