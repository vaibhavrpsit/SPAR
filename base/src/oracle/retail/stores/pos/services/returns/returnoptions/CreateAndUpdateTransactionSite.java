/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/CreateAndUpdateTransactionSite.java /main/44 2013/08/01 15:56:49 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abananan  11/14/14 - Fix to prevent recalculation of price of a non retrieved
 *                         return of a gift receipt item while resuming.
 *    abhinavs  10/14/14 - FORWARD PORT (SR: 3-9077032911)- The discount amt
 *                         cannot be greater than the selling price of the item  
 *    vtemker   07/30/13 - Forward port: Add reason code to IDISC record for
 *                         return of discounted items
 *    rgour     04/01/13 - CBR cleanup
 *    rgour     02/15/13 - Setting the current store currency code for CBR
 *    jswan     12/13/12 - Modified to prorate discount and tax for returns of
 *                         order line items.
 *    jswan     10/25/12 - Modified to support returns by order.
 *    rgour     10/18/12 - CBR fix for storing original transaction currency
 *                         code
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    icole     04/25/12 - Removed item display on CPOI as the items list is
 *                         refreshed at the ShowSaleSite so no need to display
 *                         here.
 *    cgreene   03/30/12 - get journalmanager from bus
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    icole     03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                         have more generic code, rather than heavily Pincomm.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    sgu       09/08/11 - add house account as a refund tender
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/19/11 - encryption CR
 *    blarsen   06/14/11 - Adding storeID to scrolling receipt request.
 *    icole     06/13/11 - Corrected Bug 296, ClassCastException
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    abhayg    09/29/10 - FIX FOR EJOURNAL SHOWS WRONG POS DISCOUNT VALUE
 *    npoola    09/01/10 - Set the Item send label count to zero for return
 *                         items
 *    jswan     08/11/10 - Modified to set employee ID on discounts converted
 *                         from transaction discounts to item level discounts.
 *    jswan     07/20/10 - Fixed issue with Original tenders list.
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    jswan     07/05/10 - Latest changes.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    acadar    06/10/10 - use default locale for currency display
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/06/10 - add missing EJ of non-receipted returns
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    blarsen   03/26/10 - Fix for trans-amt discount rounding error. BugDB:
 *                         9105653. When trans-amt discounts are converted to
 *                         item discounts (with quantity), an extra srli is now
 *                         created to conpensate for the rounding error.
 *    asinton   02/16/10 - Swapped out deprecated method getPersonalIDType with
 *                         localized version,
 *                         getLocalizedPersonalIDType().getCode().
 *    abondala  01/03/10 - update header date
 *    blarsen   11/06/09 - XbranchMerge
 *                         blarsen_bug8841387-rework-for-quantity-issue from
 *                         rgbustores_13.1x_branch
 *    blarsen   11/03/09 - For returns with transaction-scope discounts, the
 *                         discount amount must be divided by quantity when it
 *                         is converted to an item-scope discount.
 *    blarsen   10/01/09 - XbranchMerge
 *                         blarsen_bug8841387-validate-trans-discount-on-return-fix
 *                         from rgbustores_13.1x_branch
 *    blarsen   09/23/09 - Reworked setTransactionDiscounts(). Changed the type
 *                         of discount associated with items originally
 *                         discounted with a transaction-scope discount. Using
 *                         an item-scope discount allows the recalculations
 *                         that occur at transaction-read time to calculate the
 *                         correct extended discount price.
 *    nkgautam  04/07/09 - fix for adding gift receipt boolean in return item
 *                         object
 *    vikini    03/11/09 - Adding Discounted price to the return Item
 *    vchengeg  02/19/09 - removed the commented lines and formatted for
 *                         EJournal
 *    deghosh   01/27/09 - EJ i18n defect fixes
 *    deghosh   12/08/08 - EJ i18n changes
 *    rkar      11/12/08 - Adds/changes for POS-RM integration
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    akandru   10/31/08 - EJ Changes_I18n
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 *    akandru   10/30/08 - EJ changes
 *    asinton   10/30/08 - Checkin for POSPal
 *    mdecama   10/28/08 - I18N - Reason Codes for Customer Types.
 * ===========================================================================
     $Log:
      17   360Commerce 1.16        8/21/2007 9:13:46 AM   Mathews Kochummen
           align discount amount
      16   360Commerce 1.15        8/13/2007 3:01:32 PM   Charles D. Baker CR
           27803 - Remove unused domain property.
      15   360Commerce 1.14        7/19/2007 3:23:18 PM   Anda D. Cadar   apply
            proper formatting to the discounts
      14   360Commerce 1.13        7/10/2007 4:51:51 PM   Charles D. Baker CR
           27506 - Updated to remove old fix for truncating extra decimal
           places that are used for accuracy. Truncating is no longer
           required.
      13   360Commerce 1.12        7/9/2007 4:33:54 PM    Charles D. Baker CR
           27506 - Updated to localize format of restocking fee.
      12   360Commerce 1.11        6/4/2007 6:01:32 PM    Alan N. Sinton  CR
           26486 - Changes per review comments.
      11   360Commerce 1.10        5/14/2007 2:32:57 PM   Alan N. Sinton  CR
           26486 - EJournal enhancements for VAT.
      10   360Commerce 1.9         5/8/2007 11:32:26 AM   Anda D. Cadar
           currency changes for I18N
      9    360Commerce 1.8         4/25/2007 8:52:14 AM   Anda D. Cadar   I18N
           merge

      8    360Commerce 1.7         5/4/2006 5:11:51 PM    Brendan W. Farrell
           Remove inventory.
      7    360Commerce 1.6         4/27/2006 7:07:08 PM   Brett J. Larsen CR
           17307 - inventory functionality removal - stage 2
      6    360Commerce 1.5         3/30/2006 4:51:19 PM   Michael Wisbauer
           added origianl tax info on the returnitem object that exsist in the
            srli
      5    360Commerce 1.4         3/8/2006 7:13:39 AM    Dinesh Gautam   Set
           receipt flag in cargo
      4    360Commerce 1.3         1/22/2006 11:45:19 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:25 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:14 PM  Robert Pearse
     $
     Revision 1.24.2.1  2004/11/24 22:24:46  jdeleau
     @scr 7745 Make sure numbers for returns are negative in the ejournal.

     Revision 1.24  2004/09/28 17:30:24  cdb
     @scr 7259 Update inventory location and state in SRLI when returning with receipt.

     Revision 1.23  2004/08/23 16:15:57  cdb
     @scr 4204 Removed tab characters

     Revision 1.22  2004/07/29 00:25:10  jdeleau
     @scr 6432 For tax exempt transactions being returned, make sure the return
     transaction is not tax exempt.  The current transaction also must not retain
     any of the customer information from the transaction being returned.

     Revision 1.21  2004/07/24 17:27:49  jdeleau
     @scr 6432 Correct the way tax exempt transactions are returned, and
     make the entire return transaction also tax exempt.

     Revision 1.20  2004/07/15 23:19:30  mweis
     @scr 5583 Returns: 'ZZ' and 'ZZZ' appear in electronic journal

     Revision 1.19  2004/06/30 21:26:55  jdeleau
     @scr 5921 Void transactions were doubling returned tax on
     kit Items.  This is because the header and individual line items were
     both calculating tax.  This is now corrected.

     Revision 1.18  2004/06/24 18:33:21  jdeleau
     @scr 5805 Tax Exempt transactions should not refund tax amounts on returns.

     Revision 1.17  2004/06/21 13:14:19  dfierling
     @scr 5579 - Fixed crash on partial kit transaction

     Revision 1.16  2004/06/19 17:57:39  jdeleau
     @scr 2775 Fix return with receipt, make sure the sign is correct.

     Revision 1.15  2004/05/27 19:31:33  jdeleau
     @scr 2775 Remove unused imports as a result of tax engine rework

     Revision 1.14  2004/05/27 17:12:48  mkp1
     @scr 2775 Checking in first revision of new tax engine.

     Revision 1.13  2004/05/03 19:53:05  epd
     @scr 4264 Fixed crash bug when Gift Card is invalid

     Revision 1.12  2004/04/05 15:47:54  jdeleau
     @scr 4090 Code review comments incorporated into the codebase

     Revision 1.11  2004/03/25 20:25:15  jdeleau
     @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
     See the scr for more info.

     Revision 1.10  2004/03/22 22:39:47  epd
     @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.

     Revision 1.9  2004/03/18 23:01:56  baa
     @scr 3561 returns fixes for gift card

     Revision 1.8  2004/03/03 17:26:50  baa
     @scr 3561 add journaling of return items

     Revision 1.7  2004/03/02 23:19:04  aarvesen
     @scr 3561 check on the value of the retrieved transaction rather than the receipt

     Revision 1.6  2004/03/01 21:29:04  aarvesen
     @scr 3561  Fix so that receipt-less returns show the overridden return price

     Revision 1.5  2004/02/27 01:43:29  baa
     @scr 3561 returns - selecting return items

     Revision 1.4  2004/02/24 22:08:14  baa
     @scr 3561 continue returns dev

     Revision 1.3  2004/02/12 16:51:52  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:25  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   08 Nov 2003 01:45:00   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.1   Sep 03 2003 15:57:00   RSachdeva
 * Add CIDScreen support
 * Resolution for POS SCR-3355: Add CIDScreen support
 *
 *    Rev 1.0   Aug 29 2003 16:06:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.9   May 08 2003 17:58:34   sfl
 * Enhancement on no-receipt return item EJ recording.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.8   Apr 09 2003 12:48:30   HDyer
 * Changes from code review.
 * Resolution for POS SCR-1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.7   Mar 26 2003 17:40:28   sfl
 * Adjust return item tax amount format based on the different options of tax calculations (A. Tax by tax group, then prorate to line items, In this case, use 2-digit after decimal point for currency; B. Keep long precision at line item level, then do the rounding at transaction level. In this case, use 5-digit after decimal point for currency.)
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.6   Mar 19 2003 16:48:06   sfl
 * Make sure to obtain non-receipt return items' tax amount
 * for EJ logging.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.5   Feb 16 2003 10:43:34   mpm
 * Merged 5.1 changes.
 * Resolution for POS SCR-2053: Merge 5.1 changes into 6.0
 *
 *    Rev 1.4   Jan 10 2003 11:28:26   sfl
 * Adjusted the EJ print format for restocking fee to be consistent with price amount so that they all have two digits after decimal point.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.3   Dec 13 2002 15:59:18   HDyer
 * Added code to journal the personal ID during the return.
 * Resolution for POS-SCR 1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.2   Dec 02 2002 15:12:02   sfl
 * Display longer precision tax amount at item level for return items so that the rounding problem will be resolved.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.1   Aug 15 2002 13:12:48   jriggins
 * Replaced concat of customer first and last name to retrieval of CustomerAddressSpec.CustomerName from customerText bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:04:46   msg
 * Initial revision.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.returns.returnoptions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.DiscountableLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnData;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This creates a transaction if needed and updates the transaction with the
 * return items.
 *
 */
