package mx.grailscoder.view.customcomponents

import com.vaadin.data.Binder
import com.vaadin.data.ValueProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.data.provider.QuerySortOrder
import com.vaadin.data.validator.EmailValidator
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.event.selection.SelectionEvent
import com.vaadin.event.selection.SelectionListener
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.*
import com.vaadin.shared.ui.MarginInfo
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.themes.ValoTheme
import grails.compiler.GrailsCompileStatic
import mx.grailscoder.enums.view.UserOperationType
import mx.grailscoder.pogo.security.RoleViewPogo
import mx.grailscoder.pogo.security.UserPogo
import mx.grailscoder.presenter.EntryPointUI
import mx.grailscoder.pogo.person.PersonPogo
import mx.grailscoder.pogo.security.RolePogo
import mx.grailscoder.pogo.security.ViewAccessPogo
import mx.grailscoder.presenter.event.CustomEvent
import mx.grailscoder.presenter.event.CustomEventBus

import java.util.stream.Stream

import static mx.grailscoder.util.Grails.i18n

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya@outlook.com
 * @Date 3/7/2016

 *
 */

@SuppressWarnings("serial")
@GrailsCompileStatic
public class ProfilePreferencesWindow extends Window {

    public static final String          ID = "profilepreferenceswindow"

    private UserPogo                    userPogo
    private final Binder<UserPogo>      binder
    private TextField                   firstNameField
    private TextField                   lastNameField
    private ComboBox                    titleField
    private RadioButtonGroup<String>    sexField
    private TextField                   emailField
    private TextField                   phoneField
    private ComboBox<Locale>            language

    // user details section
    private TextField                   username
    private PasswordField               password
    private TextField                   company
    private Grid<RolePogo> rolesGrid
    private ComboBox<ViewAccessPogo> defaultView

    private UserOperationType           userOperationType

