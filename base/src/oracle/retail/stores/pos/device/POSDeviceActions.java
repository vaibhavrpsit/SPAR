/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/POSDeviceActions.java /main/30 2012/11/13 15:11:41 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       11/13/12 - enhance order print and view
 *    blarsen   09/28/12 - Added support for MPOS' multi-network-printer
 *                         feature. printDocument() now gets a printerID from
 *                         the PrinterCargo and passes it into the DAG.
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    blarsen   03/09/12 - Removing member deviceAction. This overloads the
 *                         super's member which can be confusing and dangerous.
 *    mchellap  08/17/12 - Add fiscal printer support
 *    mkutiana  02/22/12 - XbranchMerge
 *                         mkutiana_bug13728958-disable_fpdevice_based_on_parm
 *                         from rgbustores_13.4x_generic_branch
 *    mkutiana  02/21/12 - disable the fingerprintreader (at login) when
 *                         parameter is set to NoFingerprint
 *    tzgarba   10/28/11 - Removed extra JARs from POS ARU
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    blarsen   02/28/11 - Added verifyFingerprintMatch() action.
 *    blarsen   02/24/11 - Added isFingerprintReaderOnline() action which is
 *                         needed for the on/offline display display.
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    asinton   07/01/10 - Moved Select Payment Type prompt into pospal
 *    asinton   07/01/10 - Moved Select Payment Type prompt into pospal &
 *                         removed old deprecated methods
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/11/10 - convert Base64 from axis
 *    cgreene   05/11/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    acadar    12/15/09 - cleanup and refactoring of PdfReceiptPrinter's use
 *                         in POSPrinterActionGroup
 *    acadar    12/14/09 - cleanup
 *    acadar    12/07/09 - updates
 *    acadar    11/24/09 - changes for network printing
 *    acadar    11/20/09 - check in for refresh
 *    asinton   06/17/09 - Removing parameter MerchantNumber.
 *    djenning  01/15/09 - update tender select request with whether or not
 *                         giftcards are enabled
 *    asinton   12/10/08 - Fixes for Debit and card security.
 *    asinton   12/02/08 - Updates per review comments.
 *    asinton   11/25/08 - Intermittent check in.
 *    asinton   10/24/08 - Updates for POS integration.
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import java.io.Serializable;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.device.DeviceActions;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceActionIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DrawerClosedListenerIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.device.cidscreens.CIDScreenManager;
import oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;

import org.apache.log4j.Logger;

/**
 * <h1>Description:</h1>
 * The <code>POSDeviceActions</code> provides the application programming interface for POS-specific device actions
 * directly from the service code. These actions are synchronous.
 * <p>
 * <h2>Imports</h2>
 * This example illustrates how to invoke a supported device action from the application code.
 * <p>
 * The application code must import these foundation classes.
 * <ul>
 * <li>oracle.retail.stores.foundation.manager.device.DeviceException;
 * <li>oracle.retail.stores.foundation.tour.service.SessionBusIfc;
 * </ul>
 * <h2>Usage</h2>
 * The application will create a new POSDeviceActions object, passing the reference to the bus. Typically, the bus is
 * passed as a parameter to the application code through a CRF interface. Note the bus is downcast to a SessionBusIfc.
 * <p>
 * The application will gather any required parameters, and invoke the operation on the POSDeviceActions object.
 * <p>
 * If any problem occurred, a device exception will be thrown. The underlying exception can be accessed through the
 * e.getOrigException call on the DeviceException.
 *
 * <pre>
 *  try { POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus); RetailTransactionIfc t = cargo.getTransaction(); pda.printReceipt(t); } catch (DeviceException e) { logger.warn("Unable to print receipt. "+"" + e.getMessage() + ""); if (e.getOrigException()!=null) { logger.warn("DeviceException.NestedException:\n"+"" + Util.throwableToString(e.getOrigException()) + ""); } }
 *      </pre>
 *
 */
