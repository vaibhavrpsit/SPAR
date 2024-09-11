/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/POSFocusManager.java /main/24 2014/02/05 15:03:16 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  02/04/14 - released the stream handles
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    cgreene   08/24/11 - add null check before using prevFocus component
 *    asinton   01/26/11 - adding support for simulated devices to reduce
 *                         errors in the logging.
 *    cgreene   11/08/10 - add some optional debug printlns
 *    cgreene   07/14/10 - fix key handling by forwarding from frame to
 *                         rootpane
 *    cgreene   07/02/10 - check if root is null before getting next component
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:29:22 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:24:12 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:13:07 PM  Robert Pearse
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Jan 13 2004 13:39:02   baa
 * fix focus bug
 * Resolution for 3541: ID Number field on ID Origin screen has no focus
 *
 *    Rev 1.1   Sep 08 2003 17:30:40   DCobb
 * Migration to jvm 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:11:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 08 2003 18:38:58   baa
 * enable focus with tab key
 * Resolution for 2259: Web Store -No focus w/in website does not allow user to navigate w/out mouse
 *
 *    Rev 1.2   Apr 22 2003 15:38:14   baa
 * fix focus issues with the tab key
 * Resolution for POS SCR-2192: Tabbing from Manager Options Screens disables the "Enter' key
 *
 *    Rev 1.1   Aug 15 2002 17:55:56   baa
 * apply foundation  updates to UISubsystem
 *
 * Resolution for POS SCR-1769: 5.2 UI defects resulting from change to java 1.4
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.FocusTraversalPolicy;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import jpos.JposException;
import jpos.MSR;
import jpos.Scanner;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.MSRSession;
import oracle.retail.stores.foundation.manager.device.ScannerSession;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;

import org.apache.log4j.Logger;

/**
 * This class preprocess key events to support wedge keyboards. It also
 * overrides {@link #focusNextComponent()} and {@link #focusPreviousComponent()}
 * so that components contained in a {@link JScrollPane} receive focus directly.
 *
 * @version $Revision: /main/24 $
 */
public class POSFocusManager extends DefaultKeyboardFocusManager
{
    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(POSFocusManager.class);

    /** Property key used to specify keys recording file name. */
    public static final String POSRUNNER_RECORD = "POSrunner.record";

    /** Property key used to specify mode of recording. E.g. "rapid". */
    public static final String POSRUNNER_RECORDMODE = "POSrunner.recordMode";

    /** Property key used to specify whether to println the events handled. */
    public static final String POSFOCUS_DEBUG = "pos.focus.debug";

    protected boolean doKeyFiltering;
    protected KeyFilterListener keyFilterListener;
    protected Scanner scanner;
    protected MSR msr;
    protected String sessionName;
    protected boolean recordKeyStrokes = false;
    protected String recordKeyStrokesFile = null;
    protected String recordMode;
    protected FileWriter fw = null;
    protected long lastTime = 0;
    protected boolean debug = false;

    /** The key that will be consume if a typed event is dispatched. */
    private int ignoreNextKeyEvent = -1;

    /**
     *  Focus Manager Constructor
     */
    public POSFocusManager()
    {
        doKeyFiltering = true;
        recordKeyStrokesFile = System.getProperty(POSRUNNER_RECORD);
        recordMode = new String();
        recordMode = System.getProperty(POSRUNNER_RECORDMODE);
        if(recordKeyStrokesFile != null)
        {
            recordKeyStrokes = true;
        }
        debug = (System.getProperty(POSFOCUS_DEBUG) != null);
    }

