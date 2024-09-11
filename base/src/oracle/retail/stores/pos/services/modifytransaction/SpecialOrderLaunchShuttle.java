/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/SpecialOrderLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
 * ===========================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  09/30/08 - Added generated serialVersionUID
 *    mchellap  09/29/08 - QW-IIMO Updates for code review comments
 *    mchellap  09/19/08 - QW-IIMO
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.modifytransaction;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

//------------------------------------------------------------------------------
/**
    Special order launch shuttle
**/
//------------------------------------------------------------------------------
public class SpecialOrderLaunchShuttle
extends FinancialCargoShuttle
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    private static final long serialVersionUID = -7647351874025053802L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifytransaction.SpecialOrderLaunchShuttle.class);

    public static final String SHUTTLENAME = "SpecialOrderLaunchShuttle";

    /**
        Outgoing PosCargo
    **/
    protected ModifyTransactionCargo cargo = null;

    //--------------------------------------------------------------------------
    /**
       Get a local copy of the Pos cargo.
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------

    public void load(BusIfc bus)
    {
        super.load(bus);

        // retrieve Pos cargo
        cargo = (ModifyTransactionCargo) bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
       Copy required data from the Pos cargo to the Special Order Cargo.
       sets the access employee, sales associate, and register.
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------

    public void unload(BusIfc bus)
    {
        super.unload(bus);

        // retrieve special order cargo
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo) bus.getCargo();

        // set the access employee and sales associate
        specialOrderCargo.setOperator(cargo.getOperator());
        specialOrderCargo.setSalesAssociate(cargo.getSalesAssociate());
        specialOrderCargo.setRegister(cargo.getRegister());
    }
}
