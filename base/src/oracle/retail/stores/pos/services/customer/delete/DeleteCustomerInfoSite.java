/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/delete/DeleteCustomerInfoSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:41:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Mar 20 2003 18:18:48   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.0   Apr 29 2002 15:33:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:06   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:24:50   msg
 * Initial revision.
 * 
 *    Rev 1.2   19 Dec 2001 12:02:12   baa
 * disable Enter and Clear keys on delete
 * Resolution for POS SCR-98: Invalid Data Notice cites Postal Code vs. Ext Postal Code
 * Resolution for POS SCR-460: Clear & Enter enabled on Customer Delete screen
 *
 *    Rev 1.1   16 Nov 2001 10:32:56   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.delete;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.customer.common.EnterCustomerInfoSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
//--------------------------------------------------------------------------
/**
    Put up Customer Delete screen.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class DeleteCustomerInfoSite extends EnterCustomerInfoSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Displays the Customer Delete screen. <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // model to use for the UI
        CustomerInfoBeanModel model = getCustomerInfoBeanModel(bus);
        NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();

        // turn off editing
        model.setEditableFields(false);
        // disable Clear and Undo Buttons
        globalModel.setButtonEnabled(CommonActionsIfc.NEXT,false);
        globalModel.setButtonEnabled(CommonActionsIfc.CLEAR,false);        
        model.setGlobalButtonBeanModel(globalModel);
        // show the screen
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        uiManager.showScreen(POSUIManagerIfc.CUSTOMER_DELETE, model);
    }


}
