/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/FiscalPrinterActionGroup.java /main/5 2013/09/06 11:05:21 mkutiana Exp $
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
 *                         feature. Added printerID to printDocument();
 *    mchellap  08/30/12 - Enable fiscalprinter franking
 *    mchellap  08/16/12 - Fiscal printer action group
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import jpos.FiscalPrinter;
import jpos.FiscalPrinterConst;
import jpos.JposConst;
import jpos.JposException;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.DeviceMode;
import oracle.retail.stores.foundation.manager.device.FiscalPrinterSession;
import oracle.retail.stores.foundation.manager.device.PrinterSession;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModeIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;
import oracle.retail.stores.foundation.manager.ifc.device.ReceiptPrinterIfc;
import oracle.retail.stores.manager.device.FoReceiptPrinter;
import oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedReceipt;
import oracle.retail.stores.pos.services.printing.PrintingIfc;
import oracle.retail.stores.printing.PrintRequestAttributesIfc;

/**
 * FiscalPrinterActionGroup defines the FiscalPrinter specific device operations
 * available to POS applications.
 *
 * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc
 * @since 14.0
 */
public class FiscalPrinterActionGroup extends AbstractPrinterActionGroup implements POSPrinterActionGroupIfc
{
    // This id is used to tell the compiler not to generate a new
    // serialVersionUID.
    private static final long serialVersionUID = 4592361917843568774L;

    /**
     * franking start line
     */
    protected String frankingLinePurchaseOrder = "7";

    /**
     * franking lines hashmap
     */
    protected Map<String, Integer> frankingLines = new HashMap<String, Integer>(2);

    /**
     * slip insertion timeout
     */
    protected int slipInsertionTimeout = -1;

