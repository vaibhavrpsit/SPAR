/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.tender.storecredit;

import java.util.Calendar;
import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

public class MAXValidateStoreCreditGracePeriodAisle extends PosLaneActionAdapter  {
	
	private static final long serialVersionUID = 82577555985851068L;
	private static final String ST_EXPIRED_WITH_GRACEPERIOD = "Stexpiredwithgraceperiod";
	
	public void traverse(BusIfc bus) 
	{
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
		.getManager(ParameterManagerIfc.TYPE);
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();
	  try {
			int st_Grace_Period = pm.getIntegerValue(
					MAXTenderConstantsIfc.STORE_CREDIT_GRACE_PERIOD).intValue();
			EYSDate exp_Date=(EYSDate) tenderAttributes.get(MAXTenderConstantsIfc.STORE_CREDIT_EXPIRED);
			Calendar cal = Calendar.getInstance();
            cal.setTime(exp_Date.dateValue());
            cal.add(Calendar.DAY_OF_MONTH, st_Grace_Period);
			EYSDate final_expirationDate = new EYSDate(cal.getTime());
			EYSDate current_Date= new EYSDate();
			if(current_Date.after(final_expirationDate))
    		{
            	displayDialog(bus, DialogScreensIfc.ERROR, "StoreCreditGracePeriodExpired", null, ST_EXPIRED_WITH_GRACEPERIOD);
       		    return;
    		}
			
			
			
			else
			{
				bus.mail("Valid", BusIfc.CURRENT);
			}
        } catch (ParameterException e) {
			
			logger.error("" + Util.throwableToString(e) + "");
		}
	}
	
	protected void displayDialog(BusIfc bus, int screenType, String message,
			String[] args, String letter) 
	{
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		if (letter != null) {
			UIUtilities.setDialogModel(ui, screenType, message, args, letter);
		}
	}
	

}
