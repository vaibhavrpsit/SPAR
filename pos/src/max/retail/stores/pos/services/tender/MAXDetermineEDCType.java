/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *		
 * @author kumar Vaibhav
 *
 *
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

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

public class MAXDetermineEDCType extends PosSiteActionAdapter
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
		EventOriginatorInfoBean.setEventOriginator("DetermineTenderSubTourStartSite.arrive");
			ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		
		POSBaseBeanModel model=new POSBaseBeanModel();
		NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
		boolean  transReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
		     if(transReentryMode)
		     {
			     localModel.setButtonEnabled("OnlineCredit", false);
			     model.setLocalButtonBeanModel(localModel);
			     if (("CreditDebit").equals(cargo.getSubTourLetter()))
		    	 ui.showScreen(MAXPOSUIManagerIfc.CREDIT_DEBIT_PINELAB_INNOVITI, model);
		     }
		     else
		     {
		    	  localModel.setButtonEnabled("OnlineCredit", true);
				     model.setLocalButtonBeanModel(localModel);
		    	if (("CreditDebit").equals(cargo.getSubTourLetter()))
		    	 ui.showScreen(MAXPOSUIManagerIfc.CREDIT_DEBIT_PINELAB_INNOVITI, model);
				
		     }
		     
		     }
	}
