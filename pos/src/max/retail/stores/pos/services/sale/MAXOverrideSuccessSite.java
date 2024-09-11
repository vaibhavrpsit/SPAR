/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
Rev 1.0   Rahul		01/April/2014	Changes done for Business Date Mismatch Alert Prompt
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**   
    Check to see if the register is in training mode.  If so, switch
    the normal register to the training mode register.
    $Revision: 3$
 **/
//--------------------------------------------------------------------------
public class MAXOverrideSuccessSite extends PosSiteActionAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     revision number
	 **/
	public static final String revisionNumber = "$Revision: 3$";

	public static final String Invalid_Date = "InvalidDate";


	//----------------------------------------------------------------------
	/**
	 * Check to see if training mode is on.  If so, set the register
	 * to the training register.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 */
	//----------------------------------------------------------------------
	public void arrive(BusIfc bus)
	{
		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
		if(cargo.getAccessFunctionID()==MAXRoleFunctionIfc.BUSINESS_DATE_MISMATCH)
		{
			cargo.setBusinessDateMismatchOverrideSuccess(true);
			bus.mail("BusinessDateSuccess", BusIfc.CURRENT);
		}
		else if(cargo.getAccessFunctionID()==MAXRoleFunctionIfc.POS_OFFLINE_ALERT)
		{
			cargo.setPosOfflineAlertOverrideSuccess(true);
			bus.mail("PosOfflineSuccess", BusIfc.CURRENT);
		}
		else
		{
			bus.mail("CancelTransactionSuccess", BusIfc.CURRENT);
		}

	}
}
