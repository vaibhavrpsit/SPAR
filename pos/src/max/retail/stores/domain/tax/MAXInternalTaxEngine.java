/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *  Rev 1.6 Change for Bug-6332 26/12/2013 Rahul yadav
 *  Rev 1.5 Change for Bug-6332 14/06/2013 Geetika Chugh
 *  Rev 1.4 Change for Bug-6397 13/06/2013 Geetika Chugh
 *  Rev 1.3 11/06/2013	Karandeep Singh		Change for Bug-6332
 *  Rev 1.2 May 26,2013  Geetika Chugh VAT_EXTRA REQ
 *Rev 1.1 May 26,2013  Geetika Chugh VAT_EXTRA REQ 	
 *Rev 1.0 June-29 Mukesh Kumar Singh  MAX-346
 *Tax was not updating properly into database 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tax;

import java.math.BigDecimal;

import max.retail.stores.domain.lineitem.MAXItemTax;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXTransactionTotals;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItem;
import oracle.retail.stores.domain.tax.InternalTaxEngine;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

public class MAXInternalTaxEngine extends InternalTaxEngine {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Start of Rev 1.0 June-29 Mukesh Kumar Singh MAX-346
	public void calculateTax(TaxLineItemInformationIfc[] lineItems,
			TransactionTotalsIfc totals) {

		if (totals == null) {
			totals = DomainGateway.getFactory().getTransactionTotalsInstance();
		}

		TaxLineItemInformationIfc[] taxableLineItems = collectTaxableLineItems(
				lineItems, totals);
		calculateMultipleTaxAmounts(lineItems, totals);

		for (int i = 0; i < taxableLineItems.length; i++) {
			postTaxCalculation(taxableLineItems[i]);
		}

		postTaxCalculation(totals);
	}

