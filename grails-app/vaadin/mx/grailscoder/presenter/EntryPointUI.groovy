package mx.grailscoder.presenter

import com.google.common.eventbus.Subscribe
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.server.Page
import com.vaadin.server.Responsive
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinSession
import com.vaadin.shared.Position
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.Notification
import com.vaadin.ui.UI
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme
import grails.compiler.GrailsCompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Log4j
import mx.grailscoder.presenter.datasource.interfaces.RoleProvider
import mx.grailscoder.pogo.security.RoleViewPogo
import mx.grailscoder.pogo.security.UserPogo
import mx.grailscoder.pogo.security.UserRolePogo
import mx.grailscoder.pogo.security.ViewAccessPogo
import mx.grailscoder.presenter.datasource.implementation.RoleProviderImpl
import mx.grailscoder.presenter.datasource.implementation.UserProviderImpl
import mx.grailscoder.presenter.datasource.implementation.UtilitiesProviderImpl
import mx.grailscoder.presenter.datasource.interfaces.UserProvider
import mx.grailscoder.presenter.datasource.interfaces.UtilitiesProvider
import mx.grailscoder.presenter.event.CustomEvent
import mx.grailscoder.presenter.event.CustomEventBus
import mx.grailscoder.security.AuthService
import mx.grailscoder.security.ViewSecurity
import mx.grailscoder.util.Grails
import mx.grailscoder.view.LoginView
import mx.grailscoder.view.MainView
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya@outlook.com
 * @Date 2/25/2017

 *
 */
@Theme("dashboard")
@Title("Application Manager")
//@PreserveOnRefresh
//@SpringUI(path = VaadinConfiguration.APP)
@SpringUI(path = "/")
@GrailsCompileStatic
@Log4j
class EntryPointUI extends UI {

    final CustomEventBus customEventBus             = new CustomEventBus()
    final AuthService authService                   = Grails.get(AuthService)
    final UserProvider userDataProvider             = new UserProviderImpl()
    final RoleProvider roleDataProvider             = new RoleProviderImpl()
    final UtilitiesProvider utilDataProvider        = new UtilitiesProviderImpl()

    CustomEventBusSubscriber customEventBusSubscriber

    /* (non-Javadoc)
     * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
     */
    @Override
    protected void init(VaadinRequest request) {
        log.info "Starting Application Manager UI Application"
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            //Set default locale to session value
            VaadinSession.getCurrent().setAttribute("user-locale", UI.getCurrent().getSession().getLocale() ?: Locale.US)
            this.setLocale((Locale) VaadinSession.getCurrent().getAttribute("user-locale"))
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
        CustomEventBus.register(this)
        Responsive.makeResponsive(this)
        addStyleName(ValoTheme.UI_WITH_MENU)

        updateContent(null)

        // Some views need to be aware of browser resize events so a
        // BrowserResizeEvent gets fired to the event bus on every occasion.
        Page.getCurrent().addBrowserWindowResizeListener(
                new Page.BrowserWindowResizeListener() {
                    @Override
                    void browserWindowResized(
                            final Page.BrowserWindowResizeEvent event) {
                        CustomEventBus.post(new CustomEvent.BrowserResizeEvent())
                    }
                })
        customEventBusSubscriber = new CustomEventBusSubscriber()
    }

    /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    private void addErrorView() {

        getNavigator().addView(CustomViewType.ERROR.viewName, CustomViewType.ERROR.viewClass);

        ViewSecurity.add(CustomViewType.ERROR.viewClass, ['ROLE_ERROR'])
    }

    /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    private void updateContent(Authentication auth) {
        Collection<GrantedAuthority> authorities = auth?.authorities
        if (auth?.isAuthenticated()) {
            setContent(new MainView())
            removeStyleName("loginview")

            UserPogo userPogo = (UserPogo) VaadinSession.getCurrent().getAttribute(UserPogo.class)
            addErrorView()
            getNavigator().navigateTo(userPogo.defaultView)
        } else {
            setContent(new LoginView())
            addStyleName("loginview")
        }

    }

