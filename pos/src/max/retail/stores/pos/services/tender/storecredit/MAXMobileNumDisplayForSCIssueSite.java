/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.0 	May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.storecredit;

import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

public class MAXMobileNumDisplayForSCIssueSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 1L;
	
	public void arrive(BusIfc bus) {
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		 MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		
		 String customerogMobile = null;
		 if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){
				MAXSaleReturnTransaction trans=(MAXSaleReturnTransaction) cargo.getTransaction();
			     customerogMobile=trans.getCustOgMobile();
		 }
		 else {
				if(cargo.getTransaction() instanceof MAXLayawayTransaction)
			    {
				MAXLayawayTransaction trans=(MAXLayawayTransaction) cargo.getTransaction();
			     customerogMobile=trans.getCustOgMobile();
			    }
		 }
		 String[] args = new String[1];
			args[0]=customerogMobile;
			if (args[0] != null)
			{
		    	 if (args[0].equalsIgnoreCase(""))
			
				UIUtilities.setDialogModel(uiManager, DialogScreensIfc.ACKNOWLEDGEMENT, "MobileNumberNoticeforSCIssue1", args, CommonLetterIfc.SUCCESS);
			else
				UIUtilities.setDialogModel(uiManager, DialogScreensIfc.ACKNOWLEDGEMENT, "MobileNumberNoticeforSCIssue", args, CommonLetterIfc.SUCCESS);	
			}
			else
				UIUtilities.setDialogModel(uiManager, DialogScreensIfc.ACKNOWLEDGEMENT, "MobileNumberNoticeforSCIssue1", args, CommonLetterIfc.SUCCESS);

	}
}
