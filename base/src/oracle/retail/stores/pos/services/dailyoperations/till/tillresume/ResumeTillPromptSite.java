/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillresume/ResumeTillPromptSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:21 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:46 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:50:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Feb 17 2003 12:25:54   DCobb
 * Initial revision.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillresume;

// foundation imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site confirms that the user wants to resume the till. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ResumeTillPromptSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Confirms that the user wants to resume the till. <P>
        @param bus the bus
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Show Till Suspended dialog ...
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(AbstractFinancialCargo.TILL_SUSPENDED);
        model.setType(DialogScreensIfc.CONFIRMATION);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
