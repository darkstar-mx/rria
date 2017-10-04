package mx.grailscoder.conf

import com.vaadin.spring.annotation.EnableVaadin
import com.vaadin.spring.server.SpringVaadinServlet
import groovy.transform.CompileStatic
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SuppressWarnings("GroovyUnusedDeclaration")
@Configuration
@EnableVaadin
@CompileStatic
class VaadinConfiguration {

    static final String APP = "/"

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servlet = new ServletRegistrationBean(new SpringVaadinServlet(), "/*", "/VAADIN/*")
        //servlet.addInitParameter("openSessionInView", "org.springframework.orm.hibernate5.support.OpenSessionInViewFilter")

        return servlet
    }
}