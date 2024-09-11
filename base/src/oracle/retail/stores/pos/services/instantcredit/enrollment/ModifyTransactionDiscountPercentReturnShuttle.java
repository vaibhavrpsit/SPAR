/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/ModifyTransactionDiscountPercentReturnShuttle.java /main/19 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    abhayg 09/29/10 - FIX FOR EJOURNAL SHOWS WRONG POS DISCOUNT VALUE
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                      from st_rgbustores_techissueseatel_generic_branch
 *    jswan  03/24/10 - Fix an issue with instant credit enrollment discount
 *                      with special orders.
 *    abonda 01/03/10 - update header date
 *    deghos 12/23/08 - EJ i18n changes
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *
 * ===========================================================================
  $Log:
   4    360Commerce 1.3         1/22/2006 11:45:11 AM  Ron W. Haight   removed
        references to com.ibm.math.BigDecimal
   3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
   2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse
   1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse
  $
  Revision 1.4  2004/04/09 16:56:02  cdb
  @scr 4302 Removed double semicolon warnings.

  Revision 1.3  2004/02/12 16:50:42  mcs
  Forcing head revision

  Revision 1.2  2004/02/11 21:51:22  rhafernik
  @scr 0 Log4J conversion and code cleanup

  Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
  updating to pvcs 360store-current


 *
 *    Rev 1.2   Nov 24 2003 19:46:14   nrao
 * Code Review Changes.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.instantcredit.enrollment;

import java.math.BigDecimal;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;

/**
 * Shuttles data from ModifyTransacationDiscountPercent service to
 * ModifyTransaction service.
 *
 * @version $Revision: /main/19 $
 */
public class ModifyTransactionDiscountPercentReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 5003179871467691604L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ModifyTransactionDiscountPercentReturnShuttle.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "";

    // transaction
    protected RetailTransactionIfc transaction = null;

    // flag to see if there was a discount done in the service
    protected boolean doDiscount = false;

    // flag to see if a discount has to be cleared
    protected boolean clearDiscount = false;

    protected TransactionDiscountStrategyIfc discountPercent;
    protected TransactionDiscountStrategyIfc oldDiscountPercent;

    /**
     * employee ID
     */
    protected String discountEmployeeID = "";

    /**
     * Loads data from ModifyTransactionDiscountPercent service.
     *
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);

        ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo) bus.getCargo();
        doDiscount = cargo.getDoDiscount();
        discountPercent = cargo.getDiscount();
        oldDiscountPercent = cargo.getOldDiscount();
        transaction = cargo.getTransaction();
        discountEmployeeID = cargo.getEmployeeDiscountID();
    }

    /**
     * Unloads data to ModifyTransaction service.
     *
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);

        InstantCreditCargo cargo =
            (InstantCreditCargo)bus.getCargo();
        JournalManagerIfc mgr =
            (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        String discountPercentStr = "";

        // Current discount. Might be null if user leaves % field blank
        if (discountPercent != null)
        {
            BigDecimal discountRate = discountPercent.getDiscountRate();
            discountRate = discountRate.movePointRight(2);
            discountPercentStr = discountRate.toString();
        }

        // set transaction
        if (transaction != null)
        {
            cargo.setTransaction(transaction);
        }

        SaleReturnTransactionIfc trans =
            (SaleReturnTransactionIfc)cargo.getTransaction();

        // check to see if the clear key has been set
        if (clearDiscount == true && trans != null)
        {
            trans.clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
                                            DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
        }
        // else if a new discount apply it to the transaction
        else if (doDiscount == true && trans != null)
        {
            StringBuilder message = new StringBuilder();

            Object[] dataArgs = new Object[2];
            message.append(Util.EOL)
                   .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                            JournalConstantsIfc.TRANS_DISCOUNT_TAG_LABEL,
                                            null)).append(Util.EOL);
            dataArgs[0] = discountPercentStr;
            message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.DISCOUNT_TAG_LABEL,
                                    dataArgs))
                    .append(Util.EOL);
            if(discountPercent.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)) == null)
            {
                dataArgs[0] = discountPercent.getReason().getCode();
            }
            else
            {
                dataArgs[0] = discountPercent.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
            }
            message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.DISCOUNT_RSN_TAG_LABEL, dataArgs));


            mgr.journal(trans.getCashier().getEmployeeID(),
                        trans.getTransactionID(),
                        message.toString());

            trans.addTransactionDiscountDuringTender(discountPercent);
        }
    }
}
