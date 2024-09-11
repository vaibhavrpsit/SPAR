/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/MainCargoShuttleReturn.java /main/15 2014/06/06 15:03:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  06/06/14 - move training mode from main screen to admin screen
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    npoola    08/11/10 - Actual register is used for training mode instead of
 *                         OtherRegister.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:23:24 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:12:31 PM  Robert Pearse   
 *
 *  Revision 1.10  2004/09/23 00:07:13  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.9  2004/08/10 21:50:10  dcobb
 *  @scr 6792 TillStatusSite - cargo contains outdated register/till data
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;

import org.apache.log4j.Logger;

/**
 * This shuttle copies the contents of one abstract financial cargo to another.
 * 
 * @version $Revision: /main/15 $
 */
public class MainCargoShuttleReturn implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 2681341970087502018L;

    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(MainCargoShuttleReturn.class);
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/15 $";

    protected AbstractFinancialCargoIfc savedCargo;

    /**
     * Copies information from the cargo used in the service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        // get cargo reference and extract attributes
        savedCargo = (AbstractFinancialCargoIfc)bus.getCargo();
    }

    /**
     * Copies information to the cargo used in the service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        // get cargo reference and set attributes
        MainCargo cargo = (MainCargo)bus.getCargo();
        cargo.setCustomerInfo(savedCargo.getCustomerInfo());

        RegisterADO registerADO = cargo.getRegisterADO();
        RegisterIfc reg = savedCargo.getRegister();
        registerADO.fromLegacy(reg);
        registerADO.getStoreADO().fromLegacy(savedCargo.getStoreStatus());

        cargo.setTenderLimits(savedCargo.getTenderLimits());
        cargo.setOperator(savedCargo.getOperator());
        cargo.setLastReprintableTransactionID(savedCargo.getLastReprintableTransactionID());
        if (reg.getWorkstation()!= null && 
                reg.getWorkstation().isTrainingMode())
        {
            cargo.setTrainingMode(true);
        }
        else
        {
            cargo.setTrainingMode(false);
        }
    }

    /**
     * Updates the ContextFactory with the current bus
     * 
     * @param bus
     * @deprecated as of 13.4.1. Does nothing. Use {@link TourContext} instead.
     */
    protected void setupContext(BusIfc bus)
    {
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  MainCargoShuttleReturn (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}