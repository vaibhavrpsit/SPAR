/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/UpdatePreferredCustomerRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    vapartha  02/12/10 - Added code to reset the Customer Discount when the
 *                         user doesnt have Access for the setting the customer
 *                         discount.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:25 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:12  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Mar 20 2003 18:18:48   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.0   Apr 29 2002 15:33:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:52   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:24:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Feb 2002 16:44:54   pjf
 * Set customerGroups to null when index = 0.
 * Resolution for POS SCR-1273: Changng the PCD on a linked customer to None does not update financials
 *
 *    Rev 1.0   09 Jan 2002 14:17:48   baa
 * Initial revision.
 * Resolution for POS SCR-412: Adding a new customer to db w/PCD does not save PCD entry
 *
 *    Rev 1.0   Sep 21 2001 11:14:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;
// foundation imports
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    Stores customer ID entered by the user.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class UpdatePreferredCustomerRoad extends LaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Stores customer ID entered by the user. <p>
       @param bus the bus traversing this lane
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // get cargo
        CustomerCargo cargo = (CustomerCargo) bus.getCargo();

        // handle possible change in customer group
        CustomerGroupIfc newCustomerGroup = null;

        // get index of customer group selection from cargo
        int index = cargo.getSelectedCustomerGroup();

     // sets Override flag to true if override is permitted
        cargo.setOverride(true);

        // index zero is (none) and leaves newCustomerGroup at null
        if (index > 0)
        {
            int numberGroups = cargo.getNumberCustomerGroups();
            if (numberGroups > 0 && index < numberGroups)
            {
                newCustomerGroup = cargo.getCustomerGroups()[index];
            }
            if (newCustomerGroup != null)
            {
               cargo.getCustomer().setCustomerGroups(
                                   new CustomerGroupIfc[] {newCustomerGroup} );
            }
        }
        else
        {
            //"None" was selected so set customer.customerGroups to null
            cargo.getCustomer().setCustomerGroups(null);
        }
    }

  }
