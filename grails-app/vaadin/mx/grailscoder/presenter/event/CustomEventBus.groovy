package mx.grailscoder.presenter.event

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.SubscriberExceptionContext
import com.google.common.eventbus.SubscriberExceptionHandler
import mx.grailscoder.presenter.EntryPointUI

//import com.google.common.eventbus.AsyncEventBus
/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Aug 30, 2015

 *
 */
class CustomEventBus implements SubscriberExceptionHandler {

    final EventBus eventBus = new EventBus(this);
//		final EventBus eventBus = new AsyncEventBus(java.util.concurrent.Executors.newCachedThreadPool());

    static void post(final Object event) {
        EntryPointUI.getCustomEventbus().eventBus.post(event);
    }

    static void register(final Object object) {
        EntryPointUI.getCustomEventbus().eventBus.register(object);
    }

    static void unregister(final Object object) {
        EntryPointUI.getCustomEventbus().eventBus.unregister(object);
    }

    @Override
    final void handleException(final Throwable exception,
                               final SubscriberExceptionContext context) {
        exception.printStackTrace();
    }

}
