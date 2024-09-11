/* ===========================================================================
* Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * Rev 1.2 Bhanu Priya Gupta  Changes done for merge build code
 * Rev 1.1 Hitesh.dua 		09Apr,2017 done customization as per 12	tax is printing  in
  							-ve if BuyNofXgetHighestPricedXatZ and bill buster is in transaction 
  							and extendeddiscountedselling price goes to -ve.   
 * Rev 1.0 Hitesh.dua 		23dec,2016	Initial revision changes for printing customized receipt. 
 * ===========================================================================
 */
package max.retail.stores.pos.receipt;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;

import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.tax.MAXTaxInformationIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.pos.receipt.VATHelper;
import oracle.retail.stores.utility.I18NHelper;
/**
 * Class to provide VAT receipt and journal text.
 *
 * $Revision: /main/22 $
 */
public class MAXVATHelper extends VATHelper
{
     /**
     * The summary of VAt information for the above transaction
     */
    private TaxInformationIfc[] vatSummary;

    /**
     * Default constructor.
     */
    public MAXVATHelper()
    {
        super();
    }

    /**
     * Constructor for printing.
     */
    public MAXVATHelper(RetailTransactionIfc transaction)
    {
        super(transaction);
    }

    CurrencyIfc totalTax=DomainGateway.getBaseCurrencyInstance();
    /**
     * Get a summary of VAT information for the transaction. If it is
     *
     * @return
     */
    public TaxInformationIfc[] getVATSummary()
    {
    	if (vatSummary == null)
        {	
    		totalTax=DomainGateway.getBaseCurrencyInstance();
            HashMap<String,TaxInformationIfc> taxRateCodeMap = new HashMap<String,TaxInformationIfc>(0);
            // accumulate the totals for each VAT code from the sale return line items
            for (Enumeration<AbstractTransactionLineItemIfc> e = transaction.getLineItemsVector().elements(); e.hasMoreElements();)
            {
            	//changes for rev 1.0 start
                MAXSaleReturnLineItemIfc srli = (MAXSaleReturnLineItemIfc)e.nextElement();
                // Don't want to add the PriceAdjustmentLineItem since it's already accounted for
                // in the other line items.
                if(!(srli instanceof PriceAdjustmentLineItemIfc))
                {
                	MAXLineItemTaxBreakUpDetailIfc[] taxRate=srli.getLineItemTaxBreakUpDetails();
                    TaxInformationContainerIfc taxContainer = srli.getTaxInformationContainer();
                    TaxInformationIfc[] taxInfomations = taxContainer.getOrderedTaxInformation();
                    if(taxInfomations.length > 0 && taxInfomations[0] != null && taxRate!=null && taxRate.length>0)
                    {
                        TaxInformationIfc ti = taxInfomations[0];
                        	ti.setUniqueID(taxRate[0].getTaxCode());
                        //changes for rev 1.0 end
                            String uniqueID = ti.getUniqueID();
                            String vatCode = ti.getTaxRuleName();
                            if(vatCode.length() > 2)
                            {
                                vatCode = vatCode.substring(0,2);
                            }
                            TaxInformationIfc taxInfo = taxRateCodeMap.get(uniqueID);
                           if (taxRateCodeMap.containsKey(uniqueID)) {
                        	   ((MAXTaxInformationIfc)taxInfo).setTaxableAmt(((MAXTaxInformationIfc)taxInfo).getTaxableAmt().add(taxRate[0].getTaxableAmount()!=null?taxRate[0].getTaxableAmount():DomainGateway.getBaseCurrencyInstance()));
                        	   ((MAXTaxInformationIfc)taxInfo).setTaxAmt(((MAXTaxInformationIfc)taxInfo).getTaxAmt().add(taxRate[0].getTaxAmount()!=null?taxRate[0].getTaxAmount():DomainGateway.getBaseCurrencyInstance()));
            					
            				}else{
                                 	 taxInfo = DomainGateway.getFactory().getTaxInformationInstance();
                                      taxInfo.setTaxPercentageAsString(taxRate[0].getTaxRate());
                                      taxInfo.setTaxRuleName(vatCode);
                                      taxInfo.setUniqueID(uniqueID);
                                      ((MAXTaxInformationIfc)taxInfo).setTaxableAmt(taxRate[0].getTaxableAmount()!=null?taxRate[0].getTaxableAmount():DomainGateway.getBaseCurrencyInstance());
                                      ((MAXTaxInformationIfc)taxInfo).setTaxAmt(taxRate[0].getTaxAmount()!=null?taxRate[0].getTaxAmount():DomainGateway.getBaseCurrencyInstance());
                                      taxRateCodeMap.put(uniqueID, taxInfo);
                                 } 
                           totalTax=totalTax.add(taxRate[0].getTaxAmount()!=null?taxRate[0].getTaxAmount():DomainGateway.getBaseCurrencyInstance());
                            /*
                            //changes for rev 1.0 end
                            // get amounts
                            CurrencyIfc includingTax = srli.getExtendedDiscountedSellingPrice();
                            CurrencyIfc taxableAmt=taxRate[0].getTaxableAmount()!=null?taxRate[0].getTaxableAmount():DomainGateway.getBaseCurrencyInstance();
                            CurrencyIfc tax = taxContainer.getInclusiveTaxAmount();

                            // BEGIN HACK: For CR 27191 This fix is much less risky then to change
                            // the code that negates the taxes, but only if the original values are
                            // positive.  There are other places where similar hacks are implemented
                            // only to correct the tax values in the case of a POST VOID of a RETURN.
                            // Receipt printing and financial totals are examples of where these other
                            // hacks exist. The classes where the conditional negation is implemented
                            // are: TransactionTax.negate(), ItemTax.setItemTaxableAmount(), and
                            // TaxInformationContainer.negate()
                            // - Alan Sinton
                            // If they're not the same sign, negate the tax
                            if(includingTax.signum() != tax.signum())
                            {
                                tax = tax.negate();
                            }
                            // END HACK
                            CurrencyIfc excludingTax = includingTax.subtract(tax);

                            // accumulate for each vat code
                            updateInclusiveTaxableAmount(taxInfo, includingTax);
                            updateTaxAmount(taxInfo, tax);
                            //updateTaxableAmount(taxInfo, excludingTax);
                            updateTaxableAmount(taxInfo, taxableAmt);
                        
*/                    }
                }
            }

            vatSummary = new TaxInformationIfc[taxRateCodeMap.size()];
            taxRateCodeMap.values().toArray(vatSummary);
            Arrays.sort(vatSummary);
        }
        return vatSummary;
    }

