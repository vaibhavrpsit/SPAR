/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/find/CustomerHistoryLaunchShuttle.java /main/11 2014/07/24 14:13:35 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/24/14 - Avoid NullPointerException at
 *                         DisplayCustomerHistoryDetailSite.
 *    vtemker   07/14/11 - Updated comment: need to add the linked customer
 *                         details for display on status bar
 *    vtemker   07/14/11 - Linked customer details loaded into
 *                         ReturnCustomerCargo (Bug 12686871)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:38 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/09/23 00:07:15  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.3  2004/02/12 16:49:27  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:41:51  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:32:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:22   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:04   msg
 * Initial revision.
 * 
 *    Rev 1.3   10 Dec 2001 15:56:00   baa
 * Fix minor defects in customer
 * Resolution for POS SCR-99: Data on Customer Delete screen should be non-editable
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.find;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;

//------------------------------------------------------------------------------
/**

    $Log:
     3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
     2    360Commerce 1.1         3/10/2005 10:20:38 AM  Robert Pearse   
     1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
    $
    Revision 1.4  2004/09/23 00:07:15  kmcbride
    @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

    Revision 1.3  2004/02/12 16:49:27  mcs
    Forcing head revision

    Revision 1.2  2004/02/11 21:41:51  rhafernik
    @scr 0 Log4J conversion and code cleanup

    Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
    updating to pvcs 360store-current


**/
//------------------------------------------------------------------------------

public class CustomerHistoryLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1255358048788263321L;

    protected CustomerCargo customerCargo = null;
    //--------------------------------------------------------------------------
    /**
        @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
       customerCargo = (CustomerCargo)bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
        @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // grab the cargo
        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();
        cargo.setCustomer(customerCargo.getCustomer());
        cargo.setRegister(customerCargo.getRegister());
        // Fix for the issue with customer name (Bug DB ID: 12686871)
        // Need to carry the linked customer details as this is used for display
        // on the status bar
        cargo.setLinkCustomer(customerCargo.isCustomerLink());
        cargo.setPreviousCustomer(customerCargo.getPreviousCustomer());
        cargo.setOperator(customerCargo.getOperator());
    }
}
