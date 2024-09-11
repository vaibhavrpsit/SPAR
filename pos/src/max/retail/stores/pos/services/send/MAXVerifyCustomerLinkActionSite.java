/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0	    May 04, 2017	    Kritica Agarwal 	GST Changes
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.send;

import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;

/**
 * @author Kritica Agarwal
 */
public class MAXVerifyCustomerLinkActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -1293568246502056523L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SendManagerIfc txnMgr = null;
        try
        {
            txnMgr = (SendManagerIfc)ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
        }
        catch (ManagerException e)
        {
            // default to product version
            txnMgr = new SendManager();
        }
        
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        if (txnMgr.isCustomerLinked(cargo))
        {
            if ( cargo.getCustomer() == null )
            {
                cargo.setCustomer( cargo.getTransaction().getCustomer() );
            }
          //Change for Rev 1.0 :Starts
            //bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
          
            if(!((cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc) && ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isGstEnable()))
            	bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            else
            	bus.mail(new Letter(CommonLetterIfc.DONE), BusIfc.CURRENT);	
            //Change for Rev 1.0 : Ends
        }
        else
        {
            
            bus.mail(new Letter("Link"), BusIfc.CURRENT);
        }
    }    
}
