/* ===========================================================================  
* Copyright (c) 2012, 20012, Oracle and/or its affiliates. 
* All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DualDisplayBeanModel.java /main/1 2013/01/14 18:50:23 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    01/02/13 - dual display initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

/**
 * This model is used by DualDisplayBean
 * 
 * @author vbongu
 */
public class DualDisplayBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = -118957222058489391L;
    /** Marketing messages */
    protected String[] marketingMessages = null;
    /** Enable dual display boolean */
    protected boolean enableDualDisplay = false;
    
    protected Integer messagesInterval;

    /**
     * Comstructor
     */
    public DualDisplayBeanModel()
    {

    }

    /**
     * Sets the Marketing Messages
     * 
     * @param marketingMessages
     */
    public void setMarketingMessages(String[] marketingMessages)
    {
        this.marketingMessages = marketingMessages;

    }

    /**
     * Gets the Marketing Messages
     * 
     * @return marketingMessages
     */
    public String[] getMarketingMessages()
    {
        return marketingMessages;
    }

    /**
     * sets the boolean value to enable dual display
     * 
     * @param enableDualDisplay
     */
    public void setEnableDualDisplay(boolean enableDualDisplay)
    {
        this.enableDualDisplay = enableDualDisplay;
    }

    /**
     * check if dual display is enabled
     * 
     * @return enableDualDisplay
     */
    public boolean isEnableDualDisplay()
    {
        return enableDualDisplay;
    }

    /**
     * Gets the interval to loop through the messages
     * @return messagesInterval
     */
    public Integer getMessagesInterval()
    {
        return messagesInterval;
    }

    /**
     * Sets the message interval to loop through them
     * @param messagesInterval
     */
    public void setMessagesInterval(Integer messagesInterval)
    {
        this.messagesInterval = messagesInterval;
    }
}
