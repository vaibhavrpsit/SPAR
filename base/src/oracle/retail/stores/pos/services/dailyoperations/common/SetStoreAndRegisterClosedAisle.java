/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/common/SetStoreAndRegisterClosedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/24/10 - set status stale to false
 *    cgreene   02/12/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.common;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This aisle will set the cargo's register and store to closed. This is
 * useful when the system is offline and the till is closed. BackOffice may
 * have closed this register and till without this register knowing.
 * <p>
 * Will mail a {@link CommonLetterIfc#CONTINUE} letter.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class SetStoreAndRegisterClosedAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 5284796368647362812L;

    /**
     * lane name constant
     */
    public static final String LANENAME = "SetStoreAndRegisterClosedAisle";

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)bus.getCargo();
        cargo.getRegister().setStatus(AbstractFinancialEntityIfc.STATUS_CLOSED);
        cargo.getStoreStatus().setStatus(AbstractFinancialEntityIfc.STATUS_CLOSED);
        cargo.getStoreStatus().setStale(false);

        bus.mail(CommonLetterIfc.CONTINUE);
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