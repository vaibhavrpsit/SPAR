/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
MGTenderStoreCreditIfc
  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.storecredit;

import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyDecimal;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.tender.ChosenTenderSubTourLaunchShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;

//--------------------------------------------------------------------------
/**
     This class is used for credit and debit tender launch shuttle.
     $Revision: 3$
 **/
//--------------------------------------------------------------------------
public class MAXChosenTenderSubTourLaunchShuttle extends ChosenTenderSubTourLaunchShuttle
{
    /** previous letter **/
    protected String letter = null;
    protected CurrencyDecimal transactionTotal=null;
    //----------------------------------------------------------------------
    /**
        This method calls the super method and then checks the previous 
        letter in order to let the creditdebit service which start site to
        use.
        @param bus
        @see com.extendyourstore.foundation.tour.ifc.ShuttleIfc#load(com.extendyourstore.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
    	//System.out.println("MAXChosenTenderSubTourLaunchShuttle :");
    	super.load(bus);
        LetterIfc ltr = bus.getCurrentLetter();
        
        // Added by Himanshu
        TenderCargo cargo=(TenderCargo) bus.getCargo();
        transactionTotal=(CurrencyDecimal) cargo.getTransaction().getTransactionTotals().getBalanceDue();       
        
        if (ltr != null)
        {
            letter = ltr.getName();
        }
    }

    //----------------------------------------------------------------------
    /**
        This method just calls the super
        @param bus
        @see com.extendyourstore.foundation.tour.ifc.ShuttleIfc#unload(com.extendyourstore.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        TenderCargo childCargo = (TenderCargo)bus.getCargo();
        
		// Rev 1.0 starts
        ((MAXTenderCargo)childCargo).setTotalTenderAmount(transactionTotal);
		// Rev 1.0 ends
        childCargo.setSubTourLetter(letter);        
        childCargo.setPreTenderMSRModel(callingCargo.getPreTenderMSRModel());
        
    }    
}
