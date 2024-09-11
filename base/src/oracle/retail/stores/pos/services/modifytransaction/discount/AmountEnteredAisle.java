/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/discount/AmountEnteredAisle.java /main/23 2013/10/07 19:46:17 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  10/07/13 - use right dialog code for mpos application.
 *    cgreene   11/30/11 - XbranchMerge cgreene_fix_subtotals_decimal_error
 *                         from rgbustores_13.4x_generic_branch
 *    cgreene   11/28/11 - correctly initiale BigDecimals to constant values
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    abhayg    09/29/10 - FIX FOR EJOURNAL SHOWS WRONG POS DISCOUNT VALUE
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/12/10 - use default locale for display of currency
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    atirkey   01/28/09 - forward porting bug#7828237
 *    sswamygo  11/10/08 - Forward Porting Bug#7298185-MAXIMUM TRNSN DISCOUNT
 *                         PARAMETER SET TO ZERO, DISCOUNT OF 1 OR 2 IS
 *                         APPLIED.
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    10/30/08 - localization of reason codes for manual transaction
 *                         discounts
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 *    deghosh   10/29/08 - EJI18n_changes_ExtendyourStore
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
     $Log:
      8    360Commerce 1.7         4/11/2008 10:06:37 AM  Maisa De Camargo CR
           31288 - Fixed the the order to display of the Reason Code Dialog.
      7    360Commerce 1.6         11/15/2007 11:11:52 AM Christian Greene
           Added check for valid transaction amount to prevent NPE when
           dicounting txn with no items.
      6    360Commerce 1.5         5/21/2007 9:16:21 AM   Anda D. Cadar   EJ
           changes
      5    360Commerce 1.4         4/25/2007 8:52:23 AM   Anda D. Cadar   I18N
           merge

      4    360Commerce 1.3         1/22/2006 11:45:13 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:38 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:28 PM  Robert Pearse
     $
     Revision 1.8.4.2  2004/11/13 13:01:31  cdb
     @scr 7693 Updated to make app more robust when DB is missing required data.

     Revision 1.8.4.1  2004/11/12 17:27:14  cdb
     @scr 7693 Updated to make app more robust DB is missing required data.

     Revision 1.8  2004/03/22 19:27:00  cdb
     @scr 3588 Updating javadoc comments

     Revision 1.7  2004/03/22 03:49:28  cdb
     @scr 3588 Code Review Updates

     Revision 1.6  2004/03/10 22:47:07  cdb
     @scr 3588 Updated so that maximum discount isn't verified
     for transaction discounts by amount.

     Revision 1.5  2004/02/24 16:21:31  cdb
     @scr 0 Remove Deprecation warnings. Cleaned code.

     Revision 1.4  2004/02/24 00:50:40  cdb
     @scr 3588 Provided for Transaction Discounts to remove
     previously existing discounts if they Only One Discount is allowed.

     Revision 1.3  2004/02/12 16:51:10  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:48  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.5   Feb 02 2004 11:58:02   cdb
 * Replaced string literal with constant for invalid reason code ID dialog.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.4   Jan 30 2004 18:26:22   cdb
 * Refactored and added check digit validation.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.3   Jan 05 2004 13:18:12   cdb
 * Modified to use editable combo box for reason code entry.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.2   Dec 10 2003 10:34:32   rrn
 * Changed "Removed" to "Deleted"
 * Resolution for 3506: Journal format changes
 *
 *    Rev 1.1   Oct 17 2003 10:49:58   bwf
 * Check if employee discount selected and remove unused imports.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 *
 *    Rev 1.0   Aug 29 2003 16:02:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jun 03 2003 10:28:24   bwf
 * Make sure there is an item discount eligible amount to avoid crash.
 * Resolution for 2522: Changing the transaction discount from an amount of 9999999.99 to 100% results in a discount of 9999999.99 plus the price of the item.
 *
 *    Rev 1.2   Jun 02 2003 15:36:14   bwf
 * Added validation.
 * Resolution for 2522: Changing the transaction discount from an amount of 9999999.99 to 100% results in a discount of 9999999.99 plus the price of the item.
 *
 *    Rev 1.1   Mar 26 2003 15:28:26   RSachdeva
 * Removed use of CodeEntry getCode() method
 * Resolution for POS SCR-2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.0   Apr 29 2002 15:16:20   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:38:42   msg
 * Initial revision.
 *
 *    Rev 1.2   12 Mar 2002 16:53:00   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.1   Feb 05 2002 16:42:52   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:30:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.discount;

