/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/AddShippingChargeLineItemSite.java /main/2 2013/01/07 11:08:03 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     01/04/13 - Refactoring ItemManager
* yiqzhao     10/19/12 - Add shipping charge line item for external order.
* yiqzhao     10/18/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.externalorder.processorder;


import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Site that displays the methods in which a {@link SendPackageLineItemIfc} can
 * be sent.
 */
public class AddShippingChargeLineItemSite extends PosSiteActionAdapter
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision $";

    /**
     * Default Shipping Charge Service Item ID
     */
    public static final String DEFAULT_SHIPPING_CHARGE_ITEM_ID = "ShippingChargeItemID";

    //--------------------------------------------------------------------------
    /**
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	ProcessOrderCargo cargo = (ProcessOrderCargo) bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
    	ShippingMethodIfc selectedMethodOfShipping = cargo.getShippingMethod();

        //Update shipping charge line item when changing item quantity, remove send item, etc
        try
        {
	        if (selectedMethodOfShipping != null)
	        {
	        	addShippingChargeLineItem(cargo, bus);
	        }

	        //recalculate item prices, discounts, etc for all the items in the transaction
	        transaction.updateTransactionTotals();

	    } catch (ParameterException pe)
	    {
	    	logger.warn( "Parameter ShippingChargeItemID not found: " + pe.getMessage() + "");
	    } catch (DataException de)
	    {
	    	logger.warn( "No service items found: " + de.getMessage() + "");
	    }


        bus.mail(new Letter(CommonLetterIfc.DONE), BusIfc.CURRENT);
    }


    /**
     * Add a shipping charge line item into the transaction
     * @param cargo
     * @param bus
     * @return
     */
    protected SaleReturnLineItemIfc addShippingChargeLineItem(ProcessOrderCargo cargo, BusIfc bus) throws ParameterException, DataException
    {
    	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

    	SaleReturnLineItemIfc lineItem = null;
        PLUItemIfc shippingChargePLUItem = createShippingChargePLUItem(cargo, bus);
        if (shippingChargePLUItem != null)
        {
            shippingChargePLUItem.setPrice(cargo.getShippingMethod().getCalculatedShippingCharge());

            SaleReturnTransactionIfc transaction = cargo.getTransaction();

     	    lineItem = transaction.addPLUItem(shippingChargePLUItem);
     	    lineItem.setShippingCharge(true);
     	    lineItem.setSendLabelCount(transaction.getItemSendPackagesCount());

        }

        return lineItem;
    }

    /**
     * Create a shipping charge PLUItem
     * @param cargo
     * @param bus
     * @return
     */
    protected PLUItemIfc createShippingChargePLUItem(ProcessOrderCargo cargo, BusIfc bus) throws ParameterException, DataException
    {
    	UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

    	ParameterManagerIfc pm =
                (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        PLUItemIfc shippingChargeItem = null;
        try
        {
            SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
            inquiry.setLocaleRequestor(utility.getRequestLocales());
            
            inquiry.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
            inquiry.setStoreNumber(cargo.getStoreID());
            String itemID = pm.getStringValue(DEFAULT_SHIPPING_CHARGE_ITEM_ID);
            inquiry.setItemID(itemID);
            inquiry.setRetrieveFromStore(true);
            ItemManagerIfc mgr = (ItemManagerIfc)bus.getManager(ItemManagerIfc.TYPE);

            shippingChargeItem = mgr.getPluItem(inquiry);

        }catch (ParameterException pe)
        {
        	this.displayErrorDialog(bus, "SHIPPING_CHARGE_ITEM_ID_NOT_SPECIFIED");
        	throw pe;
        	//will add a dialog to indicate the issue. The system will back to Sale Item Screen without having send information in the transaction.
        }catch (DataException de)
        {
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            this.displayErrorDialog(bus, "SHIPPING_CHARGE_ITEM_ID_NOT_FOUND");
            throw de;
        }
        return shippingChargeItem;
    }


    protected void displayErrorDialog(BusIfc bus, String errorTitle)
    {
    	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("SHIPPING_CHARGE_ITEM_ID_NOT_FOUND");
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.UNDO);

        //display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
