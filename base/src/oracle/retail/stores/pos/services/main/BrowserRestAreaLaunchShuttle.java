/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/BrowserRestAreaLaunchShuttle.java /main/2 2012/10/30 16:49:44 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 10/30/12 - Logo click browser cleanup
 *    vbongu 10/23/12 - browser restarea launch shuttle
 *    
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.TierTechnicianIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.browserfoundation.BrowserFoundationCargo;

import org.apache.log4j.Logger;

/**
 * This shuttle carries the required contents from the calling service to the
 * browser service.
 * 
 * @author vbongu
 * @since 14.0
 */
public class BrowserRestAreaLaunchShuttle implements ShuttleIfc
{
    private static final long serialVersionUID = -7001945762131075970L;

    private static final Logger logger = Logger.getLogger(BrowserRestAreaLaunchShuttle.class);

    /** EmployeeIfc Object for operator information */
    protected EmployeeIfc operator = null;

    /**
     * Gets the relevant information from the current service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        if (bus.getCargo() instanceof UserAccessCargoIfc)
        {
            UserAccessCargoIfc callingCargo = (UserAccessCargoIfc) bus.getCargo();
            operator = callingCargo.getOperator();
        }
        else
        {
            operator = getOperatorFromExistingBus();
        }
    }

    /**
     * Stores information in the next service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        BrowserFoundationCargo cargo = (BrowserFoundationCargo) bus.getCargo();
        cargo.setOperator(operator);
    }

    /**
     * Return any operator found in any running bus in the tier.
     *
     * @return
     */
    protected EmployeeIfc getOperatorFromExistingBus()
    {
        try
        {
            TierTechnicianIfc tierTechnician = (TierTechnicianIfc)Dispatcher.getDispatcher().getLocalTechnician(
                    "APPLICATION");
            if (tierTechnician != null)
            {
                BusIfc[] buses = tierTechnician.getBuses();
                //If current site is MainMenu, should use browser button
                for (BusIfc bus : buses)
                {
                    if (bus.getCargo() instanceof UserAccessCargoIfc)
                    {
                        //Check if the operator is set in useraccesscargo
                        UserAccessCargoIfc useraccessCargo = (UserAccessCargoIfc)bus.getCargo();
                        EmployeeIfc operator = useraccessCargo.getOperator();
                        
                        if (operator != null)
                        {
                            // Mail RestArea to current bus
                            return operator;
                        }
                    }
                }
                logger.warn("There is no operator logged in for displaying browser.");
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
        return null;
    }
}