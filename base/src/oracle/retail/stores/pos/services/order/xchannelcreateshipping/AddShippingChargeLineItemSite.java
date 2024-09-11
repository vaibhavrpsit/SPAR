/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/AddShippingChargeLineItemSite.java /main/12 2013/05/02 10:47:36 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     05/01/13 - Save the reason code(id_lu_cd.LU_CD_ENT) rather than
*                        the description to retail price
*                        modifier(CO_MDFR_RTL_PRC.RC_MDFR_RT_PRC).
* yiqzhao     04/22/13 - Fix the issue of shipping charge in lineitem is zero.
* vtemker     04/16/13 - Moved constants in OrderLineItemIfc to
*                        OrderConstantsIfc in common project
* yiqzhao     03/13/13 - Add reason code for shipping charge override for cross
*                        channel and store send.
* yiqzhao     03/05/13 - Add Shipping Charge to EJ.
* yiqzhao     01/04/13 - Refactoring ItemManager
* yiqzhao     10/22/12 - Ade merge
* yiqzhao     10/22/12 - Mark shipping charge line item isModified flag to
*                        true.
* sgu         10/15/12 - add ordered amount
* sgu         07/03/12 - replace item disposition code to use delivery instead
*                        of ship
* sgu         07/03/12 - added xc order ship delivery date, carrier code and
*                        type code
* yiqzhao     06/29/12 - Add dialog for deleting ship item, disable change
*                        price for ship item
* yiqzhao     06/28/12 - Update shipping flow
* yiqzhao     06/11/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import java.math.BigDecimal;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.stock.ItemSearchCriteriaIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This road journals the linked customer's information.
 *
 */
public class AddShippingChargeLineItemSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "AddShippingAddressSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

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
    	XChannelShippingCargo cargo = (XChannelShippingCargo) bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

    	// Initialize bean model values
    	ShippingMethodBeanModel model = (ShippingMethodBeanModel) ui.getModel();

    	// if this is null, nothing should happen
    	ShippingMethodIfc selectedMethodOfShipping = model.getSelectedShipMethod();

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

        String currentLetterName = bus.getCurrentLetter().getName();
        if (currentLetterName.equals(CommonLetterIfc.DONE) )
        	bus.mail(new Letter(CommonLetterIfc.DONE), BusIfc.CURRENT);
        else
        	bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
    }


    /**
     * Add a shipping charge line item into the transaction
     * @param cargo
     * @param bus
     * @return
     */
    protected SaleReturnLineItemIfc addShippingChargeLineItem(XChannelShippingCargo cargo, BusIfc bus) throws ParameterException, DataException
    {
    	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // Initialize bean model values
        ShippingMethodBeanModel model = (ShippingMethodBeanModel) ui.getModel();

    	SaleReturnLineItemIfc lineItem = null;
        PLUItemIfc shippingChargePLUItem = createShippingChargePLUItem(cargo, bus);
        if (shippingChargePLUItem != null)
        {
            SaleReturnTransactionIfc transaction = cargo.getTransaction();
            
     	    if ( model.getShippingCharge().equals(model.getSelectedShipMethod().getCalculatedShippingCharge()) ) 
     	    {
     	        shippingChargePLUItem.setPrice(model.getSelectedShipMethod().getCalculatedShippingCharge());
                lineItem = transaction.addPLUItem(shippingChargePLUItem);
            }
            else
            {
                //override shipping charge
                lineItem = transaction.addPLUItem(shippingChargePLUItem);

                String reasonKey = model.getSelectedReasonKey();
                CodeEntryIfc codeEntry = cargo.getShippingChargeReasonCodes().findListEntryByCode(reasonKey);
                
     	        LocalizedCodeIfc localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();  
     	        localizedReasonCode.setCode(reasonKey);
     	        localizedReasonCode.setText(codeEntry.getLocalizedText());
     	        
     	        lineItem.getItemPrice().overridePrice(model.getShippingCharge(), localizedReasonCode);
     	        lineItem.getItemPrice().calculateItemTotal();

     	        StringBuilder sb = new StringBuilder();
     	        String reasonCodeDesc = getReasonCodeJournalString(bus, cargo.getStore().getStoreID(), lineItem.getItemPrice());
     	        sb.append(reasonCodeDesc);

     	        //actually write the journal
     	        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
     	        journal.journal(cargo.getOperator().getEmployeeID(),
     	                        cargo.getTransaction().getTransactionID(),
     	                        sb.toString());
     	    }
            
     	    lineItem.setShippingCharge(true);
     	    lineItem.setSelectedForItemModification(true);
     	    
     	    cargo.setLineItem(lineItem);

            OrderItemStatusIfc orderItemStatus = lineItem.getOrderItemStatus();
            orderItemStatus.setCrossChannelItem(true);
            orderItemStatus.setOrderedAmount(lineItem.getItemPrice().getItemTotal());
            orderItemStatus.setQuantityOrdered(BigDecimal.ONE);
            orderItemStatus.setDeliveryDetails(cargo.getDeliveryDetail());
            orderItemStatus.setItemDispositionCode(OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY);
        }

        return lineItem;
    }

    /**
     * Create a shipping charge PLUItem
     * @param cargo
     * @param bus
     * @return
     */
    protected PLUItemIfc createShippingChargePLUItem(XChannelShippingCargo cargo, BusIfc bus) throws ParameterException, DataException
    {
    	UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

    	ParameterManagerIfc pm =
                (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        PLUItemIfc shippingChargeItem = null;
        try
        {
            ItemSearchCriteriaIfc inquiry = DomainGateway.getFactory().getItemSearchCriteriaInstance();
            inquiry.setLocaleRequestor(utility.getRequestLocales());
            inquiry.setGeoCode(cargo.getStore().getGeoCode());
            inquiry.setStoreNumber(cargo.getStore().getStoreID());
            inquiry.setRetrieveFromStore(true);
            String itemID = pm.getStringValue(DEFAULT_SHIPPING_CHARGE_ITEM_ID);
            inquiry.setItemID(itemID);

            ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);

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

    /**
     * Gets the reason code journal string for the price override.
     * @param bus
     * @param cargo
     * @param ip
     * @return the reason code journal string.
     */
    protected String getReasonCodeJournalString(BusIfc bus, String storeID, ItemPriceIfc ip)
    {
        String reasonCodeDescription = ip.getItemPriceOverrideReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
        if(Util.isEmpty(reasonCodeDescription))
        {
            UtilityManagerIfc utilityManager = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            CodeListIfc reasonCodes = utilityManager.getReasonCodes(storeID, CodeListIfc.CODE_LIST_PRICE_OVERRIDE_REASON_CODES);
            String code = ip.getItemPriceOverrideReason().getCode();
            try
            {
                int codeIntValue = Integer.parseInt(code);
                reasonCodeDescription = utilityManager.getReasonCodeText(reasonCodes, codeIntValue);
            }
            catch(NumberFormatException nfe)
            {
                logger.info("Could not determine reason code", nfe);
                reasonCodeDescription = code;
            }
        }
        Object dataObject[]={reasonCodeDescription};
        String reasonCode = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REASON_CODE_LABEL,dataObject);
        return reasonCode;
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
