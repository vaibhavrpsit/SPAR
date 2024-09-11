/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/ModifyTransactionDiscountPercentReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abhayg 09/29/10 - FIX FOR EJOURNAL SHOWS WRONG POS DISCOUNT VALUE
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                      from st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/27/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    nganes 04/14/09 - Removed reason code from employee discount employee id
 *                      ej
 *    vcheng 01/27/09 - ej defect fixes
 *    vcheng 12/22/08 - EJ defect fixes
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         1/22/2006 11:45:15 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse
 * $
 * Revision 1.14  2004/07/06 17:11:34  cdb
 * @scr 5337 More cleanup.
 *
 * Revision 1.13  2004/07/06 16:50:06  cdb
 * @scr 5337 General cleanup.
 *
 * Revision 1.12  2004/03/29 20:29:47  awilliam
 * @scr 4005 % trans Discount and amount discount journal entries do not have same format
 * Revision 1.11 2004/03/22 18:35:05 cdb @scr 3588
 * Corrected some javadoc
 *
 * Revision 1.10 2004/03/22 03:49:27 cdb @scr 3588 Code Review Updates
 *
 * Revision 1.9 2004/03/16 18:30:45 cdb @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.8 2004/03/04 23:58:15 cdb @scr 3588 Updated journaling of Employee Transaction discounts.
 *
 * Revision 1.7 2004/03/03 21:03:45 cdb @scr 3588 Added employee transaction discount service.
 *
 * Revision 1.6 2004/02/26 23:59:07 cdb @scr 3588 Added journaling of transaciton reason code ID. Cleaned some code.
 *
 * Revision 1.5 2004/02/24 00:50:40 cdb @scr 3588 Provided for Transaction Discounts to remove previously existing
 * discounts if they Only One Discount is allowed.
 *
 * Revision 1.4 2004/02/13 22:24:29 cdb @scr 3588 Added dialog to indicate when discount will reduce some prices below
 * zero but not others.
 *
 * Revision 1.3 2004/02/12 16:51:34 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:52:05 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:19 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.2 Dec 16 2003 15:44:42 cdb Removed assumption that transaction exists in return shuttle. It won't if the user
 * Escapes during transaction discount before any line items are added. Resolution for 3588: Discounts/MUPS - Gap
 * Rollback
 *
 * Rev 1.1 Oct 17 2003 10:25:00 bwf Add employeeDiscountID and remove unused imports. Resolution for 3412: Feature
 * Enhancement: Employee Discount
 *
 * Rev 1.0 Aug 29 2003 16:05:18 CSchellenger Initial revision.
 *
 * Rev 1.1 Jul 11 2003 17:14:18 sfl Have format control on percentage rate value because IBM BigDecimal could generate
 * a long value. Resolution for POS SCR-3114: Trans Discount % precision incorrect during insertion
 *
 * Rev 1.0 02 May 2002 17:39:12 jbp Initial revision. Resolution for POS SCR-1626: Pricing Feature
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.pricing;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.discount.CustomerDiscountByPercentage;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageStrategy;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

