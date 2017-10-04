package mx.grailscoder.presenter

import com.google.common.eventbus.Subscribe
import com.vaadin.server.Page
import com.vaadin.shared.Position
import com.vaadin.ui.Notification
import groovy.util.logging.Log4j
import mx.grailscoder.presenter.event.CustomEvent
import mx.grailscoder.presenter.event.CustomEventBus
import mx.grailscoder.presenter.event.CustomEventBusResponse

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya@outlook.com
 * @Date 5/10/2016

 *
 */
//@GrailsCompileStatic
@Log4j
class CustomEventBusSubscriber {

    CustomEventBusSubscriber(){
        CustomEventBus.register(this)
    }

    /**
     *
     *   http://tomaszdziurko.pl/2012/01/google-guava-eventbus-easy-elegant-publisher-subscriber-cases/
    */
    @Subscribe
    void userProfileEventUpdateRequested(final CustomEvent.ProfileUpdatedEvent event) {
        log.info "userProfileEventUpdateRequested:: Start"
        try {
            CustomEventBusResponse result = EntryPointUI.getUserDataSourceProvider().updateUser(event.getUserDetails())
            log.info "Execution result was: " + ("The result is: ${result?.dump()}")
            log.info "Execution result was: " + (result?.isSuccessful ? "Successful" : "Not successful")
            if (result?.isSuccessful) {
                this.showSuccessNotification(event, "Profile updated successfully")
            } else {
                this.showErrorNotification(event, result)
            }
        } catch (Exception ex) {
            this.showUnexpectedErrorNotification(event, ex)
        }
    }

    @Subscribe
    void newUserEventRequested(final CustomEvent.NewProfileEvent event) {
        log.info "newUserEventRequested:: Start"
        try {
            CustomEventBusResponse result = EntryPointUI.getUserDataSourceProvider().createUserWithRoles(event.getUserDetails(), event.getRoles())
            log.info "Execution result was: " + ("The result is: ${result?.dump()}")
            if (result?.isSuccessful) {
                this.showSuccessNotification(event,"User created successfully")
                CustomEventBus.post(new CustomEvent.ReloadView(CustomViewType.USER))
            } else {
                this.showErrorNotification(event, result)
            }
        } catch (Exception ex) {
            this.showUnexpectedErrorNotification(event, ex)
        }
    }

    @Subscribe
    void userProfileUpdateByAdminRequested(final CustomEvent.ProfileUpdatedByAdminEvent event) {
        log.info "userProfileUpdateByAdminRequested:: Start"
        try {
            CustomEventBusResponse result = EntryPointUI.getUserDataSourceProvider().updateUser(event.getUserDetails())
            log.info "Execution result was: " + ("The result is: ${result?.dump()}")
            log.info "Execution result was: " + (result?.isSuccessful ? "Successful" : "Not successful")
            if (result?.isSuccessful) {
                this.showSuccessNotification(event, "Profile updated successfully")
            } else {
                this.showErrorNotification(event, result)
            }
        } catch (Exception ex) {
            this.showUnexpectedErrorNotification(event, ex)
        }
    }

    private showSuccessNotification(final def event, final String message){
        Notification success = new Notification(message)
        success.setDelayMsec(3000)
        success.setStyleName("bar success small")
        success.setPosition(Position.BOTTOM_CENTER)
        if(event?.getUI()) {
            success.show(event.getUI().getPage().getCurrent())
        } else {
            success.show(Page.getCurrent())
        }
        success.show(event.getUI().getPage().getCurrent())
    }

    private showErrorNotification(final def event, final CustomEventBusResponse result){
        Notification error = new Notification("Error: ${result.errorMessage}")
        error.setDelayMsec(-1)
        error.setStyleName("bar error small")
        error.setPosition(Position.BOTTOM_CENTER)
        if(event?.getUI()) {
            error.show(event.getUI().getPage().getCurrent())
        } else {
            error.show(Page.getCurrent())
        }
    }

    private showUnexpectedErrorNotification(final def event, final Exception ex){
        Notification exception = new Notification("Unexpected error")
        exception.setDelayMsec(-1)
        exception.setStyleName("bar error small")
        exception.setPosition(Position.BOTTOM_CENTER)
        log.error "Error caused by: ${ex.getStackTrace().toString()}"
        if(event?.getUI()) {
            exception.show(event.getUI().getPage().getCurrent())
        } else {
            exception.show(Page.getCurrent())
        }
    }

}
