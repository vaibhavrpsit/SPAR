/* ===========================================================================  
* Copyright (c) 2012, 20012, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DualDisplayMarketingBean.java /main/4 2013/10/31 14:10:32 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   10/31/13 - Update the messages on screen and change the
 *                         duration.
 *    ohorne    10/23/13 - Added border around marketingLabel to small offset
 *                         for text from Panel edge
 *    vbongu    01/15/13 - add loop interval property
 *    vbongu    01/02/13 - dual display initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

/**
 * Marketing bean in dual display
 * 
 * @author vbongu
 * @since 14.0
 */
public class DualDisplayMarketingBean extends BaseBeanAdapter
{

    private static final long serialVersionUID = -8275411124389372812L;
    /** The default bean name. */
    public static final String BEAN_NAME = "DualDisplayMarketingBean";

    /** Used to configure component layout with uifactory. */
    public static final String DD_UI_PREFIX = "DualDisplayMarketing";
    public static final String DD_UI_MARKETING_LABEL = "DualDisplayMarketing.marketingLabel";

    /** Interval between displaying messages in milliseconds */
    protected int INTERVAL_TIME = 10000;

    /** The messages timer. */
    protected Timer messagesTimer;

    /** Utility Manager */
    protected UtilityManagerIfc utility;

    /** JPanel for marketing */
    protected JPanel marketingPanel;
    /** JLabel */
    protected JLabel marketingLabel;

    /** Dual display bean model */
    protected DualDisplayBeanModel beanModel = new DualDisplayBeanModel();

    /**
     * Constructor
     */
    public DualDisplayMarketingBean()
    {
        UI_PREFIX = DD_UI_PREFIX;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#configure()
     */
    @Override
    public void configure()
    {

        // Initialize the panel
        setName(BEAN_NAME);
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new BorderLayout());

        // to use in future
        utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);

        buildMarketingPanel();
        add(marketingLabel, BorderLayout.CENTER);
    }

    /**
     * Build marketing panel and adds the timer for displaying messages in
     * intervals.
     */
    protected void buildMarketingPanel()
    {
        marketingLabel = uiFactory.createLabel("marketingLabel", "marketingLabel", null, DD_UI_MARKETING_LABEL);
        marketingLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Dimension d = marketingLabel.getPreferredSize();
        marketingLabel.setPreferredSize(d);

        messagesTimer = new Timer(0, new ActionListener()
        {

            String[] marketingMessages;
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (marketingMessages == null)
                {
                    marketingMessages = beanModel.getMarketingMessages();
                }
                else
                {
                    //get marketingMessage from parameter manager.
                    DispatcherIfc dispatcher = Gateway.getDispatcher();
                    ParameterManagerIfc pm = (ParameterManagerIfc) dispatcher.getManager(ParameterManagerIfc.TYPE);
                    try {
                        marketingMessages = pm.getStringValues(ParameterConstantsIfc.BASE_DualDisplayMarketingMessages);
                    } catch (ParameterException e1) {
                        // TODO Auto-generated catch block
                        logger.error("parameter not found for displaying marketing messages");
                        marketingMessages = beanModel.getMarketingMessages();
                    }
                    beanModel.setMarketingMessages(marketingMessages);
                }
                marketingLabel.setText("<html>"+marketingMessages[index]+"</html>");
                index++;
                if (index >= marketingMessages.length)
                {
                    index = 0;
                }
                updateBean();
            }            
        });
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#setModel()
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set DualDisplayBeanModel to null");
        }
        if (model instanceof DualDisplayBeanModel)
        {
            beanModel = (DualDisplayBeanModel)model;
            updateBean();
        }
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updateBean()
     */
    @Override
    protected void updateBean()
    {
        if (!messagesTimer.isRunning())
        {
            messagesTimer.start();
            INTERVAL_TIME = beanModel.getMessagesInterval();
            messagesTimer.setDelay(INTERVAL_TIME);
        }
    }

}