     public CurrencyIfc getTotalTaxAmount(){
    	
    	return totalTax;
    	
    }
     //Changes starts for merged build
     public void journalVATLineItemTax(StringBuffer buffer, SaleReturnLineItemIfc saleReturnLineItem, ItemPriceIfc itemPrice, int taxMode, int taxScope)
     {
       //String vat = I18NHelper.getString("EJournal",  "JournalEntry.VATCODELabel", null);
       Object[] dataArgs = null;
       TaxInformationContainerIfc taxInformationContainer = saleReturnLineItem
         .getTaxInformationContainer();
       System.out.println("taxInformationContainer "+taxInformationContainer);
       TaxInformationIfc[] taxInformations = taxInformationContainer
         .getOrderedTaxInformation();
       
       if ((taxInformations != null) && (taxInformations.length > 0))
       {
         CurrencyIfc tax = taxInformations[0].getTaxAmount();
         System.out.println("tax "+tax);
         CurrencyIfc price = itemPrice.getExtendedDiscountedSellingPrice();
         System.out.println("Price "+price);
         CurrencyIfc excludingTax = price.subtract(tax);
         System.out.println("excludingTax "+ excludingTax);
         /*PrintableDocumentManagerIfc printableDocumentManager = (PrintableDocumentManagerIfc)
           Gateway.getDispatcher().getManager("PrintableDocumentManager");*/

         if (((MAXTaxInformationIfc)taxInformations[0]).getLineItemTaxBreakUpDetails().length > 0)
         {
           for (MAXLineItemTaxBreakUpDetailIfc max : ((MAXTaxInformationIfc)taxInformations[0]).getLineItemTaxBreakUpDetails())
           {
             String vatRate = max.getTaxAssignment().getTaxRate().toString();
             String vatText = max.getTaxAssignment().getTaxCodeDescription();
             String vatCode = max.getTaxAssignment().getTaxCode();
             dataArgs = new Object[] { vatRate, vatText, vatCode };
             String rateCode = I18NHelper.getString("EJournal", 
               "JournalEntry.RATECODELabel", dataArgs);
             printTaxBreakUp(buffer, max.getTaxAmount(), rateCode);
           }
         }
         else
         {
           String vatRate = trimTrailingZeros(taxInformations[0]
             .getTaxPercentage().toString());
           String vatCode = taxInformations[0].getTaxRuleName();
           if ((vatCode != null) && (vatCode.length() > 2)) {
             vatCode = vatCode.substring(0, 2);
           }
           String vatText = "TAX";
           dataArgs = new Object[] { vatRate, vatText, vatCode };
           String rateCode = I18NHelper.getString("EJournal", 
             "JournalEntry.RATECODELabel", dataArgs);
           printTaxBreakUp(buffer, tax, rateCode);
         }

         printItemTaxSummary(buffer, price, excludingTax);
       }
     }

     protected void printTaxBreakUp(StringBuffer buffer, CurrencyIfc tax, String rateCode)
     {
       Object[] dataArgs = null;
       buffer.append(Util.EOL);
       dataArgs = new Object[] { rateCode, tax.toGroupFormattedString() };
       buffer.append(I18NHelper.getString("EJournal", "JournalEntry.RATECODETAXLabel", dataArgs));
     }

     protected void printItemTaxSummary(StringBuffer buffer, CurrencyIfc price, CurrencyIfc excludingTax) {
       Object[] dataArgs = null;
       buffer.append(Util.EOL);
       dataArgs = new Object[] { excludingTax.toGroupFormattedString() };
       buffer.append(I18NHelper.getString("EJournal", "JournalEntry.PriceExcluding", dataArgs));
       buffer.append(Util.EOL);
       dataArgs = new Object[] { price.toGroupFormattedString() };
       buffer.append(I18NHelper.getString("EJournal", "JournalEntry.PriceIncluding", dataArgs));
     }
     //Changes Ends for merged build
}
