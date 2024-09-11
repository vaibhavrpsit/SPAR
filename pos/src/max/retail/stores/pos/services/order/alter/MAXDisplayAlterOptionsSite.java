/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

// java imports
import java.util.Locale;

import max.retail.stores.pos.services.order.common.MAXOrderCargoIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.pos.ui.beans.TotalsBeanModel;

//------------------------------------------------------------------------------
/**

    Displays the edit item status screen for changing the order item
    status. Displays the error dialog screen is order status is Canceled,
    Completed, or Voided.

    @version $Revision: 5$
**/
//------------------------------------------------------------------------------

public class MAXDisplayAlterOptionsSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5167816263269370719L;

	/**
       class name constant
    **/
    public static final String SITENAME = "EditItemStatusSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: 5$";

    /**
       Constant for error message argument text.
    **/
    public static final String ALTER = "Alter";

    /**
       Constant for error message screen id.
    **/
    public static final String CANNOT_MODIFY_ORDER = "CannotModifyOrder";

    /**
       Constant for error message argument text.
    **/
    public static final String COMPLETED = "Completed";

    /**
       Constant for error message argument text.
    **/
    public static final String CANCELED = "Canceled";

    /**
       Constant for error message argument text.
    **/
    public static final String VOIDED = "Voided";

    /** Constant for Filled button action name **/
    public static final String FILLED_ACTION = "Filled";
    
    /** Constant for Pick up button action name **/
    public static final String PICKUP_ACTION = "Pick Up";
    
    /** Constant for Canceled button action name **/
    public static final String CANCELED_ACTION = "Canceled";

     /**
       Customer name bundle tag
     **/
     protected static final String CUSTOMER_NAME_TAG = "CustomerName";
     /**
       Customer name default text
     **/
     protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    //--------------------------------------------------------------------------
    /**
       Displays the edit item status screen to allow user to select items(s)
       from the order to change their status. Displays the error dialog screen
       is order status is Canceled, Completed, or Voided.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        //Initialize Variables
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        MAXOrderCargoIfc       cargo       = (MAXOrderCargoIfc)bus.getCargo();
        POSUIManagerIfc     ui          = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        LineItemsModel      lineModel   = new LineItemsModel();
        StatusBeanModel     sbModel     = new StatusBeanModel();
        OrderIfc            order       = (OrderIfc)cargo.getOrder();
        int                 status      =  order.getStatus().getStatus().getStatus();
        NavigationButtonBeanModel  localModel = new NavigationButtonBeanModel();

        // if not modifiable display error screen
        if (status == OrderConstantsIfc.ORDER_STATUS_CANCELED ||
            status == OrderConstantsIfc.ORDER_STATUS_COMPLETED ||
            status == OrderConstantsIfc.ORDER_STATUS_VOIDED)
        {
            // setup arg text strings for error screen
            String args[] = new String[4];
            args[0] = utility.retrieveDialogText("CannotModifyOrder.Alter",
                                                 ALTER);
            args[1] = utility.retrieveDialogText("CannotModifyOrder.Completed",
                                                 COMPLETED);
            args[2] = args[1];

            // test if order canceled to re-use screen, change ui argument
            if (status == OrderConstantsIfc.ORDER_STATUS_CANCELED)
            {
                args[1] = utility.retrieveDialogText("CannotModifyOrder.Canceled",
                                                     CANCELED);
                args[2] = args[1];
            }
            else if (status == OrderConstantsIfc.ORDER_STATUS_VOIDED)
            {
                args[1] = utility.retrieveDialogText("CannotModifyOrder.Voided",
                                                     VOIDED);
                args[2] = args[1];
            }
            // setup and display the Cannot Modify Dialog screen
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(CANNOT_MODIFY_ORDER);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);
            model.setType(DialogScreensIfc.ERROR);
            model.setArgs(args);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            //StatusBeanModel Configure
            // Create the string from the bundle.
            CustomerIfc customer = cargo.getOrder().getCustomer();
            
            Object parms[] = { customer.getFirstName(), customer.getLastName() };
            String pattern = 
              utility.retrieveText("CustomerAddressSpec",
                                   BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                                   CUSTOMER_NAME_TAG,
                                   CUSTOMER_NAME_TEXT);
            String customerName = 
              LocaleUtilities.formatComplexMessage(pattern, parms);        
            
            sbModel.setCustomerName(customerName);

            //LineItemsModel Configure
            lineModel.setStatusBeanModel(sbModel);

            // in order for the original line items statuses not to be altered the array is cloned
            AbstractTransactionLineItemIfc[] lineItems = order.getLineItems();
            AbstractTransactionLineItemIfc[] clonedLineItems = new AbstractTransactionLineItemIfc[lineItems.length];
            for(int i=0; i<lineItems.length; i++)
            {
               clonedLineItems[i] = (SaleReturnLineItemIfc)lineItems[i].clone();
            }

            lineModel.setLineItems(clonedLineItems);
            lineModel.setLocalButtonBeanModel(localModel);
            TotalsBeanModel totalsModel = new TotalsBeanModel();
            CurrencyIfc discount = cargo.getOrder().getTotals().getDiscountTotal();
            CurrencyIfc tax = cargo.getOrder().getTotals().getTaxTotal();
            CurrencyIfc grandTotal =cargo.getOrder().getTotals().getGrandTotal();
            CurrencyIfc subTotal = grandTotal.subtract(tax).add(discount);
            totalsModel.setDiscountTotal(discount.toFormattedString(locale));
            totalsModel.setTaxTotal(tax.toFormattedString(locale));
            totalsModel.setSubtotal(subTotal.toFormattedString(locale));
            totalsModel.setGrandTotal(grandTotal.toFormattedString(locale));
            totalsModel.setQuantityTotal(cargo.getOrder().getTotals().getQuantityTotal());
            lineModel.setTotalsBeanModel(totalsModel);
            //Display Screen
            ui.showScreen(MAXPOSUIManagerIfc.ALTER_ORDER, lineModel);
        }
    }  // arrive
}
