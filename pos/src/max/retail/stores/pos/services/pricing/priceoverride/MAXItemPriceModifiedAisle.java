/* ===========================================================================
* Copyright (c) 2002, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/priceoverride/ItemPriceModifiedAisle.java /main/17 2013/07/26 14:56:02 arabalas Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    arabal 07/24/13 - Set the 'priceoverride' flag after item is price
 *                      overridden and these priceoverridden items are
 *                      restricted being added to discount rules
 *    mchell 03/26/13 - fwd port: update selling price for return items
 *    asinto 05/29/12 - added code to lookup reason code text in the case when
 *                      it is missing.
 *    cgreen 03/28/12 - initial mobilepos implementation of price override
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    icole  06/10/11 - Corrected merge problem
 *    cgreen 05/23/11 - change code to only clone transaction if vat
 *                      journalling is needed
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    nganes 01/28/10 - CaptureReasonCodeForPriceOverride parameter has been
 *                      removed as part of BugDB#9279097
 *    abonda 01/03/10 - update header date
 *    akandr 10/30/08 - EJ changes
 *    acadar 10/27/08 - fixes for localization of price override reason codes
 *    acadar 10/27/08 - use localized price override reason codes
 *
 * ===========================================================================
     $Log:
      9    360Commerce 1.8         11/15/2007 11:13:46 AM Christian Greene Some
            journaling that was added for PriceOverride was cloning the
           transaction, but not the lineitem. Afterwards, the item thought it
           was already participating the rule (since it was during the
           journaling). Clone the lineitem as well.
      8    360Commerce 1.7         7/11/2007 1:57:22 PM   Alan N. Sinton  CR
           27427 - Tax amount updated for price override items for EJournal.
      7    360Commerce 1.6         6/4/2007 6:01:32 PM    Alan N. Sinton  CR
           26486 - Changes per review comments.
      6    360Commerce 1.5         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
           26486 - EJournal enhancements for VAT.
      5    360Commerce 1.4         4/25/2007 8:52:16 AM   Anda D. Cadar   I18N
           merge

      4    360Commerce 1.3         1/22/2006 11:45:17 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:33 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:30 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:41 PM  Robert Pearse
     $
     Revision 1.11.2.1  2004/10/20 13:13:47  kll
     @scr 7405: journal reason code based on parameter

     Revision 1.11  2004/09/15 16:34:22  kmcbride
     @scr 5881: Deprecating parameter retrieval logic in cargo classes and logging parameter exceptions

     Revision 1.10  2004/07/19 20:00:01  bvanschyndel
     @scr 5419 Added reason code to journal entry

     Revision 1.9  2004/06/24 21:49:39  crain
     @scr 4678 Price Override: Should remove any previously applied item discounts or markdowns

     Revision 1.8  2004/05/20 22:54:58  cdb
     @scr 4204 Removed tabs from code base again.

     Revision 1.7  2004/05/07 13:23:56  tfritz
     @scr 4678 Price override now removes any previously applied item discounts or markdowns

     Revision 1.6  2004/05/06 05:05:54  tfritz
     @scr 4605 Added new CaptureReasonCodeForPriceOverride parameter

     Revision 1.5  2004/03/22 18:35:05  cdb
     @scr 3588 Corrected some javadoc

     Revision 1.4  2004/02/13 22:24:59  cdb
     @scr 3588 Added dialog to indicate when discount will reduce
     some prices below zero but not others.

     Revision 1.3  2004/02/12 16:51:38  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:06  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Jan 05 2004 18:55:24   cdb
 * Updated to use editable combo box for reason code selection.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Aug 29 2003 16:05:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 17 2003 07:02:08   jgs
 * Modifed journaling for item discounts.
 * Resolution for 3037: The ejournal for a transaction with multiple (3) % discounts applies and removes the first two discounts on the ejournal.
 *
 *    Rev 1.2   Feb 14 2003 13:59:08   HDyer
 * Modify item price using reason code index from bean model.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   05 Jun 2002 17:13:22   jbp
 * changes for pricing feature
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   02 May 2002 17:42:56   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing.priceoverride;

import java.math.BigDecimal;

import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.FinalLetter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Perform the price override and journal the item price modification. The
 * call to {@link SaleReturnLineItemIfc#modifyItemPrice(CurrencyIfc, LocalizedCodeIfc)}
 * actually occurs during the journaling.
 *
 * @version $Revision: /main/17 $
 */
public class MAXItemPriceModifiedAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 589941380746464805L;
    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/17 $";
    
   
   
    
   
    /**
     * This site journals an item price override and mails a FinalLetter to exit
     * the service.
     *
     * @param bus BusIfc
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get price from cargo
        PricingCargo cargo = (PricingCargo)bus.getCargo();
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        PricingCargo pc = (PricingCargo) bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        // Use screenID = POSUIManagerIfc.PRICE_OVERRIDE_NOREASON; if reason code needs to be removed
        String screenID = POSUIManagerIfc.PRICE_OVERRIDE;

        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel)ui.getModel(screenID);

        String selectedReasonKey = beanModel.getSelectedReasonKey();
        LocalizedCodeIfc localizedCode = cargo.getSelectedReasonCode();
        if (localizedCode == null)
        {
            localizedCode = DomainGateway.getFactory().getLocalizedCode();
            CodeListIfc rcl = cargo.getPriceOverrideCodeList();
            if (rcl != null)
            {
                CodeEntryIfc entry = rcl.findListEntryByCode (selectedReasonKey);
                localizedCode.setCode(selectedReasonKey);
                localizedCode.setText(entry.getLocalizedText());
               
                SaleReturnLineItemIfc[] items = cargo.getItems();

                for (int i = 0; i < items.length; i++)
                {
                    SaleReturnLineItemIfc srlinteItemIfc = (SaleReturnLineItemIfc)items[i];
                    srlinteItemIfc.setHasPriceModification(true);
                }
            }
            else
            {
                localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
            }
        }
        if(!isValidOverridePrice(pc,ui,screenID,bus)){
        	  DialogBeanModel dialogModel = new DialogBeanModel();
              dialogModel.setResourceID("InvalidPriceOverridePrice");
              dialogModel.setType(DialogScreensIfc.ERROR);
              ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
         }

        else if (!selectedReasonKey.equals(CodeConstantsIfc.CODE_UNDEFINED))
        {
            journalPriceModification(bus, localizedCode, screenID);
            bus.mail(new FinalLetter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
        }
        else
        {
            // display the invalid discount error screen
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InvalidReasonCode");
            dialogModel.setType(DialogScreensIfc.ERROR);
            //dialogModel.setArgs(msg);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        
    }

    /**
     * This method journals an item price override.
     *
     * @param bus BusIfc
     * @param selectedCode The selected reason code
     */
    protected void journalPriceModification(BusIfc bus, LocalizedCodeIfc selectedCode, String screenID)
    {
        JournalManagerIfc journal =
            (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        if (journal != null)
        {
            PricingCargo cargo = (PricingCargo)bus.getCargo();
            SaleReturnLineItemIfc item = cargo.getItems()[0];
            JournalFormatterManagerIfc formatter =
                (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

            // Clear item discounts
            item.getItemPrice().clearItemDiscounts();

            //Save old item info in StringBuilder for journal
            StringBuilder sb = new StringBuilder(formatter.toJournalRemoveString(item));

            ItemDiscountStrategyIfc[] itemDiscounts =
                item.getItemPrice().getItemDiscounts();
            if((itemDiscounts != null) && (itemDiscounts.length > 0))
            {
                for(int i = 0; i < itemDiscounts.length; i++)
                {
                    if (!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit))
                    {
                        sb.append(Util.EOL);
                        sb.append(formatter.toJournalManualDiscount(item, itemDiscounts[i], true));
                    }
                }
            }

            POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            BigDecimal price = cargo.getOverridePrice();
            if (price == null)
            {
                // not specified in cargo, get price from ui
                DecimalWithReasonBeanModel beanModel = (DecimalWithReasonBeanModel)ui.getModel(screenID);
                price = beanModel.getValue();
            }

            // make the price into CurrencyIfc and apply it to the line item.
            CurrencyIfc currency = DomainGateway.getBaseCurrencyInstance(price.toString());
            item.modifyItemPrice(currency, selectedCode);
            if (item.getReturnItem() != null)
            {
                item.getReturnItem().setPrice(currency);
            }
            item.getItemPrice().calculateItemTotal();

            // BEGIN HACK: For VAT the line item's tax amounts are reported in the EJournal.
            // In order to retrieve the right values, they must be recalculated.  The
            // actual update to the transaction doesn't occur until leaving the pricing
            // service in the PricingReturnShuttle.  To fix this I calculate the tax for
            // the price overridden items in a clone of the transaction. Alan Sinton
            // NOTE: The journaling will be done against a line item clone. CMG
            boolean taxInclusiveFlag = Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);
            RetailTransactionIfc transaction = cargo.getTransaction();
            if (taxInclusiveFlag && transaction instanceof SaleReturnTransactionIfc)
            {
                SaleReturnTransactionIfc srTransaction = (SaleReturnTransactionIfc)transaction.clone();
                // line item is now a clone
                item = (SaleReturnLineItemIfc)item.clone();
                srTransaction.replaceLineItem(item, item.getLineNumber());
                srTransaction.updateTransactionTotals();
            }
            // END HACK

            //save new item info in StringBuilder for journal
            sb.append(Util.EOL);
            sb.append(formatter.toJournalString(item, null, null));

            itemDiscounts = item.getItemPrice().getItemDiscounts();
            if((itemDiscounts != null) && (itemDiscounts.length > 0))
            {
                for(int i = 0; i < itemDiscounts.length; i++)
                {
                    if (!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit))
                    {
                        sb.append(Util.EOL);
                        sb.append(item.toJournalManualDiscount(itemDiscounts[i],false, LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
                    }
                }
            }

            ItemPriceIfc ip = item.getItemPrice();
            sb.append(Util.EOL);
            //chnaged by vaibhav--made reason code variable global
             String reasonCode = getReasonCodeJournalString(bus, cargo, ip);
            sb.append(reasonCode);

            //actually write the journal
            journal.journal(cargo.getOperator().getEmployeeID(),
                            cargo.getTransactionID(),
                            sb.toString());
        }
        else
        {
            logger.warn( "No journal manager found!");
        }
    }

    /**
     * Gets the reason code journal string for the price override.
     * @param bus
     * @param cargo
     * @param ip
     * @return the reason code journal string.
     */
    protected String getReasonCodeJournalString(BusIfc bus, PricingCargo cargo, ItemPriceIfc ip)
    {
        String reasonCodeDescription = ip.getItemPriceOverrideReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
        if(Util.isEmpty(reasonCodeDescription))
        {
            UtilityManagerIfc utilityManager = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            CodeListIfc reasonCodes = utilityManager.getReasonCodes(cargo.getOperator().getStoreID(), CodeListIfc.CODE_LIST_PRICE_OVERRIDE_REASON_CODES);
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
    
    protected boolean isValidOverridePrice(PricingCargo pricingCargo,
			POSUIManagerIfc ui, String screenId,BusIfc bus) {

		SaleReturnLineItemIfc lineItem = pricingCargo.getItems()[0];
		MAXPLUItem pluItem = (MAXPLUItem)lineItem.getPLUItem();
		boolean validOverridePrice = true;
		 String priceoverrideParameter=null;
		 BigDecimal response = new BigDecimal("0.00");
		 
		/*If the Retail less Than MRP flag is set to true*/
		if (pluItem.getRetailLessThanMRPFlag()) {
			CurrencyIfc maximumRetailPriceCurrency = ((MAXPLUItemIfc) lineItem.getPLUItem())
					.getMaximumRetailPrice();
			DecimalWithReasonBeanModel beanModel = (DecimalWithReasonBeanModel) ui
					.getModel(screenId);
			BigDecimal price = beanModel.getValue();
			// make the price into CurrencyIfc
			CurrencyIfc overridePriceCurrency = DomainGateway
					.getBaseCurrencyInstance(price.toString());
			int compareCurrencyValue=overridePriceCurrency.compareTo(maximumRetailPriceCurrency);
			String selectedReasonKey = beanModel.getSelectedReasonKey();
			//Added by Vaibhav for getting the reasoncode name and get the parameter value from application.xml
	        LocalizedCodeIfc localizedCode = pricingCargo.getSelectedReasonCode();
	        String codeName = null;
	        int   pmValue = 0;
	        if (localizedCode == null)
	        {
	            localizedCode = DomainGateway.getFactory().getLocalizedCode();
	            CodeListIfc rcl = pricingCargo.getPriceOverrideCodeList();
	            if (rcl != null)
	            {
	                CodeEntryIfc entry = rcl.findListEntryByCode (selectedReasonKey);
	                codeName=entry.getCodeName();
	                codeName=codeName.replaceAll("\\s","");
	            }
	        }
	         priceoverrideParameter=codeName.concat("PriceOverrideValue");
	         ParameterManagerIfc pm2 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
	       
				try {
					pmValue = pm2.getIntegerValue(priceoverrideParameter).intValue();
				} catch (ParameterException e) {
					if (logger.isInfoEnabled())
						logger.info("MAXItemPriceModifiedAisle.isValidOverridePrice(), cannot find PriceOverrideValue paremeter.");
				}
				
				response = new BigDecimal(pmValue).setScale(2);
				CurrencyIfc paramprice = DomainGateway
						.getBaseCurrencyInstance(response.toString());
				
				int compareCurrencyValue1=overridePriceCurrency.compareTo(paramprice);
			
			if(compareCurrencyValue>0){
				validOverridePrice=false;
			}else if (compareCurrencyValue1>0) {
				validOverridePrice=false;
		}
			//end
		}
	    return validOverridePrice;
	}
}
