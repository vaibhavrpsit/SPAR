/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/discount/PercentEnteredAisle.java /main/16 2011/12/01 14:40:31 cgreene Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 11/30/11 - XbranchMerge cgreene_fix_subtotals_decimal_error from
 *                      rgbustores_13.4x_generic_branch
 *    cgreen 11/28/11 - correctly initiale BigDecimals to constant values
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    acadar 04/12/10 - use default locale for display of currency
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    dwfung 12/17/09 - fix calculation in method isValidDiscount
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    acadar 10/30/08 - localization of reason codes for manual transaction
 *                      discounts
 *
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         3/13/2008 11:56:24 PM  Vikram Gopinath
           Removed code to ROUND_HALF_UP.
      4    360Commerce 1.3         1/22/2006 11:45:13 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:03 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:02 PM  Robert Pearse
     $
     Revision 1.7.4.2  2004/11/13 13:01:30  cdb
     @scr 7693 Updated to make app more robust when DB is missing required data.

     Revision 1.7.4.1  2004/11/12 17:27:14  cdb
     @scr 7693 Updated to make app more robust DB is missing required data.

     Revision 1.7  2004/03/22 19:27:00  cdb
     @scr 3588 Updating javadoc comments

     Revision 1.6  2004/02/24 16:21:31  cdb
     @scr 0 Remove Deprecation warnings. Cleaned code.

     Revision 1.5  2004/02/24 00:50:40  cdb
     @scr 3588 Provided for Transaction Discounts to remove
     previously existing discounts if they Only One Discount is allowed.

     Revision 1.4  2004/02/23 21:43:15  cdb
     @scr 3588 Added Discount Already Applied dialog that is
     sensitive to if it's already been displayed because only
     one discount is allowed per transaction.

     Revision 1.3  2004/02/12 16:51:10  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:48  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.6   Feb 02 2004 14:22:38   cdb
 * Changed location of long value problems with entered percent.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.5   Feb 02 2004 11:57:02   cdb
 * Refactored and added Check Digit functionality.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.4   Dec 30 2003 20:27:18   cdb
 * Modified to show dialog if invalid reason code is entered.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.3   Dec 23 2003 17:43:24   cdb
 * Pending requirements finalization, removing functionality to prompt for user ID when a given Reason Code is selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.2   Dec 10 2003 10:36:22   rrn
 * Changed "Removed" to "Deleted"
 * Resolution for 3506: Journal format changes
 *
 *    Rev 1.1   Oct 17 2003 10:50:16   bwf
 * Check if employee discount selected and remove usused imports.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 *
 *    Rev 1.0   Aug 29 2003 16:02:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jul 15 2003 14:37:30   sfl
 * SCR 3170
 * Resolution for POS SCR-3170: Insert cleanup in pe_dsc
 *
 *    Rev 1.4   Jul 11 2003 17:13:16   sfl
 * Have format control on percentage rate value because IBM BigDecimal could generate a long value.
 * Resolution for POS SCR-3114: Trans Discount % precision incorrect during insertion
 *
 *    Rev 1.3   Mar 26 2003 15:33:12   RSachdeva
 * Removed use of CodeEntry getCode() method
 * Resolution for POS SCR-2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.2   Aug 08 2002 13:32:48   jriggins
 * Removed constructed dialog for the one referenced by DialogSpec.InvalidTransactionDiscountPercent in dialogText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   24 May 2002 18:54:38   vxs
 * Removed unncessary concatenations from log statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:16:32   msg
 * Initial revision.
 *
 *    Rev 1.1   28 Mar 2002 10:53:32   pjf
 * Modified to retrieve proper dialog text.
 * Resolution for POS SCR-1568: Return by Receipt with a PCD item gets the 'Item Not Eligable" dialog msg.
 *
 *    Rev 1.0   Mar 18 2002 11:38:50   msg
 * Initial revision.
 *
 *    Rev 1.4   13 Mar 2002 17:07:36   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.3   Mar 10 2002 18:00:32   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   07 Feb 2002 18:56:00   pjf
 * Corrected resourceID used for invalid discount dialog.
 * Resolution for POS SCR-1041: Changing the MaximumTransactionDiscountAmountPercent then entering a trans disc % greater than parameter returns wrong error
 *
 *    Rev 1.1   Feb 05 2002 16:42:52   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:30:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:34   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifytransaction.discount;

import java.math.BigDecimal;
import java.math.BigInteger;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
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
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

/**
 * This aisle will validate the Percentage amount entered is valid.
 * 
 * @version $Revision: /main/16 $
 */
