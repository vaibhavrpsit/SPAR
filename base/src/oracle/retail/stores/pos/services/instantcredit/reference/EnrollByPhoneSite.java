/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/reference/EnrollByPhoneSite.java /rgbustores_13.4x_generic_branch/5 2011/06/09 13:48:30 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    sgu       05/11/11 - fix instant credit cargo to use the new reponse
 *                         object
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         5/7/2008 8:55:11 PM    Alan N. Sinton  CR
 *      30295: Code modified to present  Function Unavailable dialog for House
 *       Account and Instant Credit when configured with ISD.  Code reviewed
 *      by Anda Cadar.
 * 3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:23 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:44  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 24 2003 19:54:12   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.reference;

import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/5 $
 */
public class EnrollByPhoneSite extends SiteActionAdapter
{
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -4483571684138615821L;

    /**
     * Constant for HOUSE_ACCOUNT_ENROLLMENT_FUNCTION_UNAVAILABLE
     */
    public static final String HOUSE_ACCOUNT_ENROLLMENT_FUNCTION_UNAVAILABLE = "HouseAccountEnrollmentFunctionUnavailable";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ResponseCode display = cargo.getInstantCreditResponse().getResponseCode();

        if (ResponseCode.RequestNotSupported.equals(display))
        {
            //show the account not found acknowledgment screen
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(HOUSE_ACCOUNT_ENROLLMENT_FUNCTION_UNAVAILABLE);
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "NotSupported");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InstantCreditCardError");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);

            if (display == null)
            {
                display = ResponseCode.Unknown;
            }
            dialogModel.setArgs(new String[] { display.toString() });
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }
}
