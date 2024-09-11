/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/OrderIDEnteredRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:33 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:51 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:52 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:13:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:41:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   27 Dec 2001 16:18:38   cir
 * Replace getResponse by ui.getInput()
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Sep 24 2001 13:00:14   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common; 
 
//foundation imports 
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
//------------------------------------------------------------------------------ 
/** 
    Retrieves the order ID from the ui and sets the search method to order id
    and the date range flag to false.
    <P>       
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/ 
//------------------------------------------------------------------------------ 
 
public class OrderIDEnteredRoad extends PosLaneActionAdapter 
{ 
    /** 
        revision number for this class 
    **/ 
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
 
    //------------------------------------------------------------------------------ 
    /** 
        Retrieves the order ID from the ui and sets the search method to order id
        and the date range flag to false.
        <P>       
        @param bus the bus arriving at this road 
    **/ 
    //------------------------------------------------------------------------------ 
 
    public void traverse(BusIfc bus)
    {
        OrderCargo cargo = (OrderCargo) bus.getCargo();
                
        //Initialize Variables
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        //set the cargo search values for lookup order service
        cargo.setOrderID(ui.getInput());
        cargo.setSearchMethod(OrderSearchCargoIfc.SEARCH_BY_ORDER_ID);
        cargo.setDateRange(false);
    }
}