    private ProfilePreferencesWindow(final UserPogo user, final boolean preferencesTabOpen, final UserOperationType userOperationType = UserOperationType.NOT_ALLOWED) {
        userPogo                = user?:new UserPogo(person: new PersonPogo())
        this.userOperationType  = userOperationType

        addStyleName("profile-window")
        setId(ID)
        Responsive.makeResponsive(this)

        setModal(true)
        addCloseShortcut(KeyCode.ESCAPE, null)
        setResizable(false)
        setClosable(false)
        setHeight(95.0f, Sizeable.Unit.PERCENTAGE)

        VerticalLayout content = new VerticalLayout()
        content.setSizeFull()
        content.setMargin(new MarginInfo(true, false, false, false))
        setContent(content)

        TabSheet detailsWrapper = new TabSheet()
        detailsWrapper.setSizeFull()
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR)
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP)
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS)
        content.addComponent(detailsWrapper)
        content.setExpandRatio(detailsWrapper, 1f)

        // build all of the tabs
        detailsWrapper.addComponent(buildProfileTab())
        detailsWrapper.addComponent(buildUserDetails(user))
        detailsWrapper.addComponent(buildPreferencesTab())

        if (preferencesTabOpen) {
            detailsWrapper.setSelectedTab(1)
        }

        content.addComponent(buildFooter())

        // initialize binder
        binder = new Binder<>(UserPogo.class)

        // bind UI components so that they get mapped directly to the UserPogo class
        this.bindUIComponents()

    }

    private bindUIComponents() {

        binder.forField(firstNameField).asRequired("First name must not be empty").bind(new ValueProvider<UserPogo, String>() {
            @Override
            String apply(UserPogo userPogo) {
                return userPogo.person.firstName
            }
        }, new Setter<UserPogo, String>() {
            @Override
            void accept(UserPogo userPogo, String firstName) {
                userPogo.person.firstName = firstName
            }
        })
        binder.forField(lastNameField).asRequired("Last name must not be empty").bind(new ValueProvider<UserPogo, String>() {
            @Override
            String apply(UserPogo userPogo) {
                return userPogo.person.lastName
            }
        }, new Setter<UserPogo, String>() {
            @Override
            void accept(UserPogo userPogo, String lastName) {
                userPogo.person.lastName = lastName
            }
        })

        binder.bind(titleField, new ValueProvider<UserPogo, String>() {
            @Override
            String apply(UserPogo pogo) {
                return pogo.person.title
            }
        }, new Setter<UserPogo, String>() {
            @Override
            void accept(UserPogo pogo, String titleText) {
                pogo.person.title = titleText
            }
        })
        binder.bind(sexField, new ValueProvider<UserPogo, String>() {
            @Override
            String apply(UserPogo userPogo) {
                return userPogo.person.male?"Male":"Female"
            }
        }, new Setter<UserPogo, String>() {
            @Override
            void accept(UserPogo userPogo, String isMale) {
                userPogo.person.male = isMale.equals("Male")?true:false
            }
        })

        binder.forField(emailField).withValidator(new EmailValidator("Not valid email"))
                .bind(new ValueProvider<UserPogo, String>() {
                    @Override
                    String apply(UserPogo userPogo) {
                        return userPogo.person.email
                    }
                }, new Setter<UserPogo, String>() {
                    @Override
                    void accept(UserPogo userPogo, String email) {
                        userPogo.person.email = email
                    }
        })

        binder.bind(phoneField, new ValueProvider<UserPogo, String>() {
            @Override
            String apply(UserPogo userPogo) {
                return userPogo.person.phone
            }
        }, new Setter<UserPogo, String>() {
            @Override
            void accept(UserPogo userPogo, String phone) {
                userPogo.person.phone = phone
            }
        })

        binder.forField(language).asRequired("").bind(new ValueProvider<UserPogo, Locale>() {
            @Override
            Locale apply(UserPogo userPogo) {
                return userPogo.locale
            }
        }, new Setter<UserPogo, Locale>() {
            @Override
            void accept(UserPogo userPogo, Locale locale) {
                userPogo.locale = locale
            }
        })

        binder.bind(username, "username")
        binder.bind(password, "password")
        //binder.bind(company, "company.name")

        if(this.userOperationType in [UserOperationType.ADMIN_EDITION, UserOperationType.SELF_EDITION]){
            binder.readBean(userPogo)

            // clear password field to avoid automatic changes
            password.clear()
        }

    }

    private Component buildProfileTab() {
        HorizontalLayout root = new HorizontalLayout()
        root.setCaption("Profile")
        root.setIcon(VaadinIcons.USER)
        root.setWidth(100.0f, Sizeable.Unit.PERCENTAGE)
        root.setSpacing(true)
        root.setMargin(true)
        root.addStyleName("profile-form")

        VerticalLayout pic = new VerticalLayout()
        pic.setSizeUndefined()
        pic.setSpacing(true)
        Image profilePic = new Image(null, new ThemeResource("img/profile-pic-300px.jpg"))
        profilePic.setWidth(100.0f, Sizeable.Unit.PIXELS)
        pic.addComponent(profilePic)

        Button upload = new Button("Change…", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show("Not implemented in this demo")
            }
        })
        upload.addStyleName(ValoTheme.BUTTON_TINY)
        pic.addComponent(upload)

        root.addComponent(pic)

        FormLayout details = new FormLayout()
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT)
        root.addComponent(details)
        root.setExpandRatio(details, 1)

        firstNameField = new TextField("First Name")
        firstNameField.setRequiredIndicatorVisible(true)
        details.addComponent(firstNameField)
        lastNameField = new TextField("Last Name")
        lastNameField.setRequiredIndicatorVisible(true)
        details.addComponent(lastNameField)

        titleField = new ComboBox("Title")
        titleField.setPlaceholder("Please specify")
        titleField.setItems("Mr.", "Mrs.", "Ms.")
        titleField.setEmptySelectionAllowed(false)
        details.addComponent(titleField)

        sexField = new RadioButtonGroup<>("Sex")
        sexField.setItems("Female", "Male")
        sexField.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL)
        details.addComponent(sexField)

        Label section = new Label("Contact Info")
        section.addStyleName(ValoTheme.LABEL_H4)
        section.addStyleName(ValoTheme.LABEL_COLORED)
        details.addComponent(section)

        emailField = new TextField("Email")
        emailField.setWidth("100%")
        emailField.setRequiredIndicatorVisible(true)

        details.addComponent(emailField)

        phoneField = new TextField("Phone")
        phoneField.setWidth("100%")
        details.addComponent(phoneField)

        Label languageSection = new Label("Language Options")
        languageSection.addStyleName(ValoTheme.LABEL_H4)
        languageSection.addStyleName(ValoTheme.LABEL_COLORED)
        details.addComponent(languageSection)

        language = new ComboBox<>("Language", this.getVMAvailableLocales())
        language.setPlaceholder("Please specify")
        language.setItemCaptionGenerator(new ItemCaptionGenerator<Locale>() {
            @Override
            String apply(Locale item) {
                return item.getDisplayName()
            }
        })
        language.setEmptySelectionAllowed(false)
        language.setRequiredIndicatorVisible(true)
        details.addComponent(language)

        return root
    }


    private Component buildUserDetails(UserPogo userPogo) {
        HorizontalLayout root = new HorizontalLayout()
        root.setCaption("User Details")
        root.setIcon(VaadinIcons.USER)
        root.setWidth(100.0f, Sizeable.Unit.PERCENTAGE)
        root.setSpacing(true)
        root.setMargin(true)
        root.addStyleName("profile-form")

        FormLayout details = new FormLayout()
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT)
        root.addComponent(details)
        root.setExpandRatio(details, 1)

        Label accountSection = new Label("Account Status")
        accountSection.addStyleName(ValoTheme.LABEL_H4)
        accountSection.addStyleName(ValoTheme.LABEL_COLORED)
        details.addComponent(accountSection)

        company = new TextField("Company")
        company.setReadOnly(true)
        company.setEnabled(false)
        details.addComponent(company)

        username = new TextField("Username")
        details.addComponent(username)

        password = new PasswordField("Password")
        details.addComponent(password)

        Label rolesSection = new Label("Permissions")
        rolesSection.addStyleName(ValoTheme.LABEL_H4)
        rolesSection.addStyleName(ValoTheme.LABEL_COLORED)
        details.addComponent(rolesSection)

        rolesGrid = new Grid(RolePogo.class)

        rolesGrid.setSizeFull()
        rolesGrid.setHeight("200px")
        rolesGrid.addStyleName(ValoTheme.TABLE_BORDERLESS)
        rolesGrid.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES)
        rolesGrid.addStyleName(ValoTheme.TABLE_COMPACT)
        rolesGrid.setColumnReorderingAllowed(true)
        rolesGrid.setSelectionMode(Grid.SelectionMode.MULTI)
        rolesGrid.setColumnReorderingAllowed(true)
        rolesGrid.setColumnOrder("authority")
        rolesGrid.removeColumn("metaClass")
        rolesGrid.removeColumn("id")

        rolesGrid.setSelectionMode(Grid.SelectionMode.MULTI)


        defaultView = new ComboBox<>("Select default module")

        // check for admin mode, if so it is the administrator role creating other users
        // otherwise it is the user updating it's data itself
        switch (userOperationType){
            case UserOperationType.SELF_EDITION:
                    rolesGrid.setDataProvider(buildRolesDatasource(userPogo))
                    username.setEnabled(false)
                    rolesGrid.setSelectionMode(Grid.SelectionMode.NONE)
                // TODO: load views per user by default
                break
            case [UserOperationType.ADMIN_EDITION, UserOperationType.ADMIN_CREATION]:
                    //defaultView.setEnabled(true)
                    rolesGrid.setDataProvider(new Grid.FetchItemsCallback() {
                        @Override
                        Stream<RolePogo> fetchItems(List<QuerySortOrder> sortOrder, int offset, int limit) {
                            return buildAllRolesDatasource(offset,limit).stream()
                        }
                    }, new SerializableSupplier<Integer>() {
                        @Override
                        Integer get() {
                            return EntryPointUI.getUserDataSourceProvider().countAllRoles()
                        }
                    })
                    username.setEnabled(true)
                    rolesGrid.setSelectionMode(Grid.SelectionMode.MULTI)
                    rolesGrid.addSelectionListener(new GridListener())
                break
            case UserOperationType.NOT_ALLOWED:
                break
        }

        defaultView.setPlaceholder("Select the view you want to see after logging in")
        defaultView.setDescription("This is the default module(view) you will see after logging in.")
        defaultView.setItemCaptionGenerator(new ItemCaptionGenerator<ViewAccessPogo>() {
            @Override
            String apply(ViewAccessPogo item) {
                return i18n("mx.grailscoder.view.menuselector." + item.viewName)
            }
        })

        details.addComponent(rolesGrid)
        details.addComponent(defaultView)

        return root
    }


    private Component buildPreferencesTab() {
        VerticalLayout root = new VerticalLayout()
        root.setCaption("Preferences")
        root.setIcon(VaadinIcons.COGS)
        root.setSpacing(true)
        root.setMargin(true)
        root.setSizeFull()

        Label message = new Label("Not implemented in this demo")
        message.setSizeUndefined()
        message.addStyleName(ValoTheme.LABEL_LIGHT)
        root.addComponent(message)
        root.setComponentAlignment(message, Alignment.MIDDLE_CENTER)

        return root
    }

    private ListDataProvider<RolePogo> buildRolesDatasource(UserPogo userPogo) {
        def result = EntryPointUI.getUserDataSourceProvider().getRolesForUser(userPogo).collect {it.roleApp}
        return new ListDataProvider<RolePogo>(result)
    }

    private Collection<RolePogo> buildAllRolesDatasource(Integer offset, Integer limit) {
        return EntryPointUI.getUserDataSourceProvider().getAllRoles(offset, limit)
    }

    private List<Locale> getVMAvailableLocales(){
        return EntryPointUI.getUtilitiesDataSourceProvider().getAvailableLocales()
    }

    private List<RoleViewPogo> getRoleAndViewsAssociationByRoleList(List<RolePogo> rolePogoList){ return EntryPointUI.getRoleDataSourceProvider().getAllViewsByRoleList(rolePogoList) }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout()
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR)
        footer.setWidth(100.0f, Sizeable.Unit.PERCENTAGE)

        Button ok       = new Button("OK")
        Button cancel   = new Button("Cancel")

        ok.addStyleName(ValoTheme.BUTTON_PRIMARY)
        ok.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (binder.validate().isOk()) {
                    // Updated user should also be persisted to database.

                    userPogo.person.firstName   = firstNameField.getValue()
                    userPogo.person.lastName    = lastNameField.getValue()
                    userPogo.person.title       = titleField.getValue().toString()
                    userPogo.person.male        = sexField.getValue()
                    userPogo.person.email       = emailField.getValue()
                    userPogo.person.phone       = phoneField.getValue()
                    userPogo.password           = password.getValue()
                    userPogo.locale             = language.getValue()

                    switch (userOperationType){
                        case UserOperationType.SELF_EDITION:
                            CustomEventBus.post(new CustomEvent.ProfileUpdatedEvent(userPogo, UI.getCurrent()))
                            break
                        case UserOperationType.ADMIN_CREATION:
                                userPogo.username           = username.getValue()
                                userPogo.defaultView        = defaultView.getValue().viewName
                                Set<RolePogo> roles         = rolesGrid.getSelectedItems()

                                // TODO: Remove this value set by default
                                userPogo.person.isCompany   = false

                                CustomEventBus.post(new CustomEvent.NewProfileEvent(userPogo, roles,UI.getCurrent()))
                            break
                        case UserOperationType.ADMIN_EDITION:
                            userPogo.username           = username.getValue()
                            userPogo.defaultView        = defaultView.getValue().viewName
                            Set<RolePogo> roles         = rolesGrid.getSelectedItems()

                            // TODO: Remove this value set by default
                            userPogo.person.isCompany   = false

                            CustomEventBus.post(new CustomEvent.ProfileUpdatedByAdminEvent(userPogo, roles,UI.getCurrent()))
                            break
                    }

                    close()
                }

            }
        })

        cancel.addClickListener(new ClickListener() {
            @Override
            void buttonClick(ClickEvent event) {
                close()
            }
        })

        ok.focus()
        footer.addComponent(ok)
        footer.addComponent(cancel)
        footer.setComponentAlignment(ok, Alignment.MIDDLE_RIGHT)
        //footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT)
        return footer
    }

    public static void open(final UserPogo user, final Boolean preferencesTabActive, final UserOperationType userOperationType) {
        CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent())
        Window w = new ProfilePreferencesWindow(user, preferencesTabActive, userOperationType)
        UI.getCurrent().addWindow(w)
        w.focus()
    }

    private class GridListener implements SelectionListener<RolePogo>{

        @Override
        void selectionChange(SelectionEvent<RolePogo> event) {
            def selectedItems       = event.getAllSelectedItems().toList()

            if(!selectedItems.isEmpty()){
                RolePogo firstSelected              = event.getFirstSelectedItem().get()
                List<RoleViewPogo> rolesAndViews    = getRoleAndViewsAssociationByRoleList(selectedItems)
                //defaultView.setEnabled(true)
                defaultView.setItems(rolesAndViews.collect {it.viewAccess})
                ViewAccessPogo viewPogo = rolesAndViews.find {it.roleApp.id == firstSelected.id}?.viewAccess
                if (viewPogo) {
                    defaultView.setSelectedItem(viewPogo)
                }
            } else {
                defaultView.clear()
                //defaultView.setEnabled(false)
            }

        }
    }
}
