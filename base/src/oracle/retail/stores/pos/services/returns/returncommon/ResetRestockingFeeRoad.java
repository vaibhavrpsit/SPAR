/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/ResetRestockingFeeRoad.java /main/13 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    jkoppolu  07/12/10 - Modified as part of the fix for bug#9704082
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:44 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.3  2004/02/12 16:51:46  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:06:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   17 Jan 2002 17:35:42   baa
 * Initial revision.
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.0   Sep 21 2001 11:24:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.utility.TransactionUtilityManager;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This site sets the restocking fee to 0.0
 */
public class ResetRestockingFeeRoad extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 1455269411420910810L;

    /**
     * This site sets the restocking fee to 0.0
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Get the cargo
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();

        // Set the restocking fee to zero
        ReturnItemIfc returnItem = cargo.getReturnItem();
        returnItem.setRestockingFee(DomainGateway.getBaseCurrencyInstance());
        TransactionUtilityManager.setRestockingFeeOverriddenTransaction(true);

        // Set the last item returned index.
        cargo.setLastLineItemReturnedIndex(cargo.getCurrentItem());
        // If the return is from an external order, set the return indicator for
        // the
        // corresponding external order item to true; This tracks which external
        // orders have been returned. This helps fulfill the requirement that
        // all
        // external order items must be returned.
        if (cargo.isExternalOrder())
        {
            cargo.setAssociatedExternalOrderItemReturnedStatus(cargo.getPLUItem().getReturnExternalOrderItem(), true);
        }
    }
}
