/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/FrankApplicationSite.java /main/18 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    abhineek  09/26/12 - fixed classcast exception for Franking POS
 *    rsnayak   04/24/12 - bpt for instant credit
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       08/16/11 - check for null approval status
 *    cgreene   05/27/11 - move auth response objects into domain
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    blarsen   03/10/09 - enhanced formatting methods to consider the size of
 *                         the printed characters (Chinese prints double wide)
 *                         - also enhanced to use the new slip printer property
 *                         for the printed line width
 *    kulu      01/27/09 - minor modification based on review
 *    kulu      01/26/09 - Guard against null
 *    kulu      01/26/09 - Fix the bug that House Account enroll response data
 *                         don't have padding translation at enroll franking
 *                         slip
 *
 * ===========================================================================
 * $Log:
 * 7    360Commerce 1.6         8/24/2007 3:26:51 PM   Mathews Kochummen fix
 *      print alignment
 * 6    360Commerce 1.5         7/19/2007 1:50:02 PM   Mathews Kochummen format
 *       date,time
 * 5    360Commerce 1.4         4/25/2007 8:52:25 AM   Anda D. Cadar   I18N
 *      merge
 *
 * 4    360Commerce 1.3         1/25/2006 4:11:01 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:28:13 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:46 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:11:08 PM  Robert Pearse
 *:
 * 4    .v700     1.2.1.0     11/4/2005 11:44:42     Jason L. DeLeau 4202: Fix
 *      extensibility issues for instant credit service
 * 3    360Commerce1.2         3/31/2005 15:28:13     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:21:46     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:11:08     Robert Pearse
 *
 *Revision 1.8  2004/07/27 20:30:41  jdeleau
 *@scr 6513 Fix not all responses being printed on the application
 *
 *Revision 1.7  2004/07/22 00:06:34  jdeleau
 *@scr 3665 Standardize on I18N standards across all properties files.
 *Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *Revision 1.6  2004/06/03 21:56:58  nrao
 *@scr 3916
 *Added Slip Printing for Call Error.
 *
 *Revision 1.5  2004/04/22 17:56:30  cdb
 *@scr 4452 Removing Franking Offline Behavior parameter.
 *
 *Revision 1.4  2004/03/03 23:15:15  bwf
 *@scr 0 Fixed CommonLetterIfc deprecations.
 *
 *Revision 1.3  2004/02/12 16:50:42  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Nov 25 2003 17:32:44   nrao
 * Code Review Changes. Used message formatters instead of concatenating strings.
 *
 *    Rev 1.2   Nov 24 2003 19:43:40   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

import java.util.HashMap;
import java.util.Map;

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinterConst;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.POSPrinterActionGroupIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.FrankingReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
    Mail letter to frank tender documents

    @version $Revision: /main/18 $
