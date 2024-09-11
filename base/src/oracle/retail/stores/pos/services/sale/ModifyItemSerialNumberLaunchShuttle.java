/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ModifyItemSerialNumberLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  11/11/09 - Serialisation code changes
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/15 21:43:29  baa
 *   @scr 0 continue moving out deprecated files
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   13 Jan 2004 09:00:34   awilliamson
 * Initial revision.
 *
 *    Rev 1.0   Aug 29 2003 16:01:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:17:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:30   msg
 * Initial revision.
 *
 *    Rev 1.0   14 Nov 2001 06:50:44   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.sale.SerializedItemLaunchShuttle;


/**
    This shuttle transfers data from the POS service to the Serialized Item service.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public class ModifyItemSerialNumberLaunchShuttle extends SerializedItemLaunchShuttle
{
    /**
       Copies information from the cargo used in the POS service.
       @param  bus     Service Bus
    **/
    public void load(BusIfc bus)
    {
        // retrieve cargo from the parent
        super.load(bus);
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        lineItem = cargo.getLineItem();
    }
    /**
       Copies information from the cargo used in the POS service.
       <P>
       @param  bus     Service Bus
    **/
    //public void unload(BusIfc bus)
    //see superclass for unload functionality!!!
}