/**
 * Shuttles data from ModifyTransacationDiscountPercent service to
 * ModifyTransaction service.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ModifyTransactionDiscountPercentReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 7514293791821041490L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(ModifyTransactionDiscountPercentReturnShuttle.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** transaction * */
    protected RetailTransactionIfc transaction = null;

    /** flag to see if there was a discount done in the service * */
    protected boolean doDiscount = false;

    /**
     * flag to see if a discount has to be cleared
     * 
     * @deprecated as of release 7.0. No replacement
     */
    protected boolean clearDiscount = false;

    /** The new discount amount */
    protected TransactionDiscountStrategyIfc discountPercent;

    /** The old discount amount */
    protected TransactionDiscountStrategyIfc oldDiscountPercent;

    /**
     * employee discount id
     */
    protected String employeeDiscountID = null;

    /**
     * Loads data from ModifyTransactionDiscountPercent service.
     *
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);
        // retrieve the child cargo
        ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo) bus.getCargo();
        // get all the cargo from the child service - all decisions will be in the unload
        doDiscount = cargo.getDoDiscount();
        discountPercent = cargo.getDiscount();
        oldDiscountPercent = cargo.getOldDiscount();
        transaction = cargo.getTransaction();
        employeeDiscountID = cargo.getEmployeeDiscountID();
    }

    /**
     * Unloads data to ModifyTransaction service.
     *
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);
        // retrieve the parent cargo
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        boolean onlyOneDiscount =
            cargo.isOnlyOneDiscountAllowed((ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE), logger);

        // Current discount. Might be null if user leaves % field blank
        if (discountPercent != null)
        {
            BigDecimal discountRate = discountPercent.getDiscountRate();
            if (discountRate.toString().length() > 5)
            {
                BigDecimal scaleOne = new BigDecimal(1);
                discountRate = discountRate.divide(scaleOne, 2);
            }
            discountRate = discountRate.movePointRight(2);
        }

        Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
        // Old discount
        String oldDiscountPercentStr = "";
        if (oldDiscountPercent != null)
        {
            BigDecimal discountRate = oldDiscountPercent.getDiscountRate();
            if (discountRate.toString().length() > 5)
            {
                BigDecimal scaleOne = new BigDecimal(1);
                discountRate = discountRate.divide(scaleOne, 2);
            }
            discountRate = discountRate.movePointRight(2);
            oldDiscountPercentStr = discountRate.toString();
        }
        // set transaction
        if (transaction != null)
        {
            cargo.setTransaction(transaction);
        }
        // if a new discount apply it to the transaction
        if (doDiscount == true)
        {
            //Get journal manager
            JournalManagerIfc journalManager = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
            SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc) cargo.getTransaction();

            TransactionDiscountStrategyIfc[] discounts = null;
            if (onlyOneDiscount)
            {
                cargo.removeAllManualDiscounts(null, journalManager);
                if (trans != null)
                {
                    trans.addTransactionDiscount(discountPercent);
                }
            }
            else
            {
                StringBuilder message = new StringBuilder();
                if (trans != null)
                {
                    discounts = getDiscounts(trans);
                }
                int numDiscounts = 0;
                if (discounts != null)
                {
                    numDiscounts = discounts.length;
                }
                // loop through discounts
                for (int i = 0; i < numDiscounts; i++)
                {
                    TransactionDiscountStrategyIfc discount = discounts[i];
                    if (discount instanceof TransactionDiscountByPercentageStrategy
                        && !(discount instanceof CustomerDiscountByPercentage))
                    {


                        String transDisc=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PRICING_TRANS_DISCOUNT_LABEL,null);

                        Object dataArgs[]={oldDiscountPercentStr};

                        String discPerc=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PRICING_DISCOUNT_REMOVED, dataArgs);

                        message.append(Util.EOL)
                               .append(transDisc)
                               .append(Util.EOL)
                               .append(discPerc)
                               .append(Util.EOL);

                        if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
                        {
                            Object empDataArgs[] = {discount.getDiscountEmployeeID()};
                            String empId=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EMPLOYEE_ID, empDataArgs);

                            message.append(empId);

                        }

                        else if (oldDiscountPercent.getReason() != null)
                        {
                            // This needs to be modified
                            Object reasonCodeDataArgs[]={oldDiscountPercent.getReason().getCode(),""};
                            String reasonCode=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISCOUNT_REASON_CODE, reasonCodeDataArgs);

                            message.append(reasonCode);

                        }

                        if (oldDiscountPercent.getReason() != null)
                        {
                            String reasonCodeText = oldDiscountPercent.getReason().getText(journalLocale);
                            if(reasonCodeText!=null)
                            {
                                message.append(" - ").append(reasonCodeText);
                            }
                        }


                    }
                } // end loop thru discounts
                if (message.length() > 0 && trans != null)
                {
                    journalManager.journal(trans.getCashier().getEmployeeID(), trans.getTransactionID(), message.toString());
                }
            }

            // journal new transaction discount percentage
            String strResult = discountPercent.toJournalString(journalLocale);
            if (trans != null)
            {
                journalManager.journal(trans.getCashier().getEmployeeID(), trans.getTransactionID(), strResult);
            }
            if (trans != null)
            {
                // clear and add discounts
                clearDiscounts(trans);
                trans.addTransactionDiscount(discountPercent);
            }
        }
        if (employeeDiscountID != null)
        {
            cargo.setEmployeeDiscountID(employeeDiscountID);
        }
    }

    /**
     * Gets Manual Discounts by Percentage from transaction.
     *
     * @param transaction SaleReturnTransaction with potential discounts
     * @return An array of transaction discount strategies
     */
    public TransactionDiscountStrategyIfc[] getDiscounts(SaleReturnTransactionIfc transaction)
    {
        TransactionDiscountStrategyIfc[] discountArray =
            transaction.getTransactionDiscounts(
                DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
                DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
        return discountArray;
    }

    /**
     * Clears Manual Discounts by Percentage from transaction.
     *
     * @param transaction SaleReturnTransaction with potential discounts
     */
    public void clearDiscounts(SaleReturnTransactionIfc transaction)
    {
        transaction.clearTransactionDiscounts(
            DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
            DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
    }
}