/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/main/CustomerDeleteLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:20 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:49:33  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:00  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:32:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:00   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Dec 2001 15:56:06   baa
 * Fix minor defects in customer
 * Resolution for POS SCR-99: Data on Customer Delete screen should be non-editable
 *
 *    Rev 1.0   16 Nov 2001 10:51:00   baa
 * Initial revision.
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.main;

// foundation imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;

//--------------------------------------------------------------------------
/**
    Shuttle data from the Customer services.
    $Log:
     3    360Commerce 1.2         3/31/2005 4:27:35 PM   Robert Pearse   
     2    360Commerce 1.1         3/10/2005 10:20:35 AM  Robert Pearse   
     1    360Commerce 1.0         2/11/2005 12:10:20 PM  Robert Pearse   
    $
    Revision 1.4  2004/09/23 00:07:17  kmcbride
    @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

    Revision 1.3  2004/02/12 16:49:33  mcs
    Forcing head revision

    Revision 1.2  2004/02/11 21:45:00  rhafernik
    @scr 0 Log4J conversion and code cleanup

    Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
    updating to pvcs 360store-current


**/
//--------------------------------------------------------------------------
public class CustomerDeleteLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3077827365424485974L;

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * Customer Cargo
     */
    protected CustomerMainCargo customerCargo = null;
    //----------------------------------------------------------------------
    /**
       Saves the relevent information from the current service. <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
         customerCargo = (CustomerMainCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Stores information in the next service.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();

        cargo.setTransactionID(customerCargo.getTransactionID());
        if (customerCargo.getOriginalCustomer() != null)
        {
          cargo.setOriginalCustomer(customerCargo.getOriginalCustomer());
        }
        cargo.setCustomerGroups(customerCargo.getCustomerGroups());
        cargo.setEmployee(customerCargo.getEmployee());
        cargo.setLinkDoneSwitch(customerCargo.getLinkDoneSwitch());
        cargo.setOfflineExit(customerCargo.getOfflineExit());
        cargo.setRegister(customerCargo.getRegister());
        cargo.setLink(customerCargo.isLink());
        cargo.setCustomerLink(customerCargo.isCustomerLink());
        cargo.setOperator(customerCargo.getOperator());
        cargo.setOfflineIndicator(customerCargo.getOfflineIndicator());
        cargo.setPricingGroup(customerCargo.getPricingGroup());
        cargo.setPricingGroupNames(customerCargo.getPricingGroupNames());
        cargo.setAccessFunctionID(RoleFunctionIfc.CUSTOMER_DELETE);
    }

}
