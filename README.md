RRIA is a Grails-Vaadin base architecture on top of: Grails 3 (3.2.9), Vaadin 8.1, and Spring Boot.
The name comes from a personal concept called RAD+RIA=RRIA.

The idea of RRIA is to be the base for any project/application on top of Vaadin and Grails 
that requires dinamyc role security control, this means within the application you'll be able to
create user roles and grant view permissions on each one of them, with no boilerplate.

Unlike previous versions of RRIA (not all of them were disclosed), this version no longer 
relies on old plugin Vaadin for grails2 (already migrated to grails 3), instead this uses
a profile created by "me.przepiora", it takes advantage of the vaadin plugin
for gradle (underneath uses "com.devsoap.plugin.vaadin" plugin)

[![Users view](https://github.com/grails-coder/rria/images/users.png)]

[![New User](https://github.com/grails-coder/rria/images/new-user.png)]

[![Login Details](https://github.com/grails-coder/rria/images/login-details.png)]


# What's inside
This project delivers an architecture that relies heavily on Spring Security to access each one 
of the Vaadin views. The security mechanism does not rely on Spring Security annotations for 
each view/controller/presenter, instead it lets the application to create as many roles as needed, 
then each role gets granted with permissions over Vaadin views, no boilerplate configuration is 
required.

In order to communicate view components (e.g. save button actions, etc) with other view components
it relies on Google Guava to achieve it.

Regarding the testing phase, only few examples are included, those tests cases were made
in order to test the back-end, still some effort is required to write front-end test cases.

All of the project is writen entirely in Groovy.

# Internationalization (i18n)
The application is set by default to English language, however it will detect secondly if the browser is
in a different language and will attempt to show messages in that language (Spanish and English only 
included for now), finally if the user account logged in has a different locale set then it will use it
to show the application in that language. 
In other words: English (default) is overriden by Browser, which is overriden by user preferences 
in application.

# Considerations
According with Burt Beckwith talk on Greach 2017 (https://www.youtube.com/watch?v=-nofscHeEuU)
it is not a good idea to use _hasMany_ relationship between in the Domain Model. Actually
the Grails Spring Security maintained by himself is no longer using it, hence this project 
sticks to that concept, so please don't ask for add some _hasMany_ relationships because I won't.

# How to (Configure project)
Configure like a traditional Gradle project and later set Grails SDK 3.2.9

# Running
- Run with gradle task: bootRun

# Disclaimer
I created this project for the sake of knowledge, many reads and trial-error attempts occurred
in the path, of course I based this design in many readings.

# How to Create Vaadin 8 projects on Grails 3.x (offtopic)
- Use following command line to create the app: 
grails create-app --profile=me.przepiora.vaadin-grails:web-vaadin8:0.2-SNAPSHOT <app-name>
- Add all of the required logic for your development.
