/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/PrintInquiryInfoSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:15 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/10/04 21:36:41  lzhao
 *   @scr 7300: use the texts from receipt boundle.
 *
 *   Revision 1.6  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.5  2004/06/24 16:56:44  dfierling
 *   @scr 5815 - updated to handle LocalizedDeviceException error class.
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 24 2003 19:28:40   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.LocalizedDeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.receipt.InstantCreditInquiryInfoSlip;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class PrintInquiryInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 7443939786605840944L;
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();
        boolean deviceError = false;

        try
        {
            PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
            InstantCreditInquiryInfoSlip slip = (InstantCreditInquiryInfoSlip)pdm.getParameterBeanInstance(ReceiptTypeConstantsIfc.INSTANTCREDIT_INQUIRY);
            slip.setInstantCredit(cargo.getInstantCredit());
            // set training flag
            slip.setTrainingMode((cargo.getRegister().getWorkstation().isTrainingMode()));
            // prints info
            pdm.printReceipt((SessionBusIfc)bus, slip);
        }
        catch (PrintableDocumentException e)
        {
            logger.warn(e.toString());

            deviceError = true;

            // get utility manager
            UtilityManager utility = (UtilityManager)bus.getManager(UtilityManagerIfc.TYPE);

            String msg[] = new String[1];
            if (e.getNestedException() instanceof LocalizedDeviceException)
            {
                msg[0] = e.getNestedException().getLocalizedMessage();
            }
            else if (e.getNestedException() instanceof DeviceException &&
                    ((DeviceException)e.getNestedException()).getErrorCode() != DeviceException.UNKNOWN)
            {
                msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                        BundleConstantsIfc.PRINTER_OFFLINE);
            }
            else
            {
                msg[0] = utility.retrieveDialogText("RetryContinue.UnknownPrintingError",
                        "An unknown error occurred while printing.");
            }

            UIUtilities.setDialogModel(ui, DialogScreensIfc.RETRY_CONTINUE, "RetryContinue", msg);

        }
        if (!deviceError)
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }

    }
}
