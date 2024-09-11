/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/MainCargoShuttle.java /main/14 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    npoola    08/11/10 - Actual register is used for training mode instead of
 *                         OtherRegister.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.employee.EmployeeADOIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.services.admin.AdminCargo;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;

import org.apache.log4j.Logger;

/**
 * This shuttle copies the contents of one abstract financial cargo to another.
 * 
 * @version $Revision: /main/14 $
 */
public class MainCargoShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -5131900184069358503L;
    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(MainCargoShuttle.class);
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/14 $";

    protected MainCargo callingCargo;

    /**
     * Copies information from the cargo used in the service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        // get cargo reference and extract attributes
        callingCargo = (MainCargo)bus.getCargo();
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
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)bus.getCargo();
        RegisterADO registerADO = callingCargo.getRegisterADO();
        RegisterIfc register = (RegisterIfc)registerADO.toLegacy();
        if (callingCargo.isTrainingMode())
        {
            register.getWorkstation().setTrainingMode(true);
        }
        else
        {
            register.getWorkstation().setTrainingMode(false);
        }
        StoreADO store = registerADO.getStoreADO();
        EmployeeADOIfc operator = registerADO.getOperator();

        cargo.setStoreStatus((StoreStatusIfc)store.toLegacy());
        cargo.setRegister(register);

        cargo.setTenderLimits(callingCargo.getTenderLimits());
        if (operator != null)
        {
            cargo.setOperator((EmployeeIfc)operator.toLegacy());
        }
        else
        {
            cargo.setOperator(null);
        }

        String lastReprintableTransactionID = registerADO.getLastReprintableTransactionID();
        cargo.setLastReprintableTransactionID(lastReprintableTransactionID);

        CustomerInfoIfc customerInfo = callingCargo.getCustomerInfo();
        cargo.setCustomerInfo(customerInfo);

        if (cargo instanceof AdminCargo)
        {
            AdminCargo adminCargo = (AdminCargo)cargo;
            adminCargo.setTrainingMode(callingCargo.isTrainingMode());
            adminCargo.setRegisterADO(registerADO);
        }
    }

    /**
     * Updates the ContextFactory with the current bus
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
        String strResult = new String("Class:  MainCargoShuttle (Revision " + getRevisionNumber() + ") @" + hashCode());
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