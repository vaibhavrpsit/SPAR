/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/EnterStoreNumberSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:04:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
        Displays screen for entering the store number.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EnterStoreNumberSite extends PosSiteActionAdapter
{                                                                               // begin class EnterStoreNumberSite

    /**
       site name constant
    **/
    public static final String SITENAME = "EnterStoreNumberSite";

    //--------------------------------------------------------------------------
    /**
       Displays screen for for entering the store number.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                                                   // begin arrive()

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        ui.showScreen(POSUIManagerIfc.STORE_NUMBER, new POSBaseBeanModel());


    }                                                                   // end arrive()
}                                                                               // end class EnterStoreNumberSite
