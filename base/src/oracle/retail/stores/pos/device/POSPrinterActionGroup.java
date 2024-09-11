/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/POSPrinterActionGroup.java /main/33 2013/09/06 11:05:21 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  09/06/13 - deprecated slipLineSize moved to ReceiptPrinterIfc
 *    abondala  09/04/13 - initialize collections
 *    blarsen   10/03/12 - Changed cached FOP printer to a hash map. This
 *                         supports MPOS' multi-network-printer feature. Each
 *                         MPOS device can be assigned a unique network
 *                         printer.
 *    blarsen   09/28/12 - Added support for MPOS' multi-network-printer
 *                         feature. The printer's bean ID is now passed into
 *                         printDocument().
 *    cgreene   12/20/11 - refactor some actiongroup settings into print
 *                         manager
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    cgreene   02/16/11 - move barcode prefix to posdevices.xml
 *    cgreene   12/03/10 - XbranchMerge cgreene_micrlogging from
 *                         rgbustores_13.3x_generic_branch
 *    cgreene   12/03/10 - clean up an dadd more debug logging
 *    cgreene   10/26/10 - refactor alwaysPrintLineFeed into
 *                         AbstractPrinterGroup
 *    rsnayak   10/26/10 - Bill pay ereceipt fix
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    02/05/10 - merged with Jack's changes for ereceipt printing
 *    jswan     01/29/10 - Additional modifications for attaching rebate and
 *                         gift reciepts to the EReceipt.
 *    jswan     01/28/10 - Modifications to support emailing rebate, gift and
 *                         alteration receipts with the sale reciept.
 *    acadar    01/25/10 - added login in the endJob for rendering receipts
 *    abondala  01/03/10 - update header date
 *    acadar    12/15/09 - cleanup and refactoring of PdfReceiptPrinter's use
 *                         in POSPrinterActionGroup
 *    acadar    12/07/09 - updates
 *    acadar    11/30/09 - finish XML document generation
 *    acadar    11/20/09 - check in for refresh
 *    blarsen   03/19/09 - Added constants and interface to support a more
 *                         generalized way to configure when a franking slip is
 *                         to start printing. Refactored code that parses
 *                         token/pair strings from posdevices.xml
 *    blarsen   03/10/09 - added getter/setter for new slipPrintSize printer
 *                         device property
 *    blarsen   03/06/09 - removed call to setCharactersSet(ASCII) in
 *                         getPOSPrinter. This was interferring with priting
 *                         Asian fonts. Only franking appears to call this
 *                         method. After franking, Asian characters would not
 *                         print.
 *    cgreene   02/26/09 - switch char widths to int. fix plugin to support
 *    blarsen   02/19/09 - added 2 data members and supporting methods:
 *                         alwaysPrintLineFeeds and characterWidths
 *    arathore  02/02/09 - Updated to read receiptLineSize from posdevices.xml
 *                         file.
 *    cgreene   12/10/08 - set the receipt width if the document is a blueprint
 *    arathore  11/21/08 - upadted for eReceipt feature.
 *    arathore  11/20/08 - updated for ereceipt.
 *    arathore  11/20/08 - updated for ereceipt feature.
 *    arathore  11/17/08 - updated for ereceipt feature
 *    cgreene   11/07/08 - implement buffer in ReceiptPrinter for USB printers
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import java.util.HashMap;
import java.util.Map;

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinter;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.DeviceMode;
import oracle.retail.stores.foundation.manager.device.PrinterSession;
import oracle.retail.stores.foundation.manager.device.ReceiptPrinter;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModeIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;
import oracle.retail.stores.foundation.manager.ifc.device.ReceiptPrinterIfc;
import oracle.retail.stores.manager.device.FoReceiptPrinter;
import oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedReceipt;
import oracle.retail.stores.pos.services.printing.PrintingIfc;
import oracle.retail.stores.printing.PrintRequestAttributes;
import oracle.retail.stores.printing.PrintRequestAttributesIfc;

