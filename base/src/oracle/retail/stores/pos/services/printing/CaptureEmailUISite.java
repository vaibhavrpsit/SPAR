/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/CaptureEmailUISite.java /main/6 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   03/13/12 - Bypassing e-mail entry dialog if it's supplied in
 *                         the Cargo (MPOS flow).
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    arathore  11/20/08 - updated for ereceipt.
 *    arathore  11/20/08 - updated for ereceipt feature.
 *    arathore  11/17/08 - updated for ereceipt feature
 *    arathore  11/17/08 - UI Site to capture eamil id for ereceipt.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.EmailAddressConstantsIfc;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

// ------------------------------------------------------------------------------
/**
 * Capture email id for eReceipt.
 *
 * @version $Revision: /main/6 $
 */
// ------------------------------------------------------------------------------
public class CaptureEmailUISite extends PosSiteActionAdapter {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/6 $";

    // --------------------------------------------------------------------------
    /**
     * Capture email id for eReceipt.
     * <p>
     *
     * @param bus  the bus arriving at this site
     */
    // --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        String currentLetter = bus.getCurrentLetter().getName();

        // get transaction from cargo
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTransaction();

        //Check if Email or Print & Email option is selected on ReceiptOptions screen.
        if(CommonLetterIfc.EMAIL.equals(currentLetter) || CommonLetterIfc.PRINT_AND_EMAIL.equals(currentLetter))
        {
            //email option is selected, set flag to print eReceipt.
            if(CommonLetterIfc.EMAIL.equals(currentLetter))
            {
                cargo.setPrintPaperReceipt(false);
                cargo.setPrintEreceipt(true);
            }
            else //Print & Email option is selected, set flags to print eReceipt and Paper copy.
            {
                cargo.setPrintPaperReceipt(true);
                cargo.setPrintEreceipt(true);
            }

            // if the email address is already on the cargo, it was supplied by the calling service, do not prompt, just use it
            if (Util.isEmpty(cargo.getEmailAddress()))
            {
                //get the customer from transaction
                CustomerIfc cust=trans.getCustomer();
                String email = "";

                //if customer is present in transaction then read email id to set in model.
                if(cust != null)
                {
                    EmailAddressIfc objEmail = cust.getEmailAddress(EmailAddressConstantsIfc.EMAIL_ADDRESS_TYPE_HOME);
                    if(objEmail != null){
                        email = objEmail.getEmailAddress();
                    }
                }

                DataInputBeanModel model = new DataInputBeanModel();

                //set the default email id from customer record.
                model.setValue("email", email);
                model.setValue("retypeEmail", email);

                // get the POS UI manager
                POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

                //Show screen
                uiManager.showScreen(POSUIManagerIfc.ERECEIPT_EMAIL_SCREEN, model);
            }
            else
            {
                bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
            }
        }
        else //Print option is selected on ReceiptOptions screen or not a sale transaction.
        {
            //if SaleReturnTransaction then set flag to print paper copy.
            if(trans instanceof SaleReturnTransactionIfc)
            {
                cargo.setPrintPaperReceipt(true);
                cargo.setPrintEreceipt(false);
            }

            //mail the next letter.
            bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
        }
    }
}
