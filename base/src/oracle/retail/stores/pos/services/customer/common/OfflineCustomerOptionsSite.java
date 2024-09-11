/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/OfflineCustomerOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:48 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:55:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   May 27 2003 09:17:18   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
//--------------------------------------------------------------------------
/**
    Determine what the user wants to do with a customer record when offline
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class OfflineCustomerOptionsSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Prompts the operator for action to take on a customer record.
       The operator can select Add, Find, or Delete from this menu. <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        // show the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        POSBaseBeanModel model = new POSBaseBeanModel();


        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

       //Check if customer service was call for lookup purposes only
       nModel.setButtonEnabled(CommonActionsIfc.ADD, cargo.isAddCustomerEnabled() && !cargo.isFindOnlyMode());
       nModel.setButtonEnabled(CommonActionsIfc.ADDBUS, cargo.isAddBusinessEnabled() && !cargo.isFindOnlyMode());

        ui.showScreen(POSUIManagerIfc.OFFLINE_CUSTOMER_OPTIONS,  model);

    }
}