/**
 * POSPrinterActionGroup defines the POSPrinter specific device operations
 * available to POS applications.
 *
 * @version $Revision: /main/33 $
 * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc
 */
public class POSPrinterActionGroup extends AbstractPrinterActionGroup
    implements POSPrinterActionGroupIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -9070542490763838739L;


    /**
     * franking start line
     */
    protected String frankingLinePurchaseOrder = "7";

    /**
     * franking lines hashmap
     */
    protected Map<String, Integer> frankingLines = new HashMap<String, Integer>(2);

    /**
     * flag indicating print buffering is enabled
     * @deprecated as of 13.4. Use {@link ReceiptPrinterIfc#isPrintBufferingEnabled()} instead.
     * @see ReceiptPrinter in DeviceContext.xml
     */
    protected boolean printBufferingEnabled = true;

    /**
     * size of buffer to use when buffering print. Defaults to 4000.
     * @deprecated as of 13.4. See {@link ReceiptPrinterIfc#setPrintBufferSize(int)}
     * @see ReceiptPrinter in DeviceContext.xml
     */
    protected int printBufferSize = 4000;

    /**
     * Flag for whether to interact with the print as a transaction.
     * @deprecated as of 13.4. Use {@link ReceiptPrinterIfc#isTransactionalPrinting()} instead.
     * @see DeviceContext.xml
     */
    protected boolean transactionalPrinting;

    /**
     * slip insertion timeout
     */
    protected int slipInsertionTimeout = -1;

    /**
     * number of characters that will fit on a slip print line
     * @deprecated as of 14.0 See {@link ReceiptPrinterIfc#setSlipLineSize(int)}
     * @see DeviceContext.xml
     */
    protected int slipLineSize = USE_SLIP_LINE_SIZE_DEFAULT;

    /**
     * number of characters to print on a slip print line
     */
    protected int slipPrintSize = PrintingIfc.SLIP_PRINTER_LINE_SIZE;

    /**
     * slip removal timeout
     */
    protected int slipRemovalTimeout = -1;

    /**
     * Initiates slip processing
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#beginSlipInsertion
     */
    public void beginSlipInsertion() throws DeviceException
    {

        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Calling jpos.POSPrinter.beginInsertion(int) with \"" + slipInsertionTimeout + "\"");
            getPOSPrinter().beginInsertion(slipInsertionTimeout);
            logger.debug("Returning from jpos.POSPrinter.beginInsertion(int)");
        }
        catch (JposException e)
        {

            logger.error("POSPrinter Exception: Timeout error while inserting slip. " + e.getMessage()
                    + ". Error Code: " + e.getErrorCode()
                    + ". Extended Error Code: " + e.getErrorCodeExtended());
            if (e.getErrorCode() == JposConst.JPOS_E_TIMEOUT || e.getErrorCode() == JposConst.JPOS_E_BUSY)
            {
                try
                {
                    logger.info("Trying to end printer slip insertion because of timeout");
                    closePrinter();
                }
                catch (DeviceException de)
                {
                    logger.info("Unable to end printer slip insertion because of timeout", de);
                    // No action required
                }
            }

            throw new DeviceException(DeviceException.JPOS_ERROR, "POSPrinter: slip insertion timeout", e);
        }

    }

    /**
     * Initiates form removal processing
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#beginSlipRemoval
     */
    public void beginSlipRemoval() throws DeviceException
    {

        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Calling jpos.POSPrinter.beginRemoval(int) with \"" + slipRemovalTimeout + "\"");
            getPOSPrinter().beginRemoval(slipRemovalTimeout);
            logger.debug("Returning from jpos.POSPrinter.beginRemoval(int)");
        }
        catch (JposException e)
        {
            logger.error("POSPrinter Exception: Timeout error while removing slip. " + e.getMessage()
                    + ". Error Code: " + e.getErrorCode()
                    + ". Extended Error Code: " + e.getErrorCodeExtended());
            throw new DeviceException(DeviceException.JPOS_ERROR, "POSPrinter: slip removal timeout", e);
        }

    }

    /**
     * Closes printer after timeout error
     * <P>
     * Obtains a {@link DeviceSessionIfc} from {@link DeviceTechnicianIfc}.
     * Obtains {@link jpos.POSPrinter} in {@link PrinterSession#MODE_CLOSED}
     * mode, then releases {@link jpos.POSPrinter}
     */
    public void closePrinter() throws DeviceException
    {
        DeviceTechnicianIfc dt;
        DeviceSessionIfc printerSession = null;

        try
        {
            dt = getDeviceTechnician();

            printerSession = dt.getDeviceSession(PrinterSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(PrinterSession.TYPE);
            dm.setDeviceModeName(PrinterSession.MODE_CLOSED);
            printerSession.getDeviceInMode(dm);
        }
        catch (DeviceException e)
        {
            throw e;
        }
        finally
        {
            if (printerSession != null)
            {
                printerSession.releaseDevice();
            }
        }
    }

    /**
     * CutPaper
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#cutPaper
     */
    public void cutPaper(int percentage) throws DeviceException
    {

        try
        {
            getPOSPrinter().cutPaper(percentage);
        }
        catch (JposException e)
        {
            logger.error("POSPrinter Exception: CutPaper error. " + e.getMessage()
                    + ". Error Code: " + e.getErrorCode()
                    + ". Extended Error Code: " + e.getErrorCodeExtended());
            throw new DeviceException(DeviceException.JPOS_ERROR, "POSPrinter: CutPaper error", e);
        }

    }

    /**
     * End slip insertion processing
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#endSlipInsertion
     */
    public void endSlipInsertion() throws DeviceException
    {

        try
        {
            logger.debug("Calling jpos.POSPrinter.endInsertion()");
            getPOSPrinter().endInsertion();
            logger.debug("Returning from jpos.POSPrinter.endInsertion()");
        }
        catch (JposException e)
        {
            logger.error("POSPrinter Exception: End insertion error. " + e.getMessage()
                    + ". Error Code: " + e.getErrorCode()
                    + ". Extended Error Code: " + e.getErrorCodeExtended());
            throw new DeviceException(DeviceException.JPOS_ERROR, "POSPrinter: End insertion error", e);
        }

    }

    /**
     * End slip removal processing
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#beginRemoval
     */
    public void endSlipRemoval() throws DeviceException
    {

        try
        {
            logger.debug("Calling jpos.POSPrinter.endRemoval()");
            getPOSPrinter().endRemoval();
            logger.debug("Returning from jpos.POSPrinter.endRemoval()");
        }
        catch (JposException e)
        {
            logger.error("POSPrinter Exception: End removal error. " + e.getMessage()
                    + ". Error Code: " + e.getErrorCode()
                    + ". Extended Error Code: " + e.getErrorCodeExtended());
            throw new DeviceException(DeviceException.JPOS_ERROR, "POSPrinter: End removal error", e);
        }
    }

    /**
     * Retrieves the starting line number for franking of Purchase Order.
     *
     * @return starting line number
     */
    public String getFrankingLinePurchaseOrder()
    {
        return frankingLinePurchaseOrder;
    }

    /**
     * Gets the starting lines for franking.
     *
     * @return HashMap
     */
    public Map<String, Integer> getFrankingLines()
    {
        return frankingLines;
    }

    /**
     * Gets the starting line for the various franking types.
     *
     * @see oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#getFrankingLines(String)
     */
    public int getFrankingLines(String frankingType)
    {
        Integer skipLines = frankingLines.get(frankingType);
        if (skipLines == null)
        {
            return DEFAULT_START_FRANKING_LINE;
        }

        return skipLines;
    }

    /**
     * Get the POSPrinter from the DeviceSession
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#getPOSPrinter
     */
    public POSPrinter getPOSPrinter() throws DeviceException
    {
        DeviceTechnicianIfc dt;
        DeviceSessionIfc printerSession = null;
        POSPrinter posPrinter;

        try
        {
            dt = getDeviceTechnician();

            printerSession = dt.getDeviceSession(PrinterSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(PrinterSession.TYPE);
            dm.setDeviceModeName(PrinterSession.MODE_RECEIPT);
            posPrinter = (POSPrinter)printerSession.getDeviceInMode(dm);
        }
        catch (DeviceException e)
        {
            throw e;
        }
        finally
        {
            if (printerSession != null)
            {
                printerSession.releaseDevice();
            }
        }
        return posPrinter;
    }


    /**
     * Gets the time to wait in milliseconds for inserting a slip
     *
     * @return slip insertion timeout
     */
    public int getSlipInsertionTimeout()
    {
        return slipInsertionTimeout;
    }

    /**
     * Retrieves the number of characters on a slip print line. This must be set
     * to a valid value for the hardware in use. If the value is
     * USE_SLIP_LINE_CHARACTERS_DEFAULT, the hardware default setting will be
     * used.
     * <P>
     * Currently, on a WincorNixdorf Beetle, this value must be set to 60
     * characters to fit the franking data on the check.
     *
     * @return number of slip line characters
     * @deprecated as of 14.0 See {@link ReceiptPrinterIfc#getSlipLineSize()}
     * @see DeviceContext.xml
     */
    public int getSlipLineSize()
    {
        return slipLineSize;
    }

    /**
     * Gets the time to wait in milliseconds for removing a slip
     *
     * @return slip removal timeout
     */
    public int getSlipRemovalTimeout()
    {
        return slipRemovalTimeout;
    }

    /**
     * Retrieves the print-buffering-enabled flag.
     *
     * @return print-buffering-enabled flag value
     * @deprecated as of 13.4. Use {@link ReceiptPrinterIfc#isPrintBufferingEnabled()} instead.
     * @see ReceiptPrinter in DeviceContext.xml
     */
    public boolean isPrintBufferingEnabled()
    {
        return (printBufferingEnabled);
    }

    /**
     * @see oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#getPrintBufferSize()
     * @deprecated as of 13.4. See {@link ReceiptPrinterIfc#setPrintBufferSize(int)}
     * @see ReceiptPrinter in DeviceContext.xml
     */
    public int getPrintBufferSize()
    {
        return printBufferSize;
    }

    /**
     * @see oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#isTransactionalPrinting()
     * @deprecated as of 13.4. Use {@link ReceiptPrinterIfc#isTransactionalPrinting()} instead.
     * @see ReceiptPrinter in DeviceContext.xml
     */
    public boolean isTransactionalPrinting()
    {
        return transactionalPrinting;
    }

    /**
     * @see oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#setPrintBufferSize(int)
     * @deprecated as of 13.4. See {@link ReceiptPrinterIfc#setPrintBufferSize(int)}
     * @see ReceiptPrinter in DeviceContext.xml
     */
    public void setPrintBufferSize(int printBufferSize)
    {
        this.printBufferSize = printBufferSize;
    }

    /**
     * @see oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#setTransactionalPrinting(boolean)
     * @deprecated as of 13.4. Use {@link ReceiptPrinterIfc#setTransactionalPrinting(boolean)} instead.
     * @see DeviceContext.xml
     */
    public void setTransactionalPrinting(boolean transactionalPrinting)
    {
        this.transactionalPrinting = transactionalPrinting;
    }

    /**
     * Prints the document
     *
     * @param EYSPrintableTransactionIfc
     * @throws DeviceException if error occurs
     */
    public void printDocument(EYSPrintableDocumentIfc document) throws DeviceException
    {
        printDocument(null, document);
    }


    /**
     * Prints the document
     *
     * @param printerID which printer to print the document
     * @param EYSPrintableTransactionIfc
     * @throws DeviceException if error occurs
     */
    public void printDocument(String printerID, EYSPrintableDocumentIfc document) throws DeviceException
    {
        DeviceTechnicianIfc dt;
        DeviceSessionIfc printerSession = null;
        boolean isCleanReceipt = false;

        try
        {
            dt = getDeviceTechnician();

            // obtain PrinterSession from DeviceTechnician
            printerSession = dt.getDeviceSession(PrinterSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(PrinterSession.TYPE);
            dm.setDeviceModeName(PrinterSession.MODE_RECEIPT);
            // obtain jpos.POSPrinter in asyncReceipt mode
            POSPrinter posPrinter = (POSPrinter)printerSession.getDeviceInMode(dm);

            // create simplified printer wrapper
            // Check for eReceipt in which case set cleanReceipt to true.
            if(document instanceof BlueprintedReceipt && ((BlueprintedReceipt)document).getParameterBean() instanceof ReceiptParameterBeanIfc)
            {
                ReceiptParameterBeanIfc parameters = (ReceiptParameterBeanIfc)((BlueprintedReceipt)document).getParameterBean();
                if(parameters.getTransaction() instanceof SaleReturnTransactionIfc){
                    if(parameters.isEreceipt())
                    {
                        isCleanReceipt = true;
                    }
                }
                else if (parameters.getTransaction() instanceof BillPayTransactionIfc)
                { // Added for bill pay transactions to print e-receipts
                    if (parameters.isEreceipt())
                    {
                        isCleanReceipt = true;
                    }
                }
            }

            // create ReceiptPrinterIfc object.
            ReceiptPrinterIfc receiptPrinter = getReceiptPrinter(document, isCleanReceipt);

            if (!isCleanReceipt)
            {
                receiptPrinter.setPrinter(posPrinter);
            }

            // set the configured printer onto the document
            applyReceiptPrinter(document, receiptPrinter);

            // ask the document to print itself
            document.printDocument();
        }
        catch (DeviceException e)
        {
            throw e;
        }
        finally
        {
            // release jpos.POSPrinter
            if (printerSession != null)
            {
                printerSession.releaseDevice();
            }
        }
    }

    /**
     * Creates an instance of the ReceiptPrinterIfc.
     *
     * @param EYSPrintableDocumentIfc document
     * @return ReceiptPrinterIfc
     */
    protected ReceiptPrinterIfc getReceiptPrinter(EYSPrintableDocumentIfc document, boolean isCleanReceipt) throws DeviceException
    {
        ReceiptPrinterIfc receiptPrinter = null;
        if(!isCleanReceipt)
        {
             receiptPrinter = (ReceiptPrinterIfc)BeanLocator.getDeviceBean(ReceiptPrinterIfc.BEAN_KEY);
        }
        else
        {
            FoReceiptPrinter fopReceiptPrinter = lookupFopReceiptPrinter(FoReceiptPrinter.BEAN_KEY_ERECEIPT);

            // for reports the filename consists of the blueprintName + registerid + timestamp
            // for all the rest, filename is the transaction id

            ReceiptParameterBeanIfc rpBean = ((ReceiptParameterBeanIfc)((BlueprintedReceipt)document).getParameterBean());
            String blueprintName = rpBean.getDocumentType();

            String fileName = "";
            if(rpBean.getTransaction() != null)
            {
                fileName = rpBean.getTransaction().getTransactionID();
            }
            if (!Util.isEmpty(rpBean.getEReceiptFileNameAddition()))
            {
                fileName = fileName + rpBean.getEReceiptFileNameAddition();
            }

            //set the blueprint name in the IppPrinter so we can  locate the associated fop
            fopReceiptPrinter.setOutputFileName(fileName);
            fopReceiptPrinter.setXmlDocument(getXMLTemplate(blueprintName, true));
            fopReceiptPrinter.setFopTemplate(getFOPTemplate(blueprintName, true));
            receiptPrinter = fopReceiptPrinter;
        }

        return receiptPrinter;
    }

    /**
     * PrintNormal
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#printNormal
     */
    public void printNormal(int station, String data) throws DeviceException
    {
        try
        {
            getPOSPrinter().printNormal(station, data);
        }
        catch (JposException e)
        {
            logger.error("POSPrinter Exception: PrintNormal error. " + e.getMessage() + "  Error Code:  "
                    + Integer.toString(e.getErrorCode()) + "  Extended Error Code:  "
                    + Integer.toString(e.getErrorCodeExtended()) + "");

            throw new DeviceException(DeviceException.JPOS_ERROR, "POSPrinter: PrintNormal error", e);
        }
    }

    /**
     * Sets the flag indicating printer is franking-capable. This flag is set in
     * the device script.
     *
     * @param value flag indicating printer is franking-capable
     */
    public void setFrankingCapable(boolean i)
    {
        frankingCapable = i;
    }

    /**
     * Sets the starting line number for franking of Purchase Order. The string
     * must parse to an <code>Integer</code> or a {@link NumberFormatException}
     * will be thrown.
     *
     * @param value starting line number
     *
     * @deprecated as of 13.1.  Use the more generalized "setFrankingLines" mechanism for this.
     */
    public void setFrankingLinePurchaseOrder(String lines)
    {
        frankingLinePurchaseOrder = lines;
        frankingLines.put(POSPrinterActionGroupIfc.PURCHASE_ORDER_START_FRANKING_LINE, Integer.valueOf(lines));
    }

    /**
     * Sets the print-buffering-enabled flag.
     *
     * @param value print-buffering-enabled flag value
     * @deprecated as of 13.4. Use {@link ReceiptPrinterIfc#setPrintBufferingEnabled(boolean)} instead.
     * @see ReceiptPrinter in DeviceContext.xml
     */
    public void setPrintBufferingEnabled(boolean value)
    {
        printBufferingEnabled = value;
    }



    /**
     * Sets the time to wait in milliseconds for inserting a slip.
     *
     * @param value slip insertion timeout
     */
    public void setSlipInsertionTimeout(int value)
    {
        slipInsertionTimeout = value;
    }

    /**
     * Sets the number of characters on a slip print line. This must be set to a
     * valid value for the hardware in use. If the value is
     * USE_SLIP_LINE_CHARACTERS_DEFAULT, the hardware default setting will be
     * used.
     * <P>
     * Currently, on a WincorNixdorf Beetle, this value must be set to 60
     * characters to fit the franking data on the check.
     *
     * @param value number of slip line characters
     * @deprecated as of 14.0 See {@link ReceiptPrinterIfc#setSlipLineSize(int)}
     */
    public void setSlipLineSize(int value)
    {
        slipLineSize = value;
    }

    /**
     * Sets the time to wait in milliseconds for removing a slip
     *
     * @param value slip removal timeout
     */
    public void setSlipRemovalTimeout(int i)
    {
        slipRemovalTimeout = i;
    }

    /*
     * @see oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#getSlipPrintSize
     */
    public int getSlipPrintSize()
    {
        return slipPrintSize;
    }

    /*
     * @see oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#setSlipPrintSize
     *
     */
    public void setSlipPrintSize(int slipPrintSize)
    {
        this.slipPrintSize = slipPrintSize;
    }

    /*
     * @see oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#setFrankingLines
     *
     * This method converts the string defined in posdevices.xml into a hash map of
     * UnicodeBlock names and their printed widths.
     */
    public void setFrankingLines(String frankingLines)
    {
        parseTokenString(this.frankingLines, frankingLines);
    }

    /**
     * Gets the Printer attributes
     * @return
     * @deprecated as of 13.4. Use configurations in DeviceContext.xml instead.
     */
    public PrintRequestAttributesIfc getSettings()
    {
        if(settings == null)
        {
            settings = new PrintRequestAttributes();
        }

        settings.setFoMimeType("application/pdf");
        settings.setFoFileExtension("pdf");
        settings.setFactoryInstance(this.getFactoryInstance());

        return settings;
    }
}
