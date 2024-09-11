/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/displaysendmethod/AddShippingChargeLineItemSite.java /main/7 2013/05/02 10:47:36 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     05/01/13 - Save the reason code(id_lu_cd.LU_CD_ENT) rather than
*                        the description to retail price
*                        modifier(CO_MDFR_RTL_PRC.RC_MDFR_RT_PRC).
* yiqzhao     03/13/13 - Add reason code for shipping charge override for cross
*                        channel and store send.
* yiqzhao     01/04/13 - Refactoring ItemManager
* yiqzhao     04/30/12 - remove set item discounted price
* yiqzhao     04/26/12 - handle shipping charge as sale return line item
* yiqzhao     04/16/12 - Refactor store send
* yiqzhao     04/16/12 - refactor store send in transaction totals
* yiqzhao     04/13/12 - delete shipping charge line item and update send item
* yiqzhao     04/05/12 - add shipping charge line and send package line item.
* yiqzhao     04/05/12 - Creation
* ===========================================================================
*/


package oracle.retail.stores.pos.services.send.displaysendmethod;


import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
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
import oracle.retail.stores.pos.services.send.address.SendCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Site that displays the methods in which a {@link SendPackageLineItemIfc} can
 * be sent.
 */
public class AddShippingChargeLineItemSite extends PosSiteActionAdapter
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 245309476520773631L;

	/**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/7 $";

    /**
     * Default Shipping Charge Service Item ID
     */
    public static final String DEFAULT_SHIPPING_CHARGE_ITEM_ID = "ShippingChargeItemID";

    /**
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
  	
        SendCargo cargo = (SendCargo) bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
    	// Initialize bean model values
    	ShippingMethodBeanModel model = (ShippingMethodBeanModel) ui.getModel();
    	
    	// if this is null, nothing should happen
    	ShippingMethodIfc selectedMethodOfShipping = model.getSelectedShipMethod();
    	
        //Update shipping charge line item when changing item quantity, remove send item, etc
        try
        {
	        if ( cargo.isItemUpdate() && !transaction.isTransactionLevelSendAssigned() )
	        {
	        	//item level store send update
	        	AbstractTransactionLineItemIfc lineItems[] = transaction.getLineItems();
		        for (int i=0; i<lineItems.length; i++)
		        {
		        	if ( lineItems[i] instanceof SaleReturnLineItemIfc )
		        	{
		        		SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems[i];
		        		if (lineItem.isShippingCharge() && lineItem.getSendLabelCount() == cargo.getSendIndex() )
		        		{
		            		if (selectedMethodOfShipping != null)
		            		{	     
		            			//update shipping charge line item and shipping method
		            			applyShippingMethod(cargo, selectedMethodOfShipping);
		            			lineItem.getItemPrice().setSellingPrice(model.getSelectedShipMethod().getCalculatedShippingCharge());
		            		}
		        			break;
		        		}        		
		        	}
		        }
	        }
	        else
	        {   
	        	if (selectedMethodOfShipping != null)
	        	{
	        		applyShippingMethod(cargo, selectedMethodOfShipping);
	        		addShippingChargeLineItem(cargo, bus, selectedMethodOfShipping); 
	        	}
	        }
	        //recalculate item prices, discounts, etc for all the items in the transaction
	        transaction.updateTransactionTotals();	
	
	        bus.mail(new Letter(CommonLetterIfc.DONE), BusIfc.CURRENT);
	
	    } catch (ParameterException pe)
	    {
	    	logger.warn( "Parameter ShippingChargeItemID not found: " + pe.getMessage() + "");
	    } catch (DataException de)
	    {
	    	logger.warn( "No service items found: " + de.getMessage() + "");
	    }
    }
    
    /**
     * Take the specified shipping method and apply it to the transaction in the
     * cargo.
     * 
     * @param bus
     * @param cargo
     * @param selectedMethodOfShipping
     */
    protected void applyShippingMethod(SendCargo cargo, ShippingMethodIfc selectedMethodOfShipping )
    {
       SaleReturnTransactionIfc transaction = cargo.getTransaction();
       
       if ( transaction.isTransactionLevelSendAssigned() )
       {
    	   //SendPackageInfo the sendCount in send items have been added in AssignTransactionLevelInfoSite for transaction level send
    	   transaction.updateSendPackageInfo(0, selectedMethodOfShipping, cargo.getShipToInfo());
       }
       else if ( cargo.isItemUpdate() )
       {
    	   transaction.updateSendPackageInfo(cargo.getSendIndex()-1, selectedMethodOfShipping, cargo.getShipToInfo());
       }
       else
       {
	       //Add send packages info
	       transaction.addSendPackageInfo(selectedMethodOfShipping, cargo.getShipToInfo());
       }
	   //Assign Send label count on Sale Return Line Items
	   SaleReturnLineItemIfc[] items = cargo.getLineItems();
	   for (int i = 0; i < items.length; i++)
	   {
	       items[i].setItemSendFlag(true);
	       if ( !cargo.isItemUpdate() )
	    	   items[i].setSendLabelCount(transaction.getItemSendPackagesCount());
	       else
	    	   items[i].setSendLabelCount(cargo.getSendIndex());
	
	       // set send flag for all kit components as well
	       if (items[i] instanceof KitHeaderLineItemIfc)
	       {
	      	   KitHeaderLineItemIfc kitHeader = (KitHeaderLineItemIfc)items[i];
	       	   KitComponentLineItemIfc[] kitComponents = kitHeader.getKitComponentLineItemArray();
	       	   for (int j = 0; j < kitComponents.length; j++)
	           {
	       		   kitComponents[j].setItemSendFlag(true);
	       		   if ( !cargo.isItemUpdate() )
	       		   {
	       			   kitComponents[j].setSendLabelCount(transaction.getItemSendPackagesCount());
	       		   }
	       		   else
	       		   {
	       			   kitComponents[j].setSendLabelCount(cargo.getSendIndex());
	       		   }
	           }
	       }
	   }
    }
    
    /**
     * Add a shipping charge line item into the transaction
     * @param cargo
     * @param bus
     * @return
     */
    protected SaleReturnLineItemIfc addShippingChargeLineItem(SendCargo cargo, BusIfc bus, ShippingMethodIfc selectedMethodOfShipping) throws ParameterException, DataException
    {       
    	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // Initialize bean model values
        ShippingMethodBeanModel model = (ShippingMethodBeanModel) ui.getModel();
        
    	SaleReturnLineItemIfc lineItem = null;
        PLUItemIfc shippingChargePLUItem = createShippingChargePLUItem(cargo, bus);
        if (shippingChargePLUItem != null)
        {

            shippingChargePLUItem.setPrice(model.getSelectedShipMethod().getCalculatedShippingCharge());

            SaleReturnTransactionIfc transaction = cargo.getTransaction();
        	
     	    lineItem = transaction.addPLUItem(shippingChargePLUItem);
     	    lineItem.setShippingCharge(true);
     	    lineItem.setSendLabelCount(transaction.getItemSendPackagesCount());
     	    
            if ( !model.getShippingCharge().equals(model.getSelectedShipMethod().getCalculatedShippingCharge()) ) 
            {
                //price override
                String reasonKey = model.getSelectedReasonKey();
                CodeEntryIfc codeEntry = cargo.getShippingChargeReasonCodes().findListEntryByCode(reasonKey);
                
                LocalizedCodeIfc localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
                localizedReasonCode.setCode(reasonKey);
                localizedReasonCode.setText(codeEntry.getLocalizedText());
                
                lineItem.getItemPrice().overridePrice(model.getShippingCharge(), localizedReasonCode);
                lineItem.getItemPrice().calculateItemTotal();

                StringBuilder sb = new StringBuilder();
                String reasonCode = getReasonCodeJournalString(bus, cargo.getStoreStatus().getStore().getStoreID(), lineItem.getItemPrice());
                sb.append(reasonCode);

                //actually write the journal
                JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                journal.journal(cargo.getOperator().getEmployeeID(),
                                cargo.getTransaction().getTransactionID(),
                                sb.toString());     
                
                //this value is used at printing a shipping slip 
                selectedMethodOfShipping.setCalculatedShippingCharge(model.getShippingCharge());
            }
        }
        
        return lineItem;
    }
    
    /**
     * Create a shipping charge PLUItem 
     * @param cargo
     * @param bus
     * @return
     */
    protected PLUItemIfc createShippingChargePLUItem(SendCargo cargo, BusIfc bus) throws ParameterException, DataException
    {       
    	UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

    	ParameterManagerIfc pm =
                (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
       
        PLUItemIfc shippingChargeItem = null;
        try
        {
            ItemSearchCriteriaIfc inquiry = DomainGateway.getFactory().getItemSearchCriteriaInstance();
            inquiry.setLocaleRequestor(utility.getRequestLocales());
            inquiry.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
            inquiry.setStoreNumber(cargo.getStoreStatus().getStore().getStoreID());
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
