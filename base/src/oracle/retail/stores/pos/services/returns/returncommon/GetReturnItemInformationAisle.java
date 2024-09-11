/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/GetReturnItemInformationAisle.java /main/26 2014/07/17 15:09:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/15/14 - Set original transaction info for order line item.
 *    abondala  09/04/13 - initialize collections
 *    rabhawsa  05/30/13 - updating serial number in the return item
 *    arabalas  03/28/13 - prefix required number of leading zeroes if the
 *                         receipt id does not confirm to the supported format
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    jswan     10/25/12 - Modified use method on SaleReturnLineItemIfc rather
 *                         than calcualtating the value in the site.
 *                         OrderLineItem has its own implementation of this
 *                         method.
 *    rabhawsa  03/29/12 - item condition is not mandatory field
 *    abondala  03/12/12 - return response codes localization
 *    rabhawsa  03/08/12 - RM i18n changes Added item condition
 *    abondala  09/28/11 - add house account option in RM application
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    ranojha   12/02/08 - Fixed to check for POS crash due to null entries
 *
 * ===========================================================================
 * $Log:
 *  10   360Commerce 1.9         5/18/2007 9:18:14 AM   Anda D. Cadar   EJ and
 *       currency UI changes
 *  9    360Commerce 1.8         4/25/2007 8:52:15 AM   Anda D. Cadar   I18N
 *       merge
 *       
 *  8    360Commerce 1.7         7/28/2006 6:03:16 PM   Brett J. Larsen CR 4530
 *        - default reason code fix
 *       v7x->360Commerce merge
 *  7    360Commerce 1.6         5/12/2006 5:25:32 PM   Charles D. Baker
 *       Merging with v1_0_0_53 of Returns Managament
 *  6    360Commerce 1.5         5/4/2006 5:11:51 PM    Brendan W. Farrell
 *       Remove inventory.
 *  5    360Commerce 1.4         4/27/2006 7:07:08 PM   Brett J. Larsen CR
 *       17307 - inventory functionality removal - stage 2
 *  4    360Commerce 1.3         1/22/2006 11:45:17 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:50 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse   
 * $
 *
 *  6    .v7x      1.3.1.1     7/21/2006 1:22:05 PM   Michael Wisbauer added
 *       setting default and changed reason code to be required.
 *  5    .v7x      1.3.1.0     6/23/2006 4:59:13 AM   Dinesh Gautam   CR 4530:
 *       Fix for reason code
 *
 * Revision 1.31  2004/09/24 15:46:55  jdeleau
 * @scr 7238 For taxes on items being returned with more than
 * one jurisdiction, the tax amount was not being correctly calculated.
 *
 * Revision 1.30  2004/07/13 16:26:33  jdeleau
 * @scr 6185 Fix return for non-taxable
 *
 * Revision 1.29  2004/07/07 22:10:33  jdeleau
 * @scr 5785 Tax on returns needs to be pro-rated if the original tax was 
 * on a quantified number of items.  The pro-rating needs to have
 * no rounding errors in the longer term, so that if a person is taxed
 * 68 cents on 5 items, he will only be refunded 68 cents even if he returns
 * the 5 items one at a time.
 *
 * Revision 1.28  2004/06/29 22:03:31  aachinfiev
 * Merge the changes for inventory & POS integration
 *
 * Revision 1.27  2004/06/26 14:23:48  mweis
 * @scr 5848 Cannot return -- with out receipt -- any item
 *
 * Revision 1.26  2004/06/24 21:31:42  jdeleau
 * @scr 5817 On a return, if the tax was on a quantity of items, and the return
 * is a limited number, pro-rate that tax based on the number of items
 * being returned.
 *
 * Revision 1.25  2004/05/27 19:31:33  jdeleau
 * @scr 2775 Remove unused imports as a result of tax engine rework
 *
 * Revision 1.24  2004/05/27 17:12:48  mkp1
 * @scr 2775 Checking in first revision of new tax engine.
 *
 * Revision 1.23  2004/05/20 22:54:58  cdb
 * @scr 4204 Removed tabs from code base again.
 *
 * Revision 1.22  2004/04/22 19:34:28  tmorris
 * @scr 4250 -Removed line that was multiplying item price prior to setting the price.
 *
 * Revision 1.21  2004/04/13 17:00:00  cdb
 * @scr 4253    Corrected crashing, but root problem may be earlier in flow. Step number 13?
 *
 * Revision 1.20  2004/04/12 18:52:57  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.19  2004/04/06 22:04:13  pkillick
 * @scr 4250 -Changed itemReturn.setPrice(...) to set the price based on the new qty value.
 *
 * Revision 1.18  2004/03/24 21:32:15  epd
 * @scr 3561 updated to use domain factory
 *
 * Revision 1.17  2004/03/22 22:39:47  epd
 * @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 * Revision 1.16  2004/03/17 14:30:08  baa
 * @scr 3561 Returns pass along haveReceipt flag to return items
 *
 * Revision 1.15  2004/03/16 20:16:36  epd
 * @scr 3561 fixed bug that sets gift receipt selected for retrieved return items
 *
 * Revision 1.14  2004/03/15 20:17:54  epd
 * @scr 3561 Code cleanup, bug fix
 *
 * Revision 1.13  2004/03/09 16:02:57  epd
 * @scr 3561 set retrieved transaction attribute on return item
 *
 * Revision 1.12  2004/03/05 22:45:20  aarvesen
 * @scr 3561 set the quantity
 *
 * Revision 1.11  2004/03/02 23:18:30  aarvesen
 * @scr 3561 sace the item size code on the return item rather than the cargo
 *
 * Revision 1.10  2004/03/02 18:49:54  baa
 * @scr 3561 Returns add size info to journal and receipt
 *
 * Revision 1.9  2004/03/02 15:47:16  epd
 * @scr 3561 Returns updates - tax related
 *
 * Revision 1.8  2004/03/01 19:35:28  epd
 * @scr 3561 Updates for Returns.  Items now have tax rates applied based on entered store #
 *
 * Revision 1.7  2004/03/01 15:01:53  epd
 * @scr 3561 Updates for returns - tax related work
 *
 * Revision 1.5  2004/02/26 21:54:41  epd
 * @scr 3561 Removed some code that seemed to be doing nothing.  I hope I'm correct
 *
 * Revision 1.4  2004/02/12 16:51:45  mcs
 * Forcing head revision
 *
 * Revision 1.3  2004/02/11 23:22:58  bwf
 * @scr 0 Organize imports.
 *
 * Revision 1.2  2004/02/11 21:52:30  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11
 * 01:04:20 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.5 05 Feb 2004 23:16:28 baa returs - multi items
 * 
 * Rev 1.4 Nov 20 2003 16:27:16 sfl Deprecated a not used method. Resolution
 * for POS SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.3 Oct 14 2003 17:26:02 sfl Added logic to process repeating algorithm
 * in tax table when the modulus is zero. Resolution for POS SCR-3409:
 * Repeating Tax Table applies the wrong tax when modulus equals '0'
 * 
 * Rev 1.2 Oct 10 2003 10:57:12 sfl Re-check in. P610 did not get the contents
 * from P601 when it was branched. Resolution for POS SCR-3315: Implement
 * Repeating Tax Table Algorithm
 * 
 * Rev 1.1 Sep 02 2003 14:20:12 sfl Implement repeating algorithm during
 * reading tax table based tax rules. Resolution for POS SCR-3315: Implement
 * Repeating Tax Table Algorithm
 * 
 * Rev 1.0 Aug 29 2003 16:05:48 CSchellenger Initial revision.
 * 
 * Rev 1.20 Aug 21 2003 09:12:20 sfl Use correct way to get default tax rate
 * for no-receipt return item Resolution for POS SCR-3339: RSS-Incorrect way to
 * set default tax rate for no-receipt return item
 * 
 * Rev 1.19 Jul 15 2003 14:46:22 baa allow alphanumeric values on sale
 * associate field Resolution for 3121: sales associate field not editable
 * 
 * Rev 1.18 Jun 19 2003 13:41:30 sfl Changed the way to get the tax constants.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.17 Jun 12 2003 13:41:28 sfl Record more tax data to support Canadian
 * receipt reprint. Resolution for POS SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.16 Jun 10 2003 17:37:12 sfl Improvement on partial return with
 * receipt. Resolution for POS SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.15 Jun 10 2003 11:18:12 sfl If return item is taxable, assign an
 * non-zero tax rate to it so that the item will be considered as taxable by
 * the logic in SaleReturnLineItem.java. Resolution for POS SCR-2457: Till
 * Report not printing taxable return under statistical summary report
 * 
 * Rev 1.14 Apr 28 2003 09:08:34 sfl Added original transaction checking for
 * non-receipt return. Resolution for POS SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.13 Apr 25 2003 17:03:34 sfl Added code to update tax amount per tax
 * jurisdiction when partial return is happening for return transaction with
 * receipt. Resolution for POS SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.12 Apr 07 2003 15:33:56 sfl Obtain the non-receipt return item tax
 * rules from memory instead of database read. Resolution for POS SCR-1749: POS
 * 6.0 Tax Package
 * 
 * Rev 1.11 Mar 21 2003 09:36:16 sfl Make sure to use the UI modified item
 * price when search tax table based tax rules for non-receipt return item.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.10 Mar 04 2003 12:58:52 sfl Added more logic enhancement for partial
 * return of 1) one of the different line items, 2) partial quantity of same
 * line item. Resolution for POS SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.9 Feb 28 2003 14:53:50 sfl Handle partial item return in return
 * transaction using receipt Resolution for POS SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.8 Feb 21 2003 13:30:46 crain Remove deprecated calls Resolution for
 * 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 * 
 * Rev 1.7 Feb 16 2003 10:43:30 mpm Merged 5.1 changes. Resolution for POS
 * SCR-2053: Merge 5.1 changes into 6.0
 * 
 * Rev 1.6 Feb 07 2003 12:58:06 RSachdeva Database Internationalization
 * Resolution for POS SCR-1866: I18n Database support
 * 
 * Rev 1.5 Jan 09 2003 13:01:06 sfl For the partial quantity return for return
 * items with receipt, need to portion the tax amount. Resolution for POS
 * SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.4 Dec 23 2002 12:47:32 crain Set the reason code as String in item
 * return Resolution for 1869: No reason Code Printed on Return Reciepts
 * 
 * Rev 1.3 Dec 13 2002 14:09:40 sfl Added store address state/province data
 * lookup based on store id input during non-receipt item return. Then store
 * state/province data will be used for Canandian tax rule lookup. Resolution
 * for POS SCR-1749: POS 6.0 Tax Package
 * 
 * Rev 1.2 03 Oct 2002 17:13:22 sfl Added database/flat file query to let
 * non-receipt return item to have its calculated tax data based on tax rules
 * set to the item.
 * 
 * Rev 1.1 23 May 2002 17:44:08 vxs Removed unneccessary concatenations in
 * logging statements. Resolution for POS SCR-1632: Updates for Gap - Logging
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import java.math.BigDecimal;
import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tender.TenderCharge;
import oracle.retail.stores.domain.tender.TenderGiftCard;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMap;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returntransaction.ReturnTransactionCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReturnItemInfoBeanModel;

/**
 * This gets all the item return data from the UI and continues on to the next
 * site.
 */
public class GetReturnItemInformationAisle extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 4240589151925139214L;

    /**
     * This gets all the item return data from the UI and continues on to the
     * next site.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get the UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // get the cargo
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc) bus.getCargo();

        // model for the Return Item Info bean
        ReturnItemInfoBeanModel model = null;
        // Return item
        ReturnItemIfc returnItem = null;
        if (cargo.getReturnItem() == null)
        {
            returnItem = DomainGateway.getFactory().getReturnItemInstance();
            // set the itemReturn object in the cargo
            cargo.setReturnItem(returnItem);
        }
        else
        {
            returnItem = cargo.getReturnItem();
        }
        
        // get the pluItem from the cargo
        PLUItemIfc pluItem = cargo.getPLUItem();
        SaleReturnLineItemIfc srli=null;
        if (cargo.getOriginalTransaction() != null &&
            cargo instanceof ReturnTransactionCargo)
        {
            ReturnTransactionCargo cargoTran = (ReturnTransactionCargo)cargo;
            srli = cargoTran.getSaleLineItem();
            returnItem.setFromRetrievedTransaction(true);
            returnItem.setQuantityReturnable(srli.getQuantityReturnable());
            returnItem.setQuantityPurchased(srli.getItemQuantityDecimal());
            
            // Add the original transaction information to the Return Item
            if (srli instanceof OrderLineItemIfc && srli.getOrderItemStatus().getOriginalTransactionId()!=null)
            {
                returnItem.setOriginalTransactionID(srli.getOrderItemStatus().getOriginalTransactionId());
                returnItem.setOriginalTransactionBusinessDate(srli.getOrderItemStatus().getOriginalBusinessDate());
                returnItem.setOriginalLineNumber(srli.getOrderItemStatus().getOriginalLineNumber());
                
                StoreIfc store = DomainGateway.getFactory().getStoreInstance();
                store.setStoreID(srli.getOrderItemStatus().getOriginalTransactionId().getStoreID());
                returnItem.setStore(store);
            }
            else
            {
                returnItem.setOriginalTransactionID(cargoTran.getOriginalTransactionId());
                returnItem.setOriginalTransactionBusinessDate(cargo.getOriginalTransaction().getBusinessDay());
                returnItem.setOriginalLineNumber(srli.getLineNumber());
            }
            returnItem.setHaveReceipt(cargo.haveReceipt());
            
            
            returnItem.setItemTax((ItemTaxIfc)srli.getItemTax().clone());

            // Added for POS-RM integration
            TenderLineItemIfc[] tenderLineItems = cargo.getOriginalTransaction().getTenderLineItems();
            HashMap<String, String> tenderList = new HashMap<String, String>(0);
            for (int tenderIndex=0; tenderIndex<tenderLineItems.length; tenderIndex++)
            {
                if(tenderLineItems[tenderIndex] instanceof  TenderCharge && !(tenderLineItems[tenderIndex] instanceof  TenderGiftCard))
                {
                    TenderCharge tenderCharge = (TenderCharge)tenderLineItems[tenderIndex];
                    String cardType = tenderCharge.getEncipheredCardData().getCardType(); //CRDT
                    String cardName = tenderCharge.getEncipheredCardData().getCardName(); //HouseCard
                    tenderList.put(cardType, cardName);
                } 
                else
                {
                    tenderList.put(tenderLineItems[tenderIndex].getTypeCodeString(), null);
                }                
            }
            returnItem.setTenderList(tenderList);

            if (cargo.isExternalOrder())
            {
                cargoTran.setTransferCargo(true);
            }
        }
        returnItem.setFromGiftReceipt(cargo.isGiftReceiptSelected());
        
        // get the model and the new item's quantity
        BigDecimal newItemQuantity = null;
        String serialNumber = null;
        if (pluItem.isKitHeader())
        { //UI is not displayed for Kit Header items
            //model stored in cargo will contain sale item information
            //from the original transaction
            model = cargo.getReturnItemInfo();
            newItemQuantity = BigDecimal.ONE;
        }
        else
        {
            // retrieve the bean model
            model = (ReturnItemInfoBeanModel) ui.getModel(POSUIManagerIfc.RETURN_ITEM_INFO);
            newItemQuantity = new BigDecimal(model.getQuantity().toString());
            serialNumber = model.getSerialNumber();
        }
        // set the quantity of the "current item" in the cargo
        returnItem.setItemQuantity(newItemQuantity);
        
        if (!Util.isEmpty(serialNumber))
        {
            returnItem.setSerialNumber(serialNumber);

        }
        
        //Get the item price and set the returned item price
        CurrencyIfc itemPrice = null;
        if (srli != null)
        {
        	if(srli.getPLUItem().isAvailableInCurrentStore())
        	{
        		itemPrice = srli.getSellingPrice();
        		returnItem.setPrice(itemPrice);
        	}
        	else
        	{
        		itemPrice = model.getPrice();   
        		returnItem.setPrice(itemPrice);
        		TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
        		SaleReturnTransactionIfc tempTxn = DomainGateway.getFactory().getSaleReturnTransactionInstance();
        		TransactionTaxIfc transactionTax = utility.getInitialTransactionTax();
        		double defaultTaxRate = transactionTax.getDefaultRate();
        		tempTxn.setTransactionTax(transactionTax);                
        		returnItem.setTaxRate(defaultTaxRate);
        		BigDecimal quantity = srli.getItemQuantityDecimal();
        		SaleReturnLineItemIfc slri = tempTxn.addReturnItem(pluItem, returnItem, quantity);
        		srli.setItemPrice((ItemPriceIfc)slri.getItemPrice().clone());
        		returnItem.setItemTax((ItemTaxIfc)slri.getItemTax().clone());     
        	}        	
        }
        else
        {
            itemPrice = model.getPrice();                        
            returnItem.setPrice(itemPrice);
        }
        
         
        // get the store number from the model and set in the return item
        if (returnItem.getStore()==null)
        {
            StoreIfc store = DomainGateway.getFactory().getStoreInstance();
            store.setStoreID(model.getStoreNumber());
            returnItem.setStore(store);
        }

        // get the Sales Associate from the model
        // set in Cargo for lookup
        cargo.setSalesAssociateID(model.getSalesAssociate());

        // get the receipt number from the model and set it in the return item
        String receipt = model.getReceiptNumber();
        if (receipt != null && receipt.length() > 0 && cargo.getOriginalTransaction() == null)
        {
            TransactionID id = (TransactionID)DomainGateway.getFactory().getTransactionIDInstance();
            
            //Prefix required number of leading zeroes if the receipt id doesn't confirm to the supported format.  
            receipt = id.prefixLeadingZeroes(receipt);

            returnItem.setNonRetrievedOriginalReceiptId(receipt);
        }

        if (model.getTenderSelectedIndex() > -1)
        {
            returnItem.setUserSuppliedTenderType(
                    getTenderTypeFromModel(bus, model.getTenderSelectedIndex()));
        }
        
        CodeListIfc   list = cargo.getLocalizedReasonCodes();
        String reason = model.getSelectedReasonKey();
        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        if (list != null)
        {
            CodeEntryIfc entry = list.findListEntryByCode (reason);
            if (entry!=null)
            {
                localizedCode.setText(entry.getLocalizedText());
                localizedCode.setCodeName(entry.getCodeName());
            }
            localizedCode.setCode(reason);
            returnItem.setReason(localizedCode);
        }
        else {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }
        
        CodeListIfc listItemConditions = cargo.getLocalizedItemConditionCodes();
        String itemCondition = model.getItemConditionModel().getSelectedItemConditionKey();
        LocalizedCodeIfc localizedItemConditionCode = DomainGateway.getFactory().getLocalizedCode();
        if(listItemConditions != null && !Util.isEmpty(itemCondition))
        {
            CodeEntryIfc entry = listItemConditions.findListEntryByCode(itemCondition);
        
            if(entry != null)
            {
                localizedItemConditionCode.setText(entry.getLocalizedText());
                localizedItemConditionCode.setCodeName(entry.getCodeName());
            }
            
            localizedItemConditionCode.setCode(itemCondition);
            returnItem.setItemCondition(localizedItemConditionCode);
            
        }else{
            localizedItemConditionCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
            returnItem.setItemCondition(localizedItemConditionCode);
        }
            

        // set the scanned method
        if (cargo.isItemScanned())
        {
            returnItem.setEntryMethod(EntryMethod.Scan);
        }
        else
        {
            returnItem.setEntryMethod(EntryMethod.Manual);
        }

        // Mail the letter
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

    /*
     * This method converts the index of the select tender type into
     * the integer tender type.
     */
    private int getTenderTypeFromModel(BusIfc bus, int tenderSelectedIndex)
    {
        int tenderType = -1;
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            String[] tendersDescriptors = pm.getStringValues(ReturnUtilities.SALE_TENDERS_FOR_REFUND);
            String tenderText = tendersDescriptors[tenderSelectedIndex];
            tenderType = TenderTypeMap.getTenderTypeMap().getTypeFromDescriptor(tenderText);
        }
        catch (ParameterException pe)
        {
            logger.error("GetReturnItemInformationAisle could not be retrieved from the ParameterManager.", pe);
        }
        
        return tenderType;
    }
}
