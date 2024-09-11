/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/ReturnTransactionReturnShuttle.java /rgbustores_13.4x_generic_branch/2 2011/07/26 11:52:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    jswan     07/21/10 - Fixed issues around transaction search by tender.
 *    jswan     07/16/10 - Code review changes.
 *    jswan     07/16/10 - Modifications to support the escape/undo
 *                         functionality on the ReturnItemInformation screen in
 *                         the retrieved transaction context.
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    mahising  02/20/09 - Fixed customer issue for capture customer
 *                         inforamtion
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:56 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.8  2004/07/29 00:25:10  jdeleau
 *   @scr 6432 For tax exempt transactions being returned, make sure the return
 *   transaction is not tax exempt.  The current transaction also must not retain
 *   any of the customer information from the transaction being returned.
 *
 *   Revision 1.7  2004/07/16 00:47:00  lzhao
 *   @scr 6299: return gift card.
 *
 *   Revision 1.6  2004/03/22 22:39:45  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.5  2004/02/17 20:40:28  baa
 *   @scr 3561 returns
 *
 *   Revision 1.4  2004/02/13 22:46:22  baa
 *   @scr 3561 Returns - capture tender options on original trans.
 *
 *   Revision 1.3  2004/02/12 16:51:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 19 2003 13:22:40   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.0   Aug 29 2003 16:06:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:06:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   20 Feb 2002 14:45:06   cir
 * Removed the non returnable gift cards from the return arrays
 * Resolution for POS SCR-671: Gift card - multiple item return with expended gift card error
 *
 *    Rev 1.0   Sep 21 2001 11:24:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnData;
import oracle.retail.stores.pos.services.returns.returntransaction.ReturnTransactionCargo;

/**
 * This shuttle gets the data from the Return Transaction Service.
 */
public class ReturnTransactionReturnShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 251294877847834834L;

    /**
     * Child cargo
     */
    protected ReturnTransactionCargo rtCargo = null;

    /**
     * Store data from child service in the shuttle
     * 
     * @param bus Child Service Bus.
     */
    @Override
    public void load(BusIfc bus)
    {
        rtCargo = (ReturnTransactionCargo)bus.getCargo();
    }

    /**
     * Transfer child data to parent cargo.
     * 
     * @param bus Child Service Bus to copy cargo to.
     */
    @Override
    public void unload(BusIfc bus)
    {
        ReturnFindTransCargo cargo = (ReturnFindTransCargo)bus.getCargo();
        if (rtCargo.getTransferCargo())
        {
            // Build the ReturnData object from the PLU Items,
            // SaleReturnLineItemms
            // and Return items in the ReturnTransactionCargo
            cargo.setTransferCargo(true);
            ReturnData rd = cargo.buildReturnData(rtCargo.getPLUItems(), rtCargo.getReturnSaleLineItems(),
                    rtCargo.getReturnItems());
            cargo.setReturnData(rd);

            // Set the original data
            cargo.setOriginalTransaction(rtCargo.getOriginalTransaction());
            cargo.setOriginalTransactionId(rtCargo.getOriginalTransactionId());
            cargo.setOriginalExternalOrderReturnTransactions(rtCargo.getOriginalExternalOrderReturnTransactions());
        }
        else
        {
            // Reseting the ID here forces the lookup code to use the criteria
            // rather than the transaction ID on a retry.
            cargo.setOriginalTransactionId(null);
        }

        cargo.setSearchCriteria(rtCargo.getSearchCriteria());
        cargo.setTransactionFound(rtCargo.isTransactionFound());
    }

}