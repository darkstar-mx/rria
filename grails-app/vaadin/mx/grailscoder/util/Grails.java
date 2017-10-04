package mx.grailscoder.util;

import com.vaadin.server.VaadinSession;
import grails.util.Holders;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Copy to integrate facilities
 * <p>
 * Manages access to Grails services (actually beans) and i18n.
 *
 * @author Ondrej Kvasnovsky
 */
@SuppressWarnings("unchecked")
public class Grails {

    /**
     * Returns bean from application context. Example use: Grails.get(UserService).findAllUsers()
     *
     * @param clazz bean class
     * @return bean from the context
     * @throws BeansException
     */
    public static <T> T get(final Class<T> clazz) throws BeansException {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * Returns bean from application context. Example use: Grails.get('dataSource')
     *
     * @param name of the bean
     * @return bean from the context
     * @throws BeansException
     */
    public static <T> T get(final String name) throws BeansException {
        return (T) getApplicationContext().getBean(name);
    }

    /**
     * Returns Spring ApplicationContext.
     *
     * @return Spring ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return Holders.getGrailsApplication().getMainContext();
    }

    /**
     * Returns Grails message source from the application context.
     *
     * @return grails message source
     */
    public static MessageSource getMessageSource() {
        return getApplicationContext().getBean("messageSource", MessageSource.class);
    }

    /**
     * Provides access to i18n values.
     *
     * @param key to localization property
     * @return value from properties file or key (if key value is not found)
     */
    public static String i18n(final String key) {
        return i18n(key, null, null, null);
    }

    /**
     * Provides access to i18n values.
     *
     * @param key to localization property
     * @return value from properties file or key (if key value is not found)
     */
    public static String i18n(final String key, final Locale locale) {
        return i18n(key, null, null, locale);
    }

    /**
     * Provides access to i18n values.
     *
     * @param key  to localization property
     * @param args arguments, e.g. "Hallo {0}"
     * @return value from properties file or key (if key value is not found)
     */
    public static String i18n(final String key, final Object[] args, final Locale locale) {
        return i18n(key, args, null, locale);
    }

    /**
     * Provides access to i18n values.
     *
     * @param key  to localization property
     * @param args arguments, e.g. "Hello {0}"
     * @return value from properties file or key (if key value is not found)
     */
    public static String i18n(final String key, final Object[] args,
                              final String defaultValue, Locale locale) {
        try {
            if (locale == null) {
                locale = VaadinSession.getCurrent().getLocale(); // Uso vaadin case
            }
            if (defaultValue != null && !defaultValue.isEmpty()) {
                return getMessageSource().getMessage(key, args, defaultValue, locale);
            }
            return getMessageSource().getMessage(key, args, locale);
        } catch (final Throwable t) {
            return "[" + key + "]";
        }
    }

    /**
     * Returns an unique bean name for the specified type.
     *
     * @throws NoUniqueBeanDefinitionException
     */
    public static String getUniqueBeanName(Class type) throws NoUniqueBeanDefinitionException {
        String[] beanNames = getApplicationContext().getBeanNamesForType(type);
        if (beanNames.length > 1) {
            throw new NoUniqueBeanDefinitionException(type, beanNames);
        }
        return beanNames.length > 0 ? beanNames[0] : null;
    }

    /**
     * Returns an unique bean for the specified type.
     *
     * @throws NoUniqueBeanDefinitionException
     */
    @SuppressWarnings("unused")
    public static <T> T getUniqueBean(Class<? super T> type, Object... args) throws NoUniqueBeanDefinitionException {
        return (T) getApplicationContext().getBean(getUniqueBeanName(type), args);
    }
}
