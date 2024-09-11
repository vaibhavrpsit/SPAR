/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0       Tanmaya			24/05/2013		Initial Draft: Changes for Store Credit
  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.services.tender.ChosenRefundSubTourLaunchShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;

//--------------------------------------------------------------------------
/**

 $Revision: 3$
 **/   
//--------------------------------------------------------------------------
public class MAXChosenRefundSubTourLaunchShuttle extends ChosenRefundSubTourLaunchShuttle
{
    /** previous letter **/
    protected String letter = null;
    protected static final String REFUND = "Refund";
    protected TenderADOIfc nextTender = null;
    protected EYSDate storeCreditExpiryDate = null;
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
        super.load(bus);
        LetterIfc ltr = bus.getCurrentLetter();
        if (ltr != null)
        {
            String letterName = ltr.getName();
            if (letterName != null)
            {
                letter = letterName + "Refund";
            }
        }
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        storeCreditExpiryDate = ((MAXTenderCargo)bus.getCargo()).getStoreCreditExpirtDate();
        //((MAXTenderCargo)callingCargo).setStoreCreditExpirtDate(((MAXTenderCargo)bus.getCargo()).getStoreCreditExpirtDate());
       // ((MAXTenderCargo)callingCargo).setStoreCreditExpirtDate((EYSDate)callingCargo.getTenderAttributes().get(MGTenderConstantsIfc.STORE_CREDIT_EXPIRED));
        nextTender = cargo.getNextTender();
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
        if(childCargo instanceof MAXTenderCargo)
        	((MAXTenderCargo)childCargo).setStoreCreditExpirtDate(storeCreditExpiryDate);
        childCargo.setSubTourLetter(letter);   
        childCargo.setNextTender(nextTender);
    }    
}