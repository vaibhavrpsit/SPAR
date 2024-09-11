/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/add/CustomerListSite.java /main/11 2012/05/31 18:40:56 acadar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    05/23/12 - CustomerManager refactoring
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:55:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:34:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:54   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Jan 2002 21:02:14   baa
 * partial fix ui problems
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.1   23 Oct 2001 16:52:48   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:14:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.add;
import java.util.Vector;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DualListBeanModel;
//--------------------------------------------------------------------------
/**
    List the customers that match the search criteria
    and permit the user to select one. <p>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class CustomerListSite extends PosSiteActionAdapter
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 8656139397936866147L;
    
    //----------------------------------------------------------------------
    /**
        Displays the list of customers that match the search criteria. <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void arrive(BusIfc bus)
    {
        // retrieve cargo
        CustomerCargo cargo = (CustomerCargo) bus.getCargo();

        // set the customer that was queried
        // model to use for the UI
        DualListBeanModel model = new DualListBeanModel();
        Vector topList = new Vector();
        topList.addElement(cargo.getCustomer());
        model.setTopListModel(topList);
        model.setListModel(cargo.getCustomerList());

        // show the screen
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        uiManager.showScreen(POSUIManagerIfc.CUSTOMER_SELECT_ADD, model);
    }

    
}
