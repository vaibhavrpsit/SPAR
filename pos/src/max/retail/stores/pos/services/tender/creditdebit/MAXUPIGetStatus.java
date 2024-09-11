/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *		
 * Rev 1.0  Dec 15, 2010 6:00:30 AM Sanjay.Bhalla
 * Initial revision.
 * Resolution for FES_LMG_India_Gift_Voucher_v1.0
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
     This class determines the correct start position.
     $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class MAXUPIGetStatus extends PosSiteActionAdapter
{
    //----------------------------------------------------------------------
    /**
        This method just mails the correct letter.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
	
	public void arrive(BusIfc bus) {
		
		 TenderCargo cargo = (TenderCargo) bus.getCargo();
		//EventOriginatorInfoBean.setEventOriginator("DetermineTenderSubTourStartSite.arrive");
			ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		
		POSBaseBeanModel model=new POSBaseBeanModel();
		NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
		boolean  transReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
		     if(transReentryMode)
		     {
			     localModel.setButtonEnabled("OnlineCredit", false);
			     model.setLocalButtonBeanModel(localModel);
		    	 ui.showScreen(MAXPOSUIManagerIfc.PINELAB_UPI_GETSTATUS, model);
		     }
		     else
		     {
		    	  localModel.setButtonEnabled("OnlineCredit", true);
				     model.setLocalButtonBeanModel(localModel);
		    	 ui.showScreen(MAXPOSUIManagerIfc.PINELAB_UPI_GETSTATUS, model);
				
		     }
		     
		     }
	}
