/*
 * Created on Dec 3, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package max.retail.stores.pos.services.tender.storecredit;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderConstantsIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * @author blj
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MAXGetStoreCreditNumberUISite extends PosSiteActionAdapter { 
    
    //    --------------------------------------------------------------------------
    /**
        This is the arrive method which will display the screen.
        @param bus BusIfc   
    **/
    //  --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {    
    	
    	// Added by Himanshu
    	
    	 TenderCargo cargo = (TenderCargo)bus.getCargo();
    	 HashMap tenderAttributes = cargo.getTenderAttributes();
    	 
    	 if(tenderAttributes.get(MAXTenderConstantsIfc.STORE_CREDIT_EXPIRED)!=null)
    	 {
    		 bus.mail(new Letter("CheckGracePeriod"), BusIfc.CURRENT);
    	 }
    	
    	// Ended by Himanshu
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        PromptAndResponseModel promptModel = new PromptAndResponseModel();
        UtilityIfc utility;
        try
        {
            utility = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        
        String preprintedStoreCredit = utility.getParameterValue("PrePrintedStoreCredit", "N");
        if (preprintedStoreCredit.equals("Y"))
        {
            promptModel.setMaxLength("12");
        }
        else
        {
            promptModel.setMinLength("14");
        }
        
        beanModel.setPromptAndResponseModel(promptModel);
        
        

        ui.showScreen(POSUIManagerIfc.STORE_CREDIT_TENDER_NUMBER, beanModel);
                
    }
    //  ----------------------------------------------------------------------
      /**
          Depart method retrieves input.
          @param  bus     Service Bus
      **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        LetterIfc letter = (LetterIfc) bus.getCurrentLetter();
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // If the user entered a gift certificate number
        if (letter.getName().equals(CommonLetterIfc.NEXT))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            boolean isScanned = ((POSBaseBeanModel)ui.getModel()).getPromptAndResponseModel().isScanned();
    
            // Get the gift certificate number and put in the cargo
            cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput().trim());

            if (isScanned)
            {
  // Changes starts for code merging(commenting below line as per MAX)
                //cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, TenderLineItemIfc.ENTRY_METHOD_AUTO);
            	cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Automatic);
  // Changes ends for code merging
            }
            else
            {
// Changes starts for code merging(commenting below line as per MAX)
               // cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, TenderLineItemIfc.ENTRY_METHOD_MANUAL);
            	cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
 // changes ends for code merging
            }
        }
    }
}
