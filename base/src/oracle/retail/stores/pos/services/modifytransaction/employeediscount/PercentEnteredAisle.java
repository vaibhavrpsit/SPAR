/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/employeediscount/PercentEnteredAisle.java /main/19 2013/12/10 16:54:06 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  12/10/13 - Check if the code list has any entries before using
 *                         to avoid exception when the list is empty
 *    abondala  10/07/13 - use right dialog code for mpos application.
 *    cgreene   11/30/11 - XbranchMerge cgreene_fix_subtotals_decimal_error
 *                         from rgbustores_13.4x_generic_branch
 *    cgreene   11/28/11 - correctly initiale BigDecimals to constant values
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   03/20/09 - implement printing of Employee by making it a
 *                         separate lookup reason code like damage discount
 *    atirkey   01/28/09 - forward porting bug#7828237
 *    acadar    10/30/08 - localization of reason codes for manual transaction
 *                         discounts
 *
 * ===========================================================================
     $Log:
      7    360Commerce 1.6         3/13/2008 11:56:57 PM  Vikram Gopinath
           Removed code to ROUND_HALF_UP.
      6    360Commerce 1.5         2/13/2006 3:52:41 PM   Edward B. Thorne
           Merge from PercentEnteredAisle.java, Revision 1.3.1.0
      5    360Commerce 1.4         2/6/2006 5:20:34 PM    Rohit Sachdeva
           10513: Fixing unit tests in trunk
      4    360Commerce 1.3         1/22/2006 11:45:13 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:03 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:02 PM  Robert Pearse
     $
     Revision 1.4  2004/03/22 19:27:00  cdb
     @scr 3588 Updating javadoc comments

     Revision 1.3  2004/03/22 03:49:28  cdb
     @scr 3588 Code Review Updates

     Revision 1.2  2004/03/03 23:00:12  cdb
     @scr 3588 Added tags for externalization. Added new
     Maximum Employee Transaction Discount parameter.

     Revision 1.1  2004/03/03 21:03:45  cdb
     @scr 3588 Added employee transaction discount service.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.employeediscount;

import java.math.BigDecimal;
import java.math.BigInteger;

import oracle.retail.stores.common.utility.BigDecimalExt;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This aisle will validate the Percentage amount entered is valid.
 *
 * @version $Revision: /main/19 $
 */
public class PercentEnteredAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 3045214807926852762L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /**
     * Stores the percent and reason code.
     *
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Get access to common elements
        ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo)bus.getCargo();
        // Retrieve data from UI model
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel beanModel = (POSBaseBeanModel)uiManager
                .getModel(POSUIManagerIfc.ENTER_EMPLOYEE_PERCENT_DISCOUNT);

        // Get discount percent from bean model
        BigDecimal response = new BigDecimalExt(beanModel.getPromptAndResponseModel().getResponseText()).setScale(2);

        response = response.divide(new BigDecimal("100.0"));

        // Chop off the potential long values caused by BigDecimal.
        if (response.toString().length() > 5)
        {
            BigDecimal scaleOne = new BigDecimal(1);
            response = response.divide(scaleOne, 2);
        }

        if (isValidDiscount(bus, response))
        {
            // retrieve the reason string
            TransactionDiscountByPercentageIfc percentDiscount = createDiscountStrategy(bus, cargo, response);

            // reference this discount in the cargo
            cargo.setDiscount(percentDiscount);
            cargo.setDoDiscount(true);

            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            // get maximum disc % allowed parameter
            BigInteger maxTransDiscPct = getMaximumDiscountPercent(pm);

            String[] msg = { LocaleUtilities.formatNumber(maxTransDiscPct, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE))};

            showInvalidTransactionDiscountDiscountDialog(uiManager, msg);
        }
    }

    /**
     * Validates the discount.
     *
     * @param bus The service bus
     * @param percent The discount percentage entered
     * @return boolean return true if valid
     */
    public boolean isValidDiscount(BusIfc bus, BigDecimal percent)
    {
        // Convert the beanModel value to BigInteger, because the fractional
        // portion of BigDecimal throws off the comparison to the parameter.
        // BigInteger percentInt = percent.movePointRight(2).toBigInteger();
        BigDecimal percentEntered = percent.movePointRight(2);

        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        // get maximum disc % allowed parameter
        BigDecimal maxTransDiscPct = new BigDecimal(getMaximumDiscountPercent(pm).toString());

        return (percentEntered.compareTo(maxTransDiscPct) < 1);
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
        dialogModel.setResourceID(ModifyTransactionDiscountCargo.INVALID_TRANSACTION_DISCOUNT_PERCENT_DIALOG);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(msg);

        // display dialog
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Creates discount strategy.
     *
     * @param cargo Bus cargo
     * @param percent Discount percent
     * @return The newly created TransactionDiscountByPercentage strategy
     */
    protected TransactionDiscountByPercentageIfc createDiscountStrategy(BusIfc bus,
            ModifyTransactionDiscountCargo cargo,
            BigDecimal percent)
    {
        // get reason from db
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        CodeListIfc reasonCodes = utility.getReasonCodes(cargo.getOperator().getStoreID(),CodeConstantsIfc.CODE_LIST_EMPLOYEE_DISCOUNT_REASON_CODES);
        LocalizedCodeIfc reasonCode = DomainGateway.getFactory().getLocalizedCode();
        if (reasonCodes != null && reasonCodes.getNumberOfEntries() != 0)
        {
            String defaultReason = reasonCodes.getDefaultCodeString();
            CodeEntryIfc entry =  reasonCodes.findListEntryByCode(defaultReason);
            reasonCode.setCode(defaultReason);
            reasonCode.setText(entry.getLocalizedText());
        }
        else
        {
            reasonCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }
        reasonCode.setCodeName(DiscountRuleConstantsIfc.ASSIGNMENT_BASIS_DESCRIPTORS[DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE]);

        // build discount
        TransactionDiscountByPercentageIfc percentDiscount = DomainGateway.getFactory()
                .getTransactionDiscountByPercentageInstance();
        percentDiscount.setDiscountRate(percent);
        percentDiscount.setReason(reasonCode);
        percentDiscount.setAssignmentBasis(DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
        percentDiscount.setDiscountEmployee(cargo.getEmployeeDiscountID());

        return percentDiscount;
    }

    /**
     * Returns a BigInteger, the maximum discount % allowed from the parameter
     * file.
     * <P>
     *
     * @param pm ParameterManagerIfc reference
     * @return maximum discount percent allowed as BigInteger
     **/
    private BigInteger getMaximumDiscountPercent(ParameterManagerIfc pm)
    {
        BigInteger maximum = BigInteger.valueOf(100); // default
        try
        {
            String s = pm.getStringValue(ModifyTransactionDiscountCargo.MAX_EMPLOYEE_TRANS_DISC_PCT);
            s.trim();
            maximum = new BigInteger(s);
            if (logger.isInfoEnabled())
                logger.info("Parameter read: " + ModifyTransactionDiscountCargo.MAX_EMPLOYEE_TRANS_DISC_PCT + "=["
                        + maximum + "]");
        }
        catch (ParameterException e)
        {
            logger.error("" + Util.throwableToString(e) + "");
        }

        return (maximum);
    }
}
