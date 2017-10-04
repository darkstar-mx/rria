package mx.grailscoder.view.landing

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.ui.Alignment
import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import groovy.util.logging.Log4j

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya [ at ] outlook.com
 * @Date 3/23/2017

 *
 */
@Log4j
class LandingView extends VerticalLayout implements View{

    Label welcomeText

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) {
        log.info "Entering View"
    }

    LandingView(){
        welcomeText = new Label("Welcome to Application Manager")
        HorizontalLayout horizontalLayout = new HorizontalLayout()

        horizontalLayout.addComponent(welcomeText)
        horizontalLayout.setComponentAlignment(welcomeText, Alignment.TOP_CENTER)
        addComponent(horizontalLayout)
        setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER)

    }
}