import java.math.BigDecimal;
import java.math.BigInteger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

/**
 * This aisle will validate the amount entered is valid.
 *
 * @version $Revision: /main/23 $
 */
public class AmountEnteredAisle extends PosLaneActionAdapter
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/23 $";

    /**
     * length of available space for discount value
     */
    public static final int AVAIL_DISCOUNT_LENGTH = 23;

    /**
     * constant for error dialog screen
     */
    public static final String INVALID_REASON_CODE = "InvalidReasonCode";

    /**
     * Stores the amount and reason code.
     *
     * @param bus Service Bus
     */
    public void traverse(BusIfc bus)
    {
        // Get access to common elements
        ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo)bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        // Retrieve data from UI model
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DecimalWithReasonBeanModel beanModel = (DecimalWithReasonBeanModel)uiManager
                .getModel(POSUIManagerIfc.TRANS_DISC_AMT);
        BigDecimal discountAmount = beanModel.getValue();
        String reason = beanModel.getSelectedReasonKey();
        String dialog = null;
        BigDecimal percent;

        // need to handle if there are not items in the transaction you can
        // still set the discount amount for the transaction.
        CurrencyIfc itemTotal = cargo.getItemTotal();
        if (itemTotal != null && itemTotal.signum() != CurrencyIfc.ZERO)
        {
            BigDecimal itemTotalAmount = new BigDecimal(cargo.getItemTotal().toString());
       // 	percent = new BigDecimal(discountAmount.doubleValue() /
       // 			itemTotalAmount.doubleValue());
        	percent = discountAmount.divide(itemTotalAmount, 4);
        }
        else
        {
            percent = BigDecimalConstants.ZERO_PERCENT;
        }

        // Validate the Reason Code ID Check Digit, Valid Reason Code exists

        CodeListIfc rcl = cargo.getLocalizedDiscountAmountReasonCodes();
        // Validate the Reason Code ID Check Digit, Valid Reason Code exists
        CodeEntryIfc reasonEntry = null;
        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        if (rcl != null)
        {
            reasonEntry = rcl.findListEntryByCode (reason);
            localizedCode.setCode(reason);
            localizedCode.setText(reasonEntry.getLocalizedText());
        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }

        if (reasonEntry == null || !isValidCheckDigit(utility, reasonEntry.getCode(), bus.getServiceName()))
        {
            dialog = INVALID_REASON_CODE;
        }
        else
        {
            if (isValidDiscount(bus, percent))
            {
                CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance(String.valueOf(discountAmount));

                TransactionDiscountByAmountIfc amountDiscount = createDiscountStrategy(localizedCode, amount);


                // reference this discount in the cargo
                cargo.setDiscount(amountDiscount);
                cargo.setDoDiscount(true);

            }
            else
            {
                dialog = ModifyTransactionDiscountCargo.INVALID_TRANSACTION_DISCOUNT_AMOUNT_DIALOG;
            }
        }

        if (dialog == null)
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else if (dialog.equals(INVALID_REASON_CODE))
        {
            showInvalidReasonCodeDialog(uiManager);
        }
        else if (dialog.equals(ModifyTransactionDiscountCargo.INVALID_TRANSACTION_DISCOUNT_AMOUNT_DIALOG))
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            // get maximum disc % allowed parameter
            BigInteger maxTransDiscPct = getMaximumDiscountPercent(pm, bus.getServiceName());

            String[] msg = { LocaleUtilities.formatNumber(maxTransDiscPct, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)) };

            showInvalidTransactionDiscountDiscountDialog(uiManager, msg);
        }
        else
        {
            logger.error("Unexpected dialog requested in AmountEnteredAisle: " + dialog);
        }
    }

    /**
     * Displays the invalid discount error screen.
     *
     * @param uiManager The POSUIManager
     */
    protected void showInvalidReasonCodeDialog(POSUIManagerIfc uiManager)
    {
        // display the invalid discount error screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(INVALID_REASON_CODE);
        dialogModel.setType(DialogScreensIfc.ERROR);

        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Creates discount strategy.
     *
     * @param LocalizedCodeIfc the reason code
     * @param discount Discount amount
     * @return The TransactionDiscountByAmount strategy
    */
    protected TransactionDiscountByAmountIfc createDiscountStrategy(LocalizedCodeIfc reason, CurrencyIfc discount)
    {

        TransactionDiscountByAmountIfc amountDiscount = DomainGateway.getFactory()
                .getTransactionDiscountByAmountInstance();
        amountDiscount.setDiscountAmount(discount);
        amountDiscount.setReason(reason);
        return amountDiscount;
    }

    /**
     * Check digit validation.
     *
     * @param utility utility manager
     * @param reasonCodeID the reason code ID that needs to be checked
     * @param serviceName service name
     * @return boolean return true if valid, otherwise return false
     */
    public static boolean isValidCheckDigit(UtilityManagerIfc utility, String reasonCodeID, String serviceName)
    {
        boolean isValid = false;
        if (!utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_REASON_CODE, reasonCodeID))
        {
            // If check digit is not configured for reason code, the check digit
            // function will always return true
            if (logger.isInfoEnabled())
                logger
                        .info("Invalid number received. check digit is invalid. Prompting user to re-enter the information ...");
        }
        else
        {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Returns a BigInteger, the maximum discount % allowed from the parameter
     * file.
     *
     * @param pm ParameterManagerIfc reference
     * @param serviceName service name (for log)
     * @return maximum discount percent allowed as BigInteger
     */
    private BigInteger getMaximumDiscountPercent(ParameterManagerIfc pm, String serviceName)
    {
        BigInteger maximum = BigInteger.valueOf(100); // default
        try
        {
            String s = pm.getStringValue(ParameterConstantsIfc.DISCOUNT_MaximumTransactionDiscountAmountPercent);
            s.trim();
            maximum = new BigInteger(s);
            if (logger.isInfoEnabled())
                logger.info("Parameter read: " + ParameterConstantsIfc.DISCOUNT_MaximumTransactionDiscountAmountPercent 
                        + "=[" + maximum + "]");
        }
        catch (ParameterException e)
        {
            logger.error(e);
        }

        return (maximum);
    }

    /**
     * Validates the discount.
     *
     * @param bus The service bus
     * @param percent The percentage of the discount as a Big Decimal
     * @return boolean return true if valid
     */
    public boolean isValidDiscount(BusIfc bus, BigDecimal percent)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        // get maximum disc % allowed parameter
   //     BigInteger maxTransDiscPct = getMaximumDiscountPercent(pm, bus.getServiceName());
        BigDecimal maxTransDiscPct = new BigDecimal(getMaximumDiscountPercent(pm,
                bus.getServiceName()).toString());
        BigDecimal maxTransDiscPctInBigDec = new BigDecimal(maxTransDiscPct.doubleValue()/100.0);
        return (percent.compareTo(maxTransDiscPctInBigDec) < 1);
    }

    /**
     * Displays the invalid discount error screen.
     *
     * @param uiManager The POSUIManager
     * @param msg The string array representing the arguments for the dialog
     */
    protected void showInvalidTransactionDiscountDiscountDialog(POSUIManagerIfc uiManager, String[] msg)
    {
        // display the invalid discount error screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(ModifyTransactionDiscountCargo.INVALID_TRANSACTION_DISCOUNT_AMOUNT_DIALOG);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(msg);

        // display dialog
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
