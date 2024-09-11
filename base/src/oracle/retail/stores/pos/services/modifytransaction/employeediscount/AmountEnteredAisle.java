/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/employeediscount/AmountEnteredAisle.java /main/21 2013/12/10 16:54:06 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  12/10/13 - Check if the code list has any entries before using
 *                         to avoid exception when the list is empty
 *    abondala  10/07/13 - use right dialog code for mpos application.
 *    abhayg    09/29/10 - FIX FOR EJOURNAL SHOWS WRONG POS DISCOUNT VALUE
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       11/19/09 - make sure the item total is not zero when
 *                         calculating discount percent
 *    cgreene   03/20/09 - implement printing of Employee by making it a
 *                         separate lookup reason code like damage discount
 *    sswamygo  02/03/09 - Added logic to check if the discount amount entered
 *                         exceeds the Maximum Employee Transaction Discount Amount/Percent%
 *    sgu       01/14/09 - convert user entered currency amount into non locale
 *                         sensitive, decimal format before calling
 *                         CurrencyIfc.setStringValue
 *    acadar    10/30/08 - localization of reason codes for manual transaction
 *                         discounts
 *
 * ===========================================================================

 $Log:
 4    360Commerce 1.3         4/25/2007 8:52:22 AM   Anda D. Cadar   I18N
 merge

 3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:19:37 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:09:28 PM  Robert Pearse
 $
 Revision 1.4  2004/03/22 03:49:28  cdb
 @scr 3588 Code Review Updates

 Revision 1.3  2004/03/10 22:47:07  cdb
 @scr 3588 Updated so that maximum discount isn't verified
 for transaction discounts by amount.

 Revision 1.2  2004/03/03 23:00:12  cdb
 @scr 3588 Added tags for externalization. Added new
 Maximum Employee Transaction Discount parameter.

 Revision 1.1  2004/03/03 21:03:45  cdb
 @scr 3588 Added employee transaction discount service.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.employeediscount;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
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
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This aisle will validate the amount entered is valid.
 *
 * @version $Revision: /main/21 $
 */
public class AmountEnteredAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -7302763586563406589L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/21 $";

    /**
     * Stores the amount and reason code.
     *
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Get access to common elements
        ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo)bus.getCargo();
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel beanModel = (POSBaseBeanModel)uiManager
                .getModel(POSUIManagerIfc.ENTER_EMPLOYEE_AMOUNT_DISCOUNT);

        // Create the discount strategy
        CurrencyIfc discountAmt = DomainGateway.getBaseCurrencyInstance(beanModel.getPromptAndResponseModel()
                .getResponseText());
        BigDecimal discountAmtEntered = discountAmt.getDecimalValue();

        BigDecimal itemTotal = cargo.getItemTotal().getDecimalValue();
        BigDecimal discountPct = null;
        if(itemTotal != null && itemTotal.signum() != 0)
        {
        	//Convert the disocunt amount into the percentage of the transaction total
            discountPct = discountAmtEntered.divide(itemTotal, 4).multiply(new BigDecimal("100.00"));
        }
        else
        {
        	discountPct = BigDecimal.ZERO;
        }

        if (isValidDiscount(bus, discountPct))
        {
            // retrieve the reason string
            TransactionDiscountByAmountIfc amountDiscount = createDiscountStrategy(bus, cargo, discountAmt);

            // reference this discount in the cargo
            cargo.setDiscount(amountDiscount);
            cargo.setDoDiscount(true);

            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            // get maximum disc % allowed parameter
            BigDecimal maxTransDiscPct = getMaximumDiscountPercent(pm);

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
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        // get maximum disc % allowed parameter
        BigDecimal maxTransDiscPct = getMaximumDiscountPercent(pm);

        return (percent.compareTo(maxTransDiscPct) < 1);
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

    /**
     * Creates discount strategy.
     *
     * @param cargo The bus cargo
     * @param discount Discount amount
     * @param serviceName The name of the calling service
     * @return The TransactionDiscountByAmount strategy
     */
    protected TransactionDiscountByAmountIfc createDiscountStrategy(BusIfc bus,
            ModifyTransactionDiscountCargo cargo,
            CurrencyIfc discount)
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

        // create discount
        TransactionDiscountByAmountIfc amountDiscount = DomainGateway.getFactory()
                .getTransactionDiscountByAmountInstance();
        amountDiscount.setDiscountAmount(discount);
        amountDiscount.setReason(reasonCode);
        amountDiscount.setAssignmentBasis(DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
        amountDiscount.setDiscountEmployee(cargo.getEmployeeDiscountID());

        return amountDiscount;
    }

    /**
     * Returns a BigInteger, the maximum discount % allowed from the parameter
     * file.
     *
     * @param pm ParameterManagerIfc reference
     * @return maximum discount percent allowed as BigInteger
     */
    private BigDecimal getMaximumDiscountPercent(ParameterManagerIfc pm)
    {
        BigDecimal maximum = new BigDecimal("100"); // default
        try
        {
            String s = pm.getStringValue(ModifyTransactionDiscountCargo.MAX_EMPLOYEE_TRANS_DISC_PCT);
            s.trim();
            maximum = new BigDecimal(s);
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
