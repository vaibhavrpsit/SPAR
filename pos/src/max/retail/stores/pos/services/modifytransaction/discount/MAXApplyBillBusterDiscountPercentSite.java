package max.retail.stores.pos.services.modifytransaction.discount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import max.retail.stores.domain.discount.MAXItemDiscountByPercentageStrategy;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountSourceIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.pricing.AbstractApplyDiscountAmountSite;
import oracle.retail.stores.pos.services.pricing.AbstractApplyDiscountPercentSite;

public class MAXApplyBillBusterDiscountPercentSite extends AbstractApplyDiscountPercentSite  {

 
    /**
	 * 
	 */
	private static final long serialVersionUID = -8347730045649720177L;

	public void arrive(BusIfc bus)
    {
        // Get access to common elements
    	MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();
        SaleReturnLineItemIfc[] lineItems = null;
     //   Vector<AbstractTransactionLineItemIfc>  lineItemsVector = new Vector<AbstractTransactionLineItemIfc>(2);

        // Get the line items from cargo
        lineItems = (SaleReturnLineItemIfc[]) cargo.getTransaction().getLineItems();
        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = (SaleReturnLineItemIfc) cargo.getTransaction().getLineItemsVector().get(0);
        }

        // If all data's been successful so far, journal the discount and
        // proceed to the next site
        
        journalAndAddDiscounts(bus, lineItems);
        
        bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
    }

    /**
     * Assuming the discounts have been previously validated:
     * <ul>
     * <li>1) Journals the removal of the previously existing discount(s) if any,
     * <li>2) Removes the previously existing discount(s) if any,
     * <li>3) Adds the new discount stragegy to the line item, and
     * <li>4) journals the new discounts.
     * </ul>
     * 
     * @param  bus       Service Bus
     * @param  lineItems The selected sale return line items
     */
    protected void journalAndAddDiscounts(BusIfc bus,
                                          SaleReturnLineItemIfc[] lineItems)
    {
    	MAXModifyTransactionDiscountCargo   cargo       = (MAXModifyTransactionDiscountCargo) bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)bus.getManager(JournalFormatterManagerIfc.TYPE);
        boolean onlyOneDiscountAllowed = cargo.isOnlyOneDiscountAllowed(pm, logger);
        ArrayList<String> itemList = null;
		 String itemId= null;
		 String targetitm=null;
		 CurrencyIfc targetitemprc= null;
		 BigDecimal targetItemqty=null;
       // ItemDiscountStrategyIfc ifc =new ItemDiscountByAmountStrategy();
       // ifc.setDiscountAmount(cargo.getInvoiceDiscountAmount());
       // ifc.setItemDiscountAmount(cargo.getInvoiceDiscountAmount());
        //int targetItemIndex=0;
        MAXItemDiscountByPercentageStrategy ifc = new MAXItemDiscountByPercentageStrategy();
        
        for(int p=0;p<cargo.getInvoiceDiscounts().size();p++)
        {
        	if(cargo.getInvoiceDiscounts().get(p).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ%offTiered_BillBuster")
        			&& cargo.getInvoiceDiscounts().get(p).getSourceList().getItemThreshold().compareTo(cargo.getTransaction().getTenderTransactionTotals().getBalanceDue())==-1
        		 &&!(cargo.getInvoiceDiscounts().get(p).getSourceList().getItemThreshold().toString().compareTo("0.00")==0))
        	{
        		//String targetItem = cargo.getInvoiceDiscounts().get(p).getTargetItemId();
        		
        		/*for(int j=0;j<cargo.getTransaction().getLineItemsVector().size();j++)
        		{
        			if(cargo.getTransaction().getLineItemsVector().get(j).getItemID().equalsIgnoreCase(cargo.getInvoiceDiscounts().get(p).getTargetItemId()))// || 
        				//	cargo.getTransaction().getLineItemsVector().get(j).getItemID().equalsIgnoreCase(targetItem) )
        			{
        				ifc.setDiscountRate(cargo.getInvoiceDiscounts().get(p).getDiscountRate());
        				
        				break;
        			}
        		}
        	}
        	break;
        }*/
        		itemList=(ArrayList<String>) cargo.getInvoiceDiscounts().get(p).getItemList();
        		for(int j=0;j<cargo.getTransaction().getLineItemsVector().size();j++)
        		{
        			//if(cargo.getTransaction().getLineItemsVector().get(j).getItemID().equalsIgnoreCase(cargo.getInvoiceDiscounts().get(p).getTargetItemId()))
        			//{
        				//ifc.setDiscountAmount(cargo.getInvoiceDiscounts().get(p).getDiscountAmount());
        				//ifc.setItemDiscountAmount(cargo.getInvoiceDiscounts().get(p).getDiscountAmount());
        			//	break;
        			
						itemId=cargo.getTransaction().getLineItemsVector().get(j).getItemID();
						for (int k=0 ;k<itemList.size();k++) {
							if(itemId.equalsIgnoreCase(itemList.get(k).toString())){
								targetitm=itemList.get(k).toString(); 
							 targetitemprc=cargo.getTransaction().getLineItemsVector().get(j).getExtendedSellingPrice();
							 targetItemqty=cargo.getTransaction().getLineItemsVector().get(j).getItemQuantityDecimal();
								ifc.setDiscountRate(cargo.getInvoiceDiscounts().get(p).getDiscountRate());
		        				//ifc.setItemDiscountAmount(cargo.getInvoiceDiscounts().get(p).getDiscountAmount());
								
								break;
							}
						}
						
					}
        			}else
        				{
        				
        			//	srli.addItemDiscount((ItemDiscountStrategyIfc) DomainGateway.getBaseCurrencyInstance());
                      //  srli.calculateLineItemPrice();
        			}
        			
        }
       // ifc.setDiscountRate(cargo.getInvoiceRuleAppliedRate());
    //    String targetitm=null;
        
        for(int i=0;i<cargo.getInvoiceDiscounts().size();i++)
      	{
      		if(cargo.getInvoiceDiscounts().get(i).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ%offTiered_BillBuster"))
      		{
      			targetitm=cargo.getInvoiceDiscounts().get(i).setTargetItemId(targetitm);
      			break;
      		}
      	}
		  
       // Set<String> itemIdSet = new HashSet<>();
        for(int k=0;k< lineItems.length;k++)
        {
        	if(cargo.getTransaction().getLineItemsVector().get(k).getItemID().equalsIgnoreCase(targetitm)|| targetitm==null)
        	{
        
        		HashMap<Integer,ItemDiscountStrategyIfc> discountHash = new HashMap<Integer,ItemDiscountStrategyIfc>(k);
        		 discountHash.put(k, ifc);
        		Set<Integer> keys = new HashSet<Integer>();
        		  keys.addAll(discountHash.keySet());
        		  Integer indexInteger = null; 
        		  SaleReturnLineItemIfc srli = null;
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
                //journal removal of previous percent discount
                ItemDiscountStrategyIfc[] currentDiscounts =
                    srli.getItemPrice().getItemDiscountsByPercentage();
                if((currentDiscounts != null) && (currentDiscounts.length > 0))
                {
                    // find the percent discount stategy that is a discount.
                    for (int j = 0; j < currentDiscounts.length; j++)
                    {
                        if (isMarkdown()) 
                        {
                            if (currentDiscounts[j].getAccountingMethod() == 
                                DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN &&
                                currentDiscounts[j].getAssignmentBasis() ==
                                    getDiscountBasis())
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
                                currentDiscounts[j].getAssignmentBasis() ==
                                    getDiscountBasis())
                            {
                                journalDiscount(bus, cargo.getOperator().getEmployeeID(),
                                                cargo.getTransaction().getTransactionID(),
                                                formatter.toJournalManualDiscount(srli, currentDiscounts[j], true),
                                                bus.getServiceName());
                            }
                        }
                    }
                }
                
                if (isMarkdown())
                {    
                    srli.clearItemMarkdownsByPercentage(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM);                    
                }
                else
                {
                    srli.clearItemDiscountsByPercentage(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM,
                            getDiscountBasis(),
                            isDamageDiscount());
                }
            }
        
            //damaged items cannot be returned so they are not eligible for
            //gift receipt.  Disable gift receipt for any gift receipted line items
            if (srli.isGiftReceiptItem() && isDamageDiscount())
            {
                srli.setGiftReceiptItem(false);
            }
            //breaking the loop so only one target item promo is applied by Kumar Vaibhav
            break;
        }     
            // add discount
        srli.removeAdvancedPricingDiscount();
        
        MAXItemDiscountByPercentageStrategy currentDiscountStrategy = (MAXItemDiscountByPercentageStrategy)discountHash.get(new Integer(index));
        currentDiscountStrategy.setItemDiscountAmount(currentDiscountStrategy.calculateItemDiscount(targetitemprc,targetItemqty));
       // if(!cargo.isInvoiceRuleAlreadyApplied()) {
        
//        	for(int i=1;i<=srli.getItemQuantity().intValue();i++) {
     //   if(!itemIdSet.contains(cargo.getTransaction().getLineItemsVector().get(k).getItemID())) {
         
      //  srli.setSelectedForItemSplit(true);
        srli.addItemDiscount(currentDiscountStrategy);
          srli.calculateLineItemPrice();
         
       //   itemIdSet.add(cargo.getTransaction().getLineItemsVector().get(k).getItemID());
//          break;
        	
          
         
          cargo.setInvoiceDiscountAmount(currentDiscountStrategy.getItemDiscountAmount());
          cargo.setInvoiceRuleAlreadyApplied(true);
          cargo.setRuleApplied(true);
     //   }
          
          bus.mail("Next");
        }
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
