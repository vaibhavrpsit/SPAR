/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/ModifyTransactionDiscountAmountReturnShuttle.java /main/17 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                      from st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/27/10 - updating deprecated names
 *    acadar 04/09/10 - optimize calls to LocaleMAp
 *    acadar 04/05/10 - use default locale for currency and date/time display
 *    abonda 01/03/10 - update header date
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *
 * ===========================================================================
      $Log:
      7    360Commerce 1.6         11/15/2007 11:12:39 AM Christian Greene
           fixed equals comparison
      6    360Commerce 1.5         8/9/2007 10:50:40 AM   Ashok.Mondal    CR
           28179 :Fix the alignment problem with manual discount on eJournal.
      5    360Commerce 1.4         5/21/2007 9:16:22 AM   Anda D. Cadar   EJ
           changes
      4    360Commerce 1.3         4/25/2007 8:52:18 AM   Anda D. Cadar   I18N
           merge

      3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse
     $
     Revision 1.11  2004/07/06 16:50:06  cdb
     @scr 5337 General cleanup.

     Revision 1.10  2004/07/02 00:18:12  cdb
     @scr 5337 Corrected incomplete transaction discount removal in the
     case of "only one discount allowed." Wasn't removing Employee
     Transaction Discounts.

     Revision 1.9  2004/03/22 18:35:05  cdb
     @scr 3588 Corrected some javadoc

     Revision 1.8  2004/03/04 23:58:15  cdb
     @scr 3588 Updated journaling of Employee Transaction
     discounts.

     Revision 1.7  2004/03/03 21:03:45  cdb
     @scr 3588 Added employee transaction discount service.

     Revision 1.6  2004/02/26 23:59:07  cdb
     @scr 3588 Added journaling of transaciton reason code ID.
     Cleaned some code.

     Revision 1.5  2004/02/24 00:50:40  cdb
     @scr 3588 Provided for Transaction Discounts to remove
     previously existing discounts if they Only One Discount is allowed.

     Revision 1.4  2004/02/13 22:24:29  cdb
     @scr 3588 Added dialog to indicate when discount will reduce
     some prices below zero but not others.

     Revision 1.3  2004/02/12 16:51:34  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:05  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Oct 17 2003 10:24:58   bwf
 * Add employeeDiscountID and remove unused imports.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 *
 *    Rev 1.0   Aug 29 2003 16:05:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   02 May 2002 17:39:10   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.pricing;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountStrategy;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;

//--------------------------------------------------------------------------
/**
    Shuttles data from ModifyTransactionDiscountAmount service to
    Pricing service. <P>
    @version $Revision: /main/17 $
**/
//--------------------------------------------------------------------------
public class ModifyTransactionDiscountAmountReturnShuttle extends FinancialCargoShuttle
{
    /**
     *
     */
    private static final long serialVersionUID = 1465406938798315736L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.pricing.ModifyTransactionDiscountAmountReturnShuttle.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/17 $";

    /** the transaction **/
    protected RetailTransactionIfc transaction = null;

    /** flag to see if there was a discount done in the service **/
    protected boolean doDiscount = false;

    /** flag to see if a discount has to be cleared
    @deprecated as of release 7.0. No replacement **/
    protected boolean clearDiscount = false;

    /** The new discount amount **/
    protected TransactionDiscountStrategyIfc discountAmount;

    /** The old discount amount **/
    protected TransactionDiscountStrategyIfc oldDiscountAmount;
    /**
       length of available space for discount value
    **/
    protected static int AVAIL_DISCOUNT_LENGTH = 23;

    /**
     employee discount id
     **/
    protected String employeeDiscountID = null;

