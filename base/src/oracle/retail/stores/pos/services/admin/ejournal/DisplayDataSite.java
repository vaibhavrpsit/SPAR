/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/ejournal/DisplayDataSite.java /main/13 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/17/14 - Formating cleanup and move letter null check within method
 *    icole     09/10/14 - Fix bug 19597917 NPE returning to eJournal
 *                         screen from browser.  Cleaned out unused code
 *                         and deprecated methods.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:35 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:27:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/09/21 21:02:30  lzhao
 *   @scr 5123: use exception error code as part of search data.
 *
 *   Revision 1.7  2004/07/30 23:25:11  kmcbride
 *   @scr 6639: Fixing up EJ to use only the sequence number portion of the transaction id when comparing transaction ids within the same store, reg. and biz date.
 *
 *   Revision 1.6  2004/07/26 18:55:21  kll
 *   @scr 3638: printer offline flow
 *
 *   Revision 1.5  2004/05/19 13:14:20  kll
 *   @scr 3638: Offline Printer support
 *
 *   Revision 1.4  2004/05/07 11:18:40  tmorris
 *   @scr 4704 -Fixed EJournal to allow different language date formats to be searched.
 *
 *   Revision 1.3  2004/02/12 16:48:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:07  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 17 2003 09:18:10   rrn
 * Added processing of Register Number as a search criteria.
 * Resolution for 3611: EJournal to database
 * 
 *    Rev 1.0   Aug 29 2003 15:52:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jul 02 2003 11:28:48   bwf
 * Format time with HH:mm to avoid problems with ejournal.
 * Resolution for 2232: E. Journal Searches unable to search by Time if time is after 12 noon.
 * 
 *    Rev 1.3   Mar 03 2003 09:53:00   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Jul 09 2002 10:47:16   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   23 May 2002 17:43:50   vxs
 * Removed unneccessary concatenations in logging statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:40:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:03:02   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:18:30   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 10 2002 18:00:04   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   Feb 26 2002 20:44:34   dfh
 * updated error screen text
 * Resolution for POS SCR-590: Trans Not Found text errors
 *
 *
 *    Rev 1.0   Sep 21 2001 11:12:24   msg
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:07:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.ejournal;

import java.util.Locale;

