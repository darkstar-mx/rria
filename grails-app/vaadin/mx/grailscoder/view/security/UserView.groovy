package mx.grailscoder.view.security

import com.google.common.eventbus.Subscribe
import com.vaadin.contextmenu.GridContextMenu
import com.vaadin.contextmenu.Menu
import com.vaadin.contextmenu.MenuItem
import com.vaadin.data.HasValue
import com.vaadin.data.ValueProvider
import com.vaadin.data.provider.QuerySortOrder
import com.vaadin.event.ShortcutAction
import com.vaadin.event.ShortcutListener
import com.vaadin.icons.VaadinIcons
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.server.Page
import com.vaadin.server.Responsive
import com.vaadin.server.SerializableSupplier
import com.vaadin.server.VaadinSession
import com.vaadin.shared.Position
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.Grid.SelectionMode
import com.vaadin.ui.themes.ValoTheme
import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Log4j
import mx.grailscoder.enums.view.UserOperationType
import mx.grailscoder.pogo.security.UserPogo
import mx.grailscoder.presenter.EntryPointUI
import mx.grailscoder.view.customcomponents.ProfilePreferencesWindow
import mx.grailscoder.presenter.CustomViewType
import mx.grailscoder.presenter.event.CustomEvent
import mx.grailscoder.presenter.event.CustomEventBus

import java.util.stream.Stream

import static mx.grailscoder.util.Grails.i18n

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Nov 6, 2015

 *
 */
//@GrailsCompileStatic
@Log4j
@CompileStatic
class UserView extends VerticalLayout implements View {

    Grid<UserPogo> usersListGrid
    private Locale locale
    private Button createUser
    GridContextMenu<UserPogo> gridContextMenu

    private static final List<String> ALWAYS_VISIBLE_FIELDS = ["username", "enabled"]
    private static
    final List<String> ALLOWED_DEFAULT_FIELDS = ["username", "firstName", "lastName", "enabled"]
    private List<String> defaultHideableFields = []

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

    UserView() {

        locale                          = VaadinSession.getCurrent().getAttribute(UserPogo.class).locale ?: Locale.US
        usersListGrid                            = buildTable()
        setSizeFull()
        addStyleName("transactions")
        addComponent(buildToolbar())

        addComponent(usersListGrid)
        setExpandRatio(usersListGrid, 1)

        //by default all of the columns are hidable
        loadNonHideableProperties()

        CustomEventBus.register(this)
        Responsive.makeResponsive(this)
    }

    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    private loadNonHideableProperties() {
        log.info "Grid column ids are: ${(usersListGrid.getColumns()*.id).dump()}"
        defaultHideableFields = (usersListGrid.getColumns()*.id) - ALWAYS_VISIBLE_FIELDS
    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout()
        header.addStyleName("viewheader")
        header.setSpacing(true)
        Responsive.makeResponsive(header)

        Label title = new Label(i18n("mx.grailscoder.view.security.userview.title", locale))
        title.setSizeUndefined()
        title.addStyleName(ValoTheme.LABEL_H1)
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN)
        header.addComponent(title)

        header.addComponent(title)

        HorizontalLayout tools = new HorizontalLayout(buildFilter(), buildCreateUser())
        tools.setSpacing(true)
        tools.addStyleName("toolbar")
        header.addComponents(tools)

