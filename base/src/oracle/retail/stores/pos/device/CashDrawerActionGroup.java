/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/CashDrawerActionGroup.java /rgbustores_13.4x_generic_branch/1 2011/04/08 09:27:45 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   04/27/09 - added Thread.sleep to loop that checks for cash
 *                         drawer open
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:00 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:09:49 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/09/23 00:07:13  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.4  2004/03/31 20:19:01  bjosserand
 *  @scr 4093 Transaction Reentry
 *
 *  Revision 1.3  2004/02/12 16:48:34  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:30:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:51:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jan 07 2003 17:57:26   vxs
 * Initial revision.
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 * Revision: /main/7 $
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import jpos.CashDrawer;
import jpos.JposException;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.device.CashDrawerSession;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.DeviceMode;
import oracle.retail.stores.foundation.manager.device.OpenCashDrawer;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModeIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DrawerClosedListenerIfc;

/**
 * The <code>CashDrawerActionGroupIfc</code> defines the Cash Drawer specific
 * device operations available to POS applications.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc
 */
public class CashDrawerActionGroup implements CashDrawerActionGroupIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 2671379856180050308L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(CashDrawerActionGroup.class);

    /**
     * revision number supplied by source-code control system
     */
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:43; $EKW;";

    /**
     * Link back to device sessions
     */
    protected DeviceTechnicianIfc deviceTechnician;

    /**
     * Name used by device technician
     */
    protected String name;

    /**
     * The time to wait in milliseconds before sending an audible signal to the
     * user that the cash drawer is open.
     */
    protected int beepTimeout;

    /**
     * The frequency in Hertz of the audible cash drawer open alert signal.
     */
    protected int beepFrequency;

    /**
     * The duration in milliseconds of the audible cash drawer open alert
     * signal.
     */
    protected int beepDuration;

    /**
     * The delay in milliseconds between successive audible cash drawer open
     * alert signals.
     */
    protected int beepDelay;

    /**
     * The delay in milliseconds before exiting the waiting thread in the case
     * where there is no cash drawer.
     */
    protected long cashDrawerDelay;

    /**
     * indicator for beep
     */
    protected boolean beepOn;

    /**
     * wait time for drawer to open
     */
    protected long openDrawerWait = 500;

    /**
     * cash drawer session type
     */
    protected String currentCashDrawerSession = CashDrawerSession.TYPE;

    /**
     * Indicates if the cash drawer should be opened
     */
    protected boolean cashDrawerEnabled = true;

    /**
     * Sets device technician reference.
     * 
     * @see oracle.retail.stores.foundation.manager.ifc.device.DeviceActionGroupIfc#setDeviceTechnician
     */
    public void setDeviceTechnician(DeviceTechnicianIfc dt) throws DeviceException
    {
        deviceTechnician = dt;
    }

    /**
     * Gets device technician reference.
     * 
     * @see oracle.retail.stores.foundation.manager.ifc.device.DeviceActionGroupIfc#getDeviceTechnician
     * @exception DeviceException thrown if DeviceTechnician is null
     */
    public DeviceTechnicianIfc getDeviceTechnician() throws DeviceException
    {
        if (deviceTechnician == null)
        {
            throw new DeviceException("Unable to find DeviceTechnician in client session.");
        }
        return (deviceTechnician);

    }

    /**
     * @see oracle.retail.stores.foundation.manager.ifc.device.DeviceActionGroupIfc#setName
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @see oracle.retail.stores.foundation.manager.ifc.device.DeviceActionGroupIfc#getName
     */
    public String getName()
    {
        if (name == null)
        {
            return (TYPE);
        }
        return (name);

    }

    /**
     * Sets the time to wait in milliseconds before sending an audible signal to
     * the user that the cash drawer is open.
     * 
     * @param value cash-drawer-open-alert beep timeout
     */
    public void setBeepTimeout(int value)
    {
        beepTimeout = value;
    }

    /**
     * Retrieves the time to wait in milliseconds before sending an audible
     * signal to the user that the cash drawer is open.
     * 
     * @return cash-drawer-open-alert beep timeout
     */
    public int getBeepTimeout()
    {
        return beepTimeout;
    }

    /**
     * Sets the frequency in Hertz of the audible cash drawer open alert signal.
     * 
     * @param value cash-drawer-open-alert beep frequency
     */
    public void setBeepFrequency(int value)
    {
        beepFrequency = value;
    }

    /**
     * Gets the frequency in Hertz of the audible cash drawer open alert signal.
     * 
     * @return cash-drawer-open-alert beep frequency
     */
    public int getBeepFrequency()
    {
        return beepFrequency;
    }

    /**
     * Sets the duration in milliseconds of the audible cash drawer open alert
     * signal.
     * 
     * @param value cash-drawer-open-alert beep duration
     */
    public void setBeepDuration(int value)
    {
        beepDuration = value;
    }

    /**
     * Gets the duration in milliseconds of the audible cash drawer open alert
     * signal.
     * 
     * @return cash-drawer-open-alert beep duration
     */
    public int getBeepDuration()
    {
        return beepDuration;
    }

    /**
     * Sets the delay in milliseconds between successive audible cash drawer
     * open alert signals.
     * 
     * @param value cash-drawer-open-alert beep delay
     */
    public void setBeepDelay(int value)
    {
        beepDelay = value;
    }

    /**
     * Gets the delay in milliseconds between successive audible cash drawer
     * open alert signals.
     * 
     * @return cash-drawer-open-alert beep delay
     */
    public int getBeepDelay()
    {
        return beepDelay;
    }

    /**
     * Gets the delay to wait before invoking the cashDrawerClosedListener, in
     * case the cash drawer is not present.
     * 
     * @return cash-drawer-not-present-signal delay
     */
    public long getCashDrawerDelay()
    {
        return cashDrawerDelay;
    }

    /**
     * Sets the delay to wait before invoking the cashDrawerClosedListener, in
     * case the cash drawer is not present.
     * 
     * @param value cash-drawer-not-present-signal delay
     */
    public void setCashDrawerDelay(long value)
    {
        cashDrawerDelay = value;
    }

    /**
     * Returns the beep-on flag.
     * 
     * @return beep-on flag
     */
    public boolean getBeepOn()
    {
        return beepOn;
    }

    /**
     * Sets the beep-on flag.
     * 
     * @param value Beep On flag
     */
    public void setBeepOn(boolean value)
    {
        beepOn = value;
    }

    /**
     * Gets the delay to wait when opening the cash drawer
     * 
     * @return open-drawer wait
     */
    public long getOpenDrawerWait()
    {
        return openDrawerWait;
    }

    /**
     * Sets the delay to wait when opening the cash drawer
     * 
     * @return value open-drawer wait
     */
    public void setOpenDrawerWait(long value)
    {
        openDrawerWait = value;
    }

    /**
     * Opens the cash drawer and sends notification of drawer closure via
     * drawerClosedListener
     * <p>
     * If any of the cash following parameters are set from the DeviceScript,
     * they are transfered to the OpenCashDrawer bean:
     * <UL>
     * <LI>beepTimeout
     * <LI>beepFrequency
     * <LI>beepDuration
     * <LI>beepDelay
     * <LI>cashDrawerDelay
     * </UL>
     * 
     * @param drawerClosedListener allows the calling application to
     *            asynchronously wait for the cash drawer to close.
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#openCashDrawer
     */
    public void openCashDrawer(DrawerClosedListenerIfc drawerClosedListener) throws DeviceException
    {
        if (getCashDrawerEnabled())
        {
            DeviceTechnicianIfc dt = getDeviceTechnician();

            CashDrawerSession cashDrawerSession = (CashDrawerSession)dt.getDeviceSession(CashDrawerSession.TYPE);

            OpenCashDrawer openCashDrawer = new OpenCashDrawer();
            openCashDrawer.setCashDrawerSession(cashDrawerSession);
            openCashDrawer.addDrawerClosedListener(drawerClosedListener);
            if (getBeepTimeout() > 0)
            {
                openCashDrawer.setBeepTimeout(getBeepTimeout());
            }
            if (getBeepFrequency() > 0)
            {
                openCashDrawer.setBeepFrequency(getBeepFrequency());
            }
            if (getBeepDuration() > 0)
            {
                openCashDrawer.setBeepDuration(getBeepDuration());
            }
            if (getBeepDelay() > 0)
            {
                openCashDrawer.setBeepDelay(getBeepDelay());
            }
            if (getCashDrawerDelay() > 0)
            {
                openCashDrawer.setCashDrawerDelay(getCashDrawerDelay());
            }

            openCashDrawer.openCashDrawer();
        }
    }

    /**
     * get the CashDrawer device from the DeviceTechnician
     * 
     * @return value CashDrawer
     */
    public CashDrawer getCashDrawer() throws DeviceException
    {
        DeviceTechnicianIfc dt;
        DeviceSessionIfc cashDrawerSession = null;
        CashDrawer jposCashDrawer;

        try
        {
            dt = getDeviceTechnician();
            cashDrawerSession = dt.getDeviceSession(currentCashDrawerSession);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(currentCashDrawerSession);
            dm.setDeviceModeName(CashDrawerSession.MODE_READY_TO_OPEN);
            jposCashDrawer = (jpos.CashDrawer)cashDrawerSession.getDeviceInMode(dm);
        }
        catch (DeviceException e)
        {
            throw e;
        }
        finally
        {
            if (cashDrawerSession != null)
            {
                cashDrawerSession.releaseDevice();
            }
        }
        return jposCashDrawer;
    }

    /**
     * Pop open the cash drawer
     */
    public void openCashDrawer() throws DeviceException
    {
        if (getCashDrawerEnabled())
        {
            try
            {
                CashDrawer cashDrawer = getCashDrawer();
                cashDrawer.openDrawer();
                long endTime = System.currentTimeMillis() + openDrawerWait;
                boolean cashDrawerOpened = cashDrawer.getDrawerOpened();
                while (!cashDrawerOpened && System.currentTimeMillis() < endTime)
                {
                    try
                    {
                        Thread.sleep(20);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    cashDrawerOpened = cashDrawer.getDrawerOpened();
                }

                if (!cashDrawerOpened)
                {
                    throw new DeviceException(DeviceException.JPOS_ERROR,
                            "CashDrawerDevice: openDrawer failed: timeout");
                }
            }
            catch (JposException e)
            {
                logger.error(
                    "CashDrawer Exception: Error opening the cash drawer. "
                        + e.getMessage()
                        + "  Error Code:  " + e.getErrorCode()
                        + "  Extended Error Code:  " + e.getErrorCodeExtended());
                throw new DeviceException(DeviceException.JPOS_ERROR, "CashDrawerDevice: openDrawer failed", e);
            }
        }
    }

    /**
     * Check to see if cash drawer is open.
     */
    public Boolean isOpen() throws DeviceException
    {
        boolean open = false;
        if (getCashDrawerEnabled())
        {
            try
            {
                open = getCashDrawer().getDrawerOpened();
            }
            catch (JposException e)
            {
                logger.error(
                    "CashDrawer Exception: Error getting drawer opened status. "
                        + e.getMessage()
                        + "  Error Code:  " + e.getErrorCode()
                        + "  Extended Error Code:  " + e.getErrorCodeExtended());
                throw new DeviceException(DeviceException.JPOS_ERROR, "CashDrawerDevice: isOpen failed", e);
            }
        }
        return (new Boolean(open));
    }

    /**
     * Wait for the cash drawer to close
     */
    public void waitForDrawerClose() throws DeviceException
    {
        if (getCashDrawerEnabled())
        {
            try
            {
                CashDrawer cashDrawer = getCashDrawer();

                if (cashDrawer.getDrawerOpened())
                {
                    if (beepOn)
                    {
                        cashDrawer.waitForDrawerClose(beepTimeout, beepFrequency, beepDuration, beepDelay);
                    }
                    else
                    {
                        cashDrawer.waitForDrawerClose(0, 0, 0, 0);
                    }
                }
            }
            catch (JposException e)
            {
                logger.error(
                    "CashDrawer Exception: Wait for drawer close failed. "
                        + e.getMessage()
                        + "  Error Code:  " + e.getErrorCode()
                        + "  Extended Error Code:  " + e.getErrorCodeExtended());
                throw new DeviceException(DeviceException.JPOS_ERROR, "CashDrawerDevice: waitForDrawerClose failed", e);
            }
        }
    }

    /**
     * Enable alert beep.
     */
    public void alertBeepOn() throws DeviceException
    {
        beepOn = true;
    }

    /**
     * Disable alert beep.
     */
    public void alertBeepOff() throws DeviceException
    {
        beepOn = false;
    }

    /**
     * Reset cashdrawer
     */
    public void resetCashDrawer() throws DeviceException
    {
        DeviceTechnicianIfc dt;
        DeviceSessionIfc cashDrawerSession = null;

        try
        {
            dt = getDeviceTechnician();
            cashDrawerSession = dt.getDeviceSession(CashDrawerSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(CashDrawerSession.TYPE);
            dm.setDeviceModeName(CashDrawerSession.MODE_RELEASED);
            cashDrawerSession.getDeviceInMode(dm);
            dm.setDeviceModeName(CashDrawerSession.MODE_READY_TO_OPEN);
            cashDrawerSession.getDeviceInMode(dm);
        }
        catch (DeviceException e)
        {
            throw e;
        }
        finally
        {
            if (cashDrawerSession != null)
            {
                cashDrawerSession.releaseDevice();
            }
        }
    }

    /**
     * Switch the current cashdrawer session
     */
    public void switchCashDrawer(String newCashDrawerSession) throws DeviceException
    {
        DeviceTechnicianIfc dt; 
        DeviceSessionIfc cashDrawerSession = null;
        if(!currentCashDrawerSession.equals(newCashDrawerSession))
        {           
            try
            {
                dt = getDeviceTechnician();
                cashDrawerSession = dt.getDeviceSession(currentCashDrawerSession);
                DeviceModeIfc dm = new DeviceMode();
                dm.setDeviceSessionName(currentCashDrawerSession);
                dm.setDeviceModeName(CashDrawerSession.MODE_RELEASED);
                cashDrawerSession.getDeviceInMode(dm);
                cashDrawerSession.releaseDevice();

                cashDrawerSession = dt.getDeviceSession(newCashDrawerSession);
                dm = new DeviceMode();
                dm.setDeviceSessionName(newCashDrawerSession);
                dm.setDeviceModeName(CashDrawerSession.MODE_READY_TO_OPEN);
                cashDrawerSession.getDeviceInMode(dm);
                currentCashDrawerSession = newCashDrawerSession;
            }
            catch (DeviceException e)
            {
                throw e;
            }
            finally
            {
                if (cashDrawerSession != null)
                {
                    cashDrawerSession.releaseDevice();
                }
            }
        } 
    }

    /**
     * @return Returns the cashDrawerEnabled.
     */
    public boolean getCashDrawerEnabled()
    {
        return cashDrawerEnabled;
    }

    /**
     * @return Returns the cashDrawerEnabled state as a Boolean (for transport).
     */
    public Boolean isCashDrawerEnabled()
    {
        return getCashDrawerEnabled();
    }

    /**
     * @param cashDrawerEnabled The cashDrawerEnabled to set.
     */
    public void setCashDrawerEnabled(boolean cashDrawerEnabled)
    {
        this.cashDrawerEnabled = cashDrawerEnabled;
    }

}
