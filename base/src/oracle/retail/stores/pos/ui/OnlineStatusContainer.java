/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/OnlineStatusContainer.java /rgbustores_13.4x_generic_branch/2 2011/08/30 14:01:00 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     08/29/11 - Use a singleton for OnlineStatusContainer as
 *                         multiple instances resulted in erroneous status.
 *    blarsen   08/17/10 - Fixed various warnings.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:46 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
 * $
 * Revision 1.9  2004/09/23 00:07:10  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.8  2004/07/28 19:52:30  rsachdeva
 * @scr 5820 TransactionReentryMode Status I18N
 *
 * Revision 1.7  2004/07/06 22:17:28  lzhao
 * @scr 4019: make online green, offline red in status field of status bean
 *
 * Revision 1.6  2004/04/16 14:39:39  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.5  2004/03/26 21:18:20  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.4  2004/03/21 16:34:29  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.3 2004/02/12 16:52:11 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:52:28 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:21 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.0 Aug 29 2003 16:09:22 CSchellenger Initial revision.
 * 
 * Rev 1.1 14 May 2002 18:29:52 baa training mode enhancements Resolution for POS SCR-1645: Training Mode Enhancements
 * 
 * Rev 1.0 Apr 29 2002 14:45:02 msg Initial revision.
 * 
 * Rev 1.0 Mar 18 2002 11:51:42 msg Initial revision.
 * 
 * Rev 1.2 Feb 27 2002 21:25:54 mpm Continuing work on internationalization Resolution for POS SCR-351:
 * Internationalization
 * 
 * Rev 1.1 Jan 19 2002 10:28:44 mpm Initial implementation of pluggable-look-and-feel user interface. Resolution for
 * POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 * Rev 1.0 Sep 21 2001 11:33:36 msg Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;
// Java imports
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

