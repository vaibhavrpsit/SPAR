/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/GetSerialNumberRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:33 mszekely Exp $
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
 *     3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse   
 *     2    360Commerce 1.1         3/10/2005 10:21:51 AM  Robert Pearse   
 *     1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse   
 *    $
 *    Revision 1.3  2004/02/12 16:51:26  mcs
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 21:51:37  rhafernik
 *    @scr 0 Log4J conversion and code cleanup
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:11:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:41:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   29 Jan 2002 18:36:24   cir
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

//foundation imports
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This road is traversed when a serial number has been entered and the
    user has chosen to accept the input.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetSerialNumberRoad extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sets serial number entered in the UI.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PickupOrderCargo cargo   = (PickupOrderCargo)bus.getCargo();
        POSUIManagerIfc     ui   = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        //set the serial number in the serialized items array
        AbstractTransactionLineItemIfc[] lineItems = cargo.getSerializedItems();
        int counter = cargo.getSerializedItemsCounter();
        ((SaleReturnLineItemIfc)lineItems[counter]).setItemSerial(ui.getInput());
        cargo.setSerializedItemsCounter(counter + 1);
    }
}
