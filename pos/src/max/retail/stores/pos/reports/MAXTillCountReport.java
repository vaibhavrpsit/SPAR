/** ===========================================================================
 * Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
 *
 * Rev 1.0 Hitesh.dua 		28feb,2017	Initial revision.
 *  Changes for till reconcile related reports: print entered count as counted tender to be print on receipt. 
 * ============================================================================
 */
package max.retail.stores.pos.reports;

import java.util.HashMap;
import java.util.Map;

import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.pos.reports.TillCountReport;
import oracle.retail.stores.pos.reports.TillCountReportItem;

public class MAXTillCountReport extends TillCountReport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TillCountReportItem[] getTillCountReportItems()
	  {
	    if (this.tillCountReportItems == null)
	    {
	      Map reportItems = new HashMap(0);

	      FinancialCountTenderItemIfc[] summaryTenders = this.count.getSummaryTenderItems();
	      for (int i = 0; i < summaryTenders.length; i++)
	      {
	        FinancialCountTenderItemIfc summaryTender = (FinancialCountTenderItemIfc)summaryTenders[i].clone();
	        TenderDescriptorIfc td = summaryTender.getTenderDescriptor();
	        MAXTillCountReportItem reportItem =null;
	        if(summaryTender.getTenderType()==7){
	        	reportItem=(MAXTillCountReportItem)reportItems.get(Integer.toString(td.getTenderType())+":"+td.getTenderSubType());
	        }
	        else{
	        	reportItem=(MAXTillCountReportItem)reportItems.get(Integer.toString(td.getTenderType()));
	        }
	        if (reportItem == null)
	        {
	          reportItem = new MAXTillCountReportItem(this);
	          reportItem.setTenderDescriptor(td);
	          if (td.getTenderType() == 7)
		      {
	        	  ((MAXTillCountReportItem)reportItem).setCouponSubType(td.getTenderSubType());
	        	  reportItems.put(Integer.toString(td.getTenderType())+":"+td.getTenderSubType(), reportItem);
		      }
	          else{  
	          reportItems.put(Integer.toString(td.getTenderType()), reportItem);
	          }
	        }

	        if (td.getTenderType() == 0)
	        {
	          FinancialCountTenderItemIfc[] detailedTenders = this.count.getDetailTenderItemBySummaryDescription(td);
	          if (detailedTenders != null)
	          {
	            reportItem.setDenominations(detailedTenders);
	          }
	        }
	        else if (td.getTenderType() == 1)
	        {
	          reportItem.addSubType(summaryTender);
	        }

	        reportItem.addTotal(summaryTender);
	      }

	      this.tillCountReportItems = ((TillCountReportItem[])reportItems.values().toArray(new TillCountReportItem[reportItems.size()]));
	      //Arrays.sort(this.tillCountReportItems);
	    }
	    return this.tillCountReportItems;
	  }

}
