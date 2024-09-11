/* ===========================================================================
* Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/patriot/ValidateIDLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:49 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         12/13/2005 4:47:05 PM  Barry A. Pape   
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.patriot;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.validateid.ValidateIDCargoIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * 
 * This shuttle sets the behavior of the ID Validation Service
 * 
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 * 
 */
public class ValidateIDLaunchShuttle implements ShuttleIfc
{
    /**
     The logger to which log messages will be sent
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.tender.patriot.ValidateIDLaunchShuttle.class);

    /** The cargo of the calling service **/
    protected TenderCargo callingCargo;
    
    /** (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     * @param bus
     */
    public void load(BusIfc bus)
    {
        callingCargo = (TenderCargo)bus.getCargo();
    }

    /** 
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     * @param bus
     */
    public void unload(BusIfc bus)
    {
        ValidateIDCargoIfc childCargo = (ValidateIDCargoIfc)bus.getCargo();
        
        // determine which screen to display
        UtilityIfc util;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }

        childCargo.setIDTypeCodeConstant(CodeConstantsIfc.CODE_LIST_PAT_CUSTOMER_ID_TYPES);
        childCargo.setCaptureCountry(true);
        childCargo.setAllowSwipe(util.getParameterValue("DriversLicenseSwipe", "Y").equals("Y"));
        childCargo.setAlwaysCaptureIssuer(true);
    }
}
