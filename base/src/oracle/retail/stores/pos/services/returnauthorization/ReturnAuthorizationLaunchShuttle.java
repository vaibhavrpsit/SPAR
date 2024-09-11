/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returnauthorization/ReturnAuthorizationLaunchShuttle.java /rgbustores_13.4x_generic_branch/2 2011/08/09 11:31:52 cgreene Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/09/11 - formatting and removed deprecated code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.returnauthorization;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

import org.apache.log4j.Logger;

/**
 * Return Authorization launch shuttle.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class ReturnAuthorizationLaunchShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = -1243159978846907485L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ReturnAuthorizationLaunchShuttle.class);

    /**
     * The shuttle class name constant.
     */
    public static final String SHUTTLENAME = "ReturnAuthorizationLaunchShuttle";

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * Outgoing SaleCargoIfc
     */
    protected SaleCargoIfc cargo = null;

    /**
     * Get a local copy of the Sale cargo.
     * 
     * @param bus the bus being loaded
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);

        // retrieve Sale cargo
        cargo = (SaleCargoIfc)bus.getCargo();
    }

    /**
     * Copy required data from the Sale cargo to the Return Authorization Cargo.
     * sets the access employee, sales associate, and return data.
     * 
     * @param bus the bus being unloaded
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);

        // retrieve return authorization cargo
        ReturnAuthorizationCargo returnAuthorizationCargo = (ReturnAuthorizationCargo)bus.getCargo();

        // set the access employee and sales associate
        returnAuthorizationCargo.setCustomerInfo(cargo.getCustomerInfo());
        returnAuthorizationCargo.setOperator(cargo.getOperator());
        returnAuthorizationCargo.setSalesAssociate(cargo.getEmployee());
        returnAuthorizationCargo.setRegister(cargo.getRegister());

        returnAuthorizationCargo.setTransaction(cargo.getRetailTransaction());
        returnAuthorizationCargo.setOriginalReturnTransactions(cargo.getOriginalReturnTransactions());

        returnAuthorizationCargo.setReturnResponse(cargo.getReturnResponse());

        returnAuthorizationCargo.setReturnResponseLineItems(null);
    }
}