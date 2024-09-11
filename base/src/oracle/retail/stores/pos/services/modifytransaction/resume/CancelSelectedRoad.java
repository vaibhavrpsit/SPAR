/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/CancelSelectedRoad.java /main/4 2014/05/14 14:41:27 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */

/**
 * Set cancel transaction.
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.modifytransaction.resume.ModifyTransactionResumeCargo;

/**
 * @author icole
 * 
 * New in F.
 */
public class CancelSelectedRoad extends LaneActionAdapter implements LaneActionIfc
{
    /**
     * Tell compiler to not generate a new serialVersionUID.
     */
    private static final long serialVersionUID = -5430237824489438355L;

    /**
     *  This road is traversed when the yes button is pressed confirming the desire 
     *  to cancel the suspended transaction and sets the cargo for canceling the transaction.
     *  @param bus - the bus traversing this lane.
     */
    public void traverse(BusIfc bus)
    {
     // Set up Function id and send bus to check access site
        ModifyTransactionResumeCargo cargo =(ModifyTransactionResumeCargo) bus.getCargo();
        cargo.setAccessFunctionID(RoleFunctionIfc.CANCEL_TRANSACTION);
    }
}
