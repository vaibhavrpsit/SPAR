/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/SpecialOrderDepositLaunchShuttle.java /rgbustores_13.4x_generic_branch/2 2011/08/09 11:31:52 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/09/11 - formatting and removed deprecated code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/08 22:14:55  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/02/12 16:48:21  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:27:12   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 05 2003 14:55:06   sfl
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;

import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

import org.apache.log4j.Logger;

/**
 * Special order deposit launch shuttle.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
@SuppressWarnings("serial")
public class SpecialOrderDepositLaunchShuttle extends FinancialCargoShuttle
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(SpecialOrderDepositLaunchShuttle.class);

    /**
     * The shuttle class name constant.
     */
    public static final String SHUTTLENAME = "SpecialOrderDepositLaunchShuttle";

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

        // retrieve Pos cargo
        cargo = (SaleCargoIfc)bus.getCargo();
    }

    /**
     * Copy required data from the Pos cargo to the Special Order Cargo. sets
     * the access employee, sales associate, and register.
     * 
     * @param bus the bus being unloaded
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);

        // retrieve special order cargo
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo)bus.getCargo();

        // set the access employee and sales associate
        specialOrderCargo.setOperator(cargo.getOperator());
        specialOrderCargo.setSalesAssociate(cargo.getEmployee());
        specialOrderCargo.setRegister(cargo.getRegister());
        specialOrderCargo.setOrderTransaction((OrderTransactionIfc)cargo.getRetailTransaction());
    }
}
