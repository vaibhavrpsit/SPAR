/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/VerifyCustomerLinkActionSite.java /main/11 2011/02/16 09:13:29 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:32 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/05/26 16:37:47  lzhao
 *   @scr 4670: add capture customer and bill addr. same as shipping for send
 *
 *   Revision 1.3  2004/02/13 21:10:51  epd
 *   @scr 0
 *   Refactoring to the Send Application Manager
 *
 *   Revision 1.2  2004/02/12 21:54:19  epd
 *   @scr 0
 *   Refactors for Send tour
 *
 *   Revision 1.1  2004/02/12 21:36:28  epd
 *   @scr 0
 *   These files comprise all new/modified files that make up the refactored send service
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;

import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;

/**
 * @author epd
 */
public class VerifyCustomerLinkActionSite extends PosSiteActionAdapter
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
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {
            
            bus.mail(new Letter("Link"), BusIfc.CURRENT);
        }
    }    
}
