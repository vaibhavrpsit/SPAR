/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/nosale/NoSaleSlipPrintAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/07/14 - use bundle text to display error messages
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   11/13/08 - configure print beans into Spring context
 *    mdecama   10/22/08 - Added I18N changes for ReasonCodes
 *
 * $Log:
 *  5    360Commerce 1.4         4/17/2007 11:20:14 AM  Ashok.Mondal    CR
 *       10531 :V7.2.2 merge to trunk.
 *  4    360Commerce 1.3         1/25/2006 4:11:33 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  3    360Commerce 1.2         3/31/2005 4:29:09 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:42 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:45 PM  Robert Pearse
 * $:
 *  5    .v700     1.2.1.1     10/26/2005 14:07:05    Deepanshu       CR 6122:
 *       Imported the required packages missed in the previous checkin
 *  4    .v700     1.2.1.0     10/26/2005 13:51:57    Deepanshu       CR 6122:
 *       Set the bundle properties for NoSaleSlip
 *  3    360Commerce1.2         3/31/2005 15:29:09     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:23:42     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:12:45     Robert Pearse
 * $
 * Revision 1.8  2004/08/23 16:15:57  cdb
 * @scr 4204 Removed tab characters
 *
 * Revision 1.7  2004/07/22 15:05:55  awilliam
 * @scr 4465 no sale receipt print control transaction header and barcode are missing
 *
 * Revision 1.6  2004/06/25 18:20:15  jlemieux
 * removing unnecessary cast
 *
 * Revision 1.5  2004/06/24 16:57:16  dfierling
 * @scr 5815 - updated to handle LocalizedDeviceException error class.
 *
 * Revision 1.4  2004/03/03 23:15:10  bwf
 * @scr 0 Fixed CommonLetterIfc deprecations.
 *
 * Revision 1.3  2004/02/12 16:51:18  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:51:48  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Jan 09 2004 12:00:20   DCobb
 * Removed printer offline halt behavior.
 * Resolution for 3502: Remove "Printer Offline Behavior" parameter
 *
 *    Rev 1.0   Aug 29 2003 16:03:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jun 12 2003 16:50:26   sfl
 * Let the Print Receitps paramater value have control over
 * Number No Sale Receipts paramtere value.
 * Resolution for POS SCR-2506: No Sale Printing- The “Number No Sale Receipt” and “Print Receipt” Parameters are not work.ing.
 *
 *    Rev 1.3   Jun 12 2003 16:28:34   sfl
 * Reset the initial value of Number NO Sale Receipts to be zero.
 * Resolution for POS SCR-2506: No Sale Printing- The “Number No Sale Receipt” and “Print Receipt” Parameters are not work.ing.
 *
 *    Rev 1.2   Jun 12 2003 15:07:44   sfl
 * Retrieved the Number NO Sale Receipts paramter value and use it for multiple prints of No Sale slips.
 * Resolution for POS SCR-2181: No -Sale Paramater '"NumberNoSaleReceipts" is not working
 *
 *    Rev 1.1   Apr 09 2003 13:30:34   KLL
 * clean-up
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 *    Rev 1.0   Apr 09 2003 13:19:04   KLL
 * Initial revision.
 * Resolution for POS SCR-1884: Printing Functional Requirements
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.nosale;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.LocalizedDeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.NoSaleSlip;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Print the gift card inquiry slip for the customer.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @since 5.2.0
 **/
public class NoSaleSlipPrintAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -3770654829303803493L;

    /**
     * site name
     */
    public static final String LANENAME = "NoSaleSlipPrintAisle";

    /**
     * revision number assigned by revision control
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected String[] header = null;

    /**
     * Print the gift card inquiry slip for the customer.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        NoSaleCargo cargo = (NoSaleCargo)bus.getCargo();
        NoSaleTransactionIfc trans = cargo.getNoSaleTrans();
        boolean sendmail = true;
        boolean printReceipt = true;
        try
        {
            if (pm.getStringValue("PrintReceipts").compareTo("Y") == 0)
            {
                printReceipt = true;
            }
            else
            {
                printReceipt = false;
            }
        }
        catch (ParameterException pe)
        {
            logger.error("Parameter exception " + pe.getMessage());

        }
        try
        {
            if (printReceipt)
            {
                PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus
                .getManager(PrintableDocumentManagerIfc.TYPE);
                NoSaleSlip noSaleSlip = (NoSaleSlip)pdm.getParameterBeanInstance(ReceiptTypeConstantsIfc.NO_SALE);
                noSaleSlip.setNoSaleTransaction(trans);
                pdm.printReceipt((SessionBusIfc)bus, noSaleSlip);
            }
            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        }
        catch (PrintableDocumentException e)
        {
            logger.warn(bus.getServiceName() + ": Unable to print no sale slip: " + e.getMessage());

            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);

            if (e.getNestedException() != null)
            {
                logger.warn(bus.getServiceName() + ": DeviceException.NestedException:\n"
                        + Util.throwableToString(e.getNestedException()));
            }
            
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            
            String msg[] = new String[1];

            if (e.getCause()!=null && e.getCause() instanceof LocalizedDeviceException)
            {
                msg[0] = e.getCause().getLocalizedMessage();
            }
            else if (e.getCause()!=null && e.getCause() instanceof DeviceException
                    && ((DeviceException)e.getCause()).getErrorCode() != DeviceException.UNKNOWN)
            {
                msg[0] = utility.retrieveDialogText("RetryCancel.PrinterOffline", "Printer is offline.");
            }
            else
            {
                msg[0] = utility.retrieveDialogText("RetryCancel.UnknownPrintingError",
                        "An unknown error occurred while printing.");
            }
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryCancel");
            model.setType(DialogScreensIfc.RETRY_CANCEL);
            model.setArgs(msg);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

            sendmail = false;
        }

        if (sendmail)
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }
}