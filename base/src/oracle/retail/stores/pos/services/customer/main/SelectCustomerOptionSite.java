/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/main/SelectCustomerOptionSite.java /main/13 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:07 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:56:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   May 27 2003 08:48:08   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.2   May 11 2003 23:00:28   baa
 * defecxt fixes
 * 
 *    Rev 1.1   04 Sep 2002 09:05:50   djefferson
 * added support for Business Customer
 * Resolution for POS SCR-1605: Business Customer
 * 
 *    Rev 1.0   Apr 29 2002 15:32:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:14   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:58   msg
 * Initial revision.
 * 
 *    Rev 1.3   16 Nov 2001 10:34:22   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.2   24 Oct 2001 15:04:56   baa
 * customer history feature
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-229: Disable Add/Delete buttons when calling Customer for Find only
 *
 *    Rev 1.1   23 Oct 2001 16:54:30   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:16:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.main;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
//--------------------------------------------------------------------------
/**
    Determine what the user wants to do with a customer record.
    <p>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class SelectCustomerOptionSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
       Prompts the operator for action to take on a customer record.
       The operator can select Add, Find, or Delete from this menu. <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();

        // Call the journalling utility method passing in employee ID and trans ID
        // Since this is Customer functionality, transaction ID may be null.
        // Entry to Customer Service should only be journalled upon the initial entry.
        if (cargo.isInitialEntry())
        {
            if (cargo.getTransactionID() != null)
            {

              CustomerUtilities.journalCustomerEnter(bus, cargo.getOperator().getEmployeeID(),
                                                     cargo.getTransactionID());
            }
            cargo.setInitialEntry(false);
        }

        // show the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = new POSBaseBeanModel();


        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

        //Check if customer service was call for lookup purposes only
        nModel.setButtonEnabled(CommonActionsIfc.ADD, cargo.isAddCustomerEnabled() && !cargo.isFindOnlyMode());
        nModel.setButtonEnabled(CommonActionsIfc.ADDBUS, cargo.isAddBusinessEnabled() && !cargo.isFindOnlyMode());
        nModel.setButtonEnabled(CommonActionsIfc.DELETE, cargo.isDeleteEnabled() && !cargo.isFindOnlyMode());
        model.setLocalButtonBeanModel(nModel);
        ui.showScreen(POSUIManagerIfc.CUSTOMER_OPTIONS, model);

    }
}
