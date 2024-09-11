/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/AbstractApplyDiscountAmountSite.java /main/12 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         6/4/2007 6:01:32 PM    Alan N. Sinton  CR
 *         26486 - Changes per review comments.
 *    4    360Commerce 1.3         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
 *         26486 - EJournal enhancements for VAT.
 *    3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:19 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/03/22 04:02:02  cdb
 *   @scr 3588 Code Review cleanup
 *
 *   Revision 1.8  2004/03/22 03:49:27  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.7  2004/03/19 21:48:41  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review modifications. Further abstractions.
 *
 *   Revision 1.6  2004/03/16 18:30:46  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.5  2004/03/04 22:01:04  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Clear markdown items.
 *
 *   Revision 1.4  2004/03/03 23:07:42  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   javadoc.
 *
 *   Revision 1.3  2004/02/26 18:26:20  cdb
 *   @scr 3588 Item Discounts no longer have the Damage
 *   selection. Use the Damage Discount flow to apply Damage
 *   Discounts.
 *
 *   Revision 1.2  2004/02/24 22:36:29  cdb
 *   @scr 3588 Added ability to check for previously existing
 *   discounts of the same type and capture the prorate user
 *   selection. Also migrated item discounts to validate in
 *   the percent and amount entered aisle to be consistent
 *   with employee discounts.
 *
 *   Revision 1.1  2004/02/23 22:52:22  dcobb
 *   @scr 3588 Abstract common code  to abstract class.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Apply the previously validated discounts by amount.
 * 
 * @version $Revision: /main/12 $
 */
abstract public class AbstractApplyDiscountAmountSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 6709539525051454858L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Returns the discount basis.
     * 
     * @return the discount basis
     */
    abstract protected int getDiscountBasis();

    /**
     * Returns true if the discount is a damage discount.
     * 
     * @return true if the discount is a damage discount
     */
    abstract protected boolean isDamageDiscount();

    /**
     * Returns true if the discount is a markdown.
     * 
     * @return true if the discount is a markdown
     */
    abstract protected boolean isMarkdown();

    /**
     * Apply the previously validated discounts by amount.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get access to common elements
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        SaleReturnLineItemIfc[] lineItems = null;
        
        // Get the line items from cargo
        lineItems = cargo.getItems();
        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = cargo.getItem();
        }

        // If all data's been successful so far, journal the discount and proceed to the next site
        // Journal removal of previous discount, Add discounts, and Journal adding new discount
        journalAndAddDiscounts(bus, lineItems);
        bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
    }

    /**
     * Assuming the discounts have been previously validated:
     * <ul>
     * <li>1) Journals the removal of the previously existing discount(s) if any,
     * <li>2) Removes the previously existing discount(s) if any,
     * <li>3) Adds the new discount strategy to the line item, and
     * <li>4) journals the new discounts.
     * </ul>
     *  
     * @param  bus       Service Bus
     * @param  lineItems The selected sale return line items
     */
    protected void journalAndAddDiscounts(BusIfc bus,
                                          SaleReturnLineItemIfc[] lineItems)
    {
        // Get access to common elements
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)bus.getManager(JournalFormatterManagerIfc.TYPE);
        boolean onlyOneDiscountAllowed = cargo.isOnlyOneDiscountAllowed(pm, logger);
        
        HashMap<Integer, ItemDiscountStrategyIfc> discountHash = cargo.getValidDiscounts();
        Set<Integer> keys = discountHash.keySet();
        Integer indexInteger = null;
        int index = -1;
        for (Iterator<Integer> i = keys.iterator(); i.hasNext();)
        {
            indexInteger = i.next();
            index = indexInteger.intValue();
            SaleReturnLineItemIfc srli = lineItems[index];

            if (onlyOneDiscountAllowed)
            {
                // Journal and remove ALL previously existing manual discounts
                cargo.removeAllManualDiscounts(srli, (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE));
            }
            else
            {    
                // Journal previously existing discounts by amount
                ItemDiscountStrategyIfc[] currentDiscounts = srli.getItemPrice().getItemDiscountsByAmount();
                if((currentDiscounts != null) && (currentDiscounts.length > 0))
                {
                    // find the percent discount stategy that is a discount.
                    for (int j = 0; j < currentDiscounts.length; j++)
                    {
                        if (isMarkdown())
                        {
                            if (currentDiscounts[j].getAccountingMethod() == 
                                DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
                            {
                                journalDiscount(bus, cargo.getOperator().getEmployeeID(),
                                        cargo.getTransactionID(),
                                        formatter.toJournalManualDiscount(srli, currentDiscounts[j], true),
                                        bus.getServiceName()); 
                            }
                        }
                        else
                        {    
                            if (currentDiscounts[j].getAccountingMethod() == 
                                DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT &&
                                currentDiscounts[j].getAssignmentBasis() == getDiscountBasis())
                            {
                                journalDiscount(bus, cargo.getOperator().getEmployeeID(),
                                        cargo.getTransactionID(),
                                        formatter.toJournalManualDiscount(srli, currentDiscounts[j], true),
                                        bus.getServiceName());
                            }
                        }
                    }
                }
                // Remove previously existing discounts by amount
                if (isMarkdown())
                {
                    srli.clearItemMarkdownsByAmount(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM);
                }
                else
                {    
                    srli.clearItemDiscountsByAmount(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM,
                            getDiscountBasis(), 
                            isDamageDiscount());
                }
            }
            
            // add discount
            ItemDiscountByAmountIfc currentDiscountStrategy = (ItemDiscountByAmountIfc)discountHash.get(new Integer(index));
            srli.addItemDiscount(currentDiscountStrategy);
            srli.calculateLineItemPrice();
            // journal the discount
            journalDiscount(bus, cargo.getOperator().getEmployeeID(),
                    cargo.getTransactionID(),
                    formatter.toJournalManualDiscount(srli, currentDiscountStrategy, false),
                    bus.getServiceName());
            
        }
    }

    /**
     * Journals discount
     * 
     * @param employeeID The Employee ID
     * @param transactionID The Transaction ID
     * @param journalString The string to journal
     * @param serviceName debugging info
     */
    protected void journalDiscount(BusIfc bus,
                                   String employeeID,
                                   String transactionID,
                                   String journalString,
                                   String serviceName)
    {
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (journal != null)
        {
            // write to the journal
            journal.journal(employeeID,
                            transactionID,
                            journalString);
        }
        else
        {
            logger.warn( "No journal manager found!");
        }
    }
}