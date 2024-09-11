/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 
 * Copyright (c) 1998-2004 360Commerce, Inc. All Rights Reserved.
 * 
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

package max.retail.stores.pos.services.pricing;
// java imports
import java.math.BigDecimal;

import org.apache.log4j.Logger;

import max.retail.stores.pos.services.modifytransaction.discount.MAXModifyTransactionDiscountCargo;
import oracle.retail.stores.domain.discount.CustomerDiscountByPercentage;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageStrategy;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
//--------------------------------------------------------------------------
/**
 * Shuttles data from ModifyTransacationDiscountPercent service to ModifyTransaction service.
 * 
 * @version $Revision: 4$
 */
//--------------------------------------------------------------------------
public class MAXModifyTransactionDiscountPercentReturnShuttle extends FinancialCargoShuttle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -9038939713291038488L;
	/**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger =
        Logger.getLogger(max.retail.stores.pos.services.pricing.MAXModifyTransactionDiscountPercentReturnShuttle.class);
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: 4$";
    /** transaction * */
    protected RetailTransactionIfc transaction = null;
    /** flag to see if there was a discount done in the service * */
    protected boolean doDiscount = false;
    /** flag to see if a discount has to be cleared 
    @deprecated as of release 7.0. No replacement **/
    protected boolean clearDiscount = false;
    /** The new discount amount * */
    protected TransactionDiscountStrategyIfc discountPercent;
    /** The old discount amount * */
    protected TransactionDiscountStrategyIfc oldDiscountPercent;
    /**
     * employee discount id
     */
    protected String employeeDiscountID = null;
    
    protected String employeeDiscountMethod = null;  //Rev 1.0 changes
    //----------------------------------------------------------------------
    /**
     * Loads data from ModifyTransactionDiscountPercent service.
     * <P>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);
        // retrieve the child cargo
        MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();  //Rev 1.0 changes
        // get all the cargo from the child service - all decisions will be in the unload
        doDiscount = cargo.getDoDiscount();
        discountPercent = cargo.getDiscount();
        oldDiscountPercent = cargo.getOldDiscount();
        transaction = cargo.getTransaction();
        employeeDiscountID = cargo.getEmployeeDiscountID();
        employeeDiscountMethod = cargo.getEmployeeDiscountMethod();//Rev 1.0 changes
    }
    //----------------------------------------------------------------------
    /**
     * Unloads data to ModifyTransaction service.
     * <P>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);
        // retrieve the parent cargo
        MAXPricingCargo cargo = (MAXPricingCargo) bus.getCargo();  //Rev 1.0 changes
        cargo.setEmployeeDiscountMethod(employeeDiscountMethod);
        boolean onlyOneDiscount =
            cargo.isOnlyOneDiscountAllowed((ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE), logger);
        String discountPercentStr = "";
        // Current discount. Might be null if user leaves % field blank
        if (discountPercent != null)
        {
            BigDecimal discountRate = discountPercent.getDiscountRate();
            if (discountRate.toString().length() > 5)
            {
                BigDecimal scaleOne = new BigDecimal(1);
                discountRate = discountRate.divide(scaleOne, 2, BigDecimal.ROUND_HALF_UP);
            }
            discountRate = discountRate.movePointRight(2);
            discountRate = discountRate.setScale(0, BigDecimal.ROUND_HALF_UP);
            discountPercentStr = discountRate.toString();
        }
        // Old discount
        String oldDiscountPercentStr = "";
        if (oldDiscountPercent != null)
        {
            BigDecimal discountRate = oldDiscountPercent.getDiscountRate();
            if (discountRate.toString().length() > 5)
            {
                BigDecimal scaleOne = new BigDecimal(1);
                discountRate = discountRate.divide(scaleOne, 2, BigDecimal.ROUND_HALF_UP);
            }
            discountRate = discountRate.movePointRight(2);
            discountRate = discountRate.setScale(0, BigDecimal.ROUND_HALF_UP);
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
            JournalManagerIfc mgr = null;
            mgr = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
            SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc) cargo.getTransaction();
            
            TransactionDiscountStrategyIfc[] discounts = null;
            if (onlyOneDiscount)
            {
                cargo.removeAllManualDiscounts(null, mgr);
                if (trans != null)
                {
                    trans.addTransactionDiscount(discountPercent);
                }
            }
            else
            {
                StringBuffer message = new StringBuffer();
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
                        // journal removal of discount
                        message
                            .append(Util.EOL)
                            .append("TRANS: Discount")
                            .append(Util.EOL)
                            .append("  Discount: ")
                            .append("(")
                            .append(oldDiscountPercentStr)
                            .append("%) Removed")
                            .append(Util.EOL);
                        if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
                        {
                            message.append("  Emp. ID.: ").append(discount.getDiscountEmployeeID());
                            
                            
                        }
                        else
                        {
                        	if (oldDiscountPercent != null) {
                            message.append("  Disc. Rsn.: ").append(
                                new Integer(oldDiscountPercent.getReasonCode()).toString());
                        }}
                        if (oldDiscountPercent != null)
                        {
                            message.append(" - ").append(oldDiscountPercent.getReasonCodeText());
                        }
                    }
                } // end loop thru discounts
                if (message.toString() != "" && trans != null)
                {
                    mgr.journal(trans.getCashier().getEmployeeID(), trans.getTransactionID(), message.toString());
                }
                clearDiscounts(trans);
            }
            // journal new transaction discount percentage
            if(cargo.isEmployeeRemoveSelected()!=true) {
            StringBuffer strResult = new StringBuffer();
            strResult
                .append(Util.EOL)
                .append("TRANS: Discount")
                .append("                   " + "(" + discountPercentStr + "%)")
                .append(Util.EOL)
                .append("  Discount: Pct.")
                .append(Util.EOL);
            if (discountPercent.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
            {
                strResult.append("  Emp. ID.: ").append(discountPercent.getDiscountEmployeeID())
                // below code is added by atul shukla
                .append(Util.EOL);
                strResult.append("  Comany Name.: ").append( ((TransactionDiscountByPercentageStrategy) discountPercent).getEmployeeCompanyName());
            }
            else
            {
                strResult.append("  Disc. Rsn.: ").append(new Integer(discountPercent.getReasonCode()).toString());
            }
            if (discountPercent.getReasonCodeText() != null)
            {
                strResult.append(" - ").append(discountPercent.getReasonCodeText());
            }
            if (trans != null)
            {
                mgr.journal(trans.getCashier().getEmployeeID(), trans.getTransactionID(), strResult.toString());
            }
            if (trans != null)
            {
                // clear and add discounts
                clearDiscounts(trans);
                trans.addTransactionDiscount(discountPercent);
            }
        }
        }
        if (employeeDiscountID != null)
        {
            cargo.setEmployeeDiscountID(employeeDiscountID);
        }
    }
    //----------------------------------------------------------------------
    /**
     * Gets Manual Discounts by Percentage from transaction.
     * <P>
     * 
     * @param transaction
     *            SaleReturnTransaction with potential discounts
     * @return An array of transaction discount strategies
     */
    //----------------------------------------------------------------------
    public TransactionDiscountStrategyIfc[] getDiscounts(SaleReturnTransactionIfc transaction)
    {
        TransactionDiscountStrategyIfc[] discountArray =
            transaction.getTransactionDiscounts(
                DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
                DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
        return discountArray;
    }
    //----------------------------------------------------------------------
    /**
     * Clears Manual Discounts by Percentage from transaction.
     * <P>
     * 
     * @param transaction
     *            SaleReturnTransaction with potential discounts
     */
    //----------------------------------------------------------------------
    public void clearDiscounts(SaleReturnTransactionIfc transaction)
    {
        transaction.clearTransactionDiscounts(
            DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
            DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
    }
}
