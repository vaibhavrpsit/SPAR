/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registeropen/BusinessDateReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech60_part2 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/31/10 - correct logger reference
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         4/1/2008 2:30:37 PM    Deepti Sharma   CR
 *         31016 forward port from v12x -> trunk
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registeropen;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.dailyoperations.businessdate.BusinessDateCargo;

/**
 * This shuttle carries the required contents from the BusinessDate service to
 * the RegisterOpen service.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $ instead.
 */
public class BusinessDateReturnShuttle implements ShuttleIfc
{
    static final long serialVersionUID = 3529602247028914392L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(BusinessDateReturnShuttle.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * class name constant
     */
    public static final String SHUTTLENAME = "BusinessDateReturnShuttle";

    /**
     * selected business date
     */
    protected EYSDate selectedBusinessDate;

    /**
     * Copies information from the cargo used in the BusinessDate service.
     * 
     * @param bus the bus being loaded
     */
    public void load(BusIfc bus)
    {

        // get business date list count and selection
        BusinessDateCargo cargo = (BusinessDateCargo) bus.getCargo();
        selectedBusinessDate = cargo.getSelectedBusinessDate();
    }

    /**
     * Copies information to the cargo used in the Main service.
     * 
     * @param bus the bus being unloaded
     */
    public void unload(BusIfc bus)
    {

        RegisterOpenCargo cargo = (RegisterOpenCargo) bus.getCargo();
        if (selectedBusinessDate != null)
        {
            cargo.getStoreStatus().setBusinessDate(selectedBusinessDate);
        }
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