public class PercentEnteredAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 4000393262299591639L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * constant for parameter name
     */
    public static final String MAX_DISC_PCT = "MaximumTransactionDiscountAmountPercent";

    /**
     * resource id for invalid transaction discount dialog
     */
    protected static final String INVALID_TRANSACTION_DISCOUNT_DIALOG = "InvalidTransactionDiscountPercent";

    /**
     * constant for error dialog screen
     */
    public static final String INVALID_REASON_CODE = "InvalidReasonCode";

    /**
     * Stores the percent and reason code.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Get access to common elements
        ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo) bus.getCargo();
        String dialog = null;
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Retrieve data from UI model
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DecimalWithReasonBeanModel beanModel = (DecimalWithReasonBeanModel) uiManager.getModel(POSUIManagerIfc.TRANS_DISC_PCNT);
        BigDecimal percent = beanModel.getValue();
        // retrieve the amount discount
        if (percent.toString().length() >= 5)
        {
            BigDecimal scaleOne = new BigDecimal(1);
            percent = percent.divide(scaleOne, 2);
        }
        String reason = beanModel.getSelectedReasonKey();

        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        CodeListIfc rcl = cargo.getLocalizedDiscountPercentReasonCodes();
        // Validate the Reason Code ID Check Digit, Valid Reason Code exists
        CodeEntryIfc reasonEntry = null;
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



        if (reasonEntry == null ||
            !isValidCheckDigit(utility, reasonEntry.getCode(), bus.getServiceName()))
        {
            dialog = INVALID_REASON_CODE;
        }
        else
        {
            if (isValidDiscount(bus, percent))
            {
               // retrieve the reason string
                TransactionDiscountByPercentageIfc percentDiscount = createDiscountStrategy(localizedCode, percent);

                // reference this discount in the cargo
                cargo.setDiscount(percentDiscount);
                cargo.setDoDiscount(true);
            }
            else
            {
                dialog = INVALID_TRANSACTION_DISCOUNT_DIALOG;
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
        else if (dialog.equals(INVALID_TRANSACTION_DISCOUNT_DIALOG))
        {
            ParameterManagerIfc pm =
                (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            // get maximum disc % allowed parameter
            BigInteger maxTransDiscPct = getMaximumDiscountPercent(pm);

            String[] msg = { LocaleUtilities.formatNumber(maxTransDiscPct,
                                                          LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE))};

            showInvalidTransactionDiscountDiscountDialog(uiManager, msg);
        }
        else
        {
            logger.error(
                        "Unexpected dialog requested in AmountEnteredAisle: " + dialog);
        }
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
    	BigDecimal percentEntered = percent.movePointRight(2);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        BigDecimal maxTransDiscPct = new BigDecimal(getMaximumDiscountPercent(pm));
        return (percentEntered.compareTo(maxTransDiscPct) < 1);
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
     * Displays the invalid discount error screen.
     * 
     * @param uiManager The POSUIManager
     * @param msg The string array representing the arguments for the dialog
     */
    protected void showInvalidTransactionDiscountDiscountDialog(POSUIManagerIfc uiManager, String[] msg)
    {
        // display the invalid discount error screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(INVALID_TRANSACTION_DISCOUNT_DIALOG);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(msg);

        // display dialog
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
    }

    /**
     * Creates discount strategy.
     * 
     * @param LocalizedCodeIfc reason code
     * @param percent The transaction discount percentage
     * @return ItemDiscountByAmountIfc sgy
     */
    protected TransactionDiscountByPercentageIfc createDiscountStrategy(LocalizedCodeIfc reasonCode, BigDecimal percent)
    {


        TransactionDiscountByPercentageIfc percentDiscount = DomainGateway.getFactory().getTransactionDiscountByPercentageInstance();
        percentDiscount.setDiscountRate(percent);
        percentDiscount.setReason(reasonCode);


        return percentDiscount;
    }

    /**
     * Returns a BigInteger, the maximum discount % allowed from the parameter
     * file.
     * 
     * @param pm ParameterManagerIfc reference
     * @return maximum discount percent allowed as BigInteger
     */
    private BigInteger getMaximumDiscountPercent(ParameterManagerIfc pm)
    {
        BigInteger maximum = BigInteger.valueOf(100);  // default
        try
        {
            String s = pm.getStringValue(MAX_DISC_PCT);
            s.trim();
            maximum = new BigInteger(s);
            if (logger.isInfoEnabled()) logger.info(
                         "Parameter read: " + MAX_DISC_PCT + "=[" + maximum + "]");
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }

        return(maximum);
    }

    /**
     * Check digit validation.
     * 
     * @param utility utility manager
     * @param reasonCodeID the reason code ID that needs to be checked
     * @param serviceName service name
     * @return boolean return true if valid, otherwise return false
     */
    public static boolean isValidCheckDigit(UtilityManagerIfc utility,
                                            String reasonCodeID,
                                            String serviceName)
    {
        boolean isValid = false;
        if ( !utility.validateCheckDigit(
                        CheckDigitUtility.CHECK_DIGIT_FUNCTION_REASON_CODE,
                        reasonCodeID))
        {
            // If check digit is not configured for reason code, the check digit function will always return true
            if (logger.isInfoEnabled()) logger.info(
                        "Invalid number received. check digit is invalid. Prompting user to re-enter the information ...");
        }
        else
        {
            isValid = true;
        }
        return isValid;
    }
}