    /**
     * Overridden to handle the cases where focus is on the {@link JFrame} and
     * the function keys are not handled. The rootpane will be given a chance
     * to handle the event itself.
     * <p>
     * We will also ignore events if {@link #getIgnoreNextKeyEvent()} matches.
     *
     * @see java.awt.DefaultKeyboardFocusManager#dispatchEvent(java.awt.AWTEvent)
     */
    @Override
    public boolean dispatchEvent(AWTEvent e)
    {
        // check if we should ignore the event instead of dispatching it.
        if ((e.getID() == KeyEvent.KEY_TYPED ||
                e.getID() == KeyEvent.KEY_RELEASED) && e instanceof KeyEvent)
        {
            KeyEvent ke = (KeyEvent) e;
            if (ke.getKeyChar() == getIgnoreNextKeyEvent())
            {
                if (debug) System.out.println("Consuming key event for " + ke);
                if (e.getID() == KeyEvent.KEY_RELEASED)
                {
                    setIgnoreNextKeyEvent(-1);
                }
                ke.consume();
                return false;
            }
        }

        // dispatch the event.
        if (debug) System.out.println("Dispatching " + e);
        boolean dispatched = super.dispatchEvent(e);
        if (debug) System.out.println("Dispatched " + dispatched);
        if (dispatched && e instanceof KeyEvent)
        {
            KeyEvent ke = (KeyEvent)e;
            if (!ke.isConsumed())
            {
                Component comp = ke.getComponent();
                if (comp instanceof JFrame)
                {
                   if (debug) System.out.println("Redispatching to JFrame.");
                   redispatchEvent(((JFrame)comp).getRootPane(), ke);
                }
                else if (comp instanceof JDialog)
                {
                   if (debug) System.out.println("Redispatching to JDialog.");
                   redispatchEvent(((JDialog)comp).getRootPane(), ke);
                }
                else if (debug)
                {
                    System.out.println("Key not consumed by component " + ke.getComponent());
                }
            }
            else if (debug)
            {
                System.out.println("Key consumed by " + ke.getComponent());
            }
        }
        return dispatched;
    }

    /**
     * Process a key event
     *
     * @param focusedComponent the focus component
     * @param aEvent the event to be process
     */
    @Override
    public void processKeyEvent(Component focusedComponent, KeyEvent aEvent)
    {
        if(recordKeyStrokes)
        {
            recordKeyStroke(aEvent);
        }

        if (debug) System.out.println("Processing key " + aEvent  + " on component " + focusedComponent);
        super.processKeyEvent(focusedComponent, aEvent);

        if (aEvent.getID() == KeyEvent.KEY_TYPED)
        {
            if (doKeyFiltering)
            {
                getKeyFilterListener().keyTyped(aEvent);
            }
        }

    }

    /**
     * Record the keystroke into the {@link #recordKeyStrokesFile}. This method
     * should only be called if {@link #recordKeyStrokes} is turned on.
     *
     * @param aEvent
     */
    protected void recordKeyStroke(KeyEvent aEvent)
    {
        if(aEvent.getID() == KeyEvent.KEY_PRESSED)
        {
            try(FileWriter fw = new FileWriter(recordKeyStrokesFile, true);)
            {
                if(!recordMode.equals("rapid"))
                {
                    fw.write("actualTime "+System.currentTimeMillis()+"\n");
                }
                fw.write("send defaultPOSKeyboard command=keyPress&keyData="+aEvent.getKeyCode()+"\n");
            }
            catch(IOException ioe){}
        }
        else if(aEvent.getID() == KeyEvent.KEY_RELEASED)
        {
            try(FileWriter fw = new FileWriter(recordKeyStrokesFile, true);)
            {
                if(!recordMode.equals("rapid"))
                {
                    fw.write("actualTime "+System.currentTimeMillis()+"\n");
                }
                fw.write("send defaultPOSKeyboard command=keyRelease&keyData="+aEvent.getKeyCode()+"\n");
            }
            catch(IOException ioe){}
        }
    }

    /**
     * Transfers focus to the next component in the traversal policy. If the
     * focus owner is contained by a {@link JScrollPane}, focus will be
     * transfered to the next component after, which is usually the component
     * within the scroll pane, e.g. a table or list.
     *
     * @param aComponent the component that currently owns focus.
     */
    @Override
    public void focusNextComponent(Component aComponent)
    {
        Container root = aComponent.getFocusCycleRootAncestor();
        if (root != null)
        {
            FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
            Component nextFocus = policy.getComponentAfter(root,aComponent);
            if (nextFocus != null)
            {
                if (nextFocus instanceof BaseBeanAdapter)
                {
                    root = getCurrentFocusCycleRoot();
                    policy = root.getFocusTraversalPolicy();
                    nextFocus = policy.getComponentAfter(root,aComponent);
                    super.focusNextComponent(nextFocus);
                }
                else if (nextFocus instanceof JScrollPane ||
                         nextFocus.getParent() instanceof JScrollPane)
                {
                    super.focusNextComponent(nextFocus);
                }
                else if (nextFocus.isFocusable())
                {
                    super.focusNextComponent(aComponent);
                }
            }
        }
    }

