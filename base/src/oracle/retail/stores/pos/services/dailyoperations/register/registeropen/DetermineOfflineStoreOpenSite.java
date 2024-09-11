/* ===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registeropen/DetermineOfflineStoreOpenSite.java /main/1 2014/07/23 15:44:29 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   07/23/14 - Code review updates
 *    rhaight   07/03/14 - store offline open revisions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registeropen;

import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * @since 14.1
 * 
 * This site determines if the Offline Store Open tour should be called.
 * It checks the entered date against the date in the StoreStatus in
 * RegisterOpenCargo. If the entered date is later than the StoreStatus date,
 *  the offline store open tour is called.
 *  
 * @author rhaight
 *
 */
public class DetermineOfflineStoreOpenSite extends PosSiteActionAdapter
{

    /** Serial Version ID */
	private static final long serialVersionUID = -563371633593725942L;

	//--------------------------------------------------------------------------
    /**
        Determines if an offline store open is needed
        @param bus the bus
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        LetterIfc ltr = new Letter("No");
        
        RegisterOpenCargo cargo = (RegisterOpenCargo)bus.getCargo();
        
        EYSDate newBusDate = cargo.getStoreStatus().getBusinessDate();
        EYSDate oldBusDate = cargo.getRegister().getBusinessDate();
        
        if (newBusDate.after(oldBusDate))
        {
            //This is the condition where a store open needs to be executed
            ltr = new Letter("Yes");
            cargo.getStoreStatus().setStatus(AbstractFinancialEntityIfc.STATUS_CLOSED);
        }
        else
        {
            // This is the current date on the register - assume store open
           
        }
        
        bus.mail(ltr, BusIfc.CURRENT);
        
        
    }
}
