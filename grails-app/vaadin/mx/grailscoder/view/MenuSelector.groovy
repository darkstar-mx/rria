package mx.grailscoder.view

import com.google.common.eventbus.Subscribe
import com.vaadin.server.FontAwesome
import com.vaadin.server.ThemeResource
import com.vaadin.server.VaadinSession
import com.vaadin.shared.ui.ContentMode
import com.vaadin.ui.*
import com.vaadin.ui.MenuBar.Command
import com.vaadin.ui.MenuBar.MenuItem
import com.vaadin.ui.themes.ValoTheme
import grails.compiler.GrailsCompileStatic
import groovy.util.logging.Log4j
import mx.grailscoder.enums.view.UserOperationType
import mx.grailscoder.pogo.security.UserPogo
import mx.grailscoder.presenter.CustomViewType
import mx.grailscoder.presenter.EntryPointUI
import mx.grailscoder.presenter.event.CustomEvent
import mx.grailscoder.view.customcomponents.ProfilePreferencesWindow
import mx.grailscoder.presenter.event.CustomEventBus
import mx.grailscoder.util.Grails

//import ProfilePreferencesWindow
/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date 19/08/2015

 *
 */
@Log4j
@GrailsCompileStatic
class MenuSelector extends CustomComponent {

    public static final String ID                       = "applicaton-menu"
    public static final String REPORTS_BADGE_ID         = "applicaton-menu-reports-badge"
    public static final String NOTIFICATIONS_BADGE_ID   = "applicaton-menu-notifications-badge"
    private static final String STYLE_VISIBLE           = "valo-menu-visible"
    private Label notificationsBadge
    private Label reportsBadge
    private MenuItem settingsItem
    private Locale locale

    MenuSelector() {
        locale = (Locale) VaadinSession.getCurrent().getAttribute("user-locale")
        setPrimaryStyleName("valo-menu")
        setId(ID)
        setSizeUndefined()
        setCompositionRoot(buildContent())
    }

    def Component buildContent() {
        final CssLayout menuContent = new CssLayout()
        menuContent.addStyleName("sidebar")
        menuContent.addStyleName(ValoTheme.MENU_PART)
        menuContent.addStyleName("no-vertical-drag-hints")
        menuContent.addStyleName("no-horizontal-drag-hints")
        menuContent.setWidth(null)
        menuContent.setHeight("100%")

        menuContent.addComponent(buildTitle())
        menuContent.addComponent(buildUserMenu())
        menuContent.addComponent(buildToggleButton())
        menuContent.addComponent(buildMenuItems())
        menuContent.addComponent(buildMenuUtilsItems())

        return menuContent
    }

    def Component buildTitle() {
        Label logo = new Label(Grails.i18n("mx.grailscoder.view.menuselector.title", locale), ContentMode.HTML)
        logo.setSizeUndefined()
        HorizontalLayout logoWrapper = new HorizontalLayout(logo)
        logoWrapper.setComponentAlignment(logo, Alignment.MIDDLE_CENTER)
        logoWrapper.addStyleName("valo-menu-title")
        return logoWrapper
    }

    def UserPogo getCurrentUser() {
        return (UserPogo) VaadinSession.getCurrent().getAttribute(UserPogo.class)
    }

