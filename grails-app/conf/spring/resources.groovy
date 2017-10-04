// Place your Spring DSL code here
beans = {
    // In case needed, set the default language for the application
    // This will be overriden by user locate preferences
    localeResolver(org.springframework.web.servlet.i18n.SessionLocaleResolver) {
        def defaultLocale = new Locale("en", "EN")
        java.util.Locale.setDefault(defaultLocale)
    }

}