	private void calculateMultipleTaxAmounts(TaxLineItemInformationIfc[] items,
			TransactionTotalsIfc totals) {

		TaxInformationIfc totalTaxInformation = DomainGateway.getFactory()
				.getTaxInformationInstance();
		MAXMultipleTaxCalculator taxCalculator = new MAXMultipleTaxCalculator();

		CurrencyIfc itemTaxableAmount = null;
		CurrencyIfc itemTaxAmount = null;
		TaxInformationIfc itemTaxInformation = null;

		/** Change for Rev 1.3 : Start */
		TaxInformationIfc tempTotalTaxInformation = DomainGateway.getFactory().getTaxInformationInstance();
		/** Change for Rev 1.3 : End */

        //Changes for Rev 1.1 starts
        boolean resetTotalTax = false;
        //Changes for Rev 1.1 ends
		
        BigDecimal totalVat = new BigDecimal(0.00); 
		for (int i = 0; i < items.length; i++) {
			MAXSaleReturnLineItemIfc srli = (MAXSaleReturnLineItemIfc) items[i];
			itemTaxAmount = taxCalculator.calculateTotalItemTax(items[i]); 
			itemTaxableAmount = taxCalculator.getTaxInclusiveSellingRetail(
					items[i]).subtract(itemTaxAmount);
			itemTaxInformation = taxCalculator.createTaxInformation(
					itemTaxableAmount, itemTaxAmount,
					((MAXSaleReturnLineItemIfc) items[i]).getLineItemTaxBreakUpDetails());
			if ((!(srli.getPLUItem() instanceof MAXGiftCardPLUItem)) && (!(srli.getPLUItem() instanceof GiftCertificateItem))) {
			if(srli.getReturnItem() == null){
		          MAXPLUItemIfc pluitem = (MAXPLUItemIfc)srli.getPLUItem();
		          if ((pluitem.getTaxAssignments() != null) && (pluitem.getTaxAssignments()[0].getTaxRate() != null) && (pluitem.getTaxAssignments()[0].getTaxRate().toString().equals("0")))
				{
					itemTaxInformation.setTaxMode(1);
				}
		          if (((MAXItemTax)srli.getItemPrice().getItemTax()).getLineItemTaxBreakUpDetail() == null 
		        		  ||
		        		  (((MAXItemTax)srli.getItemPrice().getItemTax()).getLineItemTaxBreakUpDetail()[0].getTaxAssignment() != null) && 
		            (((MAXItemTax)srli.getItemPrice().getItemTax()).getLineItemTaxBreakUpDetail()[0].getTaxAssignment().getTaxRate().toString().equals("0.00")))
		          {
		            itemTaxInformation.setTaxMode(1);
		          }

			}

		        if ((srli.getReturnItem() != null) && (srli.getReturnItem().getItemTax() != null)) {
		          if (((MAXItemTax)srli.getItemPrice().getItemTax()).getLineItemTaxBreakUpDetail().length != 0 && !((max.retail.stores.domain.lineitem.MAXItemTax)srli.getReturnItem().getItemTax()).getLineItemTaxBreakUpDetail()[0].getTaxRate().equals("0.00"))
					{
						itemTaxInformation.setTaxMode(0);
					}
					else{
						itemTaxInformation.setTaxMode(1);
					}
			}
		        if(((MAXItemTax)srli.getItemPrice().getItemTax()).getLineItemTaxBreakUpDetail() != null && ((MAXItemTax)srli.getItemPrice().getItemTax()).getLineItemTaxBreakUpDetail() != null){
			         if((srli.getReturnItem() != null) && (((MAXItemTax)srli.getItemPrice().getItemTax()).getLineItemTaxBreakUpDetail()[0].getTaxAssignment() != null) && 
		            (((MAXItemTax)srli.getItemPrice().getItemTax()).getLineItemTaxBreakUpDetail()[0].getTaxAssignment().getTaxRate().toString().equals("0.00"))){
		        	itemTaxInformation.setTaxMode(1);
		        }
		        }
		        MAXPLUItemIfc pluitem = (MAXPLUItemIfc)srli.getPLUItem();
		          if ((pluitem.getTaxAssignments() != null) && (pluitem.getTaxAssignments()[0].getTaxRate() != null) && (pluitem.getTaxAssignments()[0].getTaxRate().toString().equals("0")))
				{
					itemTaxInformation.setTaxMode(1);
				}

		      }
			
			/** Change for Rev 1.4 : Start */
			if(srli != null && srli.isVatExtraApplied())
			{
				/** Change for Rev 1.6 :Starts */
				BigDecimal itemVatExtra =new BigDecimal(0.00);
				if(srli.isVatCollectionApplied())
				{
					itemVatExtra =  srli.getVatCollectionAmount();
					if(srli.isReturnLineItem() && itemVatExtra.compareTo(new BigDecimal(0.00))>=0)
					{
						itemVatExtra = srli.getVatCollectionAmount().negate();
					}
					else
					{
						itemVatExtra =  srli.getVatCollectionAmount();
					}
				totalVat = totalVat.add(itemVatExtra);
				/** Change for Rev 1.6: Ends */
			    }
				
				else{
					//Add By Rahul For Vat Extra value
					if(srli.isVatExtraApplied() && !srli.isVatCollectionApplied())
					{
					if(srli.isReturnLineItem())
					{
						//Rev 1.8 change start by akanksha
						if(srli.isExtendedPriceModified())
						{
							itemVatExtra=srli.getVatExtraAmount();
							
						}
						else{
					        itemVatExtra =  srli.getReturnItem().getItemTax().getItemInclusiveTaxAmount().getDecimalValue().negate();
						    }
							srli.setVatExtraAmount(itemVatExtra);
						}
						else{
							itemVatExtra=srli.getVatExtraAmount();
						}
					}
					totalVat = totalVat.add(itemVatExtra);
				   }
				//BigDecimal itemVatExtra =  srli.getVatExtraAmount();
				//totalVat = totalVat.add(itemVatExtra);
			}
			/** Change for Rev 1.4 : Ends */
			// Add only Non-Zero Tax Amount.
//			 if(itemTaxInformation.getTaxAmount().signum()!=0)
			{
				items[i].getTaxInformationContainer().addTaxInformation(
						itemTaxInformation);
//				totalTaxInformation.add(itemTaxInformation);
				//Changes for Rev 1.1 starts
				//Changes for Rev 1.5 starts
        	 if (!resetTotalTax)
           	 {
           	     totals.getTaxInformationContainer().reset();
           	 }
           	/** Change for Rev 1.5 : Ends */
           	/** Change for Rev 1.3 : Start */
           	tempTotalTaxInformation = (TaxInformationIfc) itemTaxInformation.clone();
//                totals.getTaxInformationContainer().addTaxInformation(itemTaxInformation);
           	totals.getTaxInformationContainer().addTaxInformation(tempTotalTaxInformation);
           	/** Change for Rev 1.3 : End */
                totalTaxInformation.add(itemTaxInformation);
                resetTotalTax = true;
              //Changes for Rev 1.1 ends
				
			}
		}
		/** Change for Rev 1.3 : Start */
		((MAXTransactionTotals)totals).setVatExtraTaxTotal(totalVat);
		/** Change for Rev 1.4 : Ends */
		// Add only Non-Zero Tax Amount.
		// if(totalTaxInformation.getTaxAmount().signum()!=0)
//		{
//			totals.getTaxInformationContainer().reset();
//			totals.getTaxInformationContainer().addTaxInformation(
//					totalTaxInformation);
//		}

	}

	// End of Rev 1.0 June-29 Mukesh Kumar Singh MAX-346
	
	  public void postTaxCalculation(TransactionTotalsIfc totals)
	    {
		  TaxInformationContainerIfc taxInformationContainer = totals.getTaxInformationContainer();
	        if(taxInformationContainer.getTaxAmount() == null || taxInformationContainer.getTaxAmount().toString().equals("0.00"))
	        {
	            totals.setTaxTotal(taxInformationContainer.getInclusiveTaxAmount());
	        }
	        else
	        {

	        totals.setTaxTotal(taxInformationContainer.getTaxAmount());
	        }
	        totals.setInclusiveTaxTotal(taxInformationContainer.getInclusiveTaxAmount());
	        
	        if(taxInformationContainer.getTaxAmount() == null || taxInformationContainer.getTaxAmount().toString().equals("0.00"))
	        {
	            totals.setTaxTotalUI(taxInformationContainer.getInclusiveTaxAmount());
	        }
	        else
	        totals.setTaxTotalUI(taxInformationContainer.getTaxAmount());
	     

	    }
	
	/** Change for Rev 1.3 : Start */
	public Object clone()
    {
	 MAXInternalTaxEngine newClass = new MAXInternalTaxEngine();
        setCloneAttributes(newClass);
        return newClass;
    }
	/** Change for Rev 1.3 : End */

}
