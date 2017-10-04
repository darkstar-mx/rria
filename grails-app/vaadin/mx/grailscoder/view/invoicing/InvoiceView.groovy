package mx.grailscoder.view.invoicing

import com.google.common.eventbus.Subscribe
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.server.Responsive
import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import mx.grailscoder.presenter.event.CustomEvent
import mx.grailscoder.presenter.event.CustomEventBus

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Aug 30, 2015

 *
 */
class InvoiceView extends VerticalLayout implements View {


    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

    InvoiceView() {
        setSizeFull()
        addStyleName("transactions")
        addComponent(buildToolbar())

        CustomEventBus.register(this)
        //Responsive.makeResponsive(this)
        //setContent(root)
    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout()
        header.addStyleName("viewheader")
        header.setSpacing(true)
        Responsive.makeResponsive(header)

        Label title = new Label("Users")
        title.setSizeUndefined()
        title.addStyleName(ValoTheme.LABEL_H1)
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN)
        header.addComponent(title)

        return header
    }

    @Subscribe
    public void browserResized(final CustomEvent.BrowserResizeEvent event) {
        // Some columns are collapsed when browser window width gets small
        // enough to make the table fit better.
        /*
        if (defaultColumnsVisible()) {
            for (String propertyId : DEFAULT_COLLAPSIBLE) {
                //usersListGrid.setColumnCollapsed(propertyId, Page.getCurrent().getBrowserWindowWidth() < 800)
            }
        }*/
    }

    @Override
    public void detach() {
        super.detach()
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        CustomEventBus.unregister(this)
    }

}
