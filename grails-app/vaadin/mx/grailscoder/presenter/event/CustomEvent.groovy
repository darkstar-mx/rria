package mx.grailscoder.presenter.event

import com.vaadin.ui.UI
import mx.grailscoder.pogo.security.RolePogo
import mx.grailscoder.pogo.security.UserPogo
import mx.grailscoder.presenter.CustomViewType

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Aug 30, 2015

 *
 */
abstract class CustomEvent {

    static final class UserLoginRequestedEvent {
        private final String userName, password;

        UserLoginRequestedEvent(final String userName, final String password) {
            this.userName = userName;
            this.password = password;
        }

        String getUserName() {
            return userName;
        }

        String getPassword() {
            return password;
        }
    }

    static class BrowserResizeEvent {

    }

    static class UserLoggedOutEvent {

    }

    static class NotificationsCountUpdatedEvent {
    }

    static final class ReportsCountUpdatedEvent {
        private final int count;

        ReportsCountUpdatedEvent(final int count) {
            this.count = count;
        }

        int getCount() {
            return count;
        }

    }

    static final class PostViewChangeEvent {
        private final CustomViewType view;

        PostViewChangeEvent(final CustomViewType view) {
            this.view = view;
        }

        CustomViewType getView() {
            return view;
        }
    }

    static class CloseOpenWindowsEvent {
    }

    static class ProfileUpdatedEvent {

        private final UserPogo userDetails
        private final UI ui

        ProfileUpdatedEvent (final UserPogo userDetails, final UI ui) {
            this.userDetails = userDetails
            this.ui = ui
        }

        UserPogo getUserDetails(){
            return this.userDetails
        }

        UI getUI(){
            return this.ui
        }

    }

    static class ProfileUpdatedByAdminEvent {

        private final UserPogo          userDetails
        private final Set<RolePogo>     rolePogoList
        private final UI                ui

        ProfileUpdatedByAdminEvent(final UserPogo userDetails, final Set<RolePogo> rolePogoList, final UI ui) {
            this.userDetails    = userDetails
            this.rolePogoList   = rolePogoList
            this.ui             = ui
        }

        UserPogo getUserDetails(){
            return this.userDetails
        }

        Set<RolePogo> getRoles(){
            return this.rolePogoList
        }

        UI getUI(){
            return this.ui
        }

    }

    static class NewProfileEvent {

        private final UserPogo          userDetails
        private final Set<RolePogo>     rolePogoList
        private final UI                ui

        NewProfileEvent(final UserPogo userDetails, final Set<RolePogo> rolePogoList, final UI ui) {
            this.userDetails    = userDetails
            this.rolePogoList   = rolePogoList
            this.ui             = ui
        }

        UserPogo getUserDetails(){
            return this.userDetails
        }

        Set<RolePogo> getRoles(){
            return this.rolePogoList
        }

        UI getUI(){
            return this.ui
        }

    }

    /**
     * Class designed to indicate individual views a reloading is required.
     * The reloading will be customized on each view as per different needs.
     * */
    static class ReloadView {

        private CustomViewType customViewType

        ReloadView(final CustomViewType customViewType){
            this.customViewType = customViewType
        }

        CustomViewType getCustomViewType(){
            return this.customViewType
        }

    }

}