import jpos.POSPrinterConst;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.SearchableIfc;
import oracle.retail.stores.foundation.manager.journal.JournalSearchData;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.DisplayTextBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.TransactionLookupBeanModel;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * Gets journal data and displays it.
 * 
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class DisplayDataSite extends PosSiteActionAdapter
{

    /**
     * class name constant
     */
    public static final String SITENAME = "DisplayDataSite";

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * no transactions prompt tag
     */
    protected static String NO_TRANSACTIONS_PROMPT_TAG = "TransactionNotFound.EJournalMessage";

    /**
     * no transactions prompt
     */
    protected static String NO_TRANSACTIONS_PROMPT = "No transactions match the search information entered.";

    /**
     * Printer offline dialog ID
     */
    protected static final String RETRY_CONTINUE_TAG = "RetryContinue";

    /**
     * Printer offline dialog message tag
     */
    protected static final String PRINTER_OFFLINE_TAG = "RetryContinue.PrinterOffline";

    /**
     * Printer offline default dialog message
     */
    protected static final String PRINTER_OFFLINE_TEXT = "Printer is offline.";

    /**
     * Print the ejournal entry
     */
    protected boolean printEJournal = false;

    /**
     * Arrive at site, set up data vector and do search OR process a letter
     * since we've been here before
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        EJournalCargo cargo = (EJournalCargo) bus.getCargo();
        TransactionLookupBeanModel beanmodel = cargo.getBeanModel();
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
        JournalManagerIfc jmi = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        int transactionIndex = cargo.getTransactionIndex();
        int transactionCount = cargo.getTransactionCount();
        JournalSearchData data = cargo.getData();
        String[] transactionStrings = cargo.getTransactionStrings();

        // First iteration on this site, or no data found
        if (data == null || cargo.getNewSearch())
        {
        
            // Let's get our data
            data = new JournalSearchData();
            
            // Was a start transaction entered?
            if (!beanmodel.getStartTransaction().equals(""))
            {
                // Just grab the sequence number from the transaction id
                TransactionIDIfc startId = DomainGateway.getFactory().getTransactionIDInstance();
                startId.setTransactionID(beanmodel.getStartTransaction());

                TransactionIDIfc endId = DomainGateway.getFactory().getTransactionIDInstance();
                endId.setTransactionID(beanmodel.getEndTransaction());

                data.addSearchItem(startId.getFormattedTransactionSequenceNumber(),
                                   endId.getFormattedTransactionSequenceNumber(),
                                   SearchableIfc.TYPE_LONG,
                                   SearchableIfc.POSITION_TRANSACTION_NUMBER);
                
                // Set the offset and length of the sequence number
                // based on the domain configurations to be used
                // later by the EJournal search logic.  This has
                // been added to avoid NumberFormatExceptions for
                // configurations with transaction IDs longer than
                // 19 digits.
                //
                String sequenceNumberLengthProperty =
                    DomainGateway.getProperty(
                        TransactionID.SEQUENCE_NUMBER_LENGTH_PROPERTY_NAME, "4");

                String storeIDLengthProperty =
                    DomainGateway.getProperty(
                        TransactionID.STORE_ID_LENGTH_PROPERTY_NAME, "5");

                String workstationIDLengthProperty =
                    DomainGateway.getProperty(
                        TransactionID.WORKSTATION_ID_LENGTH_PROPERTY_NAME, "3");
                
                // The sequence offset is the first digit
                // after store id and workstation id/register id
                //
                int offset = Integer.parseInt(storeIDLengthProperty) + Integer.parseInt(workstationIDLengthProperty); 
                
                data.setTransIdSequenceSpecifiers(offset, Integer.parseInt(sequenceNumberLengthProperty));
            }
            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();

            // Check date
            if (beanmodel.getStartDate().getMonth() > 0)
            {
            	//for ej search we convert locale specific date format into a common format
                String startDate = dateTimeService.formatDate(beanmodel.getStartDate().dateValue(), locale, "MM/dd/yyyy");
                String endDate = dateTimeService.formatDate(beanmodel.getEndDate().dateValue(), locale, "MM/dd/yyyy");
                data.addSearchItem(startDate, endDate, SearchableIfc.TYPE_DATE, SearchableIfc.POSITION_DATE);
                beanmodel.setFocusField("StartDateField");
            }

            // Check time (these are valid times:  00:01, 01:00)
            if (beanmodel.getStartTime().getHour() > 0 ||
                beanmodel.getStartTime().getMinute() > 0)
            {   
            	// for ej search we convert locale specific time format into a common format
                String startTime = dateTimeService.formatTime(beanmodel.getStartTime().dateValue(), locale, "HH:mm");
                String endTime = dateTimeService.formatTime(beanmodel.getEndTime().dateValue(), locale, "HH:mm");
                data.addSearchItem(startTime, endTime, SearchableIfc.TYPE_TIME, SearchableIfc.POSITION_TIME);
                beanmodel.setFocusField("StartTimeField");
            }
            
            // Check Cashier ID
            if (!beanmodel.getCashierID().equals(""))
            {
                data.addSearchItem(beanmodel.getCashierID(), SearchableIfc.POSITION_OPERATOR_ID);
                beanmodel.setFocusField("CashierIDField");
            }

            // Check Sales Associate ID
            if (!beanmodel.getSalesAssociateID().equals(""))
            {
                data.addSearchItem(beanmodel.getSalesAssociateID(), SearchableIfc.POSITION_ASSOCIATE_ID);
                beanmodel.setFocusField("SalesAssociateIDField");
            }

            if (!beanmodel.getRegisterNumber().equals(""))
            {
                data.addSearchItem(beanmodel.getRegisterNumber(), 
                                   SearchableIfc.POSITION_REGISTER_NUMBER);
                beanmodel.setFocusField("RegisterNumberField");
            }
            

            // Get the journal data using requested criteria.
            if (jmi != null)
            {
                jmi.searchJournal(data);
                transactionCount = data.getNumberFound();
            }
            else
            {
                transactionCount = -1;
            }

            cargo.setNewSearch(false);

            // If we got a hit, update the transaction count.
            if (transactionCount > 0)
            {
                transactionIndex = 0;
            }
            else
            {
                transactionIndex = -1;
            }
            cargo.setTransactionIndex(transactionIndex);
            cargo.setTransactionCount(transactionCount);
        }
        else
        {
            processLetter(bus);
        }

        transactionIndex = cargo.getTransactionIndex();
        if (printEJournal)
        {
            // Print tran data
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            String sixBlankLines = "\n\n\n\n\n\n"; // pad report
            try
            {
                pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, (cargo.getTransactionStrings())[0] +
                                sixBlankLines); // push print lines above cutter --
                pda.cutPaper(97); // cut paper 97% of its width
                
                // Set transaction to display
                if (jmi == null)
                {
                    // Shouldn't have this - something weird happened
                    DialogBeanModel model = new DialogBeanModel();
                    model.setResourceID("JournalNotFound");
                    model.setType(DialogScreensIfc.ERROR);
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                }
                else
                {
                    DisplayTextBeanModel model = new DisplayTextBeanModel();
                    jmi.getJournalData(transactionStrings, data.getVector(), transactionIndex);
                    model.setDisplayText(transactionStrings[0]);
                    model.setLocalButtonBeanModel(nModel);
                    ui.showScreen(POSUIManagerIfc.JOURNAL_DISPLAY, model);
                }

            }
            catch (DeviceException e)
            {
                logger.warn(
                        bus.getServiceName() + ": Unable to print ejournal entry: " + e.getMessage() + ".");
                
                if (e.getCause() != null)
                {
                    logger.warn(
                                bus.getServiceName() + ": DeviceException.NestedException:\n" + Util.throwableToString(e.getCause()));
                }
                
                ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
                        POSUIManagerIfc.ONLINE);

                String msg[] = new String[1];
                msg[0] = utility.retrieveDialogText(PRINTER_OFFLINE_TAG, PRINTER_OFFLINE_TEXT);

                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID(RETRY_CONTINUE_TAG);
                model.setType(DialogScreensIfc.RETRY_CONTINUE);
                model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, CommonActionsIfc.UNDO);
                model.setArgs(msg);

                // display retry/cancel dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
            printEJournal = false;
        }
        // else if we have a valid index, enable/disable the buttons appropriately.
        else if (transactionIndex >= 0)
        {
            // We have something to show on the screen
            DisplayTextBeanModel model = new DisplayTextBeanModel();
            nModel.setButtonEnabled(CommonActionsIfc.DONE, true);

            // Check to see which buttons we need to enable
            // if we're on the first one, disable the first button
            if (transactionIndex == 0)
                nModel.setButtonEnabled(CommonActionsIfc.FIRST, false);
            else
                nModel.setButtonEnabled(CommonActionsIfc.FIRST, true);

            // if current is equal to the last, disable last button
            if (transactionIndex == transactionCount - 1)
                nModel.setButtonEnabled(CommonActionsIfc.LAST, false);
            else
                nModel.setButtonEnabled(CommonActionsIfc.LAST, true);

            // if there's more ahead, set next enable
            if (transactionIndex < transactionCount - 1)
                nModel.setButtonEnabled(CommonActionsIfc.NEXT, true);
            else
                nModel.setButtonEnabled(CommonActionsIfc.NEXT, false);

            // if there's more behind, set previous enable
            if (transactionIndex > 0)
                nModel.setButtonEnabled(CommonActionsIfc.PREVIOUS, true);
            else
                nModel.setButtonEnabled(CommonActionsIfc.PREVIOUS, false);

            // Set transaction to display
            if (jmi == null)
            {
                // Shouldn't have this - something weird happened
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("JournalNotFound");
                dialogModel.setType(DialogScreensIfc.ERROR);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            else
            {
                jmi.getJournalData(transactionStrings, data.getVector(), transactionIndex);
                model.setDisplayText(transactionStrings[0]);
                model.setLocalButtonBeanModel(nModel);
                ui.showScreen(POSUIManagerIfc.JOURNAL_DISPLAY, model);
            }
        }
        else if (transactionCount == -1)
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            if ( data.getExceptionCode() == DataException.CONNECTION_ERROR  )
            {
                dialogModel.setResourceID("JournalOffline");            
            }
            else
            {
                dialogModel.setResourceID("JournalNotFound");    
            }
            dialogModel.setType(DialogScreensIfc.ERROR);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            String[] args = new String[1];

            args[0] = utility.retrieveDialogText(NO_TRANSACTIONS_PROMPT_TAG,
                                                 NO_TRANSACTIONS_PROMPT);

            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("TransactionNotFound");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(args);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }

        cargo.setData(data);
        cargo.setBeanModel(beanmodel);
    }

    /**
     * Reset the search criteria.
     * 
     * @param bus the bus departing from this site
     */
    @Override
    public void depart(BusIfc bus)
    {
        String letterName = bus.getCurrentLetter().getName();

        // If we got something other than navigation of the transaction data...
        if (letterName.equals("Done")
            || letterName.equals("Ok")
            || letterName.equals("Cancel")
            || letterName.equals("Undo"))
        {
            JournalManagerIfc jmi = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
            if (jmi != null)
            {
                EJournalCargo cargo = (EJournalCargo) bus.getCargo();
                // This will clean up the storage for our search
                jmi.searchJournal(cargo.getData());
            }
        }
        // keep the model info intact
        if (letterName.equals("Undo"))
        {
            EJournalCargo cargo = (EJournalCargo) bus.getCargo();
            TransactionLookupBeanModel beanmodel = cargo.getBeanModel();
            cargo.setBeanModel(beanmodel);
        }
    }

    /**
     * Process a letter from the navigation buttons being pressed. This can be
     * selecting a different transaction or printing the current one.
     * 
     * @param bus the bus arriving at this site
     */
    protected void processLetter(BusIfc bus)
    {
        if (bus.getCurrentLetter() == null)
        {
            // if the browser is invoked while in the midst of inspecting the eJournal there is no letter
            // when the DONE button is pressed to return from the browser.
            return;
        }

        String letterName = bus.getCurrentLetter().getName();
        EJournalCargo cargo = (EJournalCargo)bus.getCargo();

        // Update the index based on the button that was pressed.
        if (letterName.equals("First"))
        {
            cargo.setTransactionIndex(0);
        }
        else if (letterName.equals("Next"))
        {
            cargo.setTransactionIndex(cargo.getTransactionIndex() + 1);
        }
        else if (letterName.equals("Previous"))
        {
            cargo.setTransactionIndex(cargo.getTransactionIndex() - 1);
        }
        else if (letterName.equals("Last"))
        {
            cargo.setTransactionIndex(cargo.getTransactionCount() - 1);
        }
        else if (letterName.equals("Print") || letterName.equals("Retry"))
        {
            printEJournal = true;
        }
    }

}
