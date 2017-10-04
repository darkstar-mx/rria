package mx.grailscoder.presenter.datasource.implementation

import mx.grailscoder.presenter.datasource.interfaces.UtilitiesProvider

import java.text.DateFormat

/**
 *
 * @author ArmandodeJesus
 * @email aj.montoya [ at ] outlook.com
 * @Date 5/20/2017

 *
 */
class UtilitiesProviderImpl implements UtilitiesProvider{

    @Override
    List<Locale> getAvailableLocales() {

        return DateFormat.getAvailableLocales().sort(new Comparator<Locale>() {
            @Override
            int compare(Locale l1, Locale l2) {
                return l1.toString().compareTo(l2.toString())
            }
        });
    }
}
