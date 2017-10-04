package mx.grailscoder.view.pricing

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.ui.Alignment
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import groovy.util.logging.Log4j

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Aug 30, 2015

 *
 */
@Log4j
class PricingView extends VerticalLayout implements View {

    Label welcomeText

    @Override
    void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        log.info "Entering View"
    }

    PricingView(){
        welcomeText = new Label("Welcome to Pricing view")
        HorizontalLayout horizontalLayout = new HorizontalLayout()

        horizontalLayout.addComponent(welcomeText)
        horizontalLayout.setComponentAlignment(welcomeText, Alignment.TOP_CENTER)
        addComponent(horizontalLayout)
        setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER)
    }
}
