/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/SpecialOrderDepositReturnShuttle.java /rgbustores_13.4x_generic_branch/2 2011/08/09 11:31:52 cgreene Exp $
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
 *    Rev 1.1   08 Nov 2003 01:27:14   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 05 2003 14:56:22   sfl
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

/**
 * Shuttles the required data from the Special Order cargo to the Sale Cargo.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class SpecialOrderDepositReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = -3616225107492024472L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    public static final String SHUTTLENAME = "SpecialOrderDepositReturnShuttle";

    /**
     * special order cargo
     */
    protected SpecialOrderCargo specialOrderCargo;

    /**
     * Get a local copy of the SpecialOrdercargo.
     * 
     * @param bus the bus being loaded
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        // retrieve special order cargo
        specialOrderCargo = (SpecialOrderCargo)bus.getCargo();
    }

    /**
     * Copy required data from the SpecialOrder cargo to the Pos Cargo. sets the
     * retailtransaction to the special order transaction.
     * 
     * @param bus the bus being unloaded
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);

        // retrieve Sale cargo
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        cargo.setRetailTransactionIfc(specialOrderCargo.getRetailTransaction());
    }
}