//----------------------------------------------------------------------------
/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
//----------------------------------------------------------------------------
public class OnlineStatusContainer implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8799848816519715802L;

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";
    /**
     * Each element holds the status of one system or device that can be offline.
     */
    protected Hashtable<Integer, Boolean> onlineStatus = null;
    /**
     * Holds the current overall offline status.
     */
    protected boolean online = true;
    /**
     * Holds the current training mode status.
     */
    protected boolean trainingMode = false;

    /**
     * Holds the current transaction reentry mode status.
     */
    protected boolean transReentryMode = false;

    // Constants for the display text
    protected static final String ONLINE_TEXT = "Online";
    protected static final String OFFLINE_TEXT = "Offline";
    protected static final String TRAINING_MODE_TEXT = "Training Mode";
    protected static final String TRANS_REENTRY_MODE_TEXT = "Transaction Reentry Mode";

    /**
     * online text
     */
    protected String onlineText = ONLINE_TEXT;
    /**
     * offline text
     */
    protected String offlineText = OFFLINE_TEXT;
    /**
     * training mode text
     */
    protected String trainingModeText = TRAINING_MODE_TEXT;
    /**
     * transaction reentry mode text
     */
    protected String transReentryModeText = TRANS_REENTRY_MODE_TEXT;
    /**
     * transaction reentry mode tag
     */
    protected static final String TRANS_REENTRY_MODE_TAG = "TransactionReentryMode";
    /**
     * properties reference
     */
    protected Properties props = null;

    //---------------------------------------------------------------------
    /**
     * The default constructor creates a new online status hashtable.
     * <P>
     */
    //---------------------------------------------------------------------
    public OnlineStatusContainer()
    {
        onlineStatus = new Hashtable<Integer, Boolean>(10);
    }

    /**
     * A singleton instance useful for sharing status between UI beans. 
     */
    private static OnlineStatusContainer singleton;

    /**
     * Retrieve the instance of this status container that should be shared
     * between UI beans as they receive status notifications.
     * 
     * @return
     */
    public static OnlineStatusContainer getSharedInstance()
    {
        if (singleton == null)
        {
            singleton = new OnlineStatusContainer();
        }
        return singleton;
    }

    //---------------------------------------------------------------------
    /**
     * Update the the status of the the system in the hashtable and determines if the overall status has changed.
     * <P>
     * 
     * @param systemId
     *            identifies the system (i.e. printer, cashdrawer, transactions.)
     * @param parmOnline
     *            true indicates the system is online.
     * @return boolean true indicates the the overall status has changed
     */
    //---------------------------------------------------------------------
    public synchronized boolean setOnlineStatus(int systemId, boolean parmOnline)
    {
        /*
         * Update the entry in the hashtable.
         */
        Integer id = new Integer(systemId);
        Boolean status = new Boolean(parmOnline);
        boolean statusHasChanged = false;

        if (systemId == POSUIManagerIfc.TRAINING_MODE_STATUS)
        {
            trainingMode = parmOnline;
            statusHasChanged = true;
        }
        else if (systemId == POSUIManagerIfc.TRANS_REENTRY_STATUS)
        {
            transReentryMode = parmOnline;
            statusHasChanged = true;
        }
        else
        {
            // Add the the key and value to the hashtable.
            onlineStatus.put(id, status);

            /*
             * Determine if the overall status has changed.
             */
            boolean holdStatus = true;
            Enumeration<Boolean> statusEnum = onlineStatus.elements();

            // If any element in the hash table is false, then the overall
            // status is OFFLINE.
            while (statusEnum.hasMoreElements())
            {
                Boolean elementStatus = statusEnum.nextElement();
                if (!(elementStatus.booleanValue()))
                {
                    holdStatus = false;
                }
            }

            if (holdStatus != online)
            {
                statusHasChanged = true;
            }

            online = holdStatus;
        }

        return statusHasChanged;
    }

    //---------------------------------------------------------------------
    /**
     * Obtains the the status of the the system in the hashtable.
     * <P>
     * 
     * @return Hashtable onlineStatus hashtable of devices and their status.
     */
    //---------------------------------------------------------------------
    public synchronized Hashtable<Integer, Boolean> getStatusHash()
    {
        return onlineStatus;
    }

    //---------------------------------------------------------------------
    /**
     * Updates this container with the values of the new container
     * 
     * @param newContainer
     *            OnlineStatusContainer with new values
     */
    //---------------------------------------------------------------------
    public void update(OnlineStatusContainer newContainer)
    {
        Integer id = null;
        Hashtable<Integer, Boolean> newHash = newContainer.getStatusHash();

        if (newHash != null)
        {
            Enumeration<Integer> newKeys = newHash.keys();
            // If we had the entry, update it, if not, add it.
            while (newKeys.hasMoreElements())
            {
                id = newKeys.nextElement();
                onlineStatus.put(id, newHash.get(id));
            }

            Enumeration<Integer> statusKeys = onlineStatus.keys();
            Boolean status = null;
            online = true;

            // Update the overall status
            while (statusKeys.hasMoreElements())
            {
                id = statusKeys.nextElement();
                status = onlineStatus.get(id);

                if (id.intValue() == POSUIManagerIfc.TRAINING_MODE_STATUS)
                {
                    trainingMode = status.booleanValue();
                }
                else if (id.intValue() == POSUIManager.TRANS_REENTRY_STATUS)
                {
                    transReentryMode = status.booleanValue();
                }
                else if (!status.booleanValue())
                {
                    online = false;
                }
            }
        }
    }

    //---------------------------------------------------------------------
    /**
     * Returns a string containing the text associated with the current overall state.
     * 
     * @return String Online or Offline
     */
    //---------------------------------------------------------------------
    public synchronized String getStatusText()
    {

        if (trainingMode)
        {
            return trainingModeText;
        }
        else if (transReentryMode)
        {
            return transReentryModeText;
        }
        else if (online)
        {
            return onlineText;
        }
        else
        {
            return offlineText;
        }
    }

    //---------------------------------------------------------------------
    /**
     * Returns a boolean containing with the current training mode status overall state.
     * 
     * @return String Online or Offline
     */
    //---------------------------------------------------------------------
    public boolean isTrainingMode()
    {
        return trainingMode;
    }

    //---------------------------------------------------------------------
    /**
     * Returns a boolean containing the system is offline or online.
     * 
     * @return boolean Online or Offline
     */
    //---------------------------------------------------------------------
    public boolean isOnline()
    {
        return online;
    }
    
    //---------------------------------------------------------------------------
    /**
     * Sets the properties object.
     * 
     * @param props
     *            the properties object.
     */
    public void setProps(Properties props)
    {
        this.props = props;
        updatePropertyFields();
    }

    //---------------------------------------------------------------------
    /**
     * Updates property fields.
     * <P>
     */
    //---------------------------------------------------------------------
    public void updatePropertyFields()
    { // begin updatePropertyFields()
        if (props != null)
        {
            onlineText = props.getProperty("Online", ONLINE_TEXT);
            offlineText = props.getProperty("Offline", OFFLINE_TEXT);
            trainingModeText = props.getProperty("TrainingMode", TRAINING_MODE_TEXT);
            transReentryModeText = props.getProperty(TRANS_REENTRY_MODE_TAG, TRANS_REENTRY_MODE_TEXT);
        }
    } // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
     * Method to default display string function.
     * <P>
     * 
     * @return String representation of object
     */
    //---------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult =
            new String("Class:  OnlineStatusContainer (Revision " + getRevisionNumber() + ")" + hashCode());
        // pass back result
        return (strResult);
    } // end toString()

    //---------------------------------------------------------------------
    /**
     * Retrieves the Team Connection revision number.
     * <P>
     * 
     * @return String representation of revision number
     */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        return (revisionNumber);
    } // end getRevisionNumber()
    /**
     * @return Returns the transReentryMode.
     */
    public boolean isTransReentryMode()
    {
        return transReentryMode;
    }

    /**
     * @param transReentryMode
     *            The transReentryMode to set.
     */
    public void setTransReentryMode(boolean transReentryMode)
    {
        this.transReentryMode = transReentryMode;
    }

}
