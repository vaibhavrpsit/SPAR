/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
Rev 1.0   Rahul		01/April/2014	Changes done for Business Date Mismatch Alert Prompt
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    Check to see if the register is in training mode.  If so, switch
    the normal register to the training mode register.
    $Revision: 3$
 **/
//--------------------------------------------------------------------------
public class MAXBusinessDateMismatchSite extends PosSiteActionAdapter
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
		Letter letter=null;
		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		EYSDate businessDate=cargo.getStoreStatus().getBusinessDate();
		letter=new Letter(CommonLetterIfc.CONTINUE);
		if(!cargo.isBusinessDateMismatchOverrideSuccess())
		{
		// display time and date using toString()
		EYSDate currentDate=DomainGateway.getFactory().getEYSDateInstance();
//		if(currentDate.compareTo(businessDate)==0)
		if((currentDate.getYear()==businessDate.getYear())&&(currentDate.getMonth()==businessDate.getMonth())&&(currentDate.getDay()==businessDate.getDay()))
		{
			letter = new Letter(CommonLetterIfc.CONTINUE); 
					}
		else
		{
			cargo.setAccessFunctionID(MAXRoleFunctionIfc.BUSINESS_DATE_MISMATCH);
			letter = new Letter(CommonLetterIfc.INVALID); 
			
		/*	DialogBeanModel dialogBeanmodel = new DialogBeanModel();
			dialogBeanmodel.setResourceID(Invalid_Date);
			dialogBeanmodel.setType(DialogScreensIfc.ERROR);
			dialogBeanmodel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBeanmodel);*/
		}
		}
		bus.mail(letter, BusIfc.CURRENT);

	}
	
	public void depart(BusIfc bus)
	{
		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
//		cargo.setAccessFunctionID(0);
	}
}
