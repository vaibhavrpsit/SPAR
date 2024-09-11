/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registeropen/CancelOfflineStoreOpenSite.java /main/1 2014/07/23 15:44:30 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   07/23/14 - Code review updates
 *    rhaight   07/15/14 - Support for cancelling offline store open
 * 
 * ===========================================================================
 * $Log:
 *    
 * header update
 * ===========================================================================
 */package oracle.retail.stores.pos.services.dailyoperations.register.registeropen;

import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * @since 14.1
 * 
 * CancelOfflineStoreOpenSite restores the StoreStatus in the cargo
 * to the previous version carried in the RegisterOpenCargo. This site
 * is accessed when the Offline Store Open tour is cancelled
 * 
 * @author rhaight
 *
 */
public class CancelOfflineStoreOpenSite extends PosSiteActionAdapter 
{
	
	 /** Serial Version ID */
	private static final long serialVersionUID = -7420076316847271792L;

	/**
	 * Restores the store status in the RegisterOpenCargo to the previous
	 * version on a cancelled offline store open.
	 * 
	 * @param bus
	 */
	public void arrive(BusIfc bus)
	{
		 RegisterOpenCargo cargo = (RegisterOpenCargo) bus.getCargo();
	     StoreStatusIfc status = cargo.getStoreStatus();
	     StoreStatusIfc rollbackStatus = cargo.getRollbackStoreStatus();
	     
	     
		logger.warn("Cancelling offline store open for " + status.getStore().getStoreID() + 
				" on business day " + status.getBusinessDate() + " reverting to business date "
				+ rollbackStatus.getBusinessDate());
		 
		cargo.setStoreStatus(rollbackStatus);
		
		LetterIfc letter = new Letter(CommonLetterIfc.CANCEL);
		
		bus.mail(letter, BusIfc.CURRENT);
	}
}