    private void setViewPermissions(List<RoleViewPogo> roleViews) {
        log.info "setViewPermissions:: Start"
        roleViews.each {
            log.info "setViewPermissions:: view:[${it.viewAccess.viewName}], authority:[${it.roleApp.authority}]"
            CustomViewType customViewType = CustomViewType.getByViewName(it.viewAccess.viewName)
            ViewSecurity.add(customViewType.viewClass, [it.roleApp.authority])
        }

    }

    @Subscribe
    void userLoginRequested(final CustomEvent.UserLoginRequestedEvent event) {

        try {
            Authentication authToken = getAuthenticationProvider().login(event.getUserName(), event.getPassword())
            if (authToken?.isAuthenticated()) {
                UserPogo user                   = getUserDataSourceProvider().getUserDetails(event.userName)
                List<UserRolePogo> roles        = getUserDataSourceProvider().getRolesForUser(user)
                List<RoleViewPogo> roleViews    = getRoleDataSourceProvider().getAllViewsByUserAndRoleList(roles)
                List<ViewAccessPogo> views      = roleViews*.viewAccess
                try {
                    VaadinSession.getCurrent().getLockInstance().lock();
                    UserPogo userPogo = user as UserPogo
                    this.setLocale(userPogo.getLocale())
                    VaadinSession.getCurrent().setLocale(userPogo.getLocale())
                    VaadinSession.getCurrent().setAttribute(UserPogo.class, userPogo)
                    VaadinSession.getCurrent().setAttribute("user-view-permissions", processViewsInformation(views))

                    // Now when the session is reinitialized, we can enable websocket communication. Or we could have just
                    // used WEBSOCKET_XHR and skipped this step completely.
                    //getPushConfiguration().setTransport(Transport.WEBSOCKET);
                    //getPushConfiguration().setPushMode(PushMode.AUTOMATIC);

                    //Set user locale
                    VaadinSession.getCurrent().setAttribute("user-locale", userPogo?.locale ?: Locale.US)
                } finally {
                    VaadinSession.getCurrent().getLockInstance().unlock();
                }


                setViewPermissions(roleViews)
                updateContent(authToken)
            } else {
                updateContent(null)
            }
        } catch (BadCredentialsException ex) {
            Notification notif = new Notification(
                    "Login Incorrect",
                    ex.getMessage(),
                    Notification.TYPE_WARNING_MESSAGE);
            notif.setDelayMsec(20000);
            notif.setPosition(Position.TOP_CENTER)
            notif.show(Page.getCurrent())
            updateContent(null)
        }


    }

    private List<CustomViewType> processViewsInformation(List<ViewAccessPogo> viewAccessPogoList) {
        log.info "processViewsInformation:: Start"
        def flattenedList = viewAccessPogoList.flatten().unique()
        List<CustomViewType> allowedViews = ((List<ViewAccessPogo>)flattenedList).collect {CustomViewType.getByViewName(it.viewName)}
        allowedViews << CustomViewType.ERROR
        log.info "processViewsInformation:: ${allowedViews.dump()}"
        return allowedViews
    }

    @Subscribe
    void userLoggedOut(final CustomEvent.UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice this doesn't
        // invalidate the current HttpSession.

        Page.getCurrent().setLocation("/");
        VaadinSession.getCurrent().close()
        Page.getCurrent().reload()

    }

    @Subscribe
    void closeOpenWindows(final CustomEvent.CloseOpenWindowsEvent event) {
        for (Window window : getWindows()) {
            window.close()
        }
    }

    public static AuthService getAuthenticationProvider() {
        return ((EntryPointUI) getCurrent()).getAuthService()
    }

    public static UserProvider getUserDataSourceProvider() {
        return ((EntryPointUI) getCurrent()).getUserDataProvider()
    }

    public static CustomEventBus getCustomEventbus() {
        return ((EntryPointUI) getCurrent()).getCustomEventBus()
    }

    public static RoleProvider getRoleDataSourceProvider() {
        return ((EntryPointUI) getCurrent()).getRoleDataProvider()
    }

    public static UtilitiesProvider getUtilitiesDataSourceProvider(){
        return ((EntryPointUI) getCurrent()).getUtilDataProvider()
    }
}

