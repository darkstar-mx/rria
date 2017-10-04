package mx.grailscoder.view

import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.server.FontAwesome
import com.vaadin.server.Responsive
import com.vaadin.server.VaadinSession
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.themes.ValoTheme
import mx.grailscoder.presenter.event.CustomEvent
import mx.grailscoder.presenter.event.CustomEventBus

import static mx.grailscoder.util.Grails.*

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date 15/08/2015

 *
 */
class LoginView extends VerticalLayout implements View {

    static final String VIEW_NAME = "login"

    private TextField txnUsername
    private PasswordField txnPassword
    private Button btnLogin
    private Label welcome
    private Label title
    private Locale locale

    @Override
    public void enter(ViewChangeEvent event) {
        this
    }

    public LoginView() {
        locale = VaadinSession.getCurrent().getAttribute("user-locale") ?: Locale.US
        setSizeFull()
        Component loginForm = buildLoginForm()
        addComponent(loginForm)
        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER)
        Responsive.makeResponsive(this)
    }

    private Component buildLoginForm() {
        final VerticalLayout loginPanel = new VerticalLayout()
        loginPanel.setSizeUndefined()
        loginPanel.setSpacing(true)
        Responsive.makeResponsive(loginPanel)
        loginPanel.addStyleName("login-panel");

        loginPanel.addComponent(buildLabels())
        loginPanel.addComponent(buildFields())
        loginPanel.addComponent(new CheckBox(i18n("mx.grailscoder.view.loginview.rememberme", locale), true))

        return loginPanel
    }

    private Component buildFields() {
        HorizontalLayout fields = new HorizontalLayout()
        fields.setSpacing(true)
        fields.addStyleName("fields")

        txnUsername = new TextField(i18n("mx.grailscoder.view.loginview.username", locale))
        txnPassword = new PasswordField(i18n("mx.grailscoder.view.loginview.password", locale))
        btnLogin = new Button(i18n("mx.grailscoder.view.loginview.signin", locale))

        txnUsername.setIcon(FontAwesome.USER)
        txnUsername.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON)


        txnPassword.setIcon(FontAwesome.LOCK)
        txnPassword.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON)


        btnLogin.addStyleName(ValoTheme.BUTTON_PRIMARY)
        btnLogin.setClickShortcut(KeyCode.ENTER)
        btnLogin.focus()

        fields.addComponents(txnUsername, txnPassword, btnLogin)
        fields.setComponentAlignment(btnLogin, Alignment.BOTTOM_LEFT)

        btnLogin.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                /*
                Notification notif = new Notification(
                        "Login Incorrect",
                        "Checking login credentials",
                        Notification.TYPE_HUMANIZED_MESSAGE);*/
                CustomEventBus.post(new CustomEvent.UserLoginRequestedEvent(txnUsername.value, txnPassword.value));
            }
        })
        return fields
    }

    private Component buildLabels() {
        CssLayout labels = new CssLayout()
        labels.addStyleName("labels")

        welcome = new Label(i18n("mx.grailscoder.view.loginview.welcome", locale))
        title = new Label(i18n("mx.grailscoder.view.loginview.title", locale))

        welcome.setSizeUndefined()
        welcome.addStyleName(ValoTheme.LABEL_H4)
        welcome.addStyleName(ValoTheme.LABEL_COLORED)
        labels.addComponent(welcome)

        title.setSizeUndefined()
        title.addStyleName(ValoTheme.LABEL_H3)
        title.addStyleName(ValoTheme.LABEL_LIGHT)

        labels.addComponent(title)
        return labels
    }

}