public class CreateAndUpdateTransactionSite extends PosSiteActionAdapter
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = 6827832462449192175L;
    /**
     * site name constant
     */
    public static final String SITENAME = "CreateAndUpdateTransactionSite";
    /**
     * Constant for journalling
     */
    protected static final int ITEM_PRICE_LENGTH = 13;
    /**
     * Constant for journalling
     */
    protected static final int ITEM_NUMBER_LENGTH = 20;
     /**
      * Customer name bundle tag
      */
     protected static final String CUSTOMER_NAME_TAG = "CustomerName";
     /**
      * Customer name default text
      */
     protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";
     /**
      * For electronic journaling (EJ), names of countries or states that need to be marked as unknown.
      * The actual text used in the EJ will come from the "commonText" resource bundle.
      */
    protected static final String[] UNKNOWN_NAMES = { "ZZ", "ZZZ" };

    /**
     * This creates a transaction if needed and updates the transaction with the
     * return items.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the cargo and current transaction
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.FAILURE);

        // Get the return item information from the cargo
        ReturnData returnData = cargo.getReturnData();

        ReturnItemIfc[] returnItems = null;
        PLUItemIfc[] pluItems = null;
        SaleReturnLineItemIfc[] saleReturnLineItems = null;

        if (returnData != null)
        {
            returnItems = returnData.getReturnItems();
            pluItems = returnData.getPLUItems();
            saleReturnLineItems = returnData.getSaleReturnLineItems();
       }

        if(returnItems != null && returnItems.length != 0)
        {
            SaleReturnTransactionIfc transaction = cargo.getTransaction();

            // If there is no transaction ....
            if (transaction == null)
            {
                TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);

                transaction =
                        DomainGateway.getFactory().getSaleReturnTransactionInstance();

                transaction.setSalesAssociate(cargo.getSalesAssociate());
                utility.initializeTransaction(transaction, TransactionUtilityManagerIfc.GENERATE_SEQUENCE_NUMBER);
                cargo.setTransaction(transaction);
            }

            // Get the line items from the retrieve transaction
            // Process each return line item
            for(int i = 0; i < returnData.getReturnItems().length; i++)
            {
                // Prepare line item.
                SaleReturnLineItemIfc srli = null;

                ReturnItemIfc returnItem = returnItems[i];

                if ( returnItem != null )
                {
                    BigDecimal quantityReturned = returnItems[i].getItemQuantity().negate();
                    BigDecimal quantityReturnable = returnItems[i].getQuantityReturnable();

                    // If this is a manual return ...
                   if (!returnItem.isFromRetrievedTransaction())
                   {
                        // Add the line item
                        if (cargo.isExternalOrder())
                        {
                            ExternalOrderItemIfc eoi = pluItems[i].getReturnExternalOrderItem();
                            srli = transaction.addReturnItem(pluItems[i], returnItem, eoi);
                            srli.setExternalOrderItemID(eoi.getId());
                            srli.setExternalOrderParentItemID(eoi.getParentId());
                        }
                        else
                        {
                            srli = transaction.addReturnItem(pluItems[i], returnItem, quantityReturned);
                        }

                        if (!Util.isEmpty(saleReturnLineItems[i].getItemSerial()))
                        {
                            srli.setItemSerial(saleReturnLineItems[i].getItemSerial());
                        }
                        //if it is manual gift receipt return item  copy to srli
                        if (returnItem.isFromGiftReceipt())
                        {
                            srli.setGiftReceiptItem(returnItem.isFromGiftReceipt());
                        }

                        // copy entry method from return item
                        srli.setEntryMethod(returnItem.getEntryMethod());
                        journalLine(bus, cargo, pluItems, i, srli);
                    }
                    // If this is a return based on a retrieved transaction
                    else
                    {
                        // Use the Sale Return item from the transaction
                        srli = saleReturnLineItems[i];

                        // This fix was made to make sure the price reaching RM from POS is Discounted Price and not Selling Price.
                        // Makinf sure there is no null pointer and Deivide by Zero.
                        if(srli.getExtendedDiscountedSellingPrice() != null && srli.getItemPrice().getItemQuantityDecimal() != null && srli.getItemPrice().getItemQuantityDecimal().intValue() > 0)
                        {
                          CurrencyIfc price = srli.getExtendedDiscountedSellingPrice().abs().divide(srli.getItemPrice().getItemQuantityDecimal());
                          returnItem.setPrice(price);
                        }

                        if (srli.isGiftReceiptItem())
                        {
                            returnItem.setFromGiftReceipt(srli.isGiftReceiptItem());
                        }

                        srli.setReturnItem(returnItem);
                        // Set the send label count to zero. Shipment addresses do not print on
                        // the receipt for returns.
                        srli.setSendLabelCount(0);

                        // set the restocking fee from the return item in the item price
                        srli.getItemPrice().setRestockingFee(returnItem.getRestockingFee());

                       
                        
                        // Update the quantity returned value in the order line item status.
                        if (srli instanceof OrderLineItemIfc)
                        {
                            //OrderItemStatusIfc ois = ((OrderLineItemIfc)srli).getOrderItemStatus();
                            //ois.setQuantityReturned(ois.getQuantityReturned().add(quantityReturned.abs()));
                            if (cargo.getOriginalTransaction() != null)
                            {
                                ((OrderLineItemIfc)srli).setOrderID(cargo.getOriginalTransaction().getOrderID());
                            }
                            
                            // This method does the equivalent of srli.modifyItemQuantity() for order line items
                            ((OrderLineItemIfc)srli).prorateItemForReturn(quantityReturned);
                            
                            // The prorateItemForReturn() converts transaction discounts to item discounts.
                            // The transaction discount amount must be cleared to make the line totals 
                            // calculations to come out correctly.
                            ((DiscountableLineItemIfc)srli).clearTransactionDiscounts();
                            
                            // Add the line item to the transaction and journal it.
                            transaction.addLineItem(srli);
                            addTax(transaction, srli, returnItem);
                            journalLine(bus, cargo, pluItems, i, srli);
                            
                            // Save the return, discount and tax amounts to the Order Line Item Status object.
                            ((OrderLineItemIfc)srli).prepareReturnLineItem();
                            
                        }
                        else
                        {
                            srli.modifyItemQuantity(quantityReturned);

                            // convert transaction discounts to item discounts (if needed)
                            List<SaleReturnLineItemIfc> srliListRemainders = convertTransactionDiscounts(transaction, srli, returnData, i, quantityReturned, quantityReturnable);
                            
                            // the srli was split up to account for some rounding issues, new elements were added to the returnData's arrays by
                            // convertTransactionDiscounts(), so, updated references
                            if (srliListRemainders != null && srliListRemainders.size() > 0)
                            {
                                returnItems = returnData.getReturnItems();
                                pluItems = returnData.getPLUItems();
                                saleReturnLineItems = returnData.getSaleReturnLineItems();
                            }

                            transaction.addLineItem(srli);
                            addTax(transaction, srli, returnItem);
                            journalLine(bus, cargo, pluItems, i, srli);

                            // the srli was split up, add the new srli to the transaction, increment the counter so it
                            // not processed twice
                            if (srliListRemainders != null && srliListRemainders.size() > 0)
                            {
                                for (SaleReturnLineItemIfc srliNew : srliListRemainders)
                                {
                                    i++;
                                    transaction.addLineItem(srliNew);
                                    addTax(transaction, srliNew, returnItem);
                                    journalLine(bus, cargo, pluItems, i, srliNew);
                                }
                            }
                        }
                        
                    } // no receipt (manual) vs. receipt (isFromRetrievedTransaction)
                } // returnItem != null
            } // loop over returnItems

            // Add retrieved tranaction(s) to the array of original transactions.
            updateOriginalTransactionArray(cargo);

            // Add tenders from the retrieved tranaction(s) to the array of
            // original tenders.
            updateOriginalTendersArray(cargo);

            // Set the customer to the transaction and the status panel.
            CustomerIfc customer = getCustomer(transaction, cargo);

            if (customer != null)
            {
                transaction.setCustomer(customer);
                // set the customer's name in the status area
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

                UtilityManagerIfc utility =
                  (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                Object parms[] = { customer.getFirstName(), customer.getLastName() };
                String pattern = utility.retrieveText("CustomerAddressSpec",
                                       BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                                       CUSTOMER_NAME_TAG,
                                       CUSTOMER_NAME_TEXT);
                String customerName = LocaleUtilities.formatComplexMessage(pattern, parms);

                ui.customerNameChanged(customerName);
            }

            // Indicate to the return shuttle that data should be transfered to the
            // calling service.
            cargo.setTransferCargo(true);
            letter = new Letter(CommonLetterIfc.SUCCESS);
         }

        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * journal the new srli
     *
     *
     * @param bus
     * @param cargo
     * @param pluItems
     * @param index
     * @param srli
     */
    private void journalLine(BusIfc bus, ReturnOptionsCargo cargo, PLUItemIfc[] pluItems, int index,
            SaleReturnLineItemIfc srli)
    {
        // Journal each item.
        JournalManagerIfc jm = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (jm != null)
        {
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            journalLineItem(jm, srli, pluItems[index], cargo, utility);
        }
    }

    /**
     * add tax to the new srli
     *
     * @param transaction
     * @param srli
     * @param ri
     */
    private void addTax(SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc srli,
            ReturnItemIfc ri)
    {
        // Add the line item to the transaction.  When line items are added to the list
        // the transaction tax is recalculated.  We will need to negate the tax values at this point.
        SaleReturnLineItemIfc[] liList = (SaleReturnLineItemIfc[]) transaction.getLineItems();
        srli = liList[liList.length-1];
        if (srli.getItemTax().getTaxMode() != TaxIfc.TAX_MODE_EXEMPT)
        {
            srli.getItemTax().setDefaultRate(ri.getTaxRate());
            if (srli.getItemTax().getTaxScope() == TaxIfc.TAX_SCOPE_TRANSACTION)
            {
                // force item scope here
                srli.getItemTax().setTaxScope(TaxIfc.TAX_SCOPE_ITEM);
            }
        }
    }

    /**
     * Since the transaction-scope discounts are not carried over to the return
     * transaction, each item's transaction-scope discounts are converted to
     * item-scope amount discounts.
     *
     * Without this conversion the recalculation which occurs when transactions
     * are read from the database will not consider the transaction-scope discounts.
     *
     * This results in incorrect extended discount amounts in the PosLog.  (And whatever
     * else reads the transactions after they are saved.)
     *
     * Note that the reason code is not carried over since there is no mapping
     * between one discount type's codes to the item-scope amount type's code.
     *
     * Unfortunately, the conversion from a transaction discount to an item
     * discount can result in a discount rounding error.
     *
     * For instance if coolboxes x 3 (qauntity) has a $0.05 discount.  ($0.05 is not
     * evenly divisible by 3.)
     *
     * This method compensates for this by adding, when needed, an srli to contain the
     * extra discount amount.
     *
     * The additional srli is created when all items are being returned (if needed).
     *
     * To make tax, etc work properly for the new srli this method also inserts return data
     * and plu items into the returnData object.
     *
     * The SRLI parameter and its associated return data is adusted when the SRLI is split up
     * to handle the rounding error.
     *
     * IMPORTANT NOTE: The caller of this method must adust any indexes to compensate for the
     * insertion of new elements into the returnData's arrays.
     *
     * @param transaction
     * @param srli SaleReturnLineItemIfc object
     * @param returnData
     * @param returnDataIndex
     * @param quantityReturned quantity of item returned in this srli
     * @param quantityReturnable quantity of item that can be returned
     * @return returns a list of new SRLI, if needed, which contains par
     */
    protected List<SaleReturnLineItemIfc> convertTransactionDiscounts(
            SaleReturnTransactionIfc transaction,
            SaleReturnLineItemIfc srli,
            ReturnData returnData, int returnDataIndex,
            BigDecimal quantityReturned, BigDecimal quantityReturnable)
    {

        ItemDiscountStrategyIfc[] discounts = srli.getItemPrice().getTransactionDiscounts();

        // do nothing if there are no transaction discounts
        if (discounts == null || discounts.length == 0)
        {
            return null;
        }

        ReturnItemIfc returnItem1 = returnData.getReturnItems()[returnDataIndex];

        // the srliRemainder which may be needed to contain the leftover discount due to rounding errror
        List<SaleReturnLineItemIfc> srliRemainders = new ArrayList<SaleReturnLineItemIfc>();

        boolean allReturned = quantityReturned.abs().intValue() == quantityReturnable.abs().intValue();
        BigDecimal originalQuantityPurchased = returnItem1.getQuantityPurchased().abs();

        // clear transaction discounts, these are replaced with item discounts
        srli.clearTransactionDiscounts();

        CurrencyIfc originalDiscountAmount = DomainGateway.getBaseCurrencyInstance();
        ItemTransactionDiscountAuditIfc itda = DomainGateway.getFactory().getItemTransactionDiscountAuditInstance();

        // loop over all transaction discounts, converting each into an item
        // discount
        for (int i = 0; i < discounts.length; i++)
        {
            itda = (ItemTransactionDiscountAuditIfc)discounts[i];
            originalDiscountAmount = ((CurrencyIfc)itda.getDiscountAmount().clone()).abs().add(originalDiscountAmount);
        }
        
        // round down to avoid refunding more than original discount (since this
        // item discount is multiplied by quantity)
        originalDiscountAmount.setRoundingMode(BigDecimal.ROUND_FLOOR);
        CurrencyIfc newItemDiscountAmount = originalDiscountAmount.divide(originalQuantityPurchased);
        newItemDiscountAmount.setRoundingMode(BigDecimal.ROUND_HALF_UP); 
        
        // set rounding mode back to normal
        CurrencyIfc newTotalSrliDiscountAmount = newItemDiscountAmount.multiply(quantityReturned.abs());

        // compare the total discount using the rounded down total discount with
        // the actual discount from the original transaction
        CurrencyIfc unadjustedTotalDiscountAmount = newItemDiscountAmount.multiply(originalQuantityPurchased);
        CurrencyIfc remainingTotalDiscountAmount = originalDiscountAmount.subtract(unadjustedTotalDiscountAmount);
        CurrencyIfc adjustedEachDiscountAmount = newItemDiscountAmount.add(remainingTotalDiscountAmount);

        boolean roundingErrorRequiresCorrection = unadjustedTotalDiscountAmount.compareTo(originalDiscountAmount) == CurrencyIfc.LESS_THAN;

        itda.setDiscountAmount(newTotalSrliDiscountAmount);

        ItemDiscountByAmountIfc ritda = null;
        

        // this srli contains a single item, there was a rounding error, this
        // item must include the leftover (adjusted) discount
        if (allReturned && roundingErrorRequiresCorrection && quantityReturned.abs().intValue() == 1)
        {
            ritda =  createItemDiscountByAmount(adjustedEachDiscountAmount, itda);
        }
        else
        {
            ritda =  createItemDiscountByAmount(newItemDiscountAmount, itda);
        }

        srli.addItemDiscount(ritda);

        // the srli must be split up to compensate for the rounding error
        if (allReturned && roundingErrorRequiresCorrection && quantityReturned.abs().intValue() > 1)
        {
            /*
             * Create the srli item based on the remainingTotalDiscountAmount
             * until the discounted amount is less than or equal to the selling
             * price new return items and plu items must be inserted into
             * returndData for the new srli the 1st return srli and its return
             * data's quantities must be decremented to account for the new srli
             */
            boolean adjustmentRequired = true;
            // amountTobeAdjustedFortheSplitItem is the difference between the
            // selling price and each item discount amount
            CurrencyIfc amountTobeAdjustedFortheSplitItem = srli.getSellingPrice().subtract(newItemDiscountAmount);

            while (adjustmentRequired)
            {

                // the srli which may be needed to contain the leftover discount
                // due to rounding errror
                SaleReturnLineItemIfc srliRemainder = null;

                // create a return item for the new srli in the returnData
                // the new srli that contains the adjusted discount will always
                // have quantity 1
                ReturnItemIfc additionalReturnItem = (ReturnItemIfc)returnItem1.clone();
                additionalReturnItem.setQuantityReturnable(BigDecimal.ONE);
                additionalReturnItem.setItemQuantity(BigDecimal.ONE);

                // reduce quantity of the 1st return item
                returnItem1.setItemQuantity(returnItem1.getItemQuantity().subtract(BigDecimal.ONE));

                List<ReturnItemIfc> riList = new ArrayList<ReturnItemIfc>(Arrays.asList(returnData.getReturnItems()));
                riList.add(returnDataIndex + 1, additionalReturnItem);
                returnData.setReturnItems(riList.toArray(new ReturnItemIfc[0]));

                // create a plu item for the new srli in the returnData
                List<PLUItemIfc> pluList = new ArrayList<PLUItemIfc>(Arrays.asList(returnData.getPLUItems()));
                pluList.add(returnDataIndex + 1, (PLUItemIfc)returnData.getPLUItems()[returnDataIndex].clone());
                returnData.setPLUItems(pluList.toArray(new PLUItemIfc[0]));

                srliRemainder = (SaleReturnLineItemIfc)srli.clone();
                // don't keep the 1st srli's discounts
                srliRemainder.getItemPrice().clearItemDiscounts();
                
                srliRemainder.modifyItemQuantity(BigDecimal.ONE.negate());
                srliRemainder.setReturnItem(additionalReturnItem);

                srliRemainder.getItemPrice().setRestockingFee(additionalReturnItem.getRestockingFee());

                // create a srli item for the new srli in the returnData
                List<SaleReturnLineItemIfc> srliList = new ArrayList<SaleReturnLineItemIfc>(Arrays.asList(returnData
                        .getSaleReturnLineItems()));
                srliList.add(returnDataIndex + 1, srliRemainder);
                srliRemainders.add(srliRemainder);
                returnData.setSaleReturnLineItems(srliList.toArray(new SaleReturnLineItemIfc[0]));

                // create a 2nd item discount for the new srli
                ItemDiscountByAmountIfc ritda2 = DomainGateway.getFactory().getItemDiscountByAmountInstance();
                ritda2.setAssignmentBasis(itda.getAssignmentBasis());
                ritda2.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
                ritda2.setDiscountScope(DiscountRuleConstantsIfc.DISCOUNT_SCOPE_ITEM);
                ritda2.setDiscountEmployee(itda.getDiscountEmployee());

                // decrement the quantity returned value
                quantityReturned = quantityReturned.add(BigDecimal.ONE);
                // reduce the 1st srli's quantity (since one was moved into
                // srliRemainder)
                srli.modifyItemQuantity(quantityReturned);

                CurrencyIfc discountAmountForSplit = DomainGateway.getBaseCurrencyInstance();
                // Compare the remainingTotalDiscountAmount with the
                // amountTobeAdjustedFortheSplitItem , if the
                // remainingTotalDiscountAmount is greater , then it needs one
                // more split .
                // adjust the amount from the remainingTotalDiscountAmount
                if (remainingTotalDiscountAmount.compareTo(amountTobeAdjustedFortheSplitItem) == CurrencyIfc.GREATER_THAN)
                {

                    remainingTotalDiscountAmount = remainingTotalDiscountAmount
                            .subtract(amountTobeAdjustedFortheSplitItem);

                }
                // Compare the remainingTotalDiscountAmount with the
                // amountTobeAdjustedFortheSplitItem , if the
                // remainingTotalDiscountAmount is not greater , then reset the
                // adjustmentRequired to exit the loop .
                // amountTobeAdjustedFortheSplitItem is reset with
                // remainingTotalDiscountAmount
                else
                {
                    amountTobeAdjustedFortheSplitItem = remainingTotalDiscountAmount;
                    adjustmentRequired = false;

                }

                // Add the each item's discount amount to the
                // amountTobeAdjustedFortheSplitItem and set this discount to
                // the new srli item
                discountAmountForSplit = amountTobeAdjustedFortheSplitItem.add(newItemDiscountAmount);
                ritda2.setDiscountAmount(discountAmountForSplit);
                srliRemainder.addItemDiscount(ritda2);

            }

        }

        return srliRemainders;

    }

    /**
     * This method creates item discount amount object
     * @param discountAmount
     * @param itda
     * @return
     */
    @SuppressWarnings("deprecation")
    protected ItemDiscountByAmountIfc createItemDiscountByAmount (CurrencyIfc discountAmount, ItemTransactionDiscountAuditIfc itda)
    {
        ItemDiscountByAmountIfc ritda = DomainGateway.getFactory().getItemDiscountByAmountInstance(); 
        ritda.setDiscountAmount(discountAmount);
        // convert the discount to an item amount discount
        ritda.setAssignmentBasis(itda.getAssignmentBasis());
        ritda.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
        ritda.setDiscountScope(DiscountRuleConstantsIfc.DISCOUNT_SCOPE_ITEM);
        ritda.setDiscountEmployee(itda.getDiscountEmployee());
        ritda.setReason(itda.getReason());
        ritda.setPromotionId(itda.getPromotionId());
        ritda.setPromotionComponentId(itda.getPromotionComponentId());
        ritda.setPromotionComponentDetailId(itda.getPromotionComponentDetailId());
        ritda.setReferenceID(itda.getReferenceID());
        ritda.setReferenceIDCode(itda.getReferenceIDCode());
        ritda.setRuleID(itda.getRuleID());
        return ritda;
    }
    
    /**
     * Add all required return information to the ejournal. <P>
     *
     * @param jm JournalManagerIfc
     * @param item SaleReturnLineItemIfc contains data to journal
     * @param pluItem Not used
     * @param cargo containing customer info for journaling
     *
     * @parm ReturnOptionsCargo contains data to journal
     */
    protected void journalLineItem(JournalManagerIfc jm, SaleReturnLineItemIfc item,
                                   PLUItemIfc pluItem, ReturnOptionsCargo cargo, UtilityManagerIfc utility)
    {
      JournalFormatterManagerIfc formatter =
        (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
        ItemPriceIfc ip = item.getItemPrice();
        StringBuffer sb = new StringBuffer();
        CurrencyIfc amountDiscountTotals = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amountPercentDiscountTotals = DomainGateway.getBaseCurrencyInstance();
        sb.append(formatter.toJournalString(item, null, null));
        // When tax needs to keep precision at line item level and round at transaction
        // level, need to keep 5-digit after decimal point.
        if(item.getItemPrice() != null && item.getItemPrice().getItemTax() != null
                && item.getItemPrice().getItemTax().getTaxInformationContainer() != null &&
                item.getItemPrice().getItemTax().getTaxInformationContainer().getTaxAmount() != null)
        {
            String tax = item.getItemPrice().getItemTax().getTaxInformationContainer().getTaxAmount().toFormattedString();
            if(!(tax.equals("0.00") || tax.equals("0,00")))
            {
                Object dataObject[]={tax};
                String taxData = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_LABEL, dataObject);

                sb.append(Util.EOL);
                sb.append(taxData);
            }
        }

        ItemDiscountStrategyIfc[] ida = ip.getItemDiscountsByAmount();
        // accumulate amounts of item discount by amount
        if (ida.length > 0)
        {
            amountDiscountTotals =
                ((ItemDiscountByAmountIfc)ida[0]).getDiscountAmount();
            for(int i = 1; i > ida.length; i++)
            {
                amountDiscountTotals =
                    amountDiscountTotals.add
                    (((ItemDiscountByAmountIfc)ida[i]).getDiscountAmount());
            }
        }

        ItemDiscountStrategyIfc[] idp = ip.getItemDiscountsByPercentage();
        // accumulate amounts of item discounts by percent
        if (idp.length > 0)
        {
            BigDecimal percentDiscountTotals = idp[0].getDiscountRate();
            for(int i = 1; i > idp.length; i++)
            {
                percentDiscountTotals =
                    percentDiscountTotals.add(idp[i].getDiscountRate());
            }
            BigDecimal sellingPrice = new BigDecimal(ip.getSellingPrice().toString());
            percentDiscountTotals = percentDiscountTotals.multiply(sellingPrice);
            amountPercentDiscountTotals =
                DomainGateway.getBaseCurrencyInstance(percentDiscountTotals.toString());
        }

        // accumulate return item transaction discounts
        ItemDiscountStrategyIfc[] idr = ip.getReturnItemDiscounts();
        if (idr.length > 0)
        {                               // begin check return discounts
            int method = -1;
            ReturnItemTransactionDiscountAuditIfc ritda = null;
            for (int i = 0; i < idr.length; i++)
            {                           // begin loop through return discounts
                ritda = (ReturnItemTransactionDiscountAuditIfc) idr[i];
                                // exclude customer discounts
                                // These are journaled later in the stream.
                if (ritda.getAssignmentBasis() !=
                    DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER)
                {                       // begin check non-customer discounts
                    method = ritda.getOriginalDiscountMethod();
                    if (method ==
                        DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
                    {
                        amountPercentDiscountTotals =
                            amountPercentDiscountTotals.add
                            (ritda.getDiscountAmount().negate());
                    }
                    else if (method ==
                             DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
                    {
                        amountDiscountTotals =
                            amountDiscountTotals.add
                            (ritda.getDiscountAmount().negate());
                    }
                }                       // end check non-customer discounts
            }                           // end loop through return discounts
        }                               // end check return discounts

        // if amount discounts, print it
        if ((amountDiscountTotals.signum() > 0)
                || ((amountDiscountTotals.signum() < 0) && (item.isReturnLineItem())))
        {
            BigDecimal amountDiscountTotalsDecimal =
                new BigDecimal(amountDiscountTotals.toString());
            String amountDiscountString = DomainGateway.getBaseCurrencyInstance(amountDiscountTotalsDecimal.multiply(item.getReturnItem().getItemQuantity()).toString()).toGroupFormattedString();
            Object discountDataObject[]={amountDiscountString};
            String discountReturned = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISCOUNT_RETURNED, discountDataObject);

            sb.append(Util.EOL)
              .append(discountReturned);

        }

        // if percentage discounts, print it
        if ((amountPercentDiscountTotals.signum() > 0)
                || ((amountPercentDiscountTotals.signum() < 0) && (item.isReturnLineItem())))
        {
            String percentDiscountString = amountPercentDiscountTotals.multiply(item.getReturnItem().getItemQuantity()).toString();
            Object discountDataObject[]={percentDiscountString};

            String discountReturned = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISCOUNT_RETURNED_PER, discountDataObject);
            sb.append(Util.EOL).append(discountReturned);

        }

        //journal any advanced pricing discount amounts
        ItemDiscountStrategyIfc advancedPricingDiscount = item.getAdvancedPricingDiscount();
        if (advancedPricingDiscount != null)
        {
            CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();

            switch (advancedPricingDiscount.getDiscountMethod())
            {
                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE :
                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT :
                    amount = advancedPricingDiscount.getDiscountAmount();
                    break;
                case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE :
                    amount.setDecimalValue(advancedPricingDiscount.getDiscountRate());
                    amount = amount.multiply(item.getExtendedSellingPrice().negate());
                    break;
                default :
                    break;
            }

            // if advanced pricing discount, print it
            if (amount.signum() > 0)
            {
                BigDecimal amountDecimal = new BigDecimal(amount.toString());
                String amountString = DomainGateway.getBaseCurrencyInstance(amountDecimal.toString()).toGroupFormattedString();

                Object dealDiscountDataObject[]={amountString};
                String dealDiscountReturned = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DEAL_DISCOUNT_RETURNED, dealDiscountDataObject);

                sb.append(Util.EOL)
                  .append(dealDiscountReturned);

            }

        }

        // journal the restocking fee, if any
        CurrencyIfc extendedRestockingFee = ip.getExtendedRestockingFee();
        if (extendedRestockingFee != null)
        {
            if (extendedRestockingFee.compareTo(DomainGateway.getBaseCurrencyInstance())!= CurrencyIfc.EQUALS)
            {
                CurrencyIfc restockingFee = extendedRestockingFee.multiply(new BigDecimal(-1));
                String restockingFeeString = restockingFee.toGroupFormattedString();

                Object restockDataObject[]={restockingFeeString};
                String restockingFeeData = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.RESTOCKING_FEE, restockDataObject);

                sb.append(Util.EOL).append(restockingFeeData);
            }
        }

        // Sales Associate
        if (item.getSalesAssociate() != null)
        {
            String assc = item.getSalesAssociate().getEmployeeID();
            Object dataObject[]={assc};

            String salesAssociation = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SALES_ASSOCIATION, dataObject);
            sb.append(Util.EOL).append(salesAssociation);

        }

        CustomerInfoIfc customerInfo = cargo.getCustomerInfo();
        if (customerInfo != null && customerInfo.getPersonalID().getMaskedNumber() != null
                && customerInfo.getPersonalID().getMaskedNumber().length() > 0
                && isFirstReturnItem(cargo.getTransaction(), item))
        {
            Object personalIDDataObject[]={customerInfo.getPersonalID().getMaskedNumber()};
            String personalID = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PERSONAL_ID, personalIDDataObject);

            String personalIDTypeCode = "";
            if(customerInfo.getLocalizedPersonalIDType() != null)
            {
                personalIDTypeCode = customerInfo.getLocalizedPersonalIDType().getCode();
            }
            Object personalIDTypeDataObject[]={personalIDTypeCode};
            String personalIDType = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PERSONAL_ID_TYPE, personalIDTypeDataObject);

            Object stateIDDataObject[]={retrieveNameFor(customerInfo.getPersonalIDState(), utility)};
            String stateID = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.STATE_ID, stateIDDataObject);

            Object countryIDDataObject[]={retrieveNameFor(customerInfo.getPersonalIDCountry(), utility)};
            String countryID = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.COUNTRY_ID, countryIDDataObject);

            sb.append(Util.EOL)
              .append(Util.EOL)
              .append(personalID)
              .append(Util.EOL)
              .append(personalIDType)
              .append(Util.EOL)
              .append(stateID)
              .append(Util.EOL)
              .append(countryID);
        }

        jm.journal(cargo.getOperator().getLoginID(),
                   cargo.getTransaction().getTransactionID(),
                   sb.toString());
    }

    /**
     * Returns a name appropriate for the state or country passed in, which is typically the name given to us.
     * But in the case that the name is officially unknown, returns whatever the resource bundle has for this name --
     * typically a blank.
     *
     * @param name    The name of a state or country
     * @param utility The current UtilityManager
     *
     * @return An appropriate name for the state or country.
     * @see #UNKNOWN_NAMES
     */
    protected String retrieveNameFor(String name, UtilityManagerIfc utility)
    {
        // Defense
        if (utility == null || name == null)
        {
            return name;
        }

        // Check to see if we are in the land of the unknown (for example: "ZZ" or "ZZZ")
        boolean bKnown = true;   // Assume we are known
        for (int i = 0; i < UNKNOWN_NAMES.length && bKnown; ++i)
        {
            if (name.equals(UNKNOWN_NAMES[i]))
            {
                bKnown = false;  // I am "bUnknown"
            }
        }

        // Return something.  :)
        if (bKnown)
        {
            // If we are in the land of the known, simply return with the original name.
            return name;
        }

        // If we are in the land of the unknown, use the UtilityManager to determine what we should actually show.
        return utility.retrieveCommonText(name);
    }

    /**
     * Method to determine if the current item is the first return item in the
     * transaction. Used when journalling personal ID info, which only gets
     * journalled with the first return item.<P>
     *
     * @param transaction SaleReturnTransactionIfc transaction
     * @param currentItem SaleReturnLineItemIfc line item in transaction
     *
     * @return true when the current item is the first return
     *  item in the transaction.
     */
    protected boolean isFirstReturnItem(SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc currentItem)
    {
        Vector<AbstractTransactionLineItemIfc> lineItems = null;
        SaleReturnLineItemIfc li = null;
        boolean firstFlag = true;
        int returnCount = 0;

        // If either transaction or list of items is null (which shouldn't happen)
        // then just return true that this is the first return item.
        if (transaction == null)
            return firstFlag;

        lineItems = transaction.getLineItemsVector();
        if (lineItems == null)
            return firstFlag;

        // Go through the list of line items
        // If there are one or more return items found before we get to the
        // current line item, then this is not the first return item.
        for (int i=0; i<lineItems.size(); i++)
        {
            li = (SaleReturnLineItemIfc)lineItems.elementAt(i);
            if (li.isReturnLineItem())
            {
                // Increment count of the return line items
                returnCount++;

                // Quit when we've reached the current line item
                if (currentItem.equals(li))
                    break;
            }
        }

        // If there are more returns ahead of this line item, then this is
        // not the first
        if (returnCount >= 2)
        {
            firstFlag = false;
        }

        return firstFlag;  // Return result
    }

    /**
     * Add retrieved tranaction(s) to the array of original transactions.
     * @param cargo
     */
    protected void updateOriginalTransactionArray(ReturnOptionsCargo cargo)
    {
        List<SaleReturnTransactionIfc> list = cargo.getOriginalExternalOrderReturnTransactions();
        if (cargo.isExternalOrder() && list != null)
        {
            for(SaleReturnTransactionIfc trans: list)
            {
                cargo.addOriginalReturnTransaction(trans);
            }
        }
        else
        if (cargo.getOriginalTransaction() != null)
        {
            cargo.addOriginalReturnTransaction(cargo.getOriginalTransaction());
        }
    }

    /**
     * Add tenders from the retrieved tranaction(s) to the array of
     * original tenders.
     * @param cargo
     */
    protected void updateOriginalTendersArray(ReturnOptionsCargo cargo)
    {
        List<SaleReturnTransactionIfc> list = cargo.getOriginalExternalOrderReturnTransactions();
        if (cargo.isExternalOrder() && list != null)
        {
            for(SaleReturnTransactionIfc trans: list)
            {
                ReturnTenderDataElementIfc[] returnTenders = getOriginalTenders(
                        trans.getTenderLineItems());
                trans.setReturnTenderElements(returnTenders);
                cargo.appendOriginalTenders(returnTenders);
            }
        }
        else
        if (cargo.getOriginalTransaction() != null)
        {
            ReturnTenderDataElementIfc[] returnTenders = getOriginalTenders(
                    cargo.getOriginalTransaction().getTenderLineItems());
            cargo.getOriginalTransaction().setReturnTenderElements(returnTenders);
            cargo.appendOriginalTenders(cargo.getOriginalTransaction().getReturnTenderElements());
        }
    }

    /*
     * Retrieve tenders from original transaction
     */
    private ReturnTenderDataElementIfc[] getOriginalTenders(TenderLineItemIfc[] tenderList)
    {
        ReturnTenderDataElementIfc [] tenders = new ReturnTenderDataElementIfc[tenderList.length];
        for (int i =0; i < tenderList.length; i++)
        {
            tenders[i]=DomainGateway.getFactory().getReturnTenderDataElementInstance();
            tenders[i].setTenderType(tenderList[i].getTypeCode());
            if (tenderList[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
            {
                tenders[i].setCardType(((TenderChargeIfc)tenderList[i]).getCardType());
            }
            tenders[i].setAccountNumber(new String(tenderList[i].getNumber()));
            tenders[i].setTenderAmount(tenderList[i].getAmountTender());
        }
        return tenders;
    }

    /**
     * Find the customer associated with this transaction, if there is one.
     * @param transaction
     * @param cargo
     * @return
     */
    protected CustomerIfc getCustomer(SaleReturnTransactionIfc transaction, ReturnOptionsCargo cargo)
    {
        CustomerIfc customer = null;

        if (transaction.getCustomer() != null)
        {
            // if there is a customer associated with the current transaction.
            customer = transaction.getCustomer();
        }
        else
        if (cargo.getCustomer() != null)
        {
            // if there is a customer in cargo.
            customer = cargo.getCustomer();
        }
        else
        {
            if (cargo.isExternalOrder())
            {
                // if this is an external order, use the customer from the last
                // transaction returned.
                List<SaleReturnTransactionIfc> list = cargo.getOriginalExternalOrderReturnTransactions();
                if (list != null)
                {
                    for(SaleReturnTransactionIfc trans: list)
                    {
                        if (trans.getCustomer() != null)
                        {
                            customer = trans.getCustomer();
                        }
                    }
                }
            }
            else
            {
                // if there is a customer associated with the origional transaction.
                SaleReturnTransactionIfc trans = cargo.getOriginalTransaction();
                if (trans != null && trans.getCustomer() != null)
                {
                    customer = trans.getCustomer();
                }
            }
        }

        return customer;
    }
}
