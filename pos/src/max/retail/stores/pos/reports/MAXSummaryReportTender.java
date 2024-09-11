/** ===========================================================================
 * Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
 *
 * Rev 1.0 Hitesh.dua 		28feb,2017	Initial revision.
 *  Changes for till reconcile related reports: print entered count as counted tender to be print on receipt. 
 * ============================================================================
 */
package max.retail.stores.pos.reports;

import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.pos.reports.SummaryReportTender;

public class MAXSummaryReportTender extends SummaryReportTender
  implements Serializable
{
  private static final long serialVersionUID = 5522268255798296508L;
  private MAXSummaryReport summaryReport;
  private FinancialCountTenderItemIfc expectedTender;
  private FinancialCountTenderItemIfc enteredTender;

  public MAXSummaryReportTender(MAXSummaryReport summaryReport, FinancialCountTenderItemIfc expectedTender, FinancialCountTenderItemIfc enteredTender)
  {
	super(summaryReport,expectedTender,enteredTender);
	this.summaryReport = summaryReport;
    this.expectedTender = expectedTender;
    this.enteredTender = enteredTender;
  }


  public int getNumberItemsOut()
  {
    int itemsOut = 0;
    String subTenderType = "";
    FinancialCountTenderItemIfc[] fci = this.summaryReport.getFinancialEntity().getTotals().getTenderCount().getTenderItems();
    for (int i = 0; i < fci.length; i++)
    {
      int type = fci[i].getTenderDescriptor().getTenderType();
      if (type == 1)
      {
        subTenderType = fci[i].getTenderDescriptor().getTenderSubType();
        if (!subTenderType.equals(getTenderType()))
          continue;
        itemsOut = fci[i].getNumberItemsOut();
        break;
      }

      if (type == 0)
      {
        itemsOut = fci[i].getNumberItemsOut();
        ReconcilableCountIfc[] tillPickups = this.summaryReport.getFinancialEntity().getTotals().getTillPickups();
        itemsOut = addReconcileableCountsOut(itemsOut, tillPickups);

        ReconcilableCountIfc[] tillPayOuts = this.summaryReport.getFinancialEntity().getTotals().getTillPayOuts();
        itemsOut = addReconcileableCountsOut(itemsOut, tillPayOuts);
      } else {
        if (!Integer.toString(type).equals(getTenderType()))
          continue;
        itemsOut = fci[i].getNumberItemsOut();
        break;
      }
    }
    return itemsOut;
  }

  public int getNumberItemsIn()
  {
    int itemsIn = 0;
    String subTenderType = "";
    FinancialCountTenderItemIfc[] fci = this.summaryReport.getFinancialEntity().getTotals().getCombinedCount().getEntered()
    .getSummaryTenderItems();
    for (int i = 0; i < fci.length; i++)
    {
      int type = fci[i].getTenderDescriptor().getTenderType();
      if (type == 1)
      {
        subTenderType = fci[i].getTenderDescriptor().getTenderSubType();
        if (!subTenderType.equals(getTenderType()))
          continue;
        itemsIn = fci[i].getNumberItemsIn();
        break;
      }
      
      if (type == 7)
      {
        subTenderType = fci[i].getTenderDescriptor().getTenderSubType();
        if (!subTenderType.equals(getCouponSubType()))
          continue;
        itemsIn = fci[i].getNumberItemsIn();
        break;
      }

      if (type == 0)
      {
        itemsIn = fci[i].getNumberItemsIn();
        ReconcilableCountIfc[] tillLoans = this.summaryReport.getFinancialEntity().getTotals().getTillLoans();
        itemsIn = addReconcileableCountsIn(itemsIn, tillLoans);

        ReconcilableCountIfc[] tillPayIns = this.summaryReport.getFinancialEntity().getTotals().getTillPayIns();
        itemsIn = addReconcileableCountsIn(itemsIn, tillPayIns);
      }
      else {
        if (!Integer.toString(type).equals(getTenderType()))
          continue;
        itemsIn = fci[i].getNumberItemsIn();
        break;
      }
    }
    return itemsIn;
  }
  

  public CurrencyIfc getAmountCounted()
  {
    if (this.summaryReport.countTillAtClose)
    {
    	if(enteredTender.getTenderType()==-1)
      return this.enteredTender.getAmountTotal().subtract(summaryReport.getFloatClose());
    	else
    		return this.enteredTender.getAmountTotal();
    }
    return this.expectedTender.getAmountTotal();
  }
  
 /* public CurrencyIfc getTotalAmountCounted()
  {
    if (this.summaryReport.countTillAtClose)
    {
      return this.enteredTender.getAmountTotal().subtract(summaryReport.getFloatClose());
    }
    return this.expectedTender.getAmountTotal();
  }
*/
  private String couponSubType;
  public String getCouponSubType()
  {
    return couponSubType;
  }
  public void setCouponSubType(String couponSubType)
  {
	  this.couponSubType=couponSubType;
  }

}