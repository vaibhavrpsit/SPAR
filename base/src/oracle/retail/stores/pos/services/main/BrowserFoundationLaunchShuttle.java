/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/BrowserFoundationLaunchShuttle.java /main/8 2012/10/30 16:49:44 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 10/30/12 - Logo click browser cleanup
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    nkgaut 09/29/08 - A new shuttle for Broswerfoundation Service
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import oracle.retail.stores.pos.services.browserfoundation.BrowserFoundationCargo;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

public class BrowserFoundationLaunchShuttle extends MainCargoShuttle implements ShuttleIfc
{

    private static final long serialVersionUID = 1L;

    /**
     * EmployeeIfc Object for operator information
     */
    private EmployeeIfc operator = null;

    /**
     * Register information
     */
    private RegisterIfc register = null;

    /**
     * Saves the relevent information from the current service.
     * 
     * @param bus Service Bus
     */
    public void load(BusIfc bus)
    {
        MainCargo cargo = (MainCargo) bus.getCargo();
        operator = cargo.getOperator();
        register = cargo.getRegister();
    }

    /**
     * Stores information in the next service.
     * 
     * @param bus Service Bus
     */
    public void unload(BusIfc bus)
    {
        BrowserFoundationCargo cargo = (BrowserFoundationCargo) bus.getCargo();
        cargo.setOperator(operator);
        cargo.setRegister(register);
    }
}