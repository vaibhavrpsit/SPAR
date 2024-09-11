/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev	1.0 	27-Aug-2015		Geetika4.Chugh		<Comments>	
*
********************************************************************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;
import java.util.Vector;

import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.VerifyCustomerIDTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerIDBeanModel;

/**
 *  Checks to see if customer ID needs to be input
 */
public class MAXPineLabVerifyCustomerIDUISite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        // if the current credit tender is of type House Account
        // and its manually entered
        // and we haven't already captured it, get the customer information
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap tenderAttributes = cargo.getTenderAttributes();
        if ((getCardType(tenderAttributes, bus) == CreditTypeEnum.HOUSECARD) &&
            tenderAttributes.get(TenderConstants.MSR_MODEL) == null &&
            !capturedCustomerInfo(cargo.getTenderAttributes()))
        {
            HashMap tdoAttributes = new HashMap();
            tdoAttributes.put(VerifyCustomerIDTDO.BUS, bus);            

            // build bean model helper
            TDOUIIfc tdo = null;
            try
            {
                tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.VerifyCustomerID");
            }
            catch (TDOException tdoe)
            {
                // TODO Auto-generated catch block
                tdoe.printStackTrace();
            }
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.ENTER_ID_INFO, tdo.buildBeanModel(tdoAttributes));
        }
        else
        {
            bus.mail(new Letter("Continue"), BusIfc.CURRENT);
        }
    }
    
    /* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#depart(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void depart(BusIfc bus)
    {
        // if information was entered, capture it
        if (bus.getCurrentLetter().getName().equals("Next"))
        {
            // Get the information entered in the UI
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            CustomerIDBeanModel model = (CustomerIDBeanModel)ui.getModel(POSUIManagerIfc.ENTER_ID_INFO);
        
            // put info into tender attributes
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            Vector idTypes = model.getIDTypes();
            cargo.getTenderAttributes().put(TenderConstants.ID_TYPE, idTypes.get(model.getSelectedIDType()));
            cargo.getTenderAttributes().put(TenderConstants.ID_COUNTRY, model.getCountry());
            cargo.getTenderAttributes().put(TenderConstants.ID_STATE, model.getState());
            cargo.getTenderAttributes().put(TenderConstants.ID_EXPIRATION_DATE, model.getExpirationDate());
        }
    }
    
    /**
     * Determine the entered card number type
     * @param tenderAttributes
     * @return
     */
    protected CreditTypeEnum getCardType(HashMap tenderAttributes, BusIfc bus)
    {
    	
            
        // Get the card number from MSR model if swiped
        EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
       /* if (tenderAttributes.get(TenderConstants.MSR_MODEL) != null)
        {
            cardNumber = ((MSRModel)tenderAttributes.get(TenderConstants.MSR_MODEL)).getEncipheredCardData().getTruncatedAcctNumber();
        }
        else
        {
            cardNumber = (String)tenderAttributes.get(TenderConstants.NUMBER);
        }*/
        
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        return utility.determineCreditType(cardData);            
    }
    
    /**
     * Determine from tender attributes whether we already captured this info
     * @param tenderAttributes
     * @return
     */
    protected boolean capturedCustomerInfo(HashMap tenderAttributes)
    {
        boolean result = true;
        
        if (tenderAttributes.get(TenderConstants.ID_TYPE) == null ||
            tenderAttributes.get(TenderConstants.ID_STATE) == null ||
            tenderAttributes.get(TenderConstants.ID_EXPIRATION_DATE) == null)
        {
            result = false;
        }
        
        return result;
    }
}
