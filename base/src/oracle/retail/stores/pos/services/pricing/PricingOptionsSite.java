/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/PricingOptionsSite.java /main/21 2014/07/07 16:57:30 vineesin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  09/02/14 - disabled damage discount button in case of multi 
 *                         lineitem select
 *    vineesin  07/03/14 - Disabling Price Adjustment button for Order pickup
 *    jswan     06/27/13 - Modified to enable the Price Adjustment button when
 *                         there are no items in the sale screen.
 *    arabalas  05/28/13 - check if the item is a stock item and if not it is
 *                         not eligible for price adjustment
 *    arabalas  05/28/13 - Check if the item is price required and if yes
 *                         disable price adjustment option
 *    abhinavs  05/15/13 - Fix to enable or disable pricing sub menus based on
 *                         the line items
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    acadar    08/02/10 - disable price adjustment for external order items;
 *                         add thread id to the performance log and disable
 *                         perf loggin by default
 *    abondala  07/15/10 - Disable item level and pricing options for external
 *                         order items
 *    abondala  06/21/10 - updated comments
 *    abondala  06/21/10 - Disable item level editing for an external order
 *                         line item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    stallama  04/27/09 - If the selected item is a Gift Certificate item,
 *                         Price Override is disabled
 *    mahising  02/20/09 - Fixed issue for discount if no item added
 *    miparek   01/06/09 - Forward port 7314478, TIMEOUT INACTIVE WITH TRANS
 *                         CONFIG PARAMETER IS NOT WORKING CORRECTLY
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:29 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:22 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:25 PM  Robert Pearse
 *
 *   Revision 1.12.2.1  2004/11/05 00:38:39  lzhao
 *   @scr 7578: disable price override button when the item price override is set to false in database.
 *
 *   Revision 1.12  2004/06/02 12:34:16  tmorris
 *   @scr 5298 -Price Override button should be disabled if an added item is a kit header.
 *
 *   Revision 1.11  2004/05/05 18:44:53  jriggins
 *   @scr 4680 Moved Price Adjustment button from Sale to Pricing
 *
 *   Revision 1.10  2004/03/26 21:18:19  cdb
 *   @scr 4204 Removing Tabs.
 *
 *   Revision 1.9  2004/03/24 16:45:31  aschenk
 *   @scr 4140 - Price override disabled for a gift certificate item.
 *
 *   Revision 1.8  2004/03/22 18:35:05  cdb
 *   @scr 3588 Corrected some javadoc
 *
 *   Revision 1.7  2004/03/22 03:49:27  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.6  2004/03/17 15:41:58  aschenk
 *   @scr 3983 - had to add check for null item list.
 *
 *   Revision 1.5  2004/03/17 02:50:23  aschenk
 *   @scr 3983 - disabled price override button when item is a gift card
 *
 *   Revision 1.4  2004/02/25 22:51:41  dcobb
 *   @scr 3870 Feature Enhancement: Damage Discounts
 *
 *   Revision 1.3  2004/02/12 16:51:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:05  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Feb 04 2004 16:40:56   cdb
 * Adding Employee Discount service and associated screens.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Feb 03 2004 15:43:16   cdb
 * Updated to disable inaccessible buttons.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Aug 29 2003 16:05:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   05 Jun 2002 17:13:18   jbp
 * changes for pricing feature
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   02 May 2002 17:39:18   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

/**
 * This site displays pricing options.
 * 
 * @version $Revision: /main/21 $
 **/
