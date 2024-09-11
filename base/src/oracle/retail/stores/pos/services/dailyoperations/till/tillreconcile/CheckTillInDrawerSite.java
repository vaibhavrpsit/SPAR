/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/CheckTillInDrawerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse   
 *
 Revision 1.2  2004/07/09 23:27:01  dcobb
 @scr 5190 Crash on Pickup Canadian Checks
 @scr 6101  Pickup of local cash gives "Invalid Pickup" of checks error
 Backed out awilliam 5109 changes and fixed crash on pickup of Canadian checks.
 *
 Revision 1.1  2004/04/15 18:57:00  dcobb
 @scr 4205 Feature Enhancement: Till Options
 Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site checks if the till in the drawer is the till to be reconciled.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckTillInDrawerSite extends PosSiteActionAdapter
{
    /**
     revision number of this class
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Checks if the till is in the drawer. Mails "Yes" letter if the till
        is in the drawer, "No" letter otherwise.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TillReconcileCargo cargo = (TillReconcileCargo)bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        int drawerStatus = register.getDrawer(DrawerIfc.DRAWER_PRIMARY).getDrawerStatus();
        String tillID = cargo.getTillID();
        
        String letterName = CommonLetterIfc.NO;
        if (drawerStatus == AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED)
        {
            String occupyingTillID = register.getDrawer(DrawerIfc.DRAWER_PRIMARY).getOccupyingTillID();
            if (occupyingTillID.equalsIgnoreCase(tillID))
            {
                letterName = CommonLetterIfc.YES;
            }
        }
            
        bus.mail(letterName);
    }

}
