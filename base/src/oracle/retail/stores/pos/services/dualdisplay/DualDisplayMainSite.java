/* ===========================================================================  
* Copyright (c) 2012, 20012, Oracle and/or its affiliates. 
* All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dualdisplay/DualDisplayMainSite.java /main/1 2013/01/14 18:50:21 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    01/02/13 - dual display initial version
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.dualdisplay;

import java.io.Serializable;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.dualdisplay.DualDisplayManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.beans.DualDisplayBeanModel;
import oracle.retail.stores.pos.ui.beans.DualDisplayFrame;

/**
 * The site that displays the intial site in dual display screen
 * 
 * @author vbongu
 * @since 14.0
 */
public class DualDisplayMainSite extends PosSiteActionAdapter
{

    /** Serial version UID to prevent compile warnings */
    private static final long serialVersionUID = 7181902348912797354L;
    /** Dual display bean model */
    protected DualDisplayBeanModel ddModel;
    /** Enable dual display */
    protected boolean enableDualDisplay = false;
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    
    /**Timer interval between two messages */
    protected Integer messagesInterval;

    /**
     * Displays the main screen on dual display
     */
    @Override
    public void arrive(BusIfc bus)
    {

        DualDisplayManagerIfc ui = (DualDisplayManagerIfc)bus.getManager(DualDisplayManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        enableDualDisplay = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME,
                DualDisplayFrame.DUALDISPLAY_ENABLED, false);
        ddModel = new DualDisplayBeanModel();
        ddModel.setEnableDualDisplay(enableDualDisplay);

        try
        {
            if (enableDualDisplay)
            {

                Serializable[] messages = pm
                        .getParameterValues(ParameterConstantsIfc.BASE_DualDisplayMarketingMessages);
                String[] marketingMessages = new String[messages.length];
                for (int i = 0; i < messages.length; i++)
                {
                    marketingMessages[i] = (String)messages[i];
                }
                ddModel.setMarketingMessages(marketingMessages);
                
                messagesInterval = pm.getIntegerValue(ParameterConstantsIfc.BASE_DualDisplayMessagesInterval);
                ddModel.setMessagesInterval(messagesInterval);
                
                ui.showScreen(DualDisplayManagerIfc.DUALDISPLAY_MAIN, ddModel);
            }

        }
        catch (ParameterException e)
        {
            logger.error("parameter not found for displaying marketing messages");
        }

    }

}
