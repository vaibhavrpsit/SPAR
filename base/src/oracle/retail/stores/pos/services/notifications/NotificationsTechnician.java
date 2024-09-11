/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/notifications/NotificationsTechnician.java /main/5 2014/06/09 17:10:00 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  06/03/14 - mpos notifications retrieval
 *    abondala  06/02/14 - mpos notifications distribution
 *    abondala  05/30/14 - notifications UI related changes
 *    abondala  05/28/14 - notifications distribution
 *    abondala  05/28/14 - notifications available indicator message to
 *                         registers
 *    abondala  05/28/14 - notifications indicator technician
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.notifications;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jms.Message;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.jms.SubscriberTechnician;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * A technician that subscribes to the notifications topic and processes the messages.
 *
 * @since 14.1
 */
public class NotificationsTechnician extends SubscriberTechnician
{

    protected static final Logger logger = Logger.getLogger(NotificationsTechnician.class);

    /**
     * Name used to access the notifications technician within a Dispatcher.
     */
    public static final String TYPE = "NotificationsTechnician";
    
    /** Set of workstation ids. **/
    protected Set<String> workstationIdsSet = new HashSet<String> ();

    /**
     * Constructor
     */
    public NotificationsTechnician()
    {
       initialize();
    }

    /* (non-Javadoc)
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message msg)
    {

        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("NotificationsTechnician received message of notifications availability ");
            }
            
            boolean registerRetrieveNotifications = 
                    getParameterValue(ParameterConstantsIfc.NOTIFICATIONS_RegisterRetrieveNotifications, "N").equalsIgnoreCase("Y");      

            if(registerRetrieveNotifications)
            {
                notifyAllRegistersToCheckMessages();
            }
        }
        catch (Exception e)
        {
            logger.error("JMSException occurred during notification indicator message " + msg, e);
        }
    }
    
    /**
     * Notify all registers (ORPOS, MPOS) that the new messages are available to view.
     */
    protected void notifyAllRegistersToCheckMessages()
    {
        if(workstationIdsSet != null && !workstationIdsSet.isEmpty())
        {
            for (Iterator<String> iterator = workstationIdsSet.iterator(); iterator.hasNext();)
            {
                UIUtilities.addNotificationsAvailableStatus(iterator.next());
            }
        }
    }
    
    protected String getParameterValue(String parameterName, String defaultValue)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc)Gateway.getDispatcher().getManager(ParameterManagerIfc.TYPE);
        
        String result = defaultValue;
        try
        {
            if(pm != null)
            {
                result = pm.getStringValue(parameterName);
            }
        }
        catch (ParameterException e)
        {
            logger.warn(e);
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.manager.ConfigurableTechnician#configure(org.w3c.dom.Element)
     */
    @Override
    protected void configure(Element xmlRoot)
    {
    }
    
    /**
     * Gets MPOS workstationIdsSet that are available.
     * @return
     */
    public Set<String> getWorkstationIdsSet()
    {
        return workstationIdsSet;
    }

    /**
     * Sets the MPOS workstationIdsSet that are available
     * @param Set of workstationIdsSet
     */
    public void setWorkstationIdsSet(Set<String> workstationIdsSet)
    {
        this.workstationIdsSet = workstationIdsSet;
    }

    /**
     * For ORPOS application, use the "POS" as the key to 
     * retrieve notifications status. 
     * 
     * This is overridden by RegisterGroupNotificationsTechnician
     * to add the mobile registers list.
     */
    protected void initialize()
    {
        workstationIdsSet.add("POS");  
    }    
    
    /**
     * Add all the mpos workstation id's to this list to distribute the 
     * notifications indicator message.
     * 
     * @param workstationIds
     */
    protected void addWorkstationIds(List<String> workstationIds)
    {
        for (Iterator<String> iterator = workstationIds.iterator(); iterator.hasNext();)
        {
            workstationIdsSet.add(iterator.next());
        }
    }
}
