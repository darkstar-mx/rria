package mx.grailscoder.view.error

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.server.Responsive
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import mx.grailscoder.presenter.event.CustomEventBus


/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Jan 14, 2016

 *
 */
class ErrorView extends VerticalLayout implements View {

    public static final String TITLE_ID = "dashboard-title"

    Label errorLabel

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

    ErrorView() {

        HorizontalLayout header = new HorizontalLayout()
        header.addStyleName("viewheader")
        header.setSpacing(true)

        errorLabel = new Label("You don't have permissions to view the content, please contact administrator.")
        errorLabel.setId(TITLE_ID)
        errorLabel.setSizeUndefined()
        errorLabel.addStyleName(ValoTheme.LABEL_FAILURE)
        errorLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN)

        header.addComponent(errorLabel)

        setSizeFull()
        setMargin(true)

        CustomEventBus.register(this)
        Responsive.makeResponsive(this)

    }

}
