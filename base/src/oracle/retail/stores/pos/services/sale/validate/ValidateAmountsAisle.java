/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/ValidateAmountsAisle.java /main/22 2014/03/06 16:53:43 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    sgu    03/06/14 - add logic to retrieve transaction discount and tax for
 *                      CSC order
 *    abonda 09/04/13 - initialize collections
 *    blarse 06/29/12 - Changed to include unused coupon item IDs in the
 *                      INVALID_TRANS_DISC_DLR error message.
 *    asinto 06/06/12 - set entry type to ENTRY_TYPE_TRANS so that the journal
 *                      entry will be complete.
 *    asinto 06/05/12 - dont journal the totals if there are tenders applied.
 *    cgreen 03/30/12 - get journalmanager from bus
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    rabhaw 08/17/12 - wptg- removed placeholder from key
 *                      InvalidTransDiscountDlr
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    blarse 12/21/10 - Removed warnings. Changed to use an updated (and
 *                      consistent) method in DiscountUtility.
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    acadar 04/09/10 - optimize calls to LocaleMAp
 *    acadar 04/08/10 - merge to tip
 *    acadar 04/05/10 - use default locale for currency and date/time display
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    sswamy 11/05/08 - Checkin after merges
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 * ===========================================================================
     $Log:
      9    360Commerce 1.8         4/25/2007 8:52:46 AM   Anda D. Cadar   I18N
           merge

      8    360Commerce 1.7         3/29/2007 5:47:12 PM   Michael Boyd    CR
           26172 - v8x merge to trunk


           10   .v8x      1.6.1.2     1/19/2007 5:08:06 PM   Charles D. Baker
           CR
           22742 - Incorrectly checked in revision 9 against wrong CR number.
           Should've been 22742.
           9    .v8x      1.6.1.1     1/19/2007 5:04:46 PM   Charles D. Baker
           CR
           24764 - Corrected alignment of subtotal in EJournal.
           8    .v8x      1.6.1.0     1/18/2007 5:12:37 AM   Manas Sahu
           CR
           22742: Ejournal has 'Sub-Total' field header. EJournal should have
           "Subtotal' field header to be consistent with receipt

           Change in method journalTotals() Line number 550.
      7    360Commerce 1.6         2/24/2006 10:25:02 AM  Brett J. Larsen CR
           10575 - incorrect tax amount in e-journal for tax exempt
           transactions

           replacing faulty code with call to new code in JournalUtilities
      6    360Commerce 1.5         2/16/2006 9:59:25 AM   Jason L. DeLeau 4140:
            Fix flow and error message for unused store coupons.
      5    360Commerce 1.4         2/15/2006 10:03:55 AM  Brett J. Larsen CR
           10575 - journalTotals printing wrong tax info when transaction is
           tax exempt - side-effect of CR 6017
      4    360Commerce 1.3         1/22/2006 11:45:02 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:38 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:27 PM  Robert Pearse
     $
     Revision 1.15.2.1  2004/10/18 18:20:54  jdeleau
     @scr 7381 Correct printing of tax in the e-journal for when printItemTax
     is turned off.


     Revision 1.15  2004/09/30 20:21:52  jdeleau
     @scr 7263 Make printItemTax apply to e-journal as well as receipts.

     Revision 1.14  2004/08/23 16:15:59  cdb
     @scr 4204 Removed tab characters

     Revision 1.13  2004/08/10 15:51:12  rsachdeva
     @scr 6791 Transaction Level Send

     Revision 1.12  2004/07/21 18:40:36  jriggins
     @scr 6327 Added clones for references to tax information that was being inadvertantly modified globally.

     Revision 1.11  2004/07/16 19:11:35  cdb
     @scr 4559 Discount Cleanup. Corrected prorate behavior for 0 subtotal to be
     more consistent with similar behavior.

     Revision 1.10  2004/07/16 02:53:09  cdb
     @scr 4559 Re-enabled error dialog for invalid discount.

     Revision 1.9  2004/07/15 19:24:10  lzhao
     @scr 6266: align the taxable amount and tax amount.

     Revision 1.8  2004/07/14 23:02:00  lzhao
     @scr 6266: add a space between text and amount.

     Revision 1.7  2004/07/13 20:16:57  lzhao
     @scr 6090: detail tax info in ejournal

     Revision 1.6  2004/07/09 20:56:15  cdb
     @scr 6115    Removed unusual check that discount be less than discounted price. Updated to validate
     employee transaction discounts as well.

     Revision 1.5  2004/06/01 16:04:03  awilliam
     @scr 5261 invalid coupon printing on receipt

     Revision 1.4  2004/05/12 19:46:05  rsachdeva
     @scr 4670 Send: Multiple Sends Journal Total Shipping Charge

     Revision 1.3  2004/02/12 16:48:21  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:22:50  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   Nov 21 2003 13:34:52   rrn
 * Changed "Removed" to "Deleted" in journalDiscountsRemoval method.
 * Resolution for 3506: Journal format changes
 *
 *    Rev 1.1   08 Nov 2003 01:27:22   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.0   Nov 05 2003 17:39:02   sfl
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 *
 *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.sale.validate;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Validates Transaction amounts before tendering
