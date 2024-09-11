/** ===========================================================================
 * Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
 *
 * Rev 1.0 Hitesh.dua 		15feb,2017	Initial revision.
 *  Changes for till reconcile related reports: print entered count as counted tender to be print on receipt. 
 * ============================================================================
 */


package max.retail.stores.pos.reports;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.StoreSafeIfc;
import oracle.retail.stores.pos.reports.SummaryReport;
import oracle.retail.stores.pos.reports.SummaryReportTender;

/**
 * @author Hitesh.Dua
 *
 */
public class MAXSummaryReport extends SummaryReport
{
  private static final long serialVersionUID = -6999792375153031126L;
  public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
  protected static final Logger logger = Logger.getLogger(MAXSummaryReport.class);


  protected boolean countTillAtClose = true;

  protected String baseCountryCode = DomainGateway.getBaseCurrencyInstance().getCountryCode();

  private Comparator<FinancialCountTenderItemIfc> tenderItemComparator = null;

  protected StoreSafeIfc safe = null;

  public MAXSummaryReport()
  {
    super();
  }
  
  /**MAX Rev 1.1 Change : Start**/
	protected boolean isOffline = false;
	protected String OFFLINE_HEADER = "Offline Mode";
  protected CurrencyIfc dummyCashIn = DomainGateway.getBaseCurrencyInstance("0.00");
  protected CurrencyIfc dummyCashOut = DomainGateway.getBaseCurrencyInstance("0.00");
	/**MAX Rev 1.1 Change : End**/
	
  
  /**MAX Rev 1.1 Change : Start**/
	public boolean isOffline() {
		return isOffline;
	}

	public void setOffline(boolean isOffline) {
		this.isOffline = isOffline;
	}
	/**MAX Rev 1.1 Change : End**/

  public MAXSummaryReport(AbstractFinancialEntityIfc afe)
  {
    super(afe);
    this.finEnt = afe;
    setDocumentType("SummaryReport");
  }


  /* (non-Javadoc)
   * @see oracle.retail.stores.pos.reports.SummaryReport#getTenders()
   * overrided for rev 1.0
   */
  @Override
  public SummaryReportTender[] getTenders()
  {
    ReconcilableCountIfc combinedCount = finEnt.getTotals().getCombinedCount();
    FinancialCountTenderItemIfc[] enteredTenders = combinedCount.getEntered().getSummaryTenderItems();
    Arrays.sort(enteredTenders, getTenderItemComparator());
   
    MAXSummaryReportTender[] summaryTenders = new MAXSummaryReportTender[enteredTenders.length];
   // SummaryReportTender[] summaryTenders = new SummaryReportTender[expectedTenders.length];
    for (int i = enteredTenders.length - 1; i >= 0; i--)
    {
      FinancialCountTenderItemIfc enteredTender = combinedCount.getEntered().getSummaryTenderItemByDescriptor(enteredTenders[i].getTenderDescriptor());
      

    /*  if (enteredTender == null)
      {
        enteredTender = combinedCount.getEntered().getTenderItem(expectedTenders[i].getTenderDescriptor(), false);
      }*/

      summaryTenders[i] = new MAXSummaryReportTender(this, enteredTenders[i], enteredTender);
      if(enteredTender.getTenderType()==7)
      summaryTenders[i].setCouponSubType(enteredTender.getTenderSubType());
    }
    return summaryTenders;
  }

  
  /* overrided only to return MAXSummaryReportTender instance instead of SummaryReportTender
 * @see oracle.retail.stores.pos.reports.SummaryReport#getTenderTotals()
 */
public SummaryReportTender[] getTenderTotals()
  {
    ReconcilableCountIfc combinedCount = this.finEnt.getTotals().getCombinedCountForTenderReconcile();
    FinancialCountTenderItemIfc[] expectedTotals = combinedCount.getExpected().getFinancialCountTenderTotalsByCurrency();
    FinancialCountTenderItemIfc[] enteredTotals = combinedCount.getEntered().getFinancialCountTenderTotalsByCurrency();
    Arrays.sort(expectedTotals, getTenderItemComparator());
    Arrays.sort(enteredTotals, getTenderItemComparator());

    SummaryReportTender[] summaryTenders = new MAXSummaryReportTender[expectedTotals.length];
    for (int i = expectedTotals.length - 1; i >= 0; i--)
    {
      summaryTenders[i] = new MAXSummaryReportTender(this, expectedTotals[i], enteredTotals[i]);
    }
    return summaryTenders;
  }

  
}