@SuppressWarnings("serial")
public class POSDeviceActions extends DeviceActions
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(POSDeviceActions.class);

    /**
     * The default constructor, sets the bus used to access the DeviceManager.
     *
     * @param bus The bus to set
     */
    public POSDeviceActions(SessionBusIfc bus)
    {
        super(bus);
    }
    /**
     * Disable the no-args constructor, since a bus is always required.
     */
    protected POSDeviceActions()
    {
    }

    //--------------------------------------------------------------------------
    // Legacy POSDeviceActions below
    //--------------------------------------------------------------------------
    /**
     * Return the device action associated with the called operation.
     *
     * @return deviceAction associated with called operation
     *
     * @throws DeviceException if the device action has not been defined
     */
    public DeviceActionIfc getDeviceAction() throws DeviceException
    {
        if (deviceAction == null)
        {
            throw new DeviceException("Device action has not been defined.");
        }
        return (deviceAction);
    }

    /**
     * Prints the document
     *
     * @param value Document to print
     * @throws DeviceException
     *             if device error occurs
     */
    public void printDocument(EYSPrintableDocumentIfc value) throws DeviceException
    {
        final EYSPrintableDocumentIfc document = value;
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                PrinterActionGroupIfc dag =
                    (PrinterActionGroupIfc) dt.getDeviceActionGroup(PrinterActionGroupIfc.TYPE);
                CargoIfc cargo = bus.getCargo();
                if ((cargo != null) && cargo instanceof PrintingCargo)
                {
                    dag.printDocument(((PrintingCargo)cargo).getPrinterID(), document);
                }
                else
                {
                    dag.printDocument(document);
                }
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * Prints the receipt using fiscal printer.
     *
     * @param value Document to print
     * @throws DeviceException
     *             if device error occurs
     */
    public void printFiscalReceipt(EYSPrintableDocumentIfc value) throws DeviceException
    {
        final EYSPrintableDocumentIfc document = value;
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                PrinterActionGroupIfc dag =
                    (PrinterActionGroupIfc) dt.getDeviceActionGroup(PrinterActionGroupIfc.TYPE);
                dag.printFiscalReceipt(document);
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * Prints X report of the fiscal printer. The report prints all fiscal
     * activities happened after fiscal printer day starting.
     *
     * @throws DeviceException if device error occurs
     */
    public void printFiscalXReport() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                PrinterActionGroupIfc dag =
                    (PrinterActionGroupIfc) dt.getDeviceActionGroup(PrinterActionGroupIfc.TYPE);
                dag.printFiscalXReport();
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * Prints Z report of the fiscal printer. The report prints all fiscal
     * activities happened during the day and closes fiscal printer day.
     *
     * @throws DeviceException
     *             if device error occurs
     */
    public void printFiscalZReport() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                PrinterActionGroupIfc dag =
                    (PrinterActionGroupIfc) dt.getDeviceActionGroup(PrinterActionGroupIfc.TYPE);
                dag.printFiscalZReport();
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * Synchronize register and fiscal printer date.
     *
     * @throws DeviceException
     *             if device error occurs
     */
    public void setFiscalPrinterDate() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                PrinterActionGroupIfc dag =
                    (PrinterActionGroupIfc) dt.getDeviceActionGroup(PrinterActionGroupIfc.TYPE);
                dag.setFiscalPrinterDate();
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * PrintNormal.
     *
     * @param s station
     * @param d data
     *
     * @throws DeviceException if anything goes wrong while trying to print
     */
    public void printNormal(int s, String d) throws DeviceException
    {
        final int station = s;
        final String data = d;
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                PrinterActionGroupIfc dag =
                    (PrinterActionGroupIfc) dt.getDeviceActionGroup(PrinterActionGroupIfc.TYPE);
                dag.printNormal(station, data);
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * CutPaper.
     *
     * @param p percentage to cut
     * @throws DeviceException if anything goes wrong while trying to cut
     */
    public void cutPaper(int p) throws DeviceException
    {
        final int percentage = p;
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                PrinterActionGroupIfc dag =
                    (PrinterActionGroupIfc) dt.getDeviceActionGroup(PrinterActionGroupIfc.TYPE);
                dag.cutPaper(percentage);
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Initiates form insertion processing.
     *
     * @throws DeviceException if anything goes wrong while inserting
     * slip
     */
    public void beginSlipInsertion() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                POSPrinterActionGroupIfc dag =
                    (POSPrinterActionGroupIfc) dt.getDeviceActionGroup(POSPrinterActionGroupIfc.TYPE);
                dag.beginSlipInsertion();
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Initiates form removal processing.
     *
     * @throws DeviceException if anything goes wrong while removing slip
     */
    public void beginSlipRemoval() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                POSPrinterActionGroupIfc dag =
                    (POSPrinterActionGroupIfc) dt.getDeviceActionGroup(POSPrinterActionGroupIfc.TYPE);
                dag.beginSlipRemoval();
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * End slip insertion processing.
     *
     * @throws DeviceException if anything goes wrong while removing slip
     */
    public void endSlipInsertion() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                POSPrinterActionGroupIfc dag =
                    (POSPrinterActionGroupIfc) dt.getDeviceActionGroup(POSPrinterActionGroupIfc.TYPE);
                dag.endSlipInsertion();
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * End slip removal processing.
     *
     * @throws DeviceException if anything goes wrong while ending slip removal
     */
    public void endSlipRemoval() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                POSPrinterActionGroupIfc dag =
                    (POSPrinterActionGroupIfc) dt.getDeviceActionGroup(POSPrinterActionGroupIfc.TYPE);
                dag.endSlipRemoval();
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Determines if printer is franking-capable.
     *
     * @return Boolean indicator that printer is franking-capable
     * @throws DeviceException if anything goes while performing the action
     */
    public Serializable isFrankingCapable() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                PrinterActionGroupIfc dag =
                    (PrinterActionGroupIfc) dt.getDeviceActionGroup(PrinterActionGroupIfc.TYPE);
                return (dag.isFrankingCapable());
            }
        };

        return (transportValet(this));
    }

    /**
     * Writes to hard totals
     *
     * @param htObj Hard totals object to write
     * @throws DeviceException if anything goes while performing the action
     */
    public void writeHardTotals(Serializable htObj) throws DeviceException
    {

        final Serializable hardTotalsObj = htObj;

        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                HardTotalsActionGroupIfc dag =
                    (HardTotalsActionGroupIfc) dt.getDeviceActionGroup(HardTotalsActionGroupIfc.TYPE);
                dag.writeHardTotals(hardTotalsObj);
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * Writes to hard totals
     *
     * @param htObj
     *            the HardTotals object to be written
     * @param prefix
     *            the String that will be the first part of the written file's name, e.g., a workstation ID.
     * @throws DeviceException if anything goes while performing the action
     */
    public void writeHardTotals(Serializable htObj, String prefix) throws DeviceException
    {
        String oldFileName = (String) getHardTotalsDeviceName();
        String nameBase = prefix + "." + oldFileName;

        final Serializable hardTotalsObj = htObj;
        final String nb = nameBase;

        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                HardTotalsActionGroupIfc dag =
                    (HardTotalsActionGroupIfc) dt.getDeviceActionGroup(HardTotalsActionGroupIfc.TYPE);
                dag.writeHardTotals(hardTotalsObj, nb);
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Reads from hard totals
     *
     * @return the HardTotalsObject read in
     * @throws DeviceException if anything goes while performing the action
     */
    public Serializable readHardTotals() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                HardTotalsActionGroupIfc dag =
                    (HardTotalsActionGroupIfc) dt.getDeviceActionGroup(HardTotalsActionGroupIfc.TYPE);
                return (dag.readHardTotals());
            }
        };

        return (transportValet(this));

    }

    /**
     * Reads from hard totals
     *
     * @param prefix
     *            the first part of the file-name-to-be.
     * @return The hard totals object read in
     * @throws DeviceException if anything goes while performing the action
     */
    public Serializable readHardTotals(String prefix) throws DeviceException
    {
        Serializable result = null;
        String oldFileName = (String) getHardTotalsDeviceName();
        String nameBase = prefix + "." + oldFileName;

        final String nb = nameBase;

        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                HardTotalsActionGroupIfc dag =
                    (HardTotalsActionGroupIfc) dt.getDeviceActionGroup(HardTotalsActionGroupIfc.TYPE);
                return (dag.readHardTotals(nb));
            }
        };

        // Try to read the hard totals
        result = transportValet(this);
        return result;
    }

    /**
     * Gets hard totals device name.
     *
     * @return name of hard totals device
     * @throws DeviceException if anything goes while performing the action
     */
    public Serializable getHardTotalsDeviceName() throws DeviceException
    {

        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                HardTotalsActionGroupIfc dag =
                    (HardTotalsActionGroupIfc) dt.getDeviceActionGroup(HardTotalsActionGroupIfc.TYPE);
                return (dag.getHardTotalsDeviceName());
            }
        };

        return (transportValet(this));
    }

    /**
     * Sets hard totals device name.
     *
     * @param deviceName name to set the hard totals device to
     * @throws DeviceException if anything goes while performing the action
     */
    public void setHardTotalsDeviceName(String deviceName) throws DeviceException
    {

        final Serializable hardTotalsObj = deviceName;

        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                HardTotalsActionGroupIfc dag =
                    (HardTotalsActionGroupIfc) dt.getDeviceActionGroup(HardTotalsActionGroupIfc.TYPE);
                dag.setHardTotalsDeviceName(hardTotalsObj);
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Opens the cash drawer and waits for drawer closure.
     *
     * @param drawerClosedListener class that listens for drawer closing events
     * @throws DeviceException if anything goes while performing the action
     */
    public void openCashDrawer(DrawerClosedListenerIfc drawerClosedListener) throws DeviceException
    {
        final DrawerClosedListenerIfc dcl = drawerClosedListener;

        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CashDrawerActionGroupIfc dag =
                    (CashDrawerActionGroupIfc) dt.getDeviceActionGroup(CashDrawerActionGroupIfc.TYPE);
                dag.openCashDrawer(dcl);
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Open the cash drawer.
     * @throws DeviceException if anything goes while performing the action
     */
    public void openCashDrawer() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CashDrawerActionGroupIfc dag =
                    (CashDrawerActionGroupIfc) dt.getDeviceActionGroup(CashDrawerActionGroupIfc.TYPE);
                dag.openCashDrawer();
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Check to see if cash drawer is open.
     * @return whether or not the  cash drawer is open
     * @throws DeviceException if anything goes while performing the action
     */
    public Boolean isOpen() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CashDrawerActionGroupIfc dag =
                    (CashDrawerActionGroupIfc) dt.getDeviceActionGroup(CashDrawerActionGroupIfc.TYPE);
                return (dag.isOpen());
                //                return null;
            }
        };

        return ((Boolean) transportValet(this));
    }

    /**
     * Wait for the cash drawer to close.
     * @throws DeviceException if anything goes while performing the action
     */
    public void waitForDrawerClose() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CashDrawerActionGroupIfc dag =
                    (CashDrawerActionGroupIfc) dt.getDeviceActionGroup(CashDrawerActionGroupIfc.TYPE);
                dag.waitForDrawerClose();
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Enable alert beep.
     * @throws DeviceException if anything goes while performing the action
     */
    public void alertBeepOn() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CashDrawerActionGroupIfc dag =
                    (CashDrawerActionGroupIfc) dt.getDeviceActionGroup(CashDrawerActionGroupIfc.TYPE);
                dag.alertBeepOn();
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Enable alert beep.
     * @throws DeviceException if anything goes while performing the action
     */
    public void alertBeepOff() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CashDrawerActionGroupIfc dag =
                    (CashDrawerActionGroupIfc) dt.getDeviceActionGroup(CashDrawerActionGroupIfc.TYPE);
                dag.alertBeepOff();
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Reset cashdrawer
     * @throws DeviceException if anything goes while performing the action
     */
    public void resetCashDrawer() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CashDrawerActionGroupIfc dag =
                    (CashDrawerActionGroupIfc) dt.getDeviceActionGroup(CashDrawerActionGroupIfc.TYPE);
                dag.resetCashDrawer();
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Switch cashdrawer session
     *
     * @param s Name of the cash drawer session to switch to
     *
     * @throws DeviceException if anything goes while performing the action
     */
    public void switchCashDrawer(String s) throws DeviceException
    {
        final String newCashDrawerSession = s;

        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CashDrawerActionGroupIfc dag =
                    (CashDrawerActionGroupIfc) dt.getDeviceActionGroup(CashDrawerActionGroupIfc.TYPE);
                dag.switchCashDrawer(newCashDrawerSession);
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * Determines if SigCap session is simulated.
     *
     * @return Boolean indicator that SigCapSession is simulated
     * @throws DeviceException if anything goes while performing the action
     */
    public Serializable isSignatureCaptureSimulated() throws DeviceException
    {

        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                SigCapActionGroupIfc dag = (SigCapActionGroupIfc) dt.getDeviceActionGroup(SigCapActionGroupIfc.TYPE);
                return (dag.isSignatureCaptureSimulated());
            }
        };

        return (transportValet(this));
    }

    /**
     * Determines if PINPad session is simulated.
     *
     * @return Boolean indicator that PINPadSession is simulated
     * @throws DeviceException if anything goes while performing the action
     */
    public Serializable isPinPadSimulated() throws DeviceException
    {

        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                PINPadActionGroupIfc dag = (PINPadActionGroupIfc) dt.getDeviceActionGroup(PINPadActionGroupIfc.TYPE);
                return (dag.isPinPadSimulated());
            }
        };

        return (transportValet(this));
    }

    /**
     * Begin signature capture
     *
     * @param formName the form name
     * @throws DeviceException if anything goes while performing the action
     */
    public void beginCapture(final String formName) throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                SigCapActionGroupIfc dag = (SigCapActionGroupIfc) dt.getDeviceActionGroup(SigCapActionGroupIfc.TYPE);
                dag.beginCapture(formName);
                return null;
            }
        };

        transportValet(this);
    }

    /**
     * End signature capture
     *
     * @return Serialized signature data
     * @throws DeviceException if anything goes while performing the action
     */
    public Serializable endCapture() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                SigCapActionGroupIfc dag = (SigCapActionGroupIfc) dt.getDeviceActionGroup(SigCapActionGroupIfc.TYPE);
                return dag.endCapture();
            }
        };

        return transportValet(this);
    }

    /**
     * Display text on line display device
     *
     * @param row
     *            start row for text
     * @param col
     *            start col for text
     * @param text
     *            String to show in line display device.
     * @throws DeviceException if anything goes while performing the action
     */
    public void displayTextAt(final int row, final int col, final String text) throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                LineDisplayActionGroupIfc dag =
                    (LineDisplayActionGroupIfc) dt.getDeviceActionGroup(LineDisplayActionGroupIfc.TYPE);
                dag.displayTextAt(row, col, text);
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * Clears the line display device
     * @throws DeviceException if anything goes while performing the action
     */
    public void clearText() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                LineDisplayActionGroupIfc dag =
                    (LineDisplayActionGroupIfc) dt.getDeviceActionGroup(LineDisplayActionGroupIfc.TYPE);
                dag.clearText();
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * Display text on line display device
     *
     * @param item
     *            lineitem whose description/price has to be displayed
     * @throws DeviceException if anything goes while performing the action
     */
    public void lineDisplayItem(final SaleReturnLineItemIfc item) throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                LineDisplayActionGroupIfc dag =
                    (LineDisplayActionGroupIfc) dt.getDeviceActionGroup(LineDisplayActionGroupIfc.TYPE);
                dag.lineDisplayItem(item);
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * Display text on line display device
     *
     * @param item
     *            lineitem whose description/price has to be displayed
     * @throws DeviceException if anything goes while performing the action
     */
    public void lineDisplayItem(final PLUItemIfc item) throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                LineDisplayActionGroupIfc dag =
                    (LineDisplayActionGroupIfc) dt.getDeviceActionGroup(LineDisplayActionGroupIfc.TYPE);
                dag.lineDisplayItem(item);
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * Clears the line display device
     * @throws DeviceException if anything goes while performing the action
     * @deprecated as of 13.4, no replacement.
     */
    public void clearFormScreen() throws DeviceException
    {
    }

    /**
     * @throws DeviceException if anything goes while performing the action
    **/
    public void waitOnBusyCIDScreen() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CIDScreenManager dag = (CIDScreenManager) dt.getDeviceActionGroup("CIDScreenManager");
                dag.waitIfBusy();
                return null;
            }
        };
        transportValet(this);
    }

    /**
     * Send an action to the device, to be performed
     *
     *  @param action Action to perform
     *
     *  @throws DeviceException if there is any problem performing the requested action
     *
     *  @since 7.0
     */
    public void cidScreenPerformAction(final CIDAction action) throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CIDScreenManager dag = (CIDScreenManager) dt.getDeviceActionGroup("CIDScreenManager");
                dag.addAction(action);
                return null;
            }
        };
        transportValet(this);

    }

    /**
     * Determines if MSR is online.
     *
     * @return Boolean indicator that MSR is online.
     * @throws DeviceException if anything goes while performing the action
     */
    public Boolean isMSROnline() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                MSRActionGroupIfc dag = (MSRActionGroupIfc) dt.getDeviceActionGroup(MSRActionGroupIfc.TYPE);
                return (dag.isMSROnline());
            }
        };
        return ((Boolean) transportValet(this));
    }

    /**
     * Determines if Scanner is online.
     *
     * @return Boolean indicator that Scanner is online.
     * @throws DeviceException if anything goes while performing the action
     */
    public Boolean isScannerOnline() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                ScannerActionGroupIfc dag = (ScannerActionGroupIfc) dt.getDeviceActionGroup(ScannerActionGroupIfc.TYPE);
                return (dag.isScannerOnline());
            }
        };
        return ((Boolean) transportValet(this));
    }

    /**
     * Starts the thread that turns off the WAIT light at regular intervals.
     *
     * @throws DeviceException if anything goes while performing the action
     */
    public void turnWaitLightOff() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                KeyboardLightsActionGroupIfc dag =
                    (KeyboardLightsActionGroupIfc) dt.getDeviceActionGroup(KeyboardLightsActionGroupIfc.TYPE);
                dag.turnWaitLightOff();
                return (new Boolean(false));
            }
        };
        transportValet(this);
    }

    /**
     * Determines if MICR is online.
     *
     * @return Boolean indicator that MICR is online.
     * @throws DeviceException if anything goes while performing the action
     */
    public Boolean isMICROnline() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                MICRActionGroupIfc dag = (MICRActionGroupIfc) dt.getDeviceActionGroup(MICRActionGroupIfc.TYPE);
                return (dag.isMICROnline());
            }
        };
        return ((Boolean) transportValet(this));
    }

    /**
     * Determines if FingerprintReader is online.
     *
     * @return Boolean indicator that fingerprint reader is online.
     * @throws DeviceException if anything goes wrong while performing the action
     */
    public Boolean isFingerprintReaderOnline() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                FingerprintReaderActionGroupIfc dag = (FingerprintReaderActionGroupIfc) dt.getDeviceActionGroup(FingerprintReaderActionGroupIfc.TYPE);
                return (dag.isFingerprintReaderOnline());
            }
        };
        return ((Boolean) transportValet(this));
    }

    /**
     * Determines if supplied fingerprint matches the enrolled fingerprint template
     *
     * @param enrolledFingerprintTemplate the enrolled fingerprint template
     * @param fingerprint the fingerprint to compare against the template
     * @return Boolean indicator that fingerprint is a match.
     * @throws DeviceException if anything goes wrong while performing the action
     */
    public Boolean verifyFingerprintMatch(final byte[] enrolledFingerprintTemplate, final byte[] fingerprint) throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                FingerprintReaderActionGroupIfc dag = (FingerprintReaderActionGroupIfc) dt.getDeviceActionGroup(FingerprintReaderActionGroupIfc.TYPE);
                return (dag.verifyFingerprintMatch(enrolledFingerprintTemplate, fingerprint));
            }
        };
        return ((Boolean) transportValet(this));
    }

    /**
     * Deactivate the fingerprint Reader
     *
     * @throws DeviceException if anything goes while performing the action
     */
    public void deactivateFingerprintReader() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                FingerprintReaderActionGroupIfc dag = (FingerprintReaderActionGroupIfc) dt.getDeviceActionGroup(FingerprintReaderActionGroupIfc.TYPE);
                dag.deactivateFingerprintReader();
                return null;
            }
        };
        transportValet(this);
    }


    /**
     * @return Returns the cashDrawerEnabled state from the Cash Drawer Group.
     */
    public Serializable isCashDrawerEnabled() throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CashDrawerActionGroupIfc dag =
                    (CashDrawerActionGroupIfc) dt.getDeviceActionGroup(CashDrawerActionGroupIfc.TYPE);
                return (dag.isCashDrawerEnabled());
            }
        };

        return (transportValet(this));
    }

    /**
     * Reset cashdrawer
     * @throws DeviceException if anything goes while performing the action
     */
    public void setCashDrawerEnabled(final boolean cashDrawerEnabled) throws DeviceException
    {
        deviceAction = new DeviceActionIfc()
        {
            public Serializable doDeviceAction(DeviceTechnicianIfc dt) throws DeviceException
            {
                CashDrawerActionGroupIfc dag =
                    (CashDrawerActionGroupIfc) dt.getDeviceActionGroup(CashDrawerActionGroupIfc.TYPE);
                dag.setCashDrawerEnabled(cashDrawerEnabled);
                return null;
            }
        };

        transportValet(this);
    }

}
