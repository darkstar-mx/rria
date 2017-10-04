package mx.grailscoder.security

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import grails.compiler.GrailsCompileStatic
import groovy.util.logging.Log4j
import mx.grailscoder.presenter.CustomViewType
import mx.grailscoder.presenter.event.CustomEvent
import mx.grailscoder.presenter.event.CustomEventBus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date 15/08/2015

 *
 */
@GrailsCompileStatic
@Log4j
class SecuredViewChangeListener implements ViewChangeListener {

    /* (non-Javadoc)
     * @see com.vaadin.navigator.ViewChangeListener#beforeViewChange(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */

    @Override
    public boolean beforeViewChange(ViewChangeListener.ViewChangeEvent event) {
        log.info "beforeViewChange:: Start"
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Authentication authentication = (Authentication) VaadinSession.getCurrent().getAttribute("SPRING_SECURITY_CONTEXT")

        if (!authentication){
            log.info "beforeViewChange:: returning -> false"
            return false
        }

        View view = event.newView
        boolean isViewAccessible = ViewSecurity.isViewAccessible(view)
        log.info "beforeViewChange:: checking permissions: " + (isViewAccessible && authentication?.isAuthenticated())
        return isViewAccessible && authentication?.isAuthenticated()
        //return true
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.ViewChangeListener#afterViewChange(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */

    @Override
    public void afterViewChange(ViewChangeListener.ViewChangeEvent event) {

        CustomViewType view = CustomViewType.getByViewName(event.getViewName())
        // Appropriate events get fired after the view is changed.
        CustomEventBus.post(new CustomEvent.PostViewChangeEvent(view))
        CustomEventBus.post(new CustomEvent.BrowserResizeEvent())
        CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent())

        /*
        if (tracker != null) {
            // The view change is submitted as a pageview for GA tracker
            tracker.trackPageview("/dashboard/" + event.getViewName());
        }*/
    }

}
