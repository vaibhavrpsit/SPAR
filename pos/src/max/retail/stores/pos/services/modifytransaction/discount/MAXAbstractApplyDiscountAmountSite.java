package max.retail.stores.pos.services.modifytransaction.discount;

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
import oracle.retail.stores.pos.services.pricing.PricingCargo;

abstract public class MAXAbstractApplyDiscountAmountSite extends PosSiteActionAdapter {
	

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
    	MAXModifyTransactionDiscountCargo cargo1 = (MAXModifyTransactionDiscountCargo) bus.getCargo();
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
