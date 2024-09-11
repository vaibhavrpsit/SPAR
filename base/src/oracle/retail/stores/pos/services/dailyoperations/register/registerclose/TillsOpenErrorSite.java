/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registerclose/TillsOpenErrorSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:07 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/05/18 21:11:54  jriggins
 *   @scr 5160 Added support to display unreconciled tills for the RegisterCloseTillOpenError dialog
 *
 *   Revision 1.3  2004/02/12 16:49:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:30:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:15:06   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:27:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registerclose;

// foundation imports
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Displays tills open error message. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TillsOpenErrorSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Displays tills open error message. <P>
        @param bus the bus traversing this lane
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get ui handle
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Search for the unreconciled tills. Append those till IDs for display
        TillIfc tills[] = ((RegisterCloseCargo)bus.getCargo()).getRegister().getTills();
        String dialogArgs[] = new String[0];        
        if (tills != null)
        {
            StringBuffer unreconciledTillIDs = new StringBuffer();
            
            for (int i = 0; i < tills.length; i++)
            {
                if (tills[i].getStatus() != TillIfc.STATUS_RECONCILED)
                {
                    unreconciledTillIDs.append(tills[i].getTillID()).append("  ");
                }
            }
            
            dialogArgs = new String[] {unreconciledTillIDs.toString()};
        }
        
        // set bean model
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("RegisterCloseTillsOpenError");
        model.setArgs(dialogArgs);
        model.setType(DialogScreensIfc.ERROR);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
