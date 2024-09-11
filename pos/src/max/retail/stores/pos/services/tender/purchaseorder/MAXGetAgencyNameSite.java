/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  08/May/2013	Jyoti Rawal, Initial Draft: Changes for Hire Purchase Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.purchaseorder;

// java imports
import java.util.ArrayList;

import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;


//--------------------------------------------------------------------------
/**
    Gets the agency name.
    $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXGetAgencyNameSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6076747933657609162L;
	/** revision number **/
    public static final String revisionNumber = "$Revision: 3$";
    
    //----------------------------------------------------------------------
    /**
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
       MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
       String otherAgencyName = (String)cargo.getTenderAttributes().get(TenderConstants.OTHER_AGENCY_NAME); 
       POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
       if (otherAgencyName != null)
       { 

          
           UtilityManagerIfc utility =
             (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                               
           /**
            * Rev 1.0 changes start here
            */
           // create list dropdown values, "yes" and "no"
           ArrayList aList = new ArrayList();
           aList.add(utility.retrieveCommonText("Yes", "Yes"));
           aList.add(utility.retrieveCommonText("No", "No"));
           // populate model
           DataInputBeanModel dModel = new DataInputBeanModel();     
           dModel.setSelectionChoices("transactionTaxableField", aList);
			/**
	            * Rev 1.0 changes end here
	            */
           // display the appropriate screen
           UtilityIfc util;
           try
           {
               util = Utility.createInstance();
           }
           catch (ADOException e)
           {
               String message = "Configuration problem: could not instantiate UtilityIfc instance";
               logger.error(message, e);
               throw new RuntimeException(message, e);
           }
           if ("Y".equals(util.getParameterValue("CaptureTransactionTaxStatus", "N")))
           {
               ui.showScreen(POSUIManagerIfc.PURCHASE_ORDER_AGENCY_NAME_360, dModel);
           }
           else
           {
        	    dModel = new DataInputBeanModel();     
               ui.showScreen(POSUIManagerIfc.PURCHASE_ORDER_AGENCY_NAME, dModel);
           }
       }
       else
       {
           bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
       }
    }
    public void depart(BusIfc bus)
    {
        LetterIfc letter = (LetterIfc) bus.getCurrentLetter();
        MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
        // If the user entered a number of Traveller Checks
        if (letter.getName().equals("Continue"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            // Get the purchase order number
            DataInputBeanModel model = (DataInputBeanModel)ui.getModel();
            String approvalCode = (String)model.getValueAsString("approvalCodeField");
            cargo.getTenderAttributes().put(MAXTenderConstants.APPROVAL_CODE, approvalCode);
			 cargo.setApprovalCode(approvalCode);
			 bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }
}