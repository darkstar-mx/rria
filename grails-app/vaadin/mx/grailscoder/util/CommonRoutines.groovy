package mx.grailscoder.util

import groovy.util.logging.Log4j
import mx.grailscoder.person.Person
import mx.grailscoder.pogo.security.UserPogo
import mx.grailscoder.security.RoleApp
import mx.grailscoder.security.UserApp
import mx.grailscoder.pogo.person.PersonPogo
import mx.grailscoder.pogo.security.RolePogo

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Jan 14, 2016

 *
 */
@Log4j
class CommonRoutines {

    static def copyProperties(Object source, Class clazz) throws InstantiationException, IllegalAccessException {
        //log.info "copyProperties:: Start"
        def loadedClass = loadClass(clazz.name)

        /*if (!loadedClass?.id){
            throw new IllegalAccessException("Class ${loadedClass.class.toString()} doesn not contain an Id field")
        }*/

        // force the id copy otherwise it will not be copied by below routine
        //if(clazz.toString().endsWith("Pogo")){
            loadedClass.id = source?.id
        //}

        switch (clazz) {

            case UserPogo:
                source.properties.each { key, value ->
                    if (loadedClass.hasProperty(key) && !(key in ['class', 'metaClass', 'personId'])) {
                        switch (key) {
                            case 'person':
                                log.info "person property found on ${loadedClass.class.name}"
                                loadedClass[key] = value as PersonPogo
                                log.info "person property transformed"
                                break
                            case 'company':
                                //loadedClass[key] = value.asType(CompanyWrapper)
                                break
                            case 'password':
                                loadedClass[key] = "****"
                                break
                            default:
                                loadedClass[key] = value
                                break
                        }

                    }

                }
                break

            case RolePogo:
                source.properties.each { key, value ->
                    if (loadedClass.hasProperty(key) && !(key in ['class', 'metaClass'])) {
                        switch (key) {
                            /*
                        }
                            case 'id':
                                loadedClass[key] = value
                                break*/
                            default:
                                loadedClass[key] = value
                                break
                        }
                    }
                }
                break
            /*
            case CompanyWrapper:
                source.properties.each { key, value ->
                    if (loadedClass.hasProperty(key) && !(key in ['class', 'metaClass'])) {
                        switch (key) {
                            case 'address':
                                loadedClass[key] = value.asType(AddressWrapper)
                                break
                            case 'warehouses':
                                def changedWarehouses = []
                                for (changedItem in value) {
                                    changedWarehouses << changedItem.asType(WarehouseWrapper)
                                }
                                loadedClass[key] = changedWarehouses
                                break
                            case 'roles':
                                def changedItems = []
                                for (item in value) {
                                    changedItems << item.asType(RoleWrapper)
                                }
                                loadedClass[key] = changedItems
                                break
                            default:
                                //println "key found: " + key
                                loadedClass[key] = value
                        }

                         // FIXME: The GORM for MongoDB doesn't return the Id property, so a manual loading is required

                        loadedClass.id = source.id
                    }
                }
                break
            case WarehouseWrapper:
                source.properties.each { key, value ->
                    switch (key) {
                        case 'stock':
                            loadedClass[key] = value.asType(StockWrapper)
                            break
                        default:
                            loadedClass[key] = value
                    }
                }
                break
            case StockWrapper:
                source.properties.each { key, value ->
                    switch (key) {
                        case 'product':
                            loadedClass[key] = value.asType(ProductWrapper)
                            break
                        default:
                            loadedClass[key] = value
                    }
                }
                break
            */
            ///////////////////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////// Domain classes wrapping begins //////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////////////////////////
            case UserApp:
                source.properties.each { key, value ->
                    if (loadedClass.hasProperty(key) && !(key in ['class', 'metaClass'])) {
                        switch (key) {
                            case 'person':
                                loadedClass[key] = value as Person
                                break

                            case 'company':
                                //loadedClass[key] = value.asType(Company)
                                break
                            default:
                                loadedClass[key] = value
                                break
                        }

                    } else {
                        //log.error "Discarded property -> ${key}, because ${loadedClass.class.name} does not contain field: ${key.dump()} "
                    }

                }
                break

            case RoleApp:
                source.properties.each { key, value ->
                    if (loadedClass.hasProperty(key) && !(key in ['class', 'metaClass'])) {
                        switch (key) {
                            default:
                                loadedClass[key] = value
                                break
                        }
                    }
                }
                break
/*
            case Company:
                source.properties.each { key, value ->
                    if (loadedClass.hasProperty(key) && !(key in ['class', 'metaClass'])) {
                        switch (key) {
                            case 'address':
                                loadedClass[key] = value.asType(Address)
                                break
                            case 'warehouses':
                                def changedWarehouses = []
                                for (changedItem in value) {
                                    changedWarehouses << changedItem.asType(Warehouse)
                                }
                                loadedClass[key] = changedWarehouses
                                break
                            case 'roles':
                                def changedItems = []
                                for (item in value) {
                                    changedItems << item.asType(RoleApp)
                                }
                                loadedClass[key] = changedItems
                                break
                            default:
                                //println "key found: " + key
                                loadedClass[key] = value
                        }

                         // FIXME: The GORM for MongoDB doesn't return the Id property, so a manual loading is required

                        loadedClass.id = source.id
                    }
                }
                break

            case Warehouse:
                source.properties.each { key, value ->
                    switch (key) {
                        case 'stock':
                            loadedClass[key] = value.asType(Stock)
                            break
                        default:
                            loadedClass[key] = value
                    }
                }
                break

            case Stock:
                source.properties.each { key, value ->
                    switch (key) {
                        case 'product':
                            loadedClass[key] = value.asType(Product)
                            break
                        default:
                            loadedClass[key] = value
                    }
                }
                break
*/
            default:
                log.info "Default converting ${source.class.toString()} into ${clazz.toString()}"
                source.properties.each { key, value ->
                    if (loadedClass.hasProperty(key) && !(key in ['class', 'metaClass'])) {
                        loadedClass[key] = value
                    }
                }
                log.info "Success, converted ${source.class.toString()} into ${clazz.toString()}"
                log.info "Default conversion result is: ${loadedClass.dump()}"
                break
        }

        return loadedClass

    }

    static def loadClass(String className) throws InstantiationException, IllegalAccessException {
        Class.forName(className).newInstance()
    }

}
