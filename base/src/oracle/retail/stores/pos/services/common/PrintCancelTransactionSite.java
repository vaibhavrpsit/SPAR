/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/PrintCancelTransactionSite.java /main/20 2013/11/13 17:01:51 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *   mchellap   10/14/14 - Cancel transaction receipt is only printed when
 *                         cashier cancel the transaction. Cancel transaction
 *                         receipt will not be printed when timeout.
 *    yiqzhao   11/13/13 - Cancel transaction receipt is only printed when
 *                         cashier cancel the transaction. Cancel transaction
 *                         receipt will not be printed when timeout.
 *    mkutiana  01/15/13 - timeout at Sale Screen reworked for extracted
 *                         cancelSale tour
 *    blarsen   12/16/10 - Previous change broke prints for normal F12 case.
 *                         Fixed this problem.
 *    blarsen   12/09/10 - Preventing site from printing a normally completed
 *                         transaction. This can happen if the POS times out
 *                         while waiting for the cash drawer to close.
 *    abhayg    08/04/10 - TRANSACTION NUMBER IS SKIPPED DUE TO APPLICATION
 *                         TIME OUT
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    vapartha  01/27/10 - Added code to handle time out in Add Item screen.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/30/2007 7:01:38 PM   Alan N. Sinton  CR
 *         26485 - Merge from v12.0_temp.
 *    3    360Commerce 1.2         3/31/2005 4:29:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/05/20 22:54:58  cdb
 *   @scr 4204 Removed tabs from code base again.
 *
 *   Revision 1.5  2004/05/04 03:00:52  tfritz
 *   @scr 4595 Added "PrintCanceledTransactionReceipt" parameter
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 19 2004 14:24:14   DCobb
 * Corrected default value for mailLetter when trasnaction=null.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 * 
 *    Rev 1.0   Jan 15 2004 14:03:28   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site prints the current canceled transaction receipt.
    @version $Revision: /main/20 $
**/
//--------------------------------------------------------------------------
public class PrintCancelTransactionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 34634646L;

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/20 $";
    
    /**
       Retry Continue Dialog Resource ID
    **/
    public static final String RETRY_CONTINUE = "RetryContinue";
    /**
       Printer offline tag
    **/
    public static final String RETRY_CONTINUE_PRINTER_OFFLINE = "RetryContinue.PrinterOffline";
    /**
       Default Printer offline text
    **/
    public static final String DEFAULT_PRINTER_OFFLINE_TEXT = "Printer is offline.";

    //----------------------------------------------------------------------
    /**
       Prints the current canceled transaction receipt.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        boolean mailLetter = true;
        TenderableTransactionIfc transaction = null;
        
        // retrieve cargo, transaction
        if (bus.getCargo() instanceof TenderableTransactionCargoIfc)
        {
            TenderableTransactionCargoIfc tenderableCargo = (TenderableTransactionCargoIfc)bus.getCargo();
            transaction = tenderableCargo.getTenderableTransaction();
        }
        else if(bus.getCargo() instanceof BillPayCargo)
        {
            BillPayCargo cargo = (BillPayCargo) bus.getCargo();
            transaction = (TenderableTransactionIfc) cargo.getTransaction();
        }

        // Don't print completed transactions.
        // Under unusual circumstances  the cargo contains a normally completed 
        // transaction (timeout occured while waiting for cash drawer to close).
        // When cashier cancel the transaction, the current letter is not equal to TIMEOUT. Cancel transaction receipt should be printed out at this circumstance.
        if (transaction != null && !transaction.isCompleted() && !transaction.isCanceledTransactionPrinted() &&
            !bus.getCurrentLetter().getName().equals(CommonLetterIfc.TIMEOUT))
        {
            UtilityManagerIfc utility =
               (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            // set canceled status
            transaction.setTransactionStatus(TransactionIfc.STATUS_CANCELED);
            transaction.setTimestampEnd();

            // Print Receipt
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);                 
          
            try
            {
                // check parameter : should canceled receipt be printed?
                if (pm.getStringValue("PrintCanceledTransactionReceipt").equalsIgnoreCase("Y"))
                {
                    PrintableDocumentManagerIfc printableDocumentManager =
                        (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
                    ReceiptParameterBeanIfc receiptParameters =
                        printableDocumentManager.getReceiptParameterBeanInstance((SessionBusIfc)bus, transaction);
                    printableDocumentManager.printReceipt((SessionBusIfc)bus, receiptParameters);

                    // Update printer status
                    ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
                    
                    //set the flag to true to indicate that the transaction has been printed
                    transaction.setCanceledTransactionPrinted(true);
                }
            }
            catch(PrintableDocumentException e)
            {
                logger.warn("Unable to print receipt: " + e.getMessage() + "");
                            
                mailLetter = false;
                            
                // Update printer status
                ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

                if (e.getNestedException() != null)
                {
                    logger.warn("DeviceException.NestedException:\n" + Util.throwableToString(e.getNestedException()) + "");
                }

                String msg[] = new String[1];
                msg[0] = utility.retrieveDialogText(RETRY_CONTINUE_PRINTER_OFFLINE,
                                                    DEFAULT_PRINTER_OFFLINE_TEXT);

                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID(RETRY_CONTINUE);
                model.setType(DialogScreensIfc.RETRY_CONTINUE);
                model.setArgs(msg);
                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
            catch(ParameterException pe)
            {
                logger.warn("Failed to retrieve parameter: " + pe.getMessage() + "");             
            }
          
        }

        if (mailLetter) 
        {
            if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.TIMEOUT) ) 
            {
                if (bus.getCargo() instanceof TimedCargoIfc) 
                {
                    ((TimedCargoIfc) bus.getCargo()).setTimeout(true);
                } 
                else 
                {
                    logger.warn("Cargo was not prepared for timeout"+ bus.getCargo().getClass().getName());
                }
                //Send a Timeout letter                
                bus.mail(new Letter(CommonLetterIfc.TIMEOUT), BusIfc.CURRENT);
            } 
            else if (bus.getCargo() instanceof TimedCargoIfc && ((TimedCargoIfc) bus.getCargo()).isTimeout())
            {
                //Send a Timeout letter
                bus.mail(new Letter(CommonLetterIfc.TIMEOUT), BusIfc.CURRENT);
            }
            else
            {
                //Send a Continue Letter
                 bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }
        }
    }

}

