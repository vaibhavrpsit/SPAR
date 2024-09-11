/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/add/CustomerIDAlreadyExistsAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.0   Aug 29 2003 15:55:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:34:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:08   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:52   msg
 * Initial revision.
 * 
 *    Rev 1.2   18 Feb 2002 18:43:38   baa
 * save original customer info
 * Resolution for POS SCR-1242: Selecting 'Enter' on Duplicate ID screen in Customer returns the wrong information
 *
 *    Rev 1.1   16 Nov 2001 10:31:48   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:14:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.add;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//--------------------------------------------------------------------------
/**
    This aisle is traversed when the Customer Id is not unique.
    An error dialog screen is displayed for the user to acknowledge.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerIDAlreadyExistsAisle extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Displays the Duplicate Customer ID error message.
        <p>
        @param bus the bus traversing this lane
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();

        cargo.setCustomer(cargo.getOriginalCustomer());
        // Error dialog screen
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("DuplicateCustomerID");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.RETRY);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }


}
