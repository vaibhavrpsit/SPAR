/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registeropen/BusinessDateLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   03/05/10 - Change businessDate from system date to current
 *                         businessDate
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         4/1/2008 2:30:37 PM    Deepti Sharma   CR
 *       31016 forward port from v12x -> trunk
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registeropen;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.dailyoperations.businessdate.BusinessDateCargo;

/**
 * This shuttle carries the required contents from the RegisterOpen service to
 * the BusinessDate service.
 * <p>
 * This shuttle used to set the businessDateList as one minus the current system
 * date. This was changed on 05MAR10 to just set the current businessDate with
 * <code>advanceDateFlag</code> turned off. It made more sense to prompt the
 * user with what the system believes the date to be currently and let them
 * correct/confirm it instead of the prompting the system date which may have
 * no relation to the current business date.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $ instead.
 */
public class BusinessDateLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 2066203133535353381L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(BusinessDateLaunchShuttle.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * class name constant
     */
    public static final String SHUTTLENAME = "BusinessDateLaunchShuttle";

    /**
     * business day choices
     */
    protected EYSDate[] businessDateList;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        RegisterOpenCargo cargo = (RegisterOpenCargo)bus.getCargo();
        StoreStatusIfc storeStatus = cargo.getStoreStatus();

        businessDateList = new EYSDate[1];
        businessDateList[0] = storeStatus.getBusinessDate();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        // set store status list in cargo
        BusinessDateCargo cargo = (BusinessDateCargo) bus.getCargo();
        cargo.setBusinessDateList(businessDateList);
        cargo.setDatabaseOffline(true);
        cargo.setAdvanceDateFlag(false);
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