**/
//------------------------------------------------------------------------------
public class FrankApplicationSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 7119441966869981703L;

    /**
     * The name of this site.
     */
    public static final String SITENAME = "FrankApplicationSite";

    /** Printer Timeout */
    protected static final String PRINTER_TIMEOUT = "PrinterTimeout";

    /** Printer Error */
    protected static final String PRINT_ERROR = "PrintError";

    /** @deprecated as of release 7.0, no replacement */
    protected static final String OFFLINE_HALT = "OfflineHalt";

    /** @deprecated as of release 7.0, no replacement */
    protected static final String OFFLINE_PROCEED = "OfflineProceed";

    /** Slip Printer Offline Retry Continue Dialog */
    protected static final String OFFLINE_DEVICE_RETRY_CONTINUE = "RetryContinue";

    /**
     * Printer offline tag
     */
    public static final String RETRY_CONTINUE_PRINTER_OFFLINE = "RetryContinue.PrinterOffline";

    /**
     * Default Printer offline text
     */
    public static final String DEFAULT_PRINTER_OFFLINE_TEXT = "Printer Offline";

    /** Proceed */
    protected static final String PROCEED = "Proceed";

    /** Endorsement Size */
    protected static int ENDORSEMENT_SIZE = 328;

    /** Spaces */
    protected static final String spaces = "     ";

    private static final String INSTANTCREDIT_DOCTYPE = "InstantCredit";

    /**
     * Print endorsements if needed.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility =  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        POSUIManagerIfc     ui           = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        String letter = new String(CommonLetterIfc.DONE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        cargo.setInitialRequest(false);
        // see if printer can frank
        boolean frankingCapable = isPrinterFrankingCapable(bus);
        cargo.setFranked(true);
        TransactionIfc trans = cargo.getTransaction();
        boolean success = true;
        boolean frankApplication = true;
        try
        {
            String frankApp = pm.getStringValue("FrankInstantCreditApplication");
            if(frankApp != null)
            {
                frankApplication = frankApp.equalsIgnoreCase("Y") ? true : false;
            }

            // If parameter is set to true, then frank the application
            if(frankApplication)
            {
                   // If franking is configured, it's not a void and there are documents to endorse...
                if (frankingCapable)
                {
                    
                    // Load the FrankingReceiptParameterBean using Spring
                    FrankingReceiptParameterBeanIfc bean = (FrankingReceiptParameterBeanIfc) BeanLocator
                            .getApplicationBean(FrankingReceiptParameterBeanIfc.BEAN_KEY);
                    bean.setDocumentType(INSTANTCREDIT_DOCTYPE);
                   
                    bean.setTransaction(trans);    
                   
                    
                    //String offlineBehavior = pm.getStringValue(OFFLINE_BEHAVIOR);

                    if (trans.isTrainingMode())
                    {
                        bean.setTrainingMode(true);
                    }

                    // retrieve authorizer response
                    InstantCreditApprovalStatus approvalStatus = cargo.getApprovalStatus();
                    String response = null;
                    if (approvalStatus != null)
                    { 
                        response = utility.retrieveCommonText(approvalStatus.getResourceKey(), approvalStatus.getResourceKey(), LocaleConstantsIfc.RECEIPT);
                    }
                    
                    bean.setInstantCreditResponse(response);
                    if (cargo.getReferenceNumber() != null && !cargo.getReferenceNumber().trim().equals(""))
                    {
                        bean.setReferenceNumber(cargo.getReferenceNumber());
                    }

                    String argText= new String(utility.retrieveText("receipt", BundleConstantsIfc.RECEIPT_BUNDLE_NAME, "ApplicationErrorDialog", "Application"));
                    success = endorseDocument(bus, argText,
                            ui, dialogModel,bean);
                }
            }
        }
        catch (ParameterException pe)
        {
            logger.error(Util.throwableToString(pe));
        }

        if(!success)
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
    }

    /**
     * Determines if printer is franking-capable.
     *
     * @param bus instance of bus
     * @return true if printer is franking-capable, false otherwise.
     */
    protected boolean isPrinterFrankingCapable(BusIfc bus)
    {
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        boolean frankingCapable = true;

        try
        {
            frankingCapable = ((Boolean) pda.isFrankingCapable()).booleanValue();
        }
        catch (DeviceException e)
        {
            frankingCapable = false;
        }

        return (frankingCapable);
    }

    /**
     * Starts automatic endorsement process.
     *
     * @param bus BusIfc
     * @param argText String
     * @param text String
     * @param ui
     * @param dialogModel
     * @return completedTask boolean
     */
    protected boolean endorseDocument(BusIfc bus, String argText,
                                      POSUIManagerIfc ui, DialogBeanModel dialogModel,FrankingReceiptParameterBeanIfc bean)
    {
        boolean completedTask = false;
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        StatusBeanModel statusModel = new StatusBeanModel();
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
                .getManager(PrintableDocumentManagerIfc.TYPE);
        try
        {
            parModel.setArguments(argText);
            baseModel.setPromptAndResponseModel(parModel);
            // Add this item to the list so we can remove it from the original
            // list if there's a failure.
            ui.showScreen(POSUIManagerIfc.INSERT_TENDER, baseModel);
            pda.beginSlipInsertion();
            pda.endSlipInsertion();
            String text = printManager.getPreview((SessionBusIfc)bus,bean);
            pda.printNormal(POSPrinterConst.PTR_S_SLIP, text);
            ui.showScreen(POSUIManagerIfc.REMOVE_TENDER, baseModel);
            pda.beginSlipRemoval();
            pda.endSlipRemoval();

            // Update printer status
            statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
            baseModel.setStatusBeanModel(statusModel);
            completedTask = true;
        }
        catch (DeviceException e)
        {
            handlePrintingErrors(e, argText, pda, bus, dialogModel);
        }
        catch (PrintableDocumentException e)
        {
            handlePrintingErrors(argText, pda, bus, dialogModel);
        }

        return (completedTask);
    }

    /**
     * Displays printing errors dialogs
     *
     * @param exception DeviceException the exception being raised
     * @param argText String
     * @param pda POSDeviceActions
     * @param offline String
     * @param bus BusIfc reference
     * @param dialogModel
     */
    protected void handlePrintingErrors(DeviceException exception, String argText, POSDeviceActions pda,
            String offline, BusIfc bus, DialogBeanModel dialogModel)
    {
        handlePrintingErrors(exception, argText, pda, bus, dialogModel);
    }

    /**
     * Displays printing errors dialogs
     *
     * @param exception DeviceException the exception being raised
     * @param argText String
     * @param pda POSDeviceActions
     * @param bus BusIfc reference
     * @param dialogModel
     */
    protected void handlePrintingErrors(DeviceException exception, String argText,
                                         POSDeviceActions   pda,
                                         BusIfc bus,
                                         DialogBeanModel dialogModel)
    {
        Throwable oe = exception.getCause();

        JposException jpe = null;

        StatusBeanModel  statusModel = new StatusBeanModel();
        if (oe != null && oe instanceof JposException)
        {
            jpe = (JposException) oe;
        }

        // Assuming timeouts are only on insertion, not removal

        if ((jpe != null) && (jpe.getErrorCode() == JposConst.JPOS_E_TIMEOUT))
        {

            // display dialog
            dialogModel.setType(DialogScreensIfc.CONFIRMATION);
            String arg[] = new String[1];
            dialogModel.setResourceID(PRINTER_TIMEOUT);
            arg[0] = argText;
            dialogModel.setArgs(arg);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Retry");
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Discard");
        }
        else
        {

           logger.warn("Unable to frank check " + exception.getMessage());

           // Update printer status
           statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
           dialogModel.setStatusBeanModel(statusModel);
           if (oe != null)
           {
               logger.warn("DeviceException.NestedException:\n " + Util.throwableToString(oe));
           }

            // Are we Offline?
            if (  jpe != null &&
               ( (jpe.getErrorCode() == JposConst.JPOS_E_EXTENDED) ||
                 (jpe.getErrorCode() == JposConst.JPOS_E_OFFLINE)) )
            {

                // Determine what the next step should be
                dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);

                String arg[] = new String[1];
                dialogModel.setResourceID(OFFLINE_DEVICE_RETRY_CONTINUE);

                UtilityManagerIfc utility =
                    (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                arg[0] = utility.retrieveDialogText(RETRY_CONTINUE_PRINTER_OFFLINE,
                        DEFAULT_PRINTER_OFFLINE_TEXT);

                dialogModel.setArgs(arg);
                // The Continue flow
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE,"Discard");
                // The Retry flow
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,"Retry");

            }
            else // no timeout, not offline
            {
                // Try to reset the printer
                try
                {
                   pda.beginSlipRemoval();
                   pda.endSlipRemoval();
                }
                catch (DeviceException tmp)
                {
                    // Do nothing... we're already in recovery mode.
                }

                dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
                String arg[] = new String[1];
                dialogModel.setResourceID(PRINT_ERROR );
                arg[0] = argText;
                dialogModel.setArgs(arg);
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY,"Retry");
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE,"Discard");
            }
         }
         // Make sure we don't try to frank the same thing again
    }
    
    /**
     * Displays printing errors dialogs
     * 
     * @param exception PrintableDocumentException the exception being raised
     * @param argText String
     * @param pda POSDeviceActions
     * @param bus BusIfc reference
     * @param dialogModel
     */
    protected void handlePrintingErrors(String argText, POSDeviceActions pda, BusIfc bus,
            DialogBeanModel dialogModel)
    {
        dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
        String arg[] = new String[1];
        dialogModel.setResourceID(PRINT_ERROR);
        arg[0] = argText;
        dialogModel.setArgs(arg);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Print");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Discard");
    }
    
    /**
     * This method returns the same String it was passed if that string is non-
     * null or an empty string.
     *
     * @param s String to check
     * @return String safe non-null string
     */
    protected String checkNull(String s)
    {
        if (s == null)
        {
            s = new String("");
        }

        return s;
    }

    /**
     * Gets the device's character widths map from the device action group.
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#getCharWidths
     *
     * @return slip printer line size
     */
    protected  Map<String, Integer> getCharWidths()
    {
        Map<String, Integer> charWidths = new HashMap<String, Integer>(0);

        try
        {
            POSPrinterActionGroupIfc dag;
            DeviceTechnicianIfc deviceTechnician =
                (DeviceTechnicianIfc) Gateway.getDispatcher().getLocalTechnician(DeviceTechnicianIfc.TYPE);
            dag = (POSPrinterActionGroupIfc) deviceTechnician.getDeviceActionGroup(POSPrinterActionGroupIfc.TYPE);
            charWidths = dag.getCharWidths();
        }
        catch (TechnicianNotFoundException tnfe)
        {
            logger.error(tnfe);
        }
        catch (DeviceException de)
        {
            logger.error(de);
        }
        return charWidths;
    }
}
