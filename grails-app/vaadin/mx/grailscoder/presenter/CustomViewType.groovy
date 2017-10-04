package mx.grailscoder.presenter

import com.vaadin.icons.VaadinIcons
import com.vaadin.navigator.View
import com.vaadin.server.Resource
import mx.grailscoder.view.catalogs.LocationCatalogsView
import mx.grailscoder.view.company.CompanyView
import mx.grailscoder.view.error.ErrorView
import mx.grailscoder.view.landing.LandingView
import mx.grailscoder.view.payment.PaymentView
import mx.grailscoder.view.pricing.PricingView
import mx.grailscoder.view.product.CategoryView
import mx.grailscoder.view.product.ProductView
import mx.grailscoder.view.productinventory.StockView
import mx.grailscoder.view.provider.ProviderView
import mx.grailscoder.view.purchase.PurchaseView
import mx.grailscoder.view.security.UserView
import mx.grailscoder.view.customer.CustomerView
import mx.grailscoder.view.invoicing.InvoiceView
import mx.grailscoder.view.sell.SellView
import mx.grailscoder.view.warehouse.WarehouseView

/**
 *
 * @author Armando Montoya Hernandez
 * @email aj.montoya@outlook.com
 * @Date Aug 30, 2015

 *
 */
enum CustomViewType {
    LANDING("landing", "mx.grailscoder.view.menuselector.landing", LandingView.class, VaadinIcons.HOME, false)
    ,STOCK("inventory", "mx.grailscoder.view.menuselector.stock", StockView.class, VaadinIcons.HOME, true)
    , USER("user", "mx.grailscoder.view.menuselector.user", UserView.class, VaadinIcons.USERS, true)
    , INVOICE("invoice", "mx.grailscoder.view.menuselector.invoice", InvoiceView.class, VaadinIcons.INVOICE, false)
    , PAYMENT("payment", "mx.grailscoder.view.menuselector.payment", PaymentView.class, VaadinIcons.TABLE, false)
    , PRICING("pricing", "mx.grailscoder.view.menuselector.pricing", PricingView.class, VaadinIcons.DOLLAR, true)
    , SELL("sell", "mx.grailscoder.view.menuselector.sell", SellView.class, VaadinIcons.TICKET, false)
    , PRODUCT("product", "mx.grailscoder.view.menuselector.product", ProductView.class, VaadinIcons.LIST, false)
    , CATEGORY("category", "mx.grailscoder.view.menuselector.category", CategoryView.class, VaadinIcons.BARCODE, false)
    , PURCHASE("purchase", "mx.grailscoder.view.menuselector.purchase", PurchaseView.class, VaadinIcons.BARCODE, false)
    , PROVIDER("provider", "mx.grailscoder.view.menuselector.provider", ProviderView.class, VaadinIcons.USER_CARD, false)
    , COMPANY("company", "mx.grailscoder.view.menuselector.company", CompanyView.class, VaadinIcons.BUILDING, false)
    , CUSTOMER("customer", "mx.grailscoder.view.menuselector.customer", CustomerView.class, VaadinIcons.GROUP, false)
    , WAREHOUSE("warehouse", "mx.grailscoder.view.menuselector.warehouse", WarehouseView.class, VaadinIcons.WORKPLACE, false)
    , LOCATION_CATEGORY("locationcat", "mx.grailscoder.view.menuselector.locationcat", LocationCatalogsView.class, VaadinIcons.MAP_MARKER, false)
    , ERROR("error", "mx.grailscoder.view.menuselector.error", ErrorView.class, VaadinIcons.BAN, false)


    private final String viewName
    private final String localeEntry
    private final Class<? extends View> viewClass
    private final Resource icon
    private final boolean stateful
    private CustomViewType(final String viewName,
                           final String viewLocaleEntry,
                           final Class<? extends View> viewClass,
                           final Resource icon,
                           final boolean stateful) {
        this.viewName = viewName
        this.localeEntry = viewLocaleEntry
        this.viewClass = viewClass
        this.icon = icon
        this.stateful = stateful
    }

    public boolean isStateful() {
        return stateful
    }

    public String getViewName() {
        return viewName
    }

    public String getLocaleEntry() {
        return localeEntry
    }

    public Class<? extends View> getViewClass() {
        return viewClass
    }

    public Resource getIcon() {
        return icon
    }

    public static CustomViewType getByViewName(final String viewName) {
        CustomViewType result = null
        for (CustomViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType
                break
            }
        }
        return result
    }

}
