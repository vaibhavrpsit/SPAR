/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/LookupTransactionLinkedCustomerNotFoundAisle.java /main/2 2014/05/14 14:41:28 cgreene Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* cgreene     05/14/14 - rename retrieve to resume
* mjwallac    01/31/12 - incorporate code review comments.
* mjwallac    01/27/12 - Forward port: SQL Exception when trying to save a
*                        resumed order transaction that had been linked to a
*                        customer, but customer was deleted before resuming.
* mjwallac    01/27/12 - Creation
* spurkaya    10/25/11 - Modified Not to retrieve order transaction and
*                        display an error dialog when the associated customer
*                        is deleted.
*
* ===========================================================================
*/
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

@SuppressWarnings("serial")
public class LookupTransactionLinkedCustomerNotFoundAisle extends PosLaneActionAdapter
{

    public static final String revisionNumber = "$Revision: /main/2 $";

    /**
     * Display an error message, wait for user acknowledgment.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("SuspendTransactionLinkedCustomerNotFound");
        model.setButtonLetter(0, CommonLetterIfc.FAILURE);
        model.setType(DialogScreensIfc.ERROR);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
  
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
