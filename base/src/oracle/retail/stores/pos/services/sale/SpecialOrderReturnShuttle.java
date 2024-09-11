/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/SpecialOrderReturnShuttle.java /rgbustores_13.4x_generic_branch/2 2011/08/09 11:31:52 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/09/11 - formatting and removed deprecated code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:08 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/05/20 19:45:44  jeffp
 *   @scr 2389 - removed line that resets the last printable transaction id.
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 07 2003 12:38:12   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 05 2003 14:14:52   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:04:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:09:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:43:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 28 2002 15:09:02   dfh
 * set last printable trans id for reprint receipt to empty "" since last real trans may have changed thru special order / crossreach
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Dec 04 2001 16:38:02   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

/**
 * Shuttles the required data from the Special Order cargo to the Pos Cargo.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class SpecialOrderReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = -7892521824739745915L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

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
     * retailtransaction to the newly created special order transaction.
     * 
     * @param bus the bus being unloaded
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);

        // retrieve Pos cargo
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();

        // pass along the special order transaction
        cargo.setRetailTransactionIfc(specialOrderCargo.getRetailTransaction());

        // if there is a transaction, set the tender limits
        if (cargo.getRetailTransaction() != null)
        {
            cargo.getRetailTransaction().setTenderLimits(cargo.getTenderLimits());
        }

    }
}