**/
//--------------------------------------------------------------------------
public class ValidateAmountsAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -5261724879892548116L;
    /**
     * constant for error dialog screen
     */
    public static final String INVALID_TRANS_DISC = "InvalidTransactionDiscount";
    /**
     * constant for error dialog screen
     */
    public static final String INVALID_TRANS_DISC_DLR = "InvalidTransDiscountDlr";
    /**
     * constant for invalid tax error dialog screen
     */
    public static final String INVALID_TAX = "InvalidTax";
    /**
     * constant for parameter name
     */
    public static final String MAX_DISC_PCT = "MaximumTransactionDiscountAmountPercent";
    /**
     * constant for parameter name
     */
    public static final String MAX_EMP_DISC_PCT = "MaximumEmployeeTransactionDiscountAmountPercent";
    /**
     * length of available space for discount value
     */
    public static int AVAIL_DISCOUNT_LENGTH = 23;
    /**
     * default shipping charge amount string constant
     */
    public static final String NO_SHIPPING_CHARGE = "0.00";

    /**
     * total shipping charge
     */
    public static final String TOTAL_SHIPPING_CHARGE = "Total Shipping Charge ";

    /**
     * Subject the transaction discount to several tests to determine whether to
     * accept or reject the discount. For example, the transaction must contain
     * discountable items, otherwise the transaction discount must be removed.
     * The trans discount cannot exceed the maximum transaction discount
     * allowed. And, at the item level, the discount applied cannot exceed the
     * item amount.
     *
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {

        SaleCargoIfc        cargo = (SaleCargoIfc)bus.getCargo();
        ParameterManagerIfc pm    = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc     ui    = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Get the transaction
        SaleReturnTransactionIfc transaction = cargo.getTransaction();

        // get Maximum Discount Percent from parameter
        BigDecimal  maxTransDiscPct   =  getMaximumDiscountPercent(pm, bus.getServiceName());
        BigDecimal  maxEmpTransDiscPct   =  getMaximumEmployeeDiscountPercent(pm, bus.getServiceName());
        // Run through Five tests and exit if there is a failure
        while(true)
        {
            // First Test:
            // Are there items eligible for the transaction discounts applied?
            // For a web managed order (CSC order), although it is not discountable in ORPOS, it may have discount
            // applied by CSC that we have to preserve.
            if(transaction.hasTransactionDiscounts() & !transaction.hasDiscountableItems() && !transaction.isWebManagedOrder())
            {
                //clear all transaction discounts to allow operator to proceed
                transaction.clearTransactionDiscounts(true);

                // journal removal of discount due to Clear key
                journalDiscountRemoval(bus, transaction);

                String[] argument = new String[]{""};

                //setting letter as LaunchTender because we want to keep on going past the
                //error screen. Not setting the oklettername for the model will cause the
                //operator to go back to previous screen from the error screen.
                displayDialogWithArgs(INVALID_TRANS_DISC_DLR,ui,argument,"LaunchTender");
                break;
            }

            // Second Test:
            // Do any of the item's discount amounts exceed its selling price?
            if (transaction.itemsDiscountExceedsSellingPrice() )
            {
                displayDialog(INVALID_TRANS_DISC, ui);
                break;
            }

            // Third Test:
            // Does the transaction discount amount exceed the Maximum amount allowed?
            CurrencyIfc maxDiscountAmount = transaction.calculateSubtotalPercentage(maxTransDiscPct);
            CurrencyIfc maxEmpDiscountAmount = transaction.calculateSubtotalPercentage(maxEmpTransDiscPct);
           //In an exchange it is possible for the discount amount to exceed the subtotal
           if (transaction.hasTransactionDiscounts())
           {
               if (transaction.transactionDiscountsExceed(maxDiscountAmount) && !transaction.isExchange())
                {
                    //format the percent value
                    maxTransDiscPct = maxTransDiscPct.setScale(2).movePointRight(2);

                    // Build and display message that the markdown is out of range
                    Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
                    String[] msg = new String[3];
                    msg[0] = utility.retrieveCommonText("Discount", "discount", uiLocale);
                    msg[1] = maxTransDiscPct.toString();
                    msg[2] = utility.retrieveCommonText("Transaction", "transaction", uiLocale).toLowerCase(uiLocale);

                    displayDialogWithArgs("InvalidItemDiscount",ui,msg,null);
                    break;
                }

               if (transaction.transactionEmployeeDiscountsExceed(maxEmpDiscountAmount) && !transaction.isExchange())
               {
                   //format the percent value
                   maxEmpTransDiscPct = maxEmpTransDiscPct.setScale(2).movePointRight(2);

                   // Build and display message that the markdown is out of range
                   Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
                   String[] msg = new String[3];
                   msg[0] = utility.retrieveCommonText("Employee Discount", "employee discount", uiLocale);
                   msg[1] = maxEmpTransDiscPct.toString();
                   msg[2] = utility.retrieveCommonText("Transaction", "transaction", uiLocale).toLowerCase(uiLocale);

                   displayDialogWithArgs("InvalidItemDiscount",ui,msg,null);
                   break;
               }
           }

            // Fourth Test:
            // Is the tax applied to each item valid?
            if (transaction.itemsTaxExceedsSellingPrice())
            {
                displayDialog(INVALID_TAX, ui);
                break;
            }

            //Fifth Test:
            //Determine if there are store coupons scanned which cannot be applied to
            //any of the current sell items in which case just let the operator know about it.
            List<String> unusedCoupons = transaction.unappliedStoreCoupons();
            if(unusedCoupons != null && unusedCoupons.size() > 0)
            {
              //Indicate through the argument text that not all store coupons present can be
              //applied to the current items. Advanced pricing will automatically ignore
              //the inapplicable store coupon.
              String stCoupon =
                utility.retrieveDialogText("InvalidTransDiscountDlr.StoreCoupon",
                                           "store coupon.");
              String[] argument = new String[unusedCoupons.size() + 1];
              argument[0] = stCoupon;
              int i = 1;
              for (String itemID: unusedCoupons)
              {
                  argument[i] = itemID;
                  i++;
              }


              //setting letter as LaunchTender because we want to keep on going past the
              //error screen. Not setting the oklettername for the model will cause the
              //operator to go back to previous screen from the error screen.
              // Previous change caused an opportunity to bypassing the totals when store
              // coupons were not applied. Before this resulted in a failure, now the
              // tender site continues to launch.
              ///// 6    360Commerce 1.5         2/16/2006 9:59:25 AM   Jason L. DeLeau 4140:
              //// Fix flow and error message for unused store coupons.
              // To fix this, we go ahead and journal the totals as we dislay the dialog
              journalTotals(bus, cargo, transaction, pm);
              displayDialogWithArgs(INVALID_TRANS_DISC_DLR, ui, argument, "LaunchTender");
              break;
            }
            // journal totals only if no tender applied yet
            if(transaction.getTenderLineItems() == null || transaction.getTenderLineItems().length == 0)
            {
                journalTotals(bus, cargo, transaction, pm);
            }
            // Go on to Tender, since discount is valid
            bus.mail(new Letter("LaunchTender"), BusIfc.CURRENT);
            break;
        } // end while loop
    }

    /**
     * Display the appropriate dialog screen.
     *
     * @param value that represents the dialog screen
     * @param ui
     */
    protected void displayDialog(String value, POSUIManagerIfc ui)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(value);
        dialogModel.setType(DialogScreensIfc.ERROR);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Display the appropriate dialog screen with args.
     *
     * @param resource ID
     * @param ui
     * @param arguments
     * @param ok Name
     */
    protected void displayDialogWithArgs(String resourceID, POSUIManagerIfc ui, String[] msg, String okLetter)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(resourceID);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(msg);
        if(okLetter!=null)
        {
          dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, okLetter);
        }
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Returns BigDecimal, the maximum trans discount % allowed.
     *
     * @param the parameter manager
     * @param serviceName service name
     * @return maximum trans discount percent
     */
    protected BigDecimal getMaximumDiscountPercent(ParameterManagerIfc pm,
                                                   String serviceName)
    {
        // retrieve maximum trans discount % from parameter file
        BigDecimal maximum = new BigDecimal("100.00");  // default
        try
        {
            String s = pm.getStringValue(MAX_DISC_PCT);
            s.trim();
            maximum = new BigDecimal(s);
            if (logger.isInfoEnabled()) logger.info(
                         "Parameter read: " + MAX_DISC_PCT + "=[" + maximum + "]");
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }

        // convert maximum to a percent
        maximum = maximum.movePointLeft(2);

        return (maximum);
    }

    /**
     * Returns BigDecimal, the maximum employee trans discount % allowed.
     *
     * @param the parameter manager
     * @param serviceName service name
     * @return maximum employee trans discount percent
     */
    protected BigDecimal getMaximumEmployeeDiscountPercent(ParameterManagerIfc pm,
            String serviceName)
    {
        // retrieve maximum trans discount % from parameter file
        BigDecimal maximum = new BigDecimal("100.00");  // default
        try
        {
            String s = pm.getStringValue(MAX_EMP_DISC_PCT);
            s.trim();
            maximum = new BigDecimal(s);
            if (logger.isInfoEnabled()) logger.info(
                    "Parameter read: " + MAX_EMP_DISC_PCT + "=[" + maximum + "]");
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }

        // convert maximum to a percent
        maximum = maximum.movePointLeft(2);

        return (maximum);
    }

    /**
     * Journals Transaction totals.
     *
     * @param cargo
     * @param transaction
     * @param pm Parameter Manager
     */
    public void journalTotals(BusIfc bus, SaleCargoIfc cargo,
                              SaleReturnTransactionIfc transaction,
                              ParameterManagerIfc pm)
    {
        JournalManagerIfc journal =
            (JournalManagerIfc)bus.
                getManager(JournalManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().
                getManager(JournalFormatterManagerIfc.TYPE);
        if(journal != null && formatter != null)
        {
            journal.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
            journal.journal(transaction.getCashier().getLoginID(),
                            transaction.getTransactionID(),
                            formatter.journalTotals(transaction, pm));
        }

    }
    //-------------------------------------------------------------------
    /**
        journal the removal of discounts on the transaction
        @param transaction
    **/
    //-------------------------------------------------------------------
    public void journalDiscountRemoval(BusIfc bus, SaleReturnTransactionIfc transaction)
    {
         // get transaction level discount dollar amounts
        TransactionDiscountStrategyIfc[] discountAmounts = transaction.getTransactionDiscountsByAmount();
        TransactionDiscountStrategyIfc[] discountPercentages = transaction.getTransactionDiscountsByPercentage();

        JournalManagerIfc mgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        StringBuffer msg = new StringBuffer();
        if (discountAmounts != null )
        {
              for (int i = 0; i < discountAmounts.length; i++)
              {
                CurrencyIfc discountCurr = discountAmounts[i].getDiscountAmount();
                String discountAmountStr = discountCurr.toFormattedString().trim();

                msg.append(Util.EOL)
                   .append("TRANS: Discount")
                   .append(Util.SPACES.substring(discountAmountStr.length(),AVAIL_DISCOUNT_LENGTH))
                   .append(discountAmountStr)
                   .append(Util.EOL)
                   .append("  Discount: Amt. Deleted")
                   .append(Util.EOL)
                   .append("  Disc. Rsn.: ")
                   .append(discountAmounts[i].getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)))
                   .append(Util.EOL);
              }
        }

        if (discountPercentages != null )
        {
            for (int i = 0; i < discountPercentages.length; i++)
            {
                double  discountRate = 100.0 * discountPercentages[i].getDiscountRate().doubleValue();
                String  discountPercentStr = (new Double(discountRate)).toString().trim();
                msg.append(Util.EOL)
                  .append("TRANS: Discount")
                  .append(Util.EOL)
                  .append("  Discount: (")
                  .append(discountPercentStr)
                  .append("%) Deleted")
                  .append(Util.EOL)
                  .append("  Disc. Rsn.: ")
                  .append(discountPercentages[i].getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)))
                  .append(Util.EOL);
            }
        }
        String str = "";
        mgr.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
        mgr.journal(str, str, msg.toString());
    }

    /** ---------------------------------------------------------------------

        The following methods that were removed instead of deprecated in release 5.1.
        These methods should no longer be needed by this site, the logic has
        been moved to the domain.

    ***/

    //----------------------------------------------------------------------
    /**
       Validates the discounts for each item by comparing the item amount
       to the item discount.
       <p>
       @param  transaction The retail transaction to validate
       @return int 0, 1, or 2
       @deprecated as of release 5.1 - use oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc.hasTransactionDiscounts(),
       oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc.hasDiscountableItems()
       and oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc.itemsDiscountExceedsSellingPrice() instead
    ***/
    //----------------------------------------------------------------------
    public int validateDiscounts(RetailTransactionIfc transaction)
    {
        // Have to differentiate between invalid trans discount scenarios so that the
        // appropriate error message can be displayed.  Internal codes:
        // 0 = discounts okay
        // 1 = discount elig items exist on the trans, but discount exceeds item amount
        // 2 = there are no discount elig items on the trans
        int discountApplicable = 0;

        // traverse the line item vector
        int discountableItems = 0;
        Enumeration<AbstractTransactionLineItemIfc> e = transaction.getLineItemsVector().elements();
        while (e.hasMoreElements())
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) e.nextElement();

            if (DiscountUtility.isAnyDiscountEligible(lineItem))
            {
                ++discountableItems;
            }

            if (lineItem.getItemQuantityDecimal().signum() > 0
                && lineItem.getExtendedSellingPrice().compareTo(lineItem.getItemDiscountTotal())
                == CurrencyIfc.LESS_THAN)
            {   // Sale Item with discount amount greater than item amount
                discountApplicable = 1;
                break;
            }
            else if (lineItem.getItemQuantityDecimal().signum() < 0
                     && lineItem.getItemDiscountTotal().compareTo(lineItem.getExtendedSellingPrice())
                     == CurrencyIfc.LESS_THAN)
            {   // Return Item with discount amount greater than item amount
                discountApplicable = 1;
                break;
            }
        }

        TransactionDiscountStrategyIfc[] discounts =
            transaction.getTransactionDiscounts();
        if (discountableItems == 0 && discounts != null && discounts.length > 0)
        {  // there are no discount elig items on the transaction
            for (int i = 0; i < discounts.length; i++)
            {
                // if a discount by percentage strategy (but not customer) found,
                // issue error and force exit from loop
                if ( discounts[i] instanceof TransactionDiscountByPercentageIfc ||
                     discounts[i] instanceof TransactionDiscountByAmountIfc )
                {
                    discountApplicable = 2;
                    i = discounts.length;
                }
            }
        }

        return(discountApplicable);
    }

    //----------------------------------------------------------------------
    /**
       Validates the tax for each item by comparing the item amount
       to the item tax.
       <p>
       @param  transaction The retail transaction to validate
       @return True if taxes are valid, false otherwise
       @deprecated as of release 5.1 - use
       oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc.itemsTaxExceedsSellingPrice() instead
    **/
    //----------------------------------------------------------------------
    public boolean isValidTax(RetailTransactionIfc transaction)
    {
        boolean valid = true;

        Enumeration<AbstractTransactionLineItemIfc> e = transaction.getLineItemsVector().elements();
        while (e.hasMoreElements())
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)e.nextElement();
            if (lineItem.getItemQuantityDecimal().signum() > 0
                && lineItem.getExtendedSellingPrice().compareTo(lineItem.getItemTaxAmount())
                == CurrencyIfc.LESS_THAN)
            {   // Sale Item with tax amount greater than item amount
                valid = false;
                break;
            }
            else if (lineItem.getItemQuantityDecimal().signum() < 0
                     && lineItem.getItemTaxAmount().compareTo(lineItem.getExtendedSellingPrice())
                     == CurrencyIfc.LESS_THAN)
            {   // Return Item with tax amount greater than item amount
                valid = false;
                break;
            }
        }

        return(valid);
    }

    /**
     *  Generate the detail tax amount info
     *
     *  @param srli Line item to print tax for
     *  @return String tax info for ejournal
     */
    public String getTaxForItems(TransactionTotalsIfc totals)
    {
        StringBuffer taxForItems = new StringBuffer();
        Locale receiptLocale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);

        TaxInformationContainerIfc taxContainer = totals.getTaxInformationContainer();
        TaxInformationIfc taxInformationArray[] = taxContainer.getTaxInformation();
        HashMap<String, TaxInformationIfc> taxData = new HashMap<String, TaxInformationIfc>(0);
        for(int i=0; i<taxInformationArray.length; i++)
        {
            String taxIdLabel = taxInformationArray[i].getTaxRuleName();
            if(taxData.get(taxIdLabel) == null)
            {
                taxData.put(taxIdLabel, (TaxInformationIfc) taxInformationArray[i].clone());
            }
            else
            {
                TaxInformationIfc savedInfo = taxData.get(taxIdLabel);
                savedInfo.add(taxInformationArray[i]);
            }
        }
        // Sort the Tax Info by label
        String[] taxLabels = LocaleUtilities.sort(taxData.keySet().toArray(new String[0]), receiptLocale);
        for(int i=0; i<taxLabels.length; i++)
        {
            TaxInformationIfc taxInformation= taxData.get(taxLabels[i]);

            if(taxInformation.getTaxAmount().signum() != CurrencyIfc.ZERO)
            {
                StringBuffer amount = new StringBuffer(roundCurrency(taxInformation.getTaxableAmount()).toGroupFormattedString());
                StringBuffer tax = new StringBuffer(roundCurrency(taxInformation.getTaxAmount()).toGroupFormattedString());
                String taxLabel = taxLabels[i];
                if(taxLabel.length() > 5)
                {
                    taxLabel = taxLabel.substring(0, 5);
                }
                // Make sure tax label exists
                StringBuffer taxableAmount = null;
                StringBuffer taxBuffer = null;
                if(taxLabel.length() > 0)
                {
                    taxableAmount = blockLine(new StringBuffer("   " + taxLabel + " Taxable Amount"), amount);
                    taxBuffer = blockLine(new StringBuffer("   " + taxLabel + " Tax"), tax);
                }
                else // If not just use generic label
                {
                    taxableAmount = blockLine(new StringBuffer("   " + " Taxable Amount"), amount);
                    taxBuffer = blockLine(new StringBuffer("   " + " Tax"), tax);
                }
                taxForItems.append(taxableAmount).append("\n");
                taxForItems.append(taxBuffer).append("\n");
            }
        }

        if ( taxForItems.length() != 0 )
        {
            taxForItems.append("\n");
        }
        return taxForItems.toString();
    }

    //---------------------------------------------------------------------
    /**
     * Round the 5 decimal digit currency value to 2 decimal digit precision.
     * <P>
     *
     * @param input
     *            currency value to be rounded
     * @return Rounded currency value
     */
    //---------------------------------------------------------------------
    protected CurrencyIfc roundCurrency(CurrencyIfc input)
    {
        // Adjust the precision Need to do rounding in two steps, starting from
        // the 3rd decimal digit first, then round again at the 2nd decimal
        // digit.
        BigDecimal bd = new BigDecimal(input.getStringValue());
        BigDecimal bOne = new BigDecimal(1);
        bd = bd.divide(bOne, 3, BigDecimal.ROUND_HALF_UP);
        CurrencyIfc roundedCurrency = DomainGateway.getBaseCurrencyInstance(bd);

        BigDecimal bd2 = new BigDecimal(roundedCurrency.getStringValue());
        bd2 = bd2.divide(bOne, TransactionTotalsIfc.UI_PRINT_TAX_DISPLAY_SCALE, BigDecimal.ROUND_HALF_UP);

        roundedCurrency = DomainGateway.getBaseCurrencyInstance(bd2);
        return (roundedCurrency);
    }


    //---------------------------------------------------------------------
    /**
     Builds a line with the left and right strings separated by spaces.
     @param left StringBuffer
     @param right StringBuffer
     @return StrinBuffer formatted line
     */
    //---------------------------------------------------------------------
    protected StringBuffer blockLine(StringBuffer left, StringBuffer right)
    {
        int lineLength = 35;
        StringBuffer s = new StringBuffer(lineLength);
        s.append(left);

        // pad with spaces
        for (int i = left.length(); i < lineLength - right.length(); i++)
        {
            s.append(" ");
        }

        s.append(right);

        return s;
    }
}