    /**
     * number of characters that will fit on a slip print line
     * @deprecated as of 14.0 See {@link ReceiptPrinterIfc#getSlipLineSize()}
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
                logger.debug("Calling jpos.FiscalPrinter.beginInsertion(int) with \"" + slipInsertionTimeout + "\"");
            getFiscalPrinter().beginFiscalDocument(slipInsertionTimeout);
            getFiscalPrinter().beginInsertion(slipInsertionTimeout);
            logger.debug("Returning from jpos.FiscalPrinter.beginInsertion(int)");
        }
        catch (JposException e)
        {

            logger.error("FiscalPrinter Exception: Timeout error while inserting slip. " + e.getMessage()
                    + ". Error Code: " + e.getErrorCode() + ". Extended Error Code: " + e.getErrorCodeExtended());
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

            throw new DeviceException(DeviceException.JPOS_ERROR, "FiscalPrinter: slip insertion timeout", e);
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
                logger.debug("Calling jpos.FiscalPrinter.beginRemoval(int) with \"" + slipRemovalTimeout + "\"");
            getFiscalPrinter().beginRemoval(slipRemovalTimeout);
            logger.debug("Returning from jpos.FiscalPrinter.beginRemoval(int)");
        }
        catch (JposException e)
        {
            logger.error("FiscalPrinter Exception: Timeout error while removing slip. " + e.getMessage()
                    + ". Error Code: " + e.getErrorCode() + ". Extended Error Code: " + e.getErrorCodeExtended());
            throw new DeviceException(DeviceException.JPOS_ERROR, "FiscalPrinter: slip removal timeout", e);
        }

    }

    /**
     * Closes printer after timeout error
     * <P>
     * Obtains a {@link DeviceSessionIfc} from {@link DeviceTechnicianIfc}.
     * Obtains {@link jpos.FiscalPrinter} in {@link PrinterSession#MODE_CLOSED}
     * mode, then releases {@link jpos.FiscalPrinter}
     */
    public void closePrinter() throws DeviceException
    {
        DeviceTechnicianIfc dt;
        DeviceSessionIfc printerSession = null;

        try
        {
            dt = getDeviceTechnician();

            printerSession = dt.getDeviceSession(FiscalPrinterSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(FiscalPrinterSession.TYPE);
            dm.setDeviceModeName(FiscalPrinterSession.MODE_CLOSED);
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
        // For legal reasons fiscal printers do not support cut paper
        // functionality, cut happens automatically after printing fiscal
        // receipts.
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
            logger.debug("Calling jpos.FiscalPrinter.endInsertion()");
            getFiscalPrinter().endInsertion();
            logger.debug("Returning from jpos.FiscalPrinter.endInsertion()");
        }
        catch (JposException e)
        {
            logger.error("FiscalPrinter Exception: End insertion error. " + e.getMessage() + ". Error Code: "
                    + e.getErrorCode() + ". Extended Error Code: " + e.getErrorCodeExtended());
            throw new DeviceException(DeviceException.JPOS_ERROR, "FiscalPrinter: End insertion error", e);
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
            logger.debug("Calling jpos.FiscalPrinter.endRemoval()");
            getFiscalPrinter().endRemoval();
            getFiscalPrinter().endFiscalDocument();
            logger.debug("Returning from jpos.FiscalPrinter.endRemoval()");
        }
        catch (JposException e)
        {
            logger.error("FiscalPrinter Exception: End removal error. " + e.getMessage() + ". Error Code: "
                    + e.getErrorCode() + ". Extended Error Code: " + e.getErrorCodeExtended());
            throw new DeviceException(DeviceException.JPOS_ERROR, "FiscalPrinter: End removal error", e);
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
     * Get the FiscalPrinter from the DeviceSession
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#getFiscalPrinter
     */
    public FiscalPrinter getFiscalPrinter() throws DeviceException
    {
        DeviceTechnicianIfc dt;
        DeviceSessionIfc printerSession = null;
        FiscalPrinter fiscalPrinter;

        try
        {
            dt = getDeviceTechnician();

            printerSession = dt.getDeviceSession(FiscalPrinterSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(FiscalPrinterSession.TYPE);
            dm.setDeviceModeName(FiscalPrinterSession.MODE_RECEIPT);
            fiscalPrinter = (FiscalPrinter) printerSession.getDeviceInMode(dm);
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
        return fiscalPrinter;
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
     * Prints the document
     *
     * @param EYSPrintableTransactionIfc
     * @param printerID printerID is not supported for Fiscal Printers.  The default printer will be used.
     * @throws DeviceException if error occurs
     */
    public void printDocument(String printerID, EYSPrintableDocumentIfc document) throws DeviceException
    {
        printDocument(document);
    }

    /**
     * Prints the document
     *
     * @param EYSPrintableTransactionIfc
     * @throws DeviceException if error occurs
     */
    public void printDocument(EYSPrintableDocumentIfc document) throws DeviceException
    {
        DeviceTechnicianIfc dt;
        DeviceSessionIfc printerSession = null;
        boolean isCleanReceipt = false;

        try
        {
            dt = getDeviceTechnician();

            // obtain PrinterSession from DeviceTechnician
            printerSession = dt.getDeviceSession(FiscalPrinterSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(FiscalPrinterSession.TYPE);
            dm.setDeviceModeName(FiscalPrinterSession.MODE_RECEIPT);
            // obtain jpos.FiscalPrinter in asyncReceipt mode
            FiscalPrinter fiscalPrinter = (FiscalPrinter) printerSession.getDeviceInMode(dm);

            // create simplified printer wrapper
            // Check for eReceipt in which case set cleanReceipt to true.
            if (document instanceof BlueprintedReceipt
                    && ((BlueprintedReceipt) document).getParameterBean() instanceof ReceiptParameterBeanIfc)
            {
                ReceiptParameterBeanIfc parameters = (ReceiptParameterBeanIfc) ((BlueprintedReceipt) document)
                        .getParameterBean();
                if (parameters.getTransaction() instanceof SaleReturnTransactionIfc)
                {
                    if (parameters.isEreceipt())
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
                receiptPrinter.setFiscalPrinter(fiscalPrinter);
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
            // release jpos.FiscalPrinter
            if (printerSession != null)
            {
                printerSession.releaseDevice();
            }
        }
    }

    /**
     * Prints the fiscal receipt
     *
     * @param EYSPrintableTransactionIfc
     * @throws DeviceException if error occurs
     */
    public void printFiscalReceipt(EYSPrintableDocumentIfc document) throws DeviceException
    {
        DeviceTechnicianIfc dt;
        DeviceSessionIfc printerSession = null;
        boolean isCleanReceipt = false;

        try
        {
            dt = getDeviceTechnician();

            // obtain PrinterSession from DeviceTechnician
            printerSession = dt.getDeviceSession(FiscalPrinterSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(FiscalPrinterSession.TYPE);
            dm.setDeviceModeName(FiscalPrinterSession.MODE_RECEIPT);
            // obtain jpos.FiscalPrinter in asyncReceipt mode
            FiscalPrinter fiscalPrinter = (FiscalPrinter) printerSession.getDeviceInMode(dm);

            // create simplified printer wrapper
            // Check for eReceipt in which case set cleanReceipt to true.
            if (document instanceof BlueprintedReceipt
                    && ((BlueprintedReceipt) document).getParameterBean() instanceof ReceiptParameterBeanIfc)
            {
                ReceiptParameterBeanIfc parameters = (ReceiptParameterBeanIfc) ((BlueprintedReceipt) document)
                        .getParameterBean();
                if (parameters.getTransaction() instanceof SaleReturnTransactionIfc)
                {
                    if (parameters.isEreceipt())
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
                receiptPrinter.setFiscalPrinter(fiscalPrinter);
            }

            // set the configured printer onto the document
            applyReceiptPrinter(document, receiptPrinter);

            // ask the document to print itself
            document.printFiscalReceipt();

        }
        catch (DeviceException e)
        {
            throw e;
        }

        finally
        {
            // release jpos.FiscalPrinter
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
    protected ReceiptPrinterIfc getReceiptPrinter(EYSPrintableDocumentIfc document, boolean isCleanReceipt)
            throws DeviceException
    {
        ReceiptPrinterIfc receiptPrinter = null;
        if (!isCleanReceipt)
        {
            receiptPrinter = (ReceiptPrinterIfc) BeanLocator.getDeviceBean(ReceiptPrinterIfc.BEAN_KEY);
        }
        else
        {
            FoReceiptPrinter fopReceiptPrinter = lookupFopReceiptPrinter(FoReceiptPrinter.BEAN_KEY_ERECEIPT);

            // for reports the filename consists of the blueprintName +
            // registerid + timestamp
            // for all the rest, filename is the transaction id

            ReceiptParameterBeanIfc rpBean = ((ReceiptParameterBeanIfc) ((BlueprintedReceipt) document)
                    .getParameterBean());
            String blueprintName = rpBean.getDocumentType();

            String fileName = "";
            if (rpBean.getTransaction() != null)
            {
                fileName = rpBean.getTransaction().getTransactionID();
            }
            if (!Util.isEmpty(rpBean.getEReceiptFileNameAddition()))
            {
                fileName = fileName + rpBean.getEReceiptFileNameAddition();
            }

            // set the blueprint name in the IppPrinter so we can locate the
            // associated fop
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

        switch (station)
        {
            case FiscalPrinterConst.FPTR_S_SLIP:
            {
                try
                {
                    getFiscalPrinter().printFiscalDocumentLine(data);
                }
                catch (JposException e)
                {
                    logger.warn("FiscalPrinter Exception: Cannot set slip line characters: " + e.getMessage()
                            + " Error Code:  " + Integer.toString(e.getErrorCode()) + "  xtended Error Code:  "
                            + Integer.toString(e.getErrorCodeExtended()) + "");

                    throw new DeviceException(DeviceException.JPOS_ERROR,
                            "FiscalPrinter Exception: Exeception caught during printNormal", e);
                }
                break;
            }
            case FiscalPrinterConst.FPTR_S_RECEIPT:
            {
                try
                {
                    getFiscalPrinter().beginNonFiscal();
                    getFiscalPrinter().printNormal(station, data);
                    getFiscalPrinter().endNonFiscal();
                }
                catch (JposException e)
                {
                    logger.error("FiscalPrinter Exception: PrintNormal error. " + e.getMessage() + "  Error Code:  "
                            + Integer.toString(e.getErrorCode()) + "  Extended Error Code:  "
                            + Integer.toString(e.getErrorCodeExtended()) + "");

                    throw new DeviceException(DeviceException.JPOS_ERROR,
                            "FiscalPrinter Exception: Exeception caught during printNormal", e);
                }
                break;
            }
        }

    }

    /**
     * Prints various accumulated values for transactions done during a sales
     * period defined to last between two Z Reports. This report does not close
     * the day.
     */
    @Override
    public void printFiscalXReport() throws DeviceException
    {
        try
        {
            getFiscalPrinter().printXReport();
        }
        catch (JposException e)
        {
            logger.error("Caught exception while printing X report " + e.getCause(), e);
            throw new DeviceException(DeviceException.JPOS_ERROR,
                    "FiscalPrinter Exception: Exeception caught while printing X report", e);
        }

    }

    /**
     * Prints various accumulated values for transactions done during a sales
     * period defined to last between two Z Reports. This report closes the day
     * for the fiscal printer.
     */
    @Override
    public void printFiscalZReport() throws DeviceException
    {
        try
        {
            getFiscalPrinter().printZReport();
        }
        catch (JposException e)
        {
            logger.error("Caught exception while printing Z report " + e.getCause(), e);
            throw new DeviceException(DeviceException.JPOS_ERROR,
                    "FiscalPrinter Exception: Exeception caught while printing Z report", e);

        }
    }

    /**
     * Synchronize register and fiscal printer date.
     */
    @Override
    public void setFiscalPrinterDate() throws DeviceException
    {

        try
        {
            SimpleDateFormat format = new SimpleDateFormat("ddmmyyyyhhmm");
            String date = format.format(new Date(System.currentTimeMillis()));
            getFiscalPrinter().setDate(date);
        }
        catch (JposException e)
        {
            logger.error("Caught exception while setting printer date " + e.getCause(), e);
            throw new DeviceException(DeviceException.JPOS_ERROR,
                    "FiscalPrinter Exception: Caught exception while setting printer date", e);

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
     * @deprecated as of 13.1. Use the more generalized "setFrankingLines"
     *             mechanism for this.
     */
    public void setFrankingLinePurchaseOrder(String lines)
    {
        frankingLinePurchaseOrder = lines;
        frankingLines.put(POSPrinterActionGroupIfc.PURCHASE_ORDER_START_FRANKING_LINE, Integer.valueOf(lines));
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
     * @deprecated as of 14.0 See {@link ReceiptPrinterIfc#getSlipLineSize()}
     * @see DeviceContext.xml
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
     * @see
     * oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#getSlipPrintSize
     */
    public int getSlipPrintSize()
    {
        return slipPrintSize;
    }

    /*
     * @see
     * oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#setSlipPrintSize
     */
    public void setSlipPrintSize(int slipPrintSize)
    {
        this.slipPrintSize = slipPrintSize;
    }

    /*
     * @see
     * oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#setFrankingLines
     * This method converts the string defined in posdevices.xml into a hash map
     * of UnicodeBlock names and their printed widths.
     */
    public void setFrankingLines(String frankingLines)
    {
        parseTokenString(this.frankingLines, frankingLines);
    }

    /**
     * Gets the Printer attributes
     *
     * @return
     * @deprecated as of 13.4. Use configurations in DeviceContext.xml instead.
     */
    public PrintRequestAttributesIfc getSettings()
    {
        return null;
    }

    @Override
    public boolean isPrintBufferingEnabled()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isTransactionalPrinting()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getPrintBufferSize()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setPrintBufferingEnabled(boolean value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTransactionalPrinting(boolean value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPrintBufferSize(int value)
    {
        // TODO Auto-generated method stub

    }
}
