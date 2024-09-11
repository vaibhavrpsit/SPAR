/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/initialize/EmployeeValidatedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:59 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:51 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:24:34   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:03:38   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.initialize;
  
// foundation imports  
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This road is traversed when the sales associate has been verified.
    It updates the UI to display the Sales associate.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EmployeeValidatedRoad extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Updates the UI to display the sales associate.
        <p>
        @param bus the bus traversing this lane
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // get the cargo from the bus
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        ui.salesAssociateNameChanged(cargo.getEmployee().getPersonName().getFirstLastName());
    }
}

