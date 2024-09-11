/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		29/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.reports;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.reports.RegisterReport;

/**
 * Summary Report generator for VAT enabled environment.
 * $Revision: 7$
 */
public class MAXVATSummaryReport extends MAXSummaryReport
{
    /** Label for Total VAT */
    protected static String LABEL_TOTAL_VAT;

    /**
     * Gets the text from the property files
     */
    public void getPropertiesText()
    {
    	//Changes done for code merging(commenting below lines for error resolving)
        /*super.getPropertiesText();
        utility = (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        MAXVATSummaryReport.LABEL_TOTAL_VAT = utility.retrieveReportText("TotalVATLabel", "Total VAT");*/
    }

    /**
     * Overrides super's reportSalesTax() method to output a blank line.
     *
     * @param buff
     * @param ft
     * @see com.extendyourstore.pos.reports.SummaryReport#reportSalesTax(java.lang.StringBuffer, com.extendyourstore.domain.financial.FinancialTotalsIfc)
     */
    public void reportSalesTax(StringBuffer buff, FinancialTotalsIfc ft)
    {
        // per requirements output a blank line for VAT.
    	//Changes done for code merging(commenting below lines for error resolving)
       /* buff.append(registerreport.new_line);*/
    }

    /**
     * Overrides super's reportTotalSalesTax() method to output data for VAT.
     * 
     * @param buff
     * @param ft
     * @see com.extendyourstore.pos.reports.SummaryReport#reportTotalSalesTax(java.lang.StringBuffer,
     *      com.extendyourstore.domain.financial.FinancialTotalsIfc)
     */
    public void reportTotalTax(StringBuffer buff, FinancialTotalsIfc ft)
    {
        CurrencyIfc tax =
            ft.getAmountInclusiveTaxTransactionSales().subtract(
                    ft.getAmountInclusiveTaxTransactionReturns()).add(
                            ft.getAmountInclusiveTaxShippingCharges());
      //Changes done for code merging(commenting below lines for error resolving)
       /* buff.append(getFormattedLine(MAXVATSummaryReport.LABEL_TOTAL_VAT,
                                     null,
                                     tax.toGroupFormattedString(report_locale)));   */     
    }
}
