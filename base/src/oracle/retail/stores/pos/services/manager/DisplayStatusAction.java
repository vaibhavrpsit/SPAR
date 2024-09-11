/* ===========================================================================
* Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/DisplayStatusAction.java /main/3 2013/02/21 12:05:45 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  02/19/13 - Added timer model for screen timeout
 *    cgreene   07/02/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager;

import java.awt.event.ActionEvent;

import oracle.retail.stores.foundation.manager.ifc.TierTechnicianIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PopupDialogAction;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DetailStatusBeanModel;

import org.apache.log4j.Logger;

/**
 * An action that will display the {@link DisplayRegisterStatusSite} in a popup
 * dialog. This action expects that the {@link TierTechnicianIfc} is running
 * as one of the {@link Dispatcher}'s local technicians.
 *
 * @author cgreene
 * @since 13.3
 */
public class DisplayStatusAction extends PopupDialogAction
{
    private static final Logger logger = Logger.getLogger(DisplayStatusAction.class);

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            TierTechnicianIfc tierTechnician = (TierTechnicianIfc)Dispatcher.getDispatcher().getLocalTechnician("APPLICATION");
            if (tierTechnician != null)
            {
                BusIfc[] buses = tierTechnician.getBuses();
                if (buses.length > 0)
                {
                    // Setup bean model information for the UI to display
                    BusIfc bus = buses[0];
                    DetailStatusBeanModel beanModel = new DisplayStatusSite().buildStatusModel(bus);                    
                    showDialog(bus, POSUIManagerIfc.DEVICE_STATUS_DIALOG, beanModel);
                }
                else
                {
                    logger.warn("Could not find an active bus to display system status with.");
                }
            }
            else
            {
                logger.error("TierTechnician is not configured for \"APPLICATION\" for the Dispatcher.");
            }
        }
        catch (TechnicianNotFoundException ex)
        {
            logger.error("TierTechnician is not configured for \"APPLICATION\" for the Dispatcher.", ex);
        }
    }

}