        return header
    }

    @Subscribe
    public void browserResized(final CustomEvent.BrowserResizeEvent event) {
        // Some columns are collapsed when browser window width gets small
        // enough to make the table fit better.

        def isMinimizeResizeRequired = Page.getCurrent().getBrowserWindowWidth() < 800

        for (String propertyId : defaultHideableFields) {
            usersListGrid?.getColumn(propertyId)?.setHidden(isMinimizeResizeRequired)
        }


    }

    /*
    private boolean filterByProperty(final String prop, final Item item, final String text) {
        if (item == null || item.getItemProperty(prop) == null
                || item.getItemProperty(prop).getValue() == null) {
            return false
        }
        String val = item.getItemProperty(prop).getValue().toString().trim()
                .toLowerCase()
        if (val.contains(text.toLowerCase().trim())) {
            return true
        }
        return false
    }*/

    void createNewReportFromSelection() {
        //UI.getCurrent().getNavigator().navigateTo(ApplicationViewType.REPORTS.getViewName())
        //CustomEventBus.post(new TransactionReportEvent((Collection<Transaction>) table.getValue()))
        // TODO: Open up new user window
    }

    @Override
    public void detach() {
        super.detach()
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        CustomEventBus.unregister(this)
    }


    private Grid buildTable() {
        usersListGrid            = new Grid<>(UserPogo.class)
        gridContextMenu = new GridContextMenu<>(usersListGrid)
        usersListGrid.setDataProvider(new Grid.FetchItemsCallback<UserPogo>() {
            @Override
            Stream<UserPogo> fetchItems(List<QuerySortOrder> sortOrder, int offset, int limit) {
                return EntryPointUI.getUserDataSourceProvider().collectAllUsersList(offset,limit).stream()
            }
        }, new SerializableSupplier<Integer>() {
            @Override
            Integer get() {
                return EntryPointUI.getUserDataSourceProvider().countUsers()
            }
        })

        usersListGrid.addColumn(new ValueProvider<UserPogo, String>() {
            @Override
            String apply(UserPogo userPogo) {
                return userPogo.person.firstName
            }
        }).setId("firstName")

        usersListGrid.addColumn(new ValueProvider<UserPogo, String>() {
            @Override
            String apply(UserPogo userPogo) {
                return userPogo.person.lastName
            }
        }).setId("lastName")

        usersListGrid.setSizeFull()
        usersListGrid.addStyleName(ValoTheme.TABLE_BORDERLESS)
        usersListGrid.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES)
        usersListGrid.addStyleName(ValoTheme.TABLE_COMPACT)
        usersListGrid.setColumnReorderingAllowed(true)
        usersListGrid.setSelectionMode(SelectionMode.SINGLE)
        usersListGrid.setColumnReorderingAllowed(true)

        usersListGrid.setColumnOrder(ALLOWED_DEFAULT_FIELDS.toArray(new String[0]))

        //set column headers according to user locales
        usersListGrid.getColumns().each {

            if (it.id in ALLOWED_DEFAULT_FIELDS) {
                it.setCaption(i18n("mx.grailscoder.view.security.userview." + it.id, locale))
            } else {
                usersListGrid.removeColumn(it.id)
            }

        }

        gridContextMenu.addGridBodyContextMenuListener(new GridContextMenu.GridContextMenuOpenListener<UserPogo>() {
            @Override
            void onContextMenuOpen(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<UserPogo> event) {

                def userInfo    = (UserPogo) event.getItem()
                def contextMenu = event.getContextMenu()
                if(userInfo) {
                    log.info "onContextMenuOpen:: ${userInfo.dump()}"
                    contextMenu.removeItems()
                    contextMenu.addItem("Edit", new Menu.Command() {
                        @Override
                        void menuSelected(MenuItem selectedItem) {

                            ProfilePreferencesWindow.open(userInfo, false, UserOperationType.ADMIN_EDITION)
                        }
                    })
                    contextMenu.addItem("Delete", new Menu.Command() {
                        @Override
                        void menuSelected(MenuItem selectedItem) {

                        }
                    })
                    contextMenu.addSeparator()
                    contextMenu.addItem("Close", new Menu.Command() {
                        @Override
                        void menuSelected(MenuItem selectedItem) {

                        }
                    })

                }

            }
        })



        return usersListGrid
    }

    private Button buildCreateUser() {
        final Button createUser = new Button(i18n("mx.grailscoder.view.security.userview.newUser", locale))
        createUser.setDescription(i18n("mx.grailscoder.view.security.userview.newUserHint", locale))
        createUser.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                ProfilePreferencesWindow.open(null, false, UserOperationType.ADMIN_CREATION)
            }
        })
        createUser.setEnabled(true)
        return createUser
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
            @Override
            void valueChange(HasValue.ValueChangeEvent<String> event) {
                Notification notification = new Notification("Not yet implemented");
                notification.setDescription("<span>Search functionality not implemented.</span>");
                notification.setHtmlContentAllowed(true);
                notification.setStyleName("tray dark small closable login-help");
                notification.setPosition(Position.BOTTOM_CENTER);
                notification.setDelayMsec(20000);
                notification.show(Page.getCurrent());
            }
        })
        filter.setPlaceholder(i18n("mx.grailscoder.view.security.userview.filter", locale))
        filter.setIcon(VaadinIcons.SEARCH)
        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON)
        filter.addShortcutListener(new ShortcutListener("", ShortcutAction.KeyCode.ESCAPE, null) {
            @Override
            void handleAction(Object sender, Object target) {
                filter.setValue("")
            }
        })
        return filter
    }

    private class UserMenuContext<UserPogo> implements GridContextMenu.GridContextMenuOpenListener<UserPogo>{

        @Override
        void onContextMenuOpen(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent event) {

        }

    }

    private confirmDeleteAction(){
        Label message = new Label(
                "You have not saved this report. Do you want to save or discard any changes you've made to this report?");
        message.setWidth("25em");

        final Window confirmDialog = new Window("Delete User");
        //confirmDialog.setId(CONFIRM_DIALOG_ID);
        confirmDialog.addCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        confirmDialog.setModal(true);
        confirmDialog.setClosable(false);
        confirmDialog.setResizable(false);

        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);
        root.setMargin(true);
        confirmDialog.setContent(root);

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth("100%");
        footer.setSpacing(true);

        root.addComponents(message, footer);

        Button delete = new Button("Delete", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                confirmDialog.close();
                //DashboardEventBus.post(new ReportsCountUpdatedEvent(getComponentCount() - 1));
                Notification
                        .show("User deleted successfully",
                        "",
                        Notification.Type.TRAY_NOTIFICATION);
            }
        });
        delete.addStyleName(ValoTheme.BUTTON_PRIMARY);

        Button cancel = new Button("Cancel", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                confirmDialog.close();
            }
        });

        footer.addComponents(cancel, delete);

        getUI().addWindow(confirmDialog);
        confirmDialog.focus();
    }


    @Subscribe
    private realoadUsers(final CustomEvent.ReloadView reloadView){
        if (reloadView.getCustomViewType() == CustomViewType.USER){
            usersListGrid.getDataProvider().refreshAll()
        }
    }

}
