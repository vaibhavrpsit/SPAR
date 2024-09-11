/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/CheckPointUIManager.java /main/6 2014/07/18 09:53:39 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/16/14 - implement ability to timeout when notified. This
 *                         unblocks and mails timeout letter to bus.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.naming.MailboxAddress;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This class overrides methods in its parent class in order to stop any
 * screen display request from proceeding.
 * <p>
 * Since this UIManager prevents its bus from receiving letters
 * (see #addLetterListener(MailboxAddress)) while blocked, it has to be
 * explicitly told to timeout. It will then unblock and send an timeout letter
 * to its bus.
 * 
 * @author cgreene
 * @since 13.1
 */
public class CheckPointUIManager extends POSUIManager implements CheckPointUIManagerIfc
{
    protected boolean checkPointBlocked;
    protected List<MailboxAddress> blockedListeners;
    /**
     * #timeoutOccurred indicates the UI has had a timeout and this manager has
     * been notified and relayed the message to its bus. #handlingTimeout will
     * be true until the letter is mailed. While true, this UI manager will
     * ignore requests to show a screen. This prevents a screen from showing
     * one that was waiting to be unblocked before mailing the timeout letter.
     *
     * @since 14.1. */
    protected boolean timeoutOccurred, handlingTimeout;

    /**
     * Default constructor. Sets {@link #checkPointBlocked} to true.
     */
    public CheckPointUIManager()
    {
        checkPointBlocked = true;
        blockedListeners = new ArrayList<MailboxAddress>(1);
    }

    /**
     * Block adding any bus addresses from registering as a listener of the UI
     * until the check point is cleared. If {@link #setCheckPointBlocked(boolean)}
     * is called with <code>true</code> then the blocked addresses will then
     * be registered.
     * 
     * @see oracle.retail.stores.pos.ui.POSUIManager#addLetterListener(oracle.retail.stores.foundation.naming.MailboxAddress)
     */
    public void addLetterListener(MailboxAddress address)
    {
        if (!isCheckPointBlocked())
        {
            super.addLetterListener(address);
        }
        else
        {
            blockedListeners.add(address);
        }
    }

    /**
     * There will lots of status changes that set new models. Allow this to
     * proceed and not be blocked.
     * 
     * @see oracle.retail.stores.pos.ui.POSUIManager#setModel(java.lang.String, oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    @Override
    public void setModel(String screenId, UIModelIfc beanModel)
    {
        super.setModel(screenId, beanModel);
    }

    /**
     * Showing any screen should block in this tier until the application tier
     * allows it. Calls {@link #stopAtCheckPoint()}.
     * 
     * @see oracle.retail.stores.pos.ui.POSUIManager#showScreen(java.lang.String)
     */
    @Override
    public void showScreen(String screenID)
    {
        stopAtCheckPoint();
        if (!handlingTimeout)
        {
            super.showScreen(screenID);
        }
        else
        {
            logger.debug("Ignoring request to show screen \"" + screenID + "\" until Timeout letter is mailed.");
        }
    }

    /**
     * Showing any screen should block in this tier until the application tier
     * allows it. Calls {@link #stopAtCheckPoint()}.
     * 
     * @see oracle.retail.stores.pos.ui.POSUIManager#showScreen(java.lang.String, oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    @Override
    public void showScreen(String screenID, UIModelIfc beanModel)
    {
        stopAtCheckPoint();
        if (!handlingTimeout)
        {
            super.showScreen(screenID, beanModel);
        }
        else
        {
            logger.debug("Ignoring request to show screen \"" + screenID + "\" until Timeout letter is mailed.");
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.CheckPointUIManagerIfc#isCheckPointBlocked()
     */
    @Override
    public synchronized boolean isCheckPointBlocked()
    {
        return checkPointBlocked;
    }

    /**
     * Sets whether this UI manager should block method calls. Calls
     * {@link #notify()} and {@link #addLetterListener(MailboxAddress)} after
     * setting variable to <code>true</code>.
     * 
     * @param checkPointBlocked
     * @see oracle.retail.stores.pos.ui.CheckPointUIManagerIfc#setCheckPointBlocked(boolean)
     */
    @Override
    public synchronized void setCheckPointBlocked(boolean checkPointBlocked)
    {
        this.checkPointBlocked = checkPointBlocked;
        if (!checkPointBlocked)
        {
            while (blockedListeners.size() > 0)
            {
                addLetterListener(blockedListeners.remove(0));
            }
            notify(); // wake up that thread
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.CheckPointUIManagerIfc#timeout()
     */
    @Override
    public void timeout()
    {
        if (!timeoutOccurred)
        {
            // start completely blocking requests to showScreen
            handlingTimeout = true;
            // unblock the tour
            setCheckPointBlocked(false);
            // sleep a bit in case another thread was blocked trying to showScreen.
            // This gives that thread a chance to move past the checkpoint.
            try { Thread.sleep(30); } catch (InterruptedException ex) {}
            // mail Timeout letter. The bus driver's thread should start shortly
            getBus().mail(CommonLetterIfc.TIMEOUT);
            // we no longer need to block showScreen
            handlingTimeout = false;
            // mark this timeout as having occurred.
            timeoutOccurred =  true;
        }
        else
        {
            logger.debug("Ignoring timeout call since this manager has already mailed Timeout letter.");
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.gui.UIManager#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = new StringBuilder("CheckPointUIManager@");
        strResult.append(hashCode());
        strResult.append("[address=").append(address);
        strResult.append(",bus=").append(getBus());
        strResult.append(",uiTechnician=").append(uiTechnician);
        strResult.append("]");
        return strResult.toString();
    }

    /**
     * If {@link #checkPointBlocked} is true, then this method will make the
     * current thread wait until notified.
     */
    protected synchronized void stopAtCheckPoint()
    {
        while (isCheckPointBlocked())
        {
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
            }
        }
    }
}