public class PricingOptionsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 6209062824180855639L;
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/21 $";

    /**
     * Displays the pricing options
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        PricingCargo pricingCargo = (PricingCargo)bus.getCargo();
        RetailTransactionIfc currentTransaction = pricingCargo.getTransaction();
        ListBeanModel model = getModifyItemBeanModel(pricingCargo.getItems());
        boolean hasTransaction = pricingCargo.getTransaction() != null;
        if(hasTransaction)
        {
            model.setTimerModel(new DefaultTimerModel(bus, hasTransaction, null));
        }

        try
        {
            ParameterManagerIfc parameterManager =
                (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

            Boolean displayPriceAdjButtonObj
                = parameterManager.getBooleanValue(ParameterConstantsIfc.PRICEADJUSTMENT_PriceAdjustmentEnable);
            if(hasTransaction && currentTransaction instanceof OrderTransaction && currentTransaction.isOrderPickupOrCancel())
            {
                pricingCargo.setPriceAdjustmentButtonEnabled(false);
            }
            else
            {
                pricingCargo.setPriceAdjustmentButtonEnabled(displayPriceAdjButtonObj.booleanValue());
            }
        }
        catch(ParameterException pe)
        {
            logger.error("Parameter error: ", pe);
        }
        
        //check if the item is eligible for Price Adjustment
        if(pricingCargo.getItems() != null)
        {
            int totalItemsSelected = pricingCargo.getItems().length;
            int currentItem = 0;
            boolean priceRequired = false, invalidItemType = false; 
            //if anyone of the item selected is not eligible then the button is disabled
            while(currentItem < totalItemsSelected) 
            {
                //is the item price required
                priceRequired = (pricingCargo.getItems())[currentItem].getPLUItem().getItemClassification().isPriceEntryRequired();
                //is the item a STOCK ITEM
                invalidItemType = (pricingCargo.getItems())[currentItem].getPLUItem().getItemClassification().getItemType()!= ItemClassificationConstantsIfc.TYPE_STOCK;
                if(priceRequired || invalidItemType)
                    break;
                currentItem++;
            }
            if(priceRequired || invalidItemType)
            {
                pricingCargo.setPriceAdjustmentButtonEnabled(false);
            }
        }
        
        configureLocalButtons(model, pricingCargo);

        ui.showScreen(POSUIManagerIfc.PRICING_OPTIONS, model);
    }

    /**
     * Builds the ModifyItemBeanModel; this bean contains the the line item and
     * the model that sets the local navigation buttons to their correct enabled
     * states.
     * 
     * @param lineItemList The itemlist to modify.
     * @return ModifyItemBeanModel
     */
    protected ListBeanModel getModifyItemBeanModel(SaleReturnLineItemIfc[] lineItemList)
    {
        ListBeanModel model = new ListBeanModel();

        if (lineItemList != null)
        {
            model.setListModel(lineItemList);
            model.setUpdateStatusBean(false);
        }
        return model;
    }

    /**
     * Configures the local buttons
     * 
     * @param model ListBeanModel
     * @param pricingCargo PricingCargo
     */
    public void configureLocalButtons(ListBeanModel model, PricingCargo pricingCargo)
    {
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        SaleReturnLineItemIfc[] lineItemList = pricingCargo.getItems();

        // Enable Discount button only if items are added in the sale screen
        if (lineItemList != null)
        {
        navModel.setButtonEnabled(CommonActionsIfc.DISCOUNT,true);
        }
        else
        {
            navModel.setButtonEnabled(CommonActionsIfc.DISCOUNT, false);
        }
        // Enable the price adjustment button only if the button has been
        // manually configured
        // and transaction re-entry is off
        boolean transReentryModeOn =
            pricingCargo.getRegister().getWorkstation().isTransReentryMode();
        navModel.setButtonEnabled(CommonActionsIfc.PRICE_ADJUSTMENT, pricingCargo.isPriceAdjustmentButtonEnabled() && !transReentryModeOn);

        if ( lineItemList == null || lineItemList.length == 0 )
        // nothing selected
        {
            navModel.setButtonEnabled(CommonActionsIfc.PRICE_OVERRIDE, false);
            navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN, false);
            navModel.setButtonEnabled(CommonActionsIfc.EMPLOYEE, false);
            navModel.setButtonEnabled(CommonActionsIfc.DAMAGE, false);
        }
        else
        // single select
        if (lineItemList.length == 1)
        {

            if(lineItemList[0].isFromExternalOrder() && lineItemList[0].hasExternalPricing())
            {
                navModel.setButtonEnabled(CommonActionsIfc.PRICE_OVERRIDE, false);
                navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN,false);
                navModel.setButtonEnabled(CommonActionsIfc.DISCOUNT,false);
                navModel.setButtonEnabled(CommonActionsIfc.EMPLOYEE,false);
                navModel.setButtonEnabled(CommonActionsIfc.DAMAGE, false);
                navModel.setButtonEnabled(CommonActionsIfc.PRICE_ADJUSTMENT, false);
            }

            else if(lineItemList[0].isShippingCharge())
            {

            	navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN,false);
             	navModel.setButtonEnabled(CommonActionsIfc.DAMAGE, false);

            }
            else
            {
                if ( priceOverrideable(lineItemList[0]) )
                {
                    navModel.setButtonEnabled(CommonActionsIfc.PRICE_OVERRIDE, true);
                }
                else
                {
                    navModel.setButtonEnabled(CommonActionsIfc.PRICE_OVERRIDE, false);
                }
                navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN,true);
                navModel.setButtonEnabled(CommonActionsIfc.EMPLOYEE,true);
                navModel.setButtonEnabled(CommonActionsIfc.DAMAGE, true);
            }
        }
        else
        // multi select
        if(lineItemList.length > 1)
        {
        	if(checkExternalOrderItemExists(lineItemList))
        	{
        		navModel.setButtonEnabled(CommonActionsIfc.PRICE_OVERRIDE, false);
        		navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN,false);
        		navModel.setButtonEnabled(CommonActionsIfc.DISCOUNT,false);
        		navModel.setButtonEnabled(CommonActionsIfc.EMPLOYEE,false);
        		navModel.setButtonEnabled(CommonActionsIfc.DAMAGE, false);
        		navModel.setButtonEnabled(CommonActionsIfc.PRICE_ADJUSTMENT, false);

        	}
            else if(checkShippingChargeLineExists(lineItemList))
            {
            	navModel.setButtonEnabled(CommonActionsIfc.DAMAGE, false);
            	navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN,false);
            }
            else
            {
                navModel.setButtonEnabled(CommonActionsIfc.PRICE_OVERRIDE, false);
                navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN,true);
                navModel.setButtonEnabled(CommonActionsIfc.EMPLOYEE, true);
                navModel.setButtonEnabled(CommonActionsIfc.DAMAGE, false);
            }
        }
        //Tax cannot be modified for GiftCard.
        if( (lineItemList != null) && (lineItemList.length > 0) &&
            (lineItemList[0].getPLUItem() instanceof GiftCardPLUItemIfc || lineItemList[0].getPLUItem() instanceof GiftCertificateItemIfc || lineItemList[0].getPLUItem().isKitHeader()))
        {
            navModel.setButtonEnabled(CommonActionsIfc.PRICE_OVERRIDE, false);
        }

        model.setLocalButtonBeanModel(navModel);
    }

    /**
     * Check the price is overrideable for giving lineItem
     * 
     * @param lineItem SaleReturnLineItemIfc
     * @return overrideable boolean
     */
    protected boolean priceOverrideable(SaleReturnLineItemIfc lineItem)
    {
        boolean overrideable = false;
        ItemIfc item = lineItem.getPLUItem().getItem();
        if ( item != null &&
             item.getItemClassification() != null &&
             item.getItemClassification().getPriceOverridable()== true )
        {
            overrideable = true;
        }
        if (lineItem.getPLUItem() instanceof GiftCardPLUItemIfc || lineItem.getPLUItem() instanceof GiftCertificateItemIfc || lineItem.getPLUItem().isKitHeader())
        {
            overrideable = false;
        }

        return overrideable;
    }

    /**
     * Check if there are any items from ExternalOrder and
     * 
     * @param lineItemList SaleReturnLineItemIfc[]
     * @return boolean true if at least one item is from ExternalOrder
     */
    protected boolean checkExternalOrderItemExists(SaleReturnLineItemIfc[] lineItemList)
    {
        for (int i = 0; i < lineItemList.length; i++)
        {
            if(lineItemList[i].isFromExternalOrder() && lineItemList[i].hasExternalPricing())
            {
                return true;
            }
        }

        return false;
    }
    

    // Can't change the damage price and markdown of a shipping charge line.
    /**
     * Check if there are any shipping charge line
     * @param lineItemList SaleReturnLineItemIfc[]
     * @return boolean true if there is shipping charge line
     */
    protected boolean checkShippingChargeLineExists(SaleReturnLineItemIfc[] lineItemList)
    {
    	for (int i = 0; i < lineItemList.length; i++)
    	{
    		if(lineItemList[0].isShippingCharge())
    		{
    			return true;
    		}
    	}

    	return false;
    }
}