    /**
     * Transfers focus to the previous component in the traversal policy. If the
     * focus owner is contained by a {@link JScrollPane}, focus will be transfer
     * to the component before the JScrollPane.
     *
     * @param aComponent the component that currently owns focus.
     */
    @Override
    public void focusPreviousComponent(Component aComponent)
    {
        Container root = aComponent.getFocusCycleRootAncestor();
        FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
        Component prevFocus = policy.getComponentBefore(root, aComponent);

        if (prevFocus instanceof BaseBeanAdapter )
        {
            root = getCurrentFocusCycleRoot();
            policy = root.getFocusTraversalPolicy();
            prevFocus = policy.getComponentBefore(root,aComponent);
            super.focusPreviousComponent(prevFocus);
        }
        else if (prevFocus != null && (prevFocus instanceof JScrollPane ||
                 prevFocus.getParent() instanceof JScrollPane))
        {
            super.focusPreviousComponent(prevFocus);
        }
        else
        {
            super.focusPreviousComponent(aComponent);
        }
    }

    /**
     * @return the ignoreNextKeyEvent
     */
    protected synchronized int getIgnoreNextKeyEvent()
    {
        return ignoreNextKeyEvent;
    }

    /**
     * @param ignoreNextKeyEvent the ignoreNextKeyEvent to set
     */
    public synchronized void setIgnoreNextKeyEvent(int ignoreNextKeyEvent)
    {
        this.ignoreNextKeyEvent = ignoreNextKeyEvent;
    }

    /**
     * Key listener filter
     */
    public KeyFilterListener getKeyFilterListener()
    {
        DeviceTechnicianIfc dt = null;
        DeviceFilter deviceFilter = null;

        if(keyFilterListener == null)
        {

            keyFilterListener = new KeyFilterListener();
            try
            {
                dt = (DeviceTechnicianIfc)
                    Gateway.getDispatcher().getLocalTechnician(DeviceTechnicianIfc.TYPE);

                if ( dt != null)
                {
                    try
                    {
                        sessionName = ScannerSession.TYPE;
                        ScannerSession scannerSession = (ScannerSession) dt.getDeviceSession(sessionName);
                        scanner = (Scanner) scannerSession.getDevice();
                    }
                    catch (DeviceException e)
                    {
                        logger.error("DeviceException with ScannerSession", e);
                    }

                    try
                    {
                        sessionName = MSRSession.TYPE;
                        MSRSession msrSession = (MSRSession) dt.getDeviceSession(sessionName);
                        msr = (MSR) msrSession.getDevice();
                    }
                    catch (DeviceException e)
                    {
                        logger.error("DeviceException with MSRSession", e);
                    }
                }
            }
            catch (TechnicianNotFoundException e)
            {
                logger.error("Can't get DeviceTechnician", e);
            }
            catch (Exception e)
            {
                logger.error("Can't get DeviceTechnician", e);
            }

            doKeyFiltering = false;
            String pdn;
            if(scanner != null)
            {
                try
                {
                    pdn = scanner.getPhysicalDeviceName();
                    if(pdn != null)
                    {
                        if(pdn.equals("Wedge Scanner Device"))
                        {
                            deviceFilter = new DeviceFilter(scanner);
                            keyFilterListener.addDeviceFilter(deviceFilter);
                            doKeyFiltering = true;
                        }
                    }
                }
                catch (JposException je)
                {
                    logger.error("JposException occurred with scanner.", je);
                }
            }

            if(msr != null)
            {
                try
                {
                    pdn = msr.getPhysicalDeviceName();
                    if(pdn != null)
                    {
                        if(pdn.equals("Wedge MSR Device"))
                        {
                            deviceFilter = new DeviceFilter(msr,1);
                            keyFilterListener.addDeviceFilter(deviceFilter);
                            deviceFilter = new DeviceFilter(msr,2);
                            keyFilterListener.addDeviceFilter(deviceFilter);
                            doKeyFiltering = true;
                        }
                    }
                }
                catch (JposException je)
                {
                    logger.error("JposException occurred with MSR.", je);
                }
            }
            logger.info("doKeyFiltering=" + doKeyFiltering);
        }
        return keyFilterListener;
    }
}