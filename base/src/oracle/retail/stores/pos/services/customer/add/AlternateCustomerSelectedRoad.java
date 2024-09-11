/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/add/AlternateCustomerSelectedRoad.java /main/12 2012/07/10 14:37:13 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     07/10/12 - Removed extraneous line.
 *    icole     07/10/12 - Fix NullPointerException attempting add the same
 *                         customer info.
 *    acadar    05/23/12 - CustomerManager refactoring
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:28 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:10  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:41:08  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:34:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:10:58   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:42   msg
 * Initial revision.
 * 
 *    Rev 1.4   18 Feb 2002 18:43:34   baa
 * save original customer info
 * Resolution for POS SCR-1242: Selecting 'Enter' on Duplicate ID screen in Customer returns the wrong information
 *
 *    Rev 1.3   25 Jan 2002 21:02:14   baa
 * partial fix ui problems
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.2   08 Jan 2002 09:37:10   baa
 * fix flow problems
 * Resolution for POS SCR-516: Customer Select screens missing parentheses around area code
 *
 *    Rev 1.1   23 Oct 2001 16:52:02   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:14:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.add;

// imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DualListBeanModel;

//--------------------------------------------------------------------------
/**
    Road to traverse when the user has selected a customer from the list of
    customers returned by the database.    <p>
    $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class AlternateCustomerSelectedRoad extends PosLaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    //----------------------------------------------------------------------
    /**
        Replaces the entered customer with the selected one.
        <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // get the selected customer
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DualListBeanModel model = (DualListBeanModel)uiManager.getModel(POSUIManagerIfc.CUSTOMER_SELECT_ADD);
        int selection = model.getSelectedRow();
        
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        CustomerIfc[] customerListAsArray = (CustomerIfc[]) cargo.getCustomerList().toArray();

        // if a customer was selected
        if (selection >= 0 && selection < cargo.getCustomerList().size())
        {
            //cargo.setOriginalCustomer(cargo.getCustomer());
            // set the Customer to the one selected by the operator
            cargo.setCustomer(customerListAsArray[selection]);

            // Save a copy of the original, unedited customer.
            // This will be compared with the cargo when the user
            // presses Done or Link to see if a database save must be performed.
            // A save is warranted when any changes have been made to the customer data.

        }
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