    private Component buildUserMenu() {
        final MenuBar settings = new MenuBar()
        settings.addStyleName("user-menu")
        final UserPogo user = getCurrentUser()
        settingsItem = settings.addItem("", new ThemeResource("img/profile-pic-300px.jpg"), null)
        updateUserName(null)
        settingsItem.addItem(Grails.i18n("mx.grailscoder.view.menuselector.edit-profile", locale), new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                ProfilePreferencesWindow.open(user, false, UserOperationType.SELF_EDITION)
            }
        })
        settingsItem.addItem(Grails.i18n("mx.grailscoder.view.menuselector.preferences", locale), new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                ProfilePreferencesWindow.open(user, true, UserOperationType.SELF_EDITION)
            }
        })
        settingsItem.addSeparator()
        settingsItem.addItem(Grails.i18n("mx.grailscoder.view.menuselector.signout", locale), new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                CustomEventBus.post(new CustomEvent.UserLoggedOutEvent())
            }
        })
        return settings
    }

    private Component buildToggleButton() {
        Button valoMenuToggleButton = new Button(Grails.i18n("mx.grailscoder.view.menuselector.menu", locale), new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                if (getCompositionRoot().getStyleName().contains(STYLE_VISIBLE)) {
                    getCompositionRoot().removeStyleName(STYLE_VISIBLE)
                } else {
                    getCompositionRoot().addStyleName(STYLE_VISIBLE)
                }
            }
        })
        valoMenuToggleButton.setIcon(FontAwesome.LIST)
        valoMenuToggleButton.addStyleName("valo-menu-toggle")
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_BORDERLESS)
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_SMALL)
        return valoMenuToggleButton
    }

    private Component buildMenuItems() {
        CssLayout menuItemsLayout = new CssLayout()
        menuItemsLayout.addStyleName("valo-menuitems")
        final UserPogo user = getCurrentUser()

        List<CustomViewType> flattenViewPermisions = (List<CustomViewType>)VaadinSession.getCurrent().getAttribute("user-view-permissions")
        for (final CustomViewType view : CustomViewType.values()) {
            Component menuItemComponent = new ValoMenuItemButton(view, locale)
            if (view.viewName in flattenViewPermisions*.viewName && !view.viewName.equals(CustomViewType.ERROR.viewName)) {
                /*
                if (view == CustomViewType.STOCK) {
                    notificationsBadge = new Label()
                    notificationsBadge.setId(NOTIFICATIONS_BADGE_ID)
                    menuItemComponent = buildBadgeWrapper(menuItemComponent, notificationsBadge)
                }
                if (view == CustomViewType.USER) {
                    notificationsBadge = new Label()
                    notificationsBadge.setId(NOTIFICATIONS_BADGE_ID)
                    menuItemComponent = buildBadgeWrapper(menuItemComponent, notificationsBadge)
                }
                if (view == CustomViewType.LANDING) {
                    reportsBadge = new Label()
                    reportsBadge.setId(REPORTS_BADGE_ID)
                    menuItemComponent = buildBadgeWrapper(menuItemComponent, reportsBadge)
                }*/

                menuItemsLayout.addComponent(menuItemComponent)
            }
        }
        return menuItemsLayout

    }

    private Component buildMenuUtilsItems() {

        final MenuBar settings = new MenuBar()
        //settings.addStyleName("user-menu")
        /*
        settingsItem.addItem("Pricing View", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                //ProfilePreferencesWindow.open(user, false)
                log.info "buildMenuUtilsItems:: Changing to: ${CustomViewType.PRICING.viewName}"
                EntryPointUI.getCurrent().getNavigator().navigateTo(CustomViewType.PRICING.viewName)
            }
        })*/
        //return settings

        CssLayout menuItemsLayout = new CssLayout()
        menuItemsLayout.addStyleName("valo-menuitems")
        return menuItemsLayout

    }

    private Component buildBadgeWrapper(final Component menuItemButton,
                                        final Component badgeLabel) {
        CssLayout badgeWrapper = new CssLayout(menuItemButton)
        badgeWrapper.addStyleName("badgewrapper")
        badgeWrapper.addStyleName(ValoTheme.MENU_ITEM)
        badgeLabel.addStyleName(ValoTheme.MENU_BADGE)
        badgeLabel.setWidthUndefined()
        badgeLabel.setVisible(false)
        badgeWrapper.addComponent(badgeLabel)
        return badgeWrapper
    }

    @Subscribe
    public void updateUserName(final CustomEvent.ProfileUpdatedEvent event) {
        UserPogo user = getCurrentUser()
        //PersonWrapper person = user.person
        //settingsItem.setText(person?.firstName + " " + person?.lastName)
    }

    public final class ValoMenuItemButton extends Button {

        private static final String STYLE_SELECTED = "selected"

        private final CustomViewType view

        public ValoMenuItemButton(final CustomViewType view, final Locale locale) {
            this.view = view
            setPrimaryStyleName("valo-menu-item")
            setIcon(view.getIcon())
            setCaption(Grails.i18n(view.getLocaleEntry(), locale))
            CustomEventBus.register(this)
            addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final Button.ClickEvent event) {
                    log.info "Changing view to: ${view.viewName}"
                    EntryPointUI.getCurrent().getNavigator().navigateTo(view.getViewName())
                }
            })

        }

        @Subscribe
        public void postViewChange(final CustomEvent.PostViewChangeEvent event) {
            removeStyleName(STYLE_SELECTED)
            if (event.getView() == view) {
                addStyleName(STYLE_SELECTED)
            }
        }
    }

}
