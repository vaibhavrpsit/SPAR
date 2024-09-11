/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/history/NarrowHistorySearchAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:44 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:49:31  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:44:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jan 31 2003 16:18:08   bwf
 * Changed from CustomerCargo to use the correct ReturnCustomerCargo.
 * Resolution for 1938: If no trans. occurred during date range, wrong error screen opens
 * 
 *    Rev 1.1   Aug 07 2002 19:33:56   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:32:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:36   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:18   msg
 * Initial revision.
 * 
 *    Rev 1.2   05 Nov 2001 17:36:56   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 * 
 *    Rev 1.1   23 Oct 2001 16:53:34   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.history;

// foundation imports
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateSearchBeanModel;
//------------------------------------------------------------------------------
/**
   $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class NarrowHistorySearchAisle extends LaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8630947862306746671L;

   /**
       revision number supplied by PVCS
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        // Display the screen
        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DateSearchBeanModel model = new DateSearchBeanModel();
        EYSDate businessdate = cargo.getRegister().getBusinessDate();
        model.setStartDate(businessdate);
        model.setEndDate(businessdate);
        ui.showScreen(POSUIManagerIfc.NARROW_SEARCH, model);
    }
 }
