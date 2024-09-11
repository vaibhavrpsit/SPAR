/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillclose/EnterTillSite.java /main/11 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   03/20/12 - Checking the new alwaysPromptForTillId flag. This
 *                         allows POS and MPOS to use the same site.
 *    blarsen   03/20/12 - If there is exactly one open till on the register,
 *                         close it (don't prompt operator for the till id).
 *                         (For MPOS.)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:29 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:49:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:57:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:28:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:28:36   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:18:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillclose;

// Foundation imports
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    Displays screen for entering till identifier.
    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class EnterTillSite extends PosSiteActionAdapter
{                                       // begin class EnterTillSite

    /**
       revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
       site name constant
    **/
    public static final String SITENAME = "EnterTillSite";

    //--------------------------------------------------------------------------
    /**
       Displays screen for entering till identifier.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()

        TillCloseCargo cargo = (TillCloseCargo)bus.getCargo();

        TillIfc theOpenTill = getOpenTill(cargo.getRegister());

        // if there is exactly one open till, then close that till and don't prompt for the till id
        // TODO MPOS: step through this on POS - confirm this will work for both apps
        if (cargo.alwaysPromptForTillId() || theOpenTill == null)
        {
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.ENTER_TILL_ID, new POSBaseBeanModel());
        }
        else
        {
            cargo.setTill(theOpenTill);
            cargo.setTillID(theOpenTill.getTillID());
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }

    }                                   // end arrive()

    /**
     * For MPOS do not prompt if there is exactly one till open.
     * If there is exactly one till open, return that till.
     *
     * @param cargo
     * @return the one open till
     */
    protected TillIfc getOpenTill(RegisterIfc register)
    {
        TillIfc theOpenTill = null;
        int openTillCount = 0;

        if (register != null)
        {
            for (TillIfc till: register.getTills())
            {
                if (till.getStatus() == TillIfc.STATUS_OPEN)
                {
                    openTillCount++;
                    theOpenTill = till;
                }
            }
        }

        if (openTillCount == 1)
        {
            return theOpenTill;
        }
        else
        {
            return null;
        }

    }

}                                       // end class EnterTillSite
