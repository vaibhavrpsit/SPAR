/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/IsNotWebManagedOrderSignal.java /main/3 2014/05/14 17:22:12 sgu Exp $
 * ===========================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/14/14 - remove check for ATG order in original return
 *                         transaction
 *    cgreene   03/11/14 - add support for returning ASA ordered items
 *    cgreene   02/06/14 - add trafficlight that displays dialog when an action
 *                         is attempted on non-editable web-managed
 *                         transaction.
 *    cgreene   02/05/14 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * A signal that checks the transaction to see if it is from the web commerce
 * system (ATG). In which case, that transaction should not be allowed to
 * be modified, so a dialog warning will be displayed, and the road associated
 * with this signal will be marked as not clear.
 *
 * @author cgreene
 * @since 14.0.1
 */
public class IsNotWebManagedOrderSignal implements TrafficLightIfc
{
    private static final long serialVersionUID = -4163439604106668356L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(IsNotWebManagedOrderSignal.class);

    /** Resource ID for the Dialog Message that a Web Managed order cannot be modified. */
    public static final String DIALOG_RESOURCE_ID = "WebManagedOrderCannotBeModified";

    /**
     * Checks to see if the till is suspended.
     * 
     * @return true if the till is suspended, false otherwise.
     */
    public boolean roadClear(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

        if (isWebManaged(cargo))
        {
            logger.info("Transaction is web-managed. Preventing modification.");
            showWarningDialog(bus);
            return false;
        }

        return true;
    }

    /**
     * Returns true if there is a sale transaction in the cargo 
     * that is flagged a web-managed (from ATG).
     *
     * @param cargo
     * @return
     */
    protected boolean isWebManaged(SaleCargoIfc cargo)
    {
        boolean webManaged = cargo.getTransaction() != null && cargo.getTransaction().isWebManagedOrder();
        return webManaged;
    }

    /**
     * Display the dialog that warns the function being attempted cannot be
     * performed on this transaction.
     *
     * @param bus
     */
    protected void showWarningDialog(BusIfc bus)
    {
        // Get the managers from the bus
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean", display the error dialog.
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(DIALOG_RESOURCE_ID);
        dialogModel.setType(DialogScreensIfc.ERROR_NO_BUTTONS);

        // set and display the model
        ui.showDialogAndWait(POSUIManagerIfc.DIALOG_POPUP, dialogModel);
    }
}