/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/TooManyMatchesFoundAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:10 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:31  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:22:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:34:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:29:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when too many items have been found by the
    database query.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TooManyMatchesFoundAisle extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Displays an error screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // update  bean model
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("ErrorTooManyMatches");
        dialogModel.setType(DialogScreensIfc.ERROR);

        // display dialog
        POSUIManagerIfc  ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
    }
}
