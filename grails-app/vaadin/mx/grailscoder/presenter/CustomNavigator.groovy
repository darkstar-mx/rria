package mx.grailscoder.presenter

import com.vaadin.navigator.Navigator
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewProvider
import com.vaadin.server.VaadinSession
import com.vaadin.ui.ComponentContainer
import grails.compiler.GrailsCompileStatic
import groovy.util.logging.Log4j
import mx.grailscoder.security.SecuredViewChangeListener
import com.vaadin.ui.UI

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Aug 30, 2015

 *
 */
@GrailsCompileStatic
@Log4j
class CustomNavigator extends Navigator {

    final SecuredViewChangeListener securedViewChangeListener   = new SecuredViewChangeListener()
    static final CustomViewType ERROR_VIEW                      = CustomViewType.ERROR
    ViewProvider errorViewProvider

    CustomNavigator(final ComponentContainer container) {
        super(UI.getCurrent(), container)
        addViewChangeListener(securedViewChangeListener)
        initViewProviders()
    }

    private def initViewProviders() {
        log.info "initViewProviders:: Start"
        List<CustomViewType> flattenViewPermissions = (List<CustomViewType>) VaadinSession.getCurrent().getAttribute("user-view-permissions")
        // A dedicated view provider is added for each separate view type
        for (final CustomViewType viewType : CustomViewType.values()) {

            //Create only allowed views, the rest will be omitted
            if (viewType.viewName in flattenViewPermissions*.viewName) {

                log.info "initViewProviders:: view found -> ${viewType.viewName}"

                ViewProvider viewProvider = new Navigator.ClassBasedViewProvider(viewType.getViewName(), viewType.getViewClass()) {
                    // This field caches an already initialized view instance if the
                    // view should be cached (stateful views).
                    private View cachedInstance

                    @Override
                    public View getView(final String viewName) {
                        View result = null
                        if (viewType.getViewName().equals(viewName)) {
                            if (viewType.isStateful()) {
                                // Stateful views get lazily instantiated
                                if (cachedInstance == null) {
                                    cachedInstance = super.getView(viewType.getViewName())
                                }
                                result = cachedInstance
                            } else {
                                // Non-stateful views get instantiated every time
                                // they're navigated to
                                result = super.getView(viewType.getViewName())
                            }
                        }
                        return result
                    }
                }


                if (viewType == ERROR_VIEW) {
                    errorViewProvider = viewProvider
                }
                addView(viewProvider.getViewName(), viewProvider.getViewClass())

            } else {
                continue
            }

        }

        setErrorProvider(new ViewProvider() {
            @Override
            public String getViewName(final String viewAndParameters) {
                return ERROR_VIEW.getViewName()
            }

            @Override
            public View getView(final String viewName) {
                return errorViewProvider.getView(ERROR_VIEW.getViewName())
            }
        })
    }

}
