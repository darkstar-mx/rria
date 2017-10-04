package rria

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import mx.grailscoder.security.VaadinSessionSecurityContextHolderStrategy
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.core.context.SecurityContextHolder

@ComponentScan(["rria", "mx.grailscoder"])
class Application extends GrailsAutoConfiguration {

    static {
        // Use a custom SecurityContextHolderStrategy
        // not using this causes Spring Sessions to become null after few seconds of creation
        SecurityContextHolder.setStrategyName(VaadinSessionSecurityContextHolderStrategy.class.getName());
    }

    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}