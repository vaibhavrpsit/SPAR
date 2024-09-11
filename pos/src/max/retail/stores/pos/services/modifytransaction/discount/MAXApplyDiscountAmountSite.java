package max.retail.stores.pos.services.modifytransaction.discount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.discount.MAXItemDiscountByAmountStrategy;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.pricing.AbstractApplyDiscountAmountSite;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXApplyDiscountAmountSite extends AbstractApplyDiscountAmountSite {
	
    //----------------------------------------------------------------------
   
    //----------------------------------------------------------------------
    /**
     *   Returns identifiction for discount by amount screen name. <P>
     *   @return POSUIManagerIfc.ITEM_DISC_AMT
     */
    //----------------------------------------------------------------------
    public String getUIModel()
    {
        return POSUIManagerIfc.ITEM_DISC_AMT;
    }



    /**
     *   Returns list of Reason Code data for discount by amount. <P>
     *   @param   cargo  The pricing cargo
     *   @return  the reason code list
    */
    public CodeListIfc getDiscountAmountCodeList(PricingCargo cargo)
    {
        return cargo.getLocalizedDiscountAmountCodeList();
    }


	
	
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Returns the discount basis.
     * 
     * @return the discount basis
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get access to common elements
    	//System.out.println("Hello DiscountAmount");
    	MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();
        SaleReturnLineItemIfc[] lineItems = null;
      
        
        // Get the line items from cargo
        lineItems = (SaleReturnLineItemIfc[]) cargo.getTransaction().getLineItems();
        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = (SaleReturnLineItemIfc) cargo.getTransaction().getLineItemsVector().get(0);
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
    protected void journalAndAddDiscounts(BusIfc bus,SaleReturnLineItemIfc[] lineItems)
    {
        // Get access to common elements
    	MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)bus.getManager(JournalFormatterManagerIfc.TYPE);
     //  boolean onlyOneDiscountAllowed = cargo.isOnlyOneDiscountAllowed(pm, logger);
        ArrayList<String> itemList = null;
		 String itemId= null;
		 String targetitm=null;
		 SaleReturnLineItemIfc srli = null;
		 
        
        MAXItemDiscountByAmountStrategy ifc =new MAXItemDiscountByAmountStrategy();

        for(int p=0;p<cargo.getInvoiceDiscounts().size();p++)
        {
        	if(cargo.getInvoiceDiscounts().get(p).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ$offTiered_BillBuster") 
        			&& cargo.getInvoiceDiscounts().get(p).getSourceList().getItemThreshold().compareTo(cargo.getTransaction().getTenderTransactionTotals().getBalanceDue())==-1
        			 &&!(cargo.getInvoiceDiscounts().get(p).getSourceList().getItemThreshold().toString().compareTo("0.00")==0))
        	{
        		itemList=(ArrayList<String>) cargo.getInvoiceDiscounts().get(p).getItemList();
        		for(int j=0;j<cargo.getTransaction().getLineItemsVector().size();j++)
        		{
//        			if(cargo.getTransaction().getLineItemsVector().get(j).getExtendedDiscountedSellingPrice().compareTo(cargo.getTransaction().getLineItemsVector().get(j).getExtendedSellingPrice())== -1) {
//        			   itemList=null;
//        			}
						itemId=cargo.getTransaction().getLineItemsVector().get(j).getItemID();
//						if(itemList!=null) {
						for (int k=0 ;k<itemList.size();k++) {
							if(itemId.equalsIgnoreCase(itemList.get(k).toString())){
								targetitm=itemList.get(k).toString();
								ifc.setDiscountAmount(cargo.getInvoiceDiscounts().get(p).getDiscountAmount());
		        				ifc.setItemDiscountAmount(cargo.getInvoiceDiscounts().get(p).getDiscountAmount());
								break;
//							}
						}
        			}
        		}
        			
					
        			}
        			
        }
    

        	
        			
        
        
        //int targetItemIndex=0;
        //String targetitm=null;
        boolean onlyOneDiscountAllowed = cargo.isOnlyOneDiscountAllowed(pm, logger);
        
		/*
		 * for(int i=0;i<cargo.getInvoiceDiscounts().size();i++) {
		 * if(cargo.getInvoiceDiscounts().get(i).getDescription().equalsIgnoreCase(
		 * "Buy$NorMoreGetYatZ$offTiered_BillBuster")) {
		 * targetitm=cargo.getInvoiceDiscounts().get(i).setTargetItemId(targetitm);
		 * 
		 * break; } }
		 */
		  
        
        for(int k=0;k< lineItems.length;k++)
        {
        	if(cargo.getTransaction().getLineItemsVector().get(k).getItemID().equalsIgnoreCase(targetitm)|| targetitm==null )
        	{
        		///targetItemIndex = j;
                HashMap<Integer,ItemDiscountStrategyIfc> discountHash = new HashMap<Integer,ItemDiscountStrategyIfc>(k);
      		  discountHash.put(k, ifc);
      		  Set<Integer> keys = new HashSet<Integer>();
      		  keys.addAll(discountHash.keySet());
      		  Integer indexInteger = null; 
      		 
      		  
      		  
      		
      		  int index = -1;
      		  for (Iterator<Integer> i = keys.iterator(); i.hasNext();)
      	        {
      	            indexInteger = i.next();
      	            index = indexInteger.intValue();
      	             srli = lineItems[index];	             
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
      	                                         cargo.getTransaction().getTransactionID(),
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
      	                                         cargo.getTransaction().getTransactionID(),
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
      	             //breaking the loop so only one target item promo is applied by Kumar Vaibhav
      	          
      	             break;
      	        }
				/*
				 * if(targetitm==null) { srli.removeAdvancedPricingDiscount(); }
				 */
      		srli.removeAdvancedPricingDiscount();
      		  ItemDiscountByAmountIfc currentDiscountStrategy = (ItemDiscountByAmountIfc)discountHash.get(new Integer(index));
                
      		  if (!cargo.isInvoiceRuleAlreadyApplied()) {
      		//	for(int i=1;srli.getItemQuantity().intValue()<=i;i++) {
      		  srli.addItemDiscount(currentDiscountStrategy);
                srli.calculateLineItemPrice();
      			}
                
      		    
                cargo.setInvoiceDiscountAmount(currentDiscountStrategy.getDiscountAmount());
                
                cargo.setInvoiceRuleAlreadyApplied(true);
                cargo.setRuleApplied(true);
      		  }
      		  }
                
                bus.mail("Next");
      			         
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



	@Override
	protected int getDiscountBasis() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	protected boolean isDamageDiscount() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	protected boolean isMarkdown() {
		// TODO Auto-generated method stub
		return false;
	}


}
