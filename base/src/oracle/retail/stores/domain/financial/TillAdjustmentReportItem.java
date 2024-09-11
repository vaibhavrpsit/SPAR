/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/TillAdjustmentReportItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    glwang    02/26/09 - get count type from TillAdjustmentTransaction
 *                         instead of FinancialCount
 *    glwang    02/26/09 - enhance till pickup to support detail count
 *    glwang    02/25/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;

/**
 * A class to contain an array of FinancialCountTenderItem for a
 * tenderDescriptor. 
 *
 * This class is designed to be used in TillPickup/TillLoan receipt
 * blue print.
 * @since 13.1
 */
public class TillAdjustmentReportItem implements Serializable
{
	private static final long serialVersionUID = -3478372479724929254L;
	
	/**
	 * summary tender 
	 */
	private FinancialCountTenderItemIfc summaryTender;	
	
	/**
	  an array of FinancialCountTenderItem objects 
      matching the specified tenderDescriptor.
    */
	private List<FinancialCountTenderItemIfc> detailTenders;
	
	/**
	 * 
	 * @param td
	 */
	public TillAdjustmentReportItem(FinancialCountTenderItemIfc summary)
	{
		this.summaryTender = summary;
		detailTenders = new ArrayList<FinancialCountTenderItemIfc>();
	}
	
	/**
	 * 
	 * @return summaryTender
	 */
	public FinancialCountTenderItemIfc getSummaryTender()
	{
		return this.summaryTender;
	}
	
	/**
	 * 
	 * @param summary summary 
	 */
	public void setSummaryTender(FinancialCountTenderItemIfc summary)
	{
		this.summaryTender = summary;
	}
	
	/**
	 * 
	 * @param detailTenders
	 */
	public void addDetailTender(FinancialCountTenderItemIfc delTender)
	{
		this.detailTenders.add(delTender);
	}
	
	/**
	 * 
	 * @return an array of FinancialCountTenderItem objects matching 
	 * the specified summary tender descriptor.
	 */
	public FinancialCountTenderItemIfc[] getDetailTenders()
	{
		return this.detailTenders.toArray(new FinancialCountTenderItem[this.detailTenders.size()]);
	}
	
	
	/**
	 * 
	 * @return true if summary tender description is check.
	 */
	public boolean isTenderTypeCheck()
	{
		return (this.summaryTender.getDescription() != null) &&
				this.summaryTender.getDescription().equals(DomainGateway.getFactory()
                .getTenderTypeMapInstance()
                .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK));
	}
	
	public int getNumberItemTotals()
	{
		int total = 0;
		for(FinancialCountTenderItemIfc detTender: detailTenders)
		{
			total +=detTender.getNumberItemsTotal();
		}
		return total;
	}
}
