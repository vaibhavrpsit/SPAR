/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/CancelOfflineLayawayRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:46 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/02/12 16:50:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:20:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:21:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OfflinePaymentBeanModel;

//------------------------------------------------------------------------------
/**
    This class resets the offlinePaymentBeanModel. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class CancelOfflineLayawayRoad extends LaneActionAdapter
{                                       // begin class CancelOfflineLayawayRoad
    /**
        lane name constant
    **/
    public static final String LANENAME = "CancelOfflineLayawayRoad";
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //--------------------------------------------------------------------------
    /**
        resets offlinePaymentBeanModel <P>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()
        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);      
                                                   
        OfflinePaymentBeanModel beanModel =
            (OfflinePaymentBeanModel) ui.getModel(POSUIManagerIfc.LAYAWAY_OFFLINE);
        if (beanModel != null)
        {
            beanModel.resetModel();
        }       
    }                                   // end traverse()
}                                   // end CancelOfflineLayawayRoad

    
