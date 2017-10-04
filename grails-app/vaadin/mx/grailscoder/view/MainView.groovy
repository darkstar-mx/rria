package mx.grailscoder.view

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.CssLayout
import com.vaadin.ui.HorizontalLayout
import mx.grailscoder.presenter.CustomNavigator


/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date 19/08/2015

 *
 */
class MainView extends HorizontalLayout implements View {

    static final String VIEW_NAME = "main"

    public MainView() {
        setSizeFull();
        addStyleName("mainview");

        addComponent(new MenuSelector());

        ComponentContainer content = new CssLayout();
        content.addStyleName("view-content");
        content.setSizeFull();
        addComponent(content);
        setExpandRatio(content, 1.0f);

        new CustomNavigator(content);
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */

    @Override
    public void enter(ViewChangeEvent event) {
        //this
    }

}
