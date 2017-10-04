package mx.grailscoder.view.productinventory

import com.google.common.eventbus.Subscribe
import com.vaadin.event.LayoutEvents.LayoutClickEvent
import com.vaadin.event.LayoutEvents.LayoutClickListener
import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.server.FontAwesome
import com.vaadin.server.Responsive
import com.vaadin.server.Sizeable.Unit
import com.vaadin.server.VaadinSession
import com.vaadin.ui.*
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.MenuBar.Command
import com.vaadin.ui.MenuBar.MenuItem
import com.vaadin.ui.themes.ValoTheme
import mx.grailscoder.presenter.event.CustomEvent
import mx.grailscoder.presenter.event.CustomEventBus
import mx.grailscoder.util.Grails

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Aug 30, 2015

 *
 */
class StockView extends Panel implements View {

    public static final String EDIT_ID = "dashboard-edit"
    public static final String TITLE_ID = "dashboard-title"

    private Label titleLabel
    private NotificationsButton notificationsButton
    private CssLayout dashboardPanels
    private final VerticalLayout root
    private Window notificationsWindow
    private Locale locale

    public StockView() {
        locale = VaadinSession.getCurrent().getAttribute("user-locale") ?: Locale.US

        addStyleName(ValoTheme.PANEL_BORDERLESS)
        setSizeFull()
        CustomEventBus.register(this)

        root = new VerticalLayout()
        root.setSizeFull()
        root.setMargin(true)
        root.addStyleName("dashboard-view")
        setContent(root)
        Responsive.makeResponsive(root)

        root.addComponent(buildHeader())

        Component content = buildContent()
        root.addComponent(content)
        root.setExpandRatio(content, 1)

        // All the open sub-windows should be closed whenever the root layoutComponent
        // gets clicked.
        root.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutClickEvent event) {
                CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent())
            }
        })
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout()
        header.addStyleName("viewheader")
        header.setSpacing(true)

        titleLabel = new Label(Grails.i18n("mx.grailscoder.view.productinventory.stockview.title", locale))
        titleLabel.setId(TITLE_ID)
        titleLabel.setSizeUndefined()
        titleLabel.addStyleName(ValoTheme.LABEL_H1)
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN)
        header.addComponent(titleLabel)

        notificationsButton = buildNotificationsButton()
        Component edit = buildEditButton()
        HorizontalLayout tools = new HorizontalLayout(notificationsButton, edit)
        tools.setSpacing(true)
        tools.addStyleName("toolbar")
        header.addComponent(tools)

        return header
    }

    private NotificationsButton buildNotificationsButton() {
        NotificationsButton result = new NotificationsButton()
        /*
        result.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                openNotificationsPopup(event)
            }
        })*/
        return result
    }

    private Component buildEditButton() {
        Button result = new Button()
        result.setId(EDIT_ID)
        result.setIcon(FontAwesome.EDIT)
        result.addStyleName("icon-edit")
        result.addStyleName(ValoTheme.BUTTON_ICON_ONLY)
        result.setDescription("Edit Dashboard")
        return result
    }

    private Component buildContent() {
        dashboardPanels = new CssLayout()
        dashboardPanels.addStyleName("dashboard-panels")
        Responsive.makeResponsive(dashboardPanels)
        return dashboardPanels
    }

    private Component createContentWrapper(final Component content) {
        final CssLayout slot = new CssLayout()
        slot.setWidth("100%")
        slot.addStyleName("dashboard-panel-slot")

        CssLayout card = new CssLayout()
        card.setWidth("100%")
        card.addStyleName(ValoTheme.LAYOUT_CARD)

        HorizontalLayout toolbar = new HorizontalLayout()
        toolbar.addStyleName("dashboard-panel-toolbar")
        toolbar.setWidth("100%")

        Label caption = new Label(content.getCaption())
        caption.addStyleName(ValoTheme.LABEL_H4)
        caption.addStyleName(ValoTheme.LABEL_COLORED)
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN)
        content.setCaption(null)

        MenuBar tools = new MenuBar()
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS)
        MenuItem max = tools.addItem("", FontAwesome.EXPAND, new Command() {

            @Override
            public void menuSelected(final MenuItem selectedItem) {
                /*
                if (!slot.getStyleName().contains("max")) {
                    selectedItem.setIcon(FontAwesome.COMPRESS)
                    toggleMaximized(slot, true)
                } else {
                    slot.removeStyleName("max")
                    selectedItem.setIcon(FontAwesome.EXPAND)
                    toggleMaximized(slot, false)
                }
                */
            }
        })
        max.setStyleName("icon-only")
        MenuItem root = tools.addItem("", FontAwesome.COG, null)
        root.addItem("Configure", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                Notification.show("Not implemented in this demo")
            }
        })
        root.addSeparator()
        root.addItem("Close", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                Notification.show("Not implemented in this demo")
            }
        })

        toolbar.addComponents(caption, tools)
        toolbar.setExpandRatio(caption, 1)
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT)

        card.addComponents(toolbar, content)
        slot.addComponent(card)
        return slot
    }

    private void openNotificationsPopup(final ClickEvent notificationEvent) {
        VerticalLayout notificationsLayout = new VerticalLayout()
        notificationsLayout.setMargin(true)
        notificationsLayout.setSpacing(true)

        Label title = new Label("Notifications")
        title.addStyleName(ValoTheme.LABEL_H3)
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN)
        notificationsLayout.addComponent(title)

        /*
        Collection<CustomNotification> notifications = EntryPointUI.getAuthenticationProvider()?.getNotifications()
        CustomEventBus.post(new CustomEvent.NotificationsCountUpdatedEvent())

        for (CustomNotification notification : notifications) {
            VerticalLayout notificationLayout = new VerticalLayout()
            notificationLayout.addStyleName("notification-item")

            Label titleLabel = new Label(notification.getFirstName() + " "
                    + notification.getLastName() + " "
                    + notification.getAction())
            titleLabel.addStyleName("notification-title")

            Label timeLabel = new Label(notification.getPrettyTime())
            timeLabel.addStyleName("notification-time")

            Label contentLabel = new Label(notification.getContent())
            contentLabel.addStyleName("notification-content")

            notificationLayout.addComponents(titleLabel, timeLabel,
                    contentLabel)
            notificationsLayout.addComponent(notificationLayout)
        }*/

        HorizontalLayout footer = new HorizontalLayout()
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR)
        footer.setWidth("100%")
        Button showAll = new Button("View All Notifications",
                new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        Notification.show("Not implemented in this demo")
                    }
                })
        showAll.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
        showAll.addStyleName(ValoTheme.BUTTON_SMALL)
        footer.addComponent(showAll)
        footer.setComponentAlignment(showAll, Alignment.TOP_CENTER)
        notificationsLayout.addComponent(footer)

        if (notificationsWindow == null) {
            notificationsWindow = new Window()
            notificationsWindow.setWidth(300.0f, Unit.PIXELS)
            notificationsWindow.addStyleName("notifications")
            notificationsWindow.setClosable(false)
            notificationsWindow.setResizable(false)
            notificationsWindow.setDraggable(false)
            notificationsWindow.setCloseShortcut(KeyCode.ESCAPE, null)
            notificationsWindow.setContent(notificationsLayout)
        }

        if (!notificationsWindow.isAttached()) {
            notificationsWindow.setPositionY(notificationEvent.getClientY() - notificationEvent.getRelativeY() + 40)
            getUI().addWindow(notificationsWindow)
            notificationsWindow.focus()
        } else {
            notificationsWindow.close()
        }
    }

    @Override
    public void enter(final ViewChangeEvent event) {
        notificationsButton.updateNotificationsCount(null)
    }

    private void toggleMaximized(final Component panel, final boolean maximized) {
        /*
        for (Iterator<Component> item = root.iterator() item.hasNext()) {
            item.next().setVisible(!maximized)
        }
        dashboardPanels.setVisible(true)
        for (Iterator<Component> item = dashboardPanels.iterator() item.hasNext()) {
            Component c = item.next()
            c.setVisible(!maximized)
        }
        */
        if (maximized) {
            panel.setVisible(true)
            panel.addStyleName("max")
        } else {
            panel.removeStyleName("max")
        }
    }


    public static final class NotificationsButton extends Button {
        static final String STYLE_UNREAD = "unread"
        public static final String ID = "dashboard-notifications"

        public NotificationsButton() {
            setIcon(FontAwesome.BELL)
            setId(ID)
            addStyleName("notifications")
            addStyleName(ValoTheme.BUTTON_ICON_ONLY)
            InventoryEventBus.register(this)
        }

        @Subscribe
        public void updateNotificationsCount(
                final CustomEvent.NotificationsCountUpdatedEvent event) {
            //setUnreadCount(ApplicationManagerUI.getAuthenticationProvider().getUnreadNotificationsCount())
            setUnreadCount(3)
        }

        public void setUnreadCount(final int count) {
            setCaption(String.valueOf(count))

            String description = "Notifications"
            if (count > 0) {
                addStyleName(STYLE_UNREAD)
                description += " (" + count + " unread)"
            } else {
                removeStyleName(STYLE_UNREAD)
            }
            setDescription(description)
        }
    }

}