    //----------------------------------------------------------------------
    /**
       Loads data from ModifyTransactionDiscountAmount service. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);

        // retrieve the child cargo
        ModifyTransactionDiscountCargo cargo;
        cargo = (ModifyTransactionDiscountCargo)bus.getCargo();

        // get all the cargo from the child service - all decisions will be in the unload
        doDiscount        = cargo.getDoDiscount();
        discountAmount    = cargo.getDiscount();
        oldDiscountAmount = cargo.getOldDiscount();
        transaction       = cargo.getTransaction();
        employeeDiscountID = cargo.getEmployeeDiscountID();
    }

    //----------------------------------------------------------------------
    /**
       Loads data for Pricing service. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);

        // retrieve the parent cargo
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        boolean onlyOneDiscount = cargo.isOnlyOneDiscountAllowed((ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE), logger);

        JournalManagerIfc mgr =
            (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        String     discountAmountStr = "";
        Locale locale =  LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

        // Current discount. Might be null if user leaves disc amt field blank.
        if (discountAmount != null)
        {
            CurrencyIfc discountCurr =
                ((TransactionDiscountByAmountStrategy)discountAmount).getDiscountAmount();
            discountAmountStr = discountCurr.negate().toGroupFormattedString().trim();
        }

        // set transaction
        if (transaction != null)
        {
            cargo.setTransaction(transaction);
        }

        // old discount
        String  oldDiscountAmountStr = "";
        if (oldDiscountAmount != null)
        {
            CurrencyIfc  oldDiscountCurr =
                ((TransactionDiscountByAmountStrategy)oldDiscountAmount).getDiscountAmount();
            oldDiscountAmountStr  = oldDiscountCurr.toGroupFormattedString().trim();
        }

        // if a new discount apply it to the transaction
        if (doDiscount == true)
        {
            StringBuffer message = new StringBuffer();

            // nmw - this has to go in AMountEnteredAisle and PercentEnteredAisle
            // so discount can be found w/o using cargo (which may not have setDiscount executed
            // yet
            SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc)cargo.getTransaction();

            TransactionDiscountStrategyIfc[] discounts = null;
            if (onlyOneDiscount)
            {
                cargo.removeAllManualDiscounts(null, mgr);
                if (trans != null)
                {
                    trans.addTransactionDiscount(discountAmount);
                }
            }
            else
            {
                discounts = getDiscounts(trans);

                int numDiscounts = 0;
                if (discounts != null)
                {
                    numDiscounts = discounts.length;
                }
                for (int i = 0; i < numDiscounts; i++)
                {
                    TransactionDiscountStrategyIfc discount = discounts[i];

                    if (discount instanceof TransactionDiscountByAmountStrategy)
                    {

                        Object dataArgs[] = {oldDiscountAmountStr};
                        String transDisc=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANS_DISCOUNT, dataArgs);

                        String discAmtRemoved=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISCOUNT_AMT_REMOVED, null);

                    	message.append(Util.EOL)
                    		   .append(transDisc)
                    		   .append(Util.EOL)
                    		   .append(discAmtRemoved)
                    		   .append(Util.EOL);

                        if (discountAmount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
                        {

                            Object empDataArgs[] = {discountAmount.getDiscountEmployeeID()};
                            String empId=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EMPLOYEE_ID, empDataArgs);

                            message.append(empId);

                        }
                        else
                        {

                        	String reasonCodeText ="";
                            if (oldDiscountAmount.getReason() != null)
                            {


                            	reasonCodeText = oldDiscountAmount.getReason().getText(locale);

                            }
                            Object reasonCodeDataArgs[]={oldDiscountAmount.getReason().getCode(),reasonCodeText};
                            String reasonCode=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISCOUNT_REASON_CODE, reasonCodeDataArgs);

                            message.append(reasonCode);
                        }
                    }
                } // end loop thru discounts

                if (!message.toString().trim().equals(""))
                {
                    mgr.journal(cargo.getTransaction().getCashier().getEmployeeID(),
                                cargo.getTransaction().getTransactionID(),
                                message.toString());
                }
            }

            // journal new transaction discount amount
            StringBuffer    strResult = new StringBuffer();


            Object discDataArgs[] = {discountAmountStr};
            String transDisc=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANS_DISCOUNT, discDataArgs);

            String discAmt=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISCOUNT_AMT, null);

            strResult.append(Util.EOL)
        		   .append(transDisc)
        		   .append(Util.EOL)
        		   .append(discAmt)
        		   .append(Util.EOL);
            if (discountAmount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
            {
                Object empDataArgs[] = {discountAmount.getDiscountEmployeeID()};
                String empId=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EMPLOYEE_ID, empDataArgs);

                strResult.append(empId);


            }
            else
            {
            	String reasonCodeText ="";
                if (discountAmount.getReason() != null)
                {
                	reasonCodeText = discountAmount.getReason().getText(locale);
                }
                Object reasonCodeDataArgs[]={discountAmount.getReason().getCode(),reasonCodeText};
                String reasonCode=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISCOUNT_REASON_CODE, reasonCodeDataArgs);

                strResult.append(reasonCode);


            }



            mgr.journal(cargo.getTransaction().getCashier().getEmployeeID(),
                        cargo.getTransaction().getTransactionID(),
                        strResult.toString());

            // clear discounts and add them
            clearDiscounts(trans);
            trans.addTransactionDiscount(discountAmount);

        }
        if(employeeDiscountID != null)
        {
            cargo.setEmployeeDiscountID(employeeDiscountID);
        }
    }

    //----------------------------------------------------------------------
    /**
     Gets Manual Discounts by Amount from transaction. <P>
     @param  transaction  SaleReturnTransaction with potential discounts
     @return An array of transaction discount strategies
     **/
    //----------------------------------------------------------------------
    public TransactionDiscountStrategyIfc[] getDiscounts(SaleReturnTransactionIfc transaction)
    {
        TransactionDiscountStrategyIfc[] discountArray =
            transaction.getTransactionDiscounts(
                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
                    DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
        return discountArray;
    }

    //----------------------------------------------------------------------
    /**
     Clears Manual Discounts by Amount from transaction. <P>
     @param  transaction  SaleReturnTransaction with potential discounts
     **/
    //----------------------------------------------------------------------
    public void clearDiscounts(SaleReturnTransactionIfc transaction)
    {
        transaction.clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
                DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
    }

}
