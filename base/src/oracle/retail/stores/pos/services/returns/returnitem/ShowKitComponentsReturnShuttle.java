/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/ShowKitComponentsReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:00 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:14 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/03/12 19:36:48  epd
 *   @scr 3561 Updates for handling kit items in non-retrieved no receipt returns
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   20 Nov 2001 09:12:10   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   Sep 21 2001 11:25:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// pos
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.returns.returnkit.ReturnKitCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the return item service with kit component items that
    were modified by the return transaction service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ShowKitComponentsReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7230010468043148183L;

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       Return kit cargo
    **/
    ReturnKitCargo kitCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from child service.
       <P>
       @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        kitCargo = (ReturnKitCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by parent service.
       <P>
       @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        ReturnItemCargo riCargo = (ReturnItemCargo)bus.getCargo();
        
        // copy all return items and sale items to item cargo from kit cargo
        ReturnItemIfc[] returnItems = kitCargo.getReturnItems();
        for (int i = 0; returnItems != null && i < returnItems.length; i++)
        {
            riCargo.addReturnItem(returnItems[i]);
            riCargo.setCurrentItem(riCargo.getCurrentItem()+1);
        }
        
        SaleReturnLineItemIfc[] lineItems = kitCargo.getReturnSaleLineItems();
        for (int i = 0; lineItems != null && i < lineItems.length; i++)
        {
            riCargo.addReturnSaleLineItem(lineItems[i]);
        }
        
    }

}
