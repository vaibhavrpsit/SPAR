/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  08/May/2013	Jyoti Rawal, Initial Draft: Changes for Hire Purchase Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;

public interface MAXTenderPurchaseOrderIfc extends TenderPurchaseOrderIfc {

	public String getApprovalCode();

	// ----------------------------------------------------------------------------
	/**
	 * Sets approval code.
	 * <P>
	 * 
	 * @param value
	 *            approval code
	 **/
	// ----------------------------------------------------------------------------
	public void setApprovalCode(String value);